package com.mygdx.hadal.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.serialization.KryoSerialization;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.packets.PacketsAttacks;
import com.mygdx.hadal.server.packets.PacketsLoadout;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.*;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.utils.TiledObjectUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

/**
 * This is the server of the game.
 * @author Nurgarita Nelfram
 */
public class KryoServer {
	
	//Me server
	private Server server;
	
	//This is the gsm of the server
	public final GameStateManager gsm;
	
	//These keep track of all connected players, their mice and scores.
	private ObjectMap<Integer, User> users;

	//name of the server to be displayed in the lobby state
	private String serverName = "";

	public KryoServer(GameStateManager gameStateManager) {
		this.gsm = gameStateManager;
	}
	
	/**
	 * This is called upon starting a new server. initialize server and tracked client data 
	 * start is false if we are loading singleplayer and don't actually want the server to start
	 */
	public void init(boolean start) {
		//this apparently saves a bit of time when serializing certain classes
		Kryo kryo = new Kryo() {

			@Override
			public boolean isFinal(Class type) {
				if (type.equals(Array.class) || type.equals(ObjectMap.class) || type.equals(Vector2.class) || type.equals(Vector3.class)) {
					return true;
				} else {
					return super.isFinal(type);
				}
			}
		};
		KryoSerialization serialization = new KryoSerialization(kryo);
		this.server = new Server(65536, 32768, serialization);

		this.users = new ObjectMap<>();

		//reset used teams. This is needed to prevent all the usable "alignments" from being used up if server is remade
		AlignmentFilter.resetUsedAlignments();

		users.put(0, new User(null, new SavedPlayerFields(gsm.getLoadout().getName(), 0), new SavedPlayerFieldsExtra()));

		if (!start) { return; }

		Listener packetListener = new Listener() {
			
			@Override
			public void disconnected(final Connection c) {
				final PlayState ps = getPlayState();

				//Identify the player that disconnected
				User user = users.get(c.getID());
				if (user != null && ps != null) {

					//free up the disconnected user's player slot
					user.getHitBoxFilter().setUsed(false);

					Player player = user.getPlayer();
					if (player != null) {
						ps.addPacketEffect(() -> {

							//Inform all that the player disconnected and kill the player
							if (player.getPlayerData() != null) {
								player.getPlayerData().die(ps.getWorldDummy().getBodyData(), DamageSource.DISCONNECT);
							}
							addNotificationToAll(ps, "", UIText.CLIENT_DISCONNECTED.text(player.getName()),
									true, DialogType.SYSTEM);
						});
					}
					ps.addPacketEffect(() -> {

						//remove disconnecting player from users
						users.remove(c.getID());
						ps.getScoreWindow().syncScoreTable();
						sendToAllTCP(new Packets.RemoveScore(c.getID()));
					});
				}

				//If in a victory state, count a disconnect as ready so disconnected players don't prevent return to hub.
				if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof final ResultsState vs) {
					Gdx.app.postRunnable(() -> vs.readyPlayer(c.getID()));
				}
			}
			
			/**
        	 * Note that the order of these if/else is according to approximate frequency of packets.
        	 * This might have a (very minor) effect on performance or something idk
        	 */
			@Override
			public void received(final Connection c, Object o) {
				if (o instanceof final PacketsSync.SyncClientSnapshot p) {
					final PlayState ps = getPlayState();

					User user = users.get(c.getID());
					if (user != null && ps != null) {
						Player player = user.getPlayer();
						if (player != null) {
							player.onReceiveSync(p, p.timestamp);
						}
					}
				}

				/*
				 * This packet is sent periodically to inform the server of the client's inputs
				 * this includes what buttons the client is holding as well as their mouse position
				 */
				if (o instanceof final Packets.SyncKeyStrokes p) {
					final PlayState ps = getPlayState();

					User user = users.get(c.getID());
					if (user != null && ps != null) {

						Player player = user.getPlayer();
						if (player != null) {
							if (player.getController() != null) {
								ps.addPacketEffect(() -> player.getController().syncClientKeyStrokes(p.mouseX, p.mouseY,
										p.playerX, p.playerY, p.actions, p.timestamp));
							}
						}
					}
				}

				if (o instanceof final PacketsAttacks.SingleClientIndependent p) {

					final PlayState ps = getPlayState();
					User user = users.get(c.getID());
					if (user != null && ps != null) {
						Player player = user.getPlayer();
						if (player != null) {
							ps.addPacketEffect(() -> {
								if (p instanceof PacketsAttacks.SingleClientDependent p1) {
									Hitbox hbox;
									if (p instanceof PacketsAttacks.SingleClientDependentExtra p2) {
										hbox = p.attack.initiateSyncedAttackSingle(ps, player, p.pos, p.velo, c.getID(), false, p2.extraFields);
									} else {
										hbox = p.attack.initiateSyncedAttackSingle(ps, player, p.pos, p.velo, c.getID(), false);
									}
									hbox.setEntityID(new UUID(p1.uuidMSB, p1.uuidLSB));
								} else {
									if (p instanceof PacketsAttacks.SingleClientIndependentExtra p1) {
										p.attack.initiateSyncedAttackSingle(ps, player, p.pos, p.velo, c.getID(), false, p1.extraFields);
									} else {
										p.attack.initiateSyncedAttackSingle(ps, player, p.pos, p.velo, c.getID(), false);
									}
								}
							});
						}
					}
				}

				if (o instanceof final PacketsAttacks.MultiClientIndependent p) {
					final PlayState ps = getPlayState();
					User user = users.get(c.getID());
					if (user != null && ps != null) {
						Player player = user.getPlayer();
						if (player != null) {
							ps.addPacketEffect(() -> {
								if (p instanceof PacketsAttacks.MultiClientDependent p1) {
									Hitbox[] hboxes;
									if (p instanceof PacketsAttacks.MultiClientDependentExtra p2) {
										hboxes = p.attack.initiateSyncedAttackMulti(ps, player, p.weaponVelo, p.pos, p.velo, c.getID(), false, p2.extraFields);
									} else {
										hboxes = p.attack.initiateSyncedAttackMulti(ps, player, p.weaponVelo, p.pos, p.velo, c.getID(), false);
									}
									for (int i = 0; i < hboxes.length; i++) {
										hboxes[i].setEntityID(new UUID(p1.uuidMSB[i], p1.uuidLSB[i]));
									}
								} else {
									if (p instanceof PacketsAttacks.MultiClientIndependentExtra p2) {
										p.attack.initiateSyncedAttackMulti(ps, player, p.weaponVelo, p.pos, p.velo, c.getID(), false, p2.extraFields);
									} else {
										p.attack.initiateSyncedAttackMulti(ps, player, p.weaponVelo, p.pos, p.velo, c.getID(), false);
									}
								}
							});
						}
					}
				}

				if (o instanceof final PacketsAttacks.SyncedAttackNoHbox p) {
					final PlayState ps = getPlayState();
					User user = users.get(c.getID());
					if (user != null && ps != null) {
						Player player = user.getPlayer();
						if (player != null) {
							ps.addPacketEffect(() -> {
								if (p instanceof PacketsAttacks.SyncedAttackNoHboxExtra p1) {
									p.attack.initiateSyncedAttackNoHbox(ps, player, p.pos, c.getID(), false, p.independent, p1.extraFields);
								} else {
									p.attack.initiateSyncedAttackNoHbox(ps, player, p.pos, c.getID(), false, p.independent);
								}
							});
						}
					}
				}

				/*
				 * The Client has connected.
				 * Notify clients and create a new player for the client. Also, tell the new client what level to load
				 */
				else if (o instanceof final Packets.PlayerConnect p) {
					final PlayState ps = getPlayState();
					if (ps != null) {
						HashMap<String, Integer> modeSettings = gsm.getSetting().getModeSettings(ps.getMode());
						if (p.firstTime) {
							
							//reject clients with wrong version
							if (!HadalGame.VERSION.equals(p.version)) {
								sendToTCP(c.getID(), new Packets.ConnectReject(UIText.INCOMPATIBLE.text(HadalGame.VERSION)));
								return;
							}

							//if no server password, the client connects.
							if (!"".equals(gsm.getSetting().getServerPassword())) {
								//password being null indicates the client just attempted to connect.
								//otherwise, we check whether the password entered matches
								if (p.password == null) {
									sendToTCP(c.getID(), new Packets.PasswordRequest());
									return;
								} else if (!gsm.getSetting().getServerPassword().equals(p.password)){
									sendToTCP(c.getID(), new Packets.ConnectReject(UIText.INCORRECT_PASSWORD.text()));
									return;
								}
							}
							addNotificationToAllExcept(ps, c.getID(), "", UIText.CLIENT_CONNECTED.text(p.name), true, DialogType.SYSTEM);

							//clients joining full servers or in the middle of matches join as spectators
							if (getNumPlayers() >= ps.getGsm().getSetting().getMaxPlayers() + 1) {
								sendToTCP(c.getID(), new Packets.LoadLevel(ps.getLevel(), ps.getMode(), modeSettings, p.firstTime, true));
								return;
							}

							//joining midgame in modes which do not allow for it makes client join as spectator
							if (!ps.getMode().isJoinMidGame()) {
								sendToTCP(c.getID(), new Packets.LoadLevel(ps.getLevel(), ps.getMode(), modeSettings, p.firstTime, true));
								return;
							}
						}
                        sendToTCP(c.getID(), new Packets.LoadLevel(ps.getLevel(), ps.getMode(), modeSettings, p.firstTime, false));
					}
				}
				
				/*
				 * The Client has loaded the level.
				 * Announce the new player joining and catchup the new client.
				 * this is also where we determine if the client is a spectator or not
				 */
				else if (o instanceof final Packets.ClientLoaded p) {
					final PlayState ps = getPlayState();
					//notify players of new joiners
					if (p.firstTime) {
						sendNotification(c.getID(), "", UIText.CLIENT_JOINED.text(serverName), false, DialogType.SYSTEM);
					}

					//catch up client
					if (ps != null) {
						ps.addPacketEffect(() -> {
							ps.catchUpClient(c.getID());

							//client joins as a spectator if their packet specifies so,
							//or if they were previously a spectator and are joining a non-hub level
							boolean spectator = p.spectator || (p.lastSpectator && !ps.getMode().isHub());

							//If the client has already been created, we create a new player, otherwise we reuse their old data.
							User user = users.get(c.getID());
							if (user != null) {

								//if the player is told to start as a spectator or was a spectator prior to the match, they join as a spectator
								if (user.isSpectator()) {
									spectator = true;
								}

								Player player = user.getPlayer();
								if (player != null) {

									//alive check prevents duplicate players if entering/respawning simultaneously
									if (!player.isAlive() && player.getPlayerData() != null) {
										if (ps.isReset()) {
											createNewClientPlayer(ps, c.getID(), p.name, p.loadout, player.getPlayerData(),
													true, spectator, p.firstTime, null);
										} else {
											createNewClientPlayer(ps, c.getID(), p.name, player.getPlayerData().getLoadout(),
													player.getPlayerData(), false, spectator, p.firstTime, null);
										}
									}
								} else {
									createNewClientPlayer(ps, c.getID(), p.name, p.loadout, null, true, spectator,
											p.firstTime, null);
								}
							} else {
								createNewClientPlayer(ps, c.getID(), p.name, p.loadout, null, true, spectator,
										p.firstTime, null);
							}

							//this just updates user's "last primary weapon" which is only used for a single artifact rn
							User userUpdated = users.get(c.getID());
							if (userUpdated != null) {
								userUpdated.setLastEquippedPrimary(p.loadout.multitools[0]);
							}

							//sync client ui elements
							sendToTCP(c.getID(), new Packets.SyncUI(ps.getUiExtra().getMaxTimer(), ps.getUiExtra().getTimer(),
									ps.getUiExtra().getTimerIncr(),	AlignmentFilter.currentTeams, AlignmentFilter.teamScores));
							sendToTCP(c.getID(), new Packets.SyncSharedSettings(ps.getGsm().getSharedSetting()));
						});
					}
				}
				
				/*
				 * The Client has loaded the level.
				 * sync the client's loadout and activate the event connected to the start point.
				 */
				else if (o instanceof Packets.ClientPlayerCreated) {
					User user = users.get(c.getID());
					if (user != null) {
						Player player = user.getPlayer();
						if (player != null) {
							if (player.getPlayerData() != null) {
								HadalGame.server.sendToAllTCP(new PacketsLoadout.SyncWholeLoadout(
										c.getID(), player.getPlayerData().getLoadout(), false));
								player.activateStartingEvents();
							}
						}
					}
				}
				
				/*
				 * The Client has changed their loadout (in the hub because anywhere else is handled server side)
				 * Find the client changing and update their player loadout
				 */
				else if (o instanceof final PacketsLoadout.SyncLoadoutClient p) {
					final PlayState ps = getPlayState();

    				User user = users.get(c.getID());
					if (user != null && ps != null) {
						Player player = user.getPlayer();
						if (player != null) {
							if (player.getPlayerData() != null) {
								ps.addPacketEffect(() -> {
									if (p instanceof PacketsLoadout.SyncEquipClient s) {
										player.getPlayerData().syncEquip(s.equip);
										player.getPlayerData().syncServerEquipChangeEcho(c.getID(), s.equip);
									}
									else if (p instanceof PacketsLoadout.SyncArtifactAddClient s) {
										player.getPlayerData().addArtifact(s.artifactAdd, false, s.save);
									}
									else if (p instanceof PacketsLoadout.SyncArtifactRemoveClient s) {
										player.getPlayerData().removeArtifact(s.artifactRemove, false);
									}
									else if (p instanceof PacketsLoadout.SyncActiveClient s) {
										player.getPlayerData().syncActive(s.active);
										player.getPlayerData().syncServerActiveChangeEcho(c.getID(), s.active);
									}
									else if (p instanceof PacketsLoadout.SyncCharacterClient s) {
										player.getPlayerData().setCharacter(s.character);
										player.getPlayerData().syncServerCharacterChange(s.character);
									}
									else if (p instanceof PacketsLoadout.SyncTeamClient s) {
										player.getPlayerData().setTeam(s.team);
										player.getPlayerData().syncServerTeamChange(s.team);
									}
									else if (p instanceof PacketsLoadout.SyncCosmeticClient s) {
										player.getPlayerData().setCosmetic(s.cosmetic);
										player.getPlayerData().syncServerCosmeticChange(s.cosmetic);
									}
								});
							}
						}
					}
        		}

				else if (o instanceof final PacketsLoadout.SyncWholeLoadout p) {
					final PlayState ps = getPlayState();
					User user = users.get(c.getID());
					if (user != null && ps != null) {
						Player player = user.getPlayer();
						if (player != null) {
							if (player.getPlayerData() != null) {
								ps.addPacketEffect(() -> {
									player.getPlayerData().syncLoadout(p.loadout, false, false);
									player.getPlayerData().syncServerWholeLoadoutChange();
								});
							}
						}
					}
				}

				/*
				 * A Client has unpaused the game
				 * Return to the PlayState and inform everyone who unpaused.
				 */
				else if (o instanceof Packets.Unpaused) {
        			if (!gsm.getStates().empty()) {
						User user = users.get(c.getID());
						if (user != null) {
							Player player = user.getPlayer();

							//if pauses are enabled, unpause and remove pause state (and setting state)
							if (player != null && gsm.getSetting().isMultiplayerPause()) {
								if (gsm.getStates().peek() instanceof final PauseState ps) {
									addNotificationToAll(ps.getPs(), "", UIText.SERVER_UNPAUSED.text(player.getName()), true, DialogType.SYSTEM);
									ps.setToRemove(true);
								}
								if (gsm.getStates().peek() instanceof final SettingState ss) {
									addNotificationToAll(ss.getPlayState(), "", UIText.SERVER_UNPAUSED.text(player.getName()), true, DialogType.SYSTEM);
									ss.setToRemove(true);
								}
								if (gsm.getStates().peek() instanceof final AboutState as) {
									addNotificationToAll(as.getPlayState(), "", UIText.SERVER_UNPAUSED.text(player.getName()), true, DialogType.SYSTEM);
									as.setToRemove(true);
								}
								HadalGame.server.sendToAllTCP(new Packets.Unpaused());
							}
						}
        			}
				}
				
				/*
				 * A Client has sent the server a message.
				 * Display the notification and echo it to all clients  
				 */
				else if (o instanceof final Packets.ClientChat p) {
					final PlayState ps = getPlayState();
					if (ps != null) {
						addChatToAll(ps, p.text, p.type, c.getID());
					}
				}

				/*
				 * client tries to pause. We pause the game if pause is enabled
				 */
				else if (o instanceof final Packets.Paused p) {
					final PlayState ps = getPlayState();
					if (ps != null) {
						if (gsm.getSetting().isMultiplayerPause()) {
							ps.addPacketEffect(() -> gsm.addPauseState(ps, p.pauser, PlayState.class, true));
						}
					}
				}

				/*
				 * Respond to this packet sent from the client periodically so the client knows their latency.
				 */
				else if (o instanceof final Packets.LatencySyn p) {
					final PlayState ps = getPlayState();

					User user = users.get(c.getID());
					if (user != null && ps != null) {
						SavedPlayerFields score = user.getScores();
						if (score != null) {
							if (score.getPing() != p.latency) {
								score.setPing(p.latency);
								user.setScoreUpdated(true);
							}
						}
						server.sendToUDP(c.getID(), new Packets.LatencyAck(ps.getTimer(), p.timestamp));
					}
				}

				/*
				 * The client tried to emote. make them emote, if possible
				 */
				else if (o instanceof final Packets.SyncEmote p) {
					final PlayState ps = getPlayState();
					User user = users.get(c.getID());
					if (user != null && ps != null) {
						Player player = user.getPlayer();
						if (player != null) {
							ps.addPacketEffect(() -> ps.getChatWheel().emote(player, p.emoteIndex));
						}
					}
				}

				/*
				 * A Client has said they want to enter spectator mode
				 */
				else if (o instanceof Packets.StartSpectate) {
					final PlayState ps = getPlayState();
					if (ps != null) {
						User user = users.get(c.getID());
						if (user != null) {
							Player player = user.getPlayer();
							if (player != null) {
								ps.addPacketEffect(() -> ps.becomeSpectator(player, true));
							}
						}
					}
				}
				
				/*
				 * A Client has said they want to exit spectator mode
				 */
				else if (o instanceof final Packets.EndSpectate p) {
					final PlayState ps = getPlayState();
					if (ps != null) {
						ps.addPacketEffect(() -> {
							users.get(c.getID()).getScoresExtra().setLoadout(p.loadout);
							ps.exitSpectator(users.get(c.getID()));
						});
					}
				}
				
				/*
				 * A Client has said they are ready to return to hub state from results state
				 * Ready that player in the results state.
				 */
				else if (o instanceof Packets.ClientReady) {
					if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof final ResultsState vs) {
						Gdx.app.postRunnable(() -> vs.readyPlayer(c.getID()));
					}
				}

				/*
				 * A Client has typed /killme and wants their player to be killed (not disconnected)
				 */
				else if (o instanceof Packets.ClientYeet) {
					final PlayState ps = getPlayState();
					if (ps != null) {
						User user = users.get(c.getID());
						if (user != null) {
							Player player = user.getPlayer();
							if (player != null) {
								if (player.getPlayerData() != null) {
									ps.addPacketEffect(() -> player.getPlayerData().receiveDamage(9999, new Vector2(),
											player.getPlayerData(), false, null, DamageSource.MISC));
								}
							}
						}
					}
				}

				else if (o instanceof final Packets.DeleteClientSelf p) {
					final PlayState ps = getPlayState();
					if (null != ps) {
						User vic = users.get(c.getID());
						if (null != vic) {
							ps.addPacketEffect(() -> {
								if (null != vic.getPlayer()) {
									HadalEntity perp = ps.findEntity(p.uuidMSB, p.uuidLSB);
									if (perp instanceof Schmuck schmuck) {
										vic.getPlayer().getPlayerData().die(schmuck.getBodyData(), p.source, p.tags);
									} else {
										vic.getPlayer().getPlayerData().die(ps.getWorldDummy().getBodyData(), p.source, p.tags);
									}
								}
							});
						}
					}
				}

				else if (o instanceof Packets.ActivateEvent p) {
					final PlayState ps = getPlayState();
					if (null != ps) {
						User user = users.get(c.getID());
						if (user != null) {
							Player player = user.getPlayer();
							if (player != null) {
								ps.addPacketEffect(() -> {
									HadalEntity entity = ps.findEntity(p.uuidMSB, p.uuidLSB);
									if (entity != null) {
										if (entity instanceof Event event) {
											event.getEventData().preActivate(null, player);
										}
									}
								});
							}
						}
					}
				}

				else if (o instanceof Packets.RequestStartSyncedEvent p) {
					final PlayState ps = getPlayState();
					if (null != ps) {
						ps.addPacketEffect(() -> {
							Event event = TiledObjectUtil.getTriggeredEvents().get(p.triggeredID);
							if (null != event) {
								Object packet = event.onServerSyncInitial();
								if (null != packet) {
									sendToTCP(c.getID(), packet);
								}
							}
						});
					}
				}
			}
		};
		
//        server.addListener(new Listener.LagListener(50, 100, packetListener));
		server.addListener(packetListener);

		try {
			server.bind(gsm.getSetting().getPortNumber(), gsm.getSetting().getPortNumber());
		} catch (IOException e) {
			if (gsm.getStates().peek() instanceof LobbyState lobby) {
				lobby.setNotification(UIText.PORT_FAIL.text(Integer.toString(gsm.getSetting().getPortNumber())));
			}
		}	
		registerPackets();
		server.start();
	}
	
	/**
	 * This is called whenever the server creates a new player for a client
	 * @param ps: This is the server's current play state
	 * @param connID: This is the connID of the client requesting a new player
	 * @param name: The name of the new player
	 * @param loadout: The loadout of the new player
	 * @param data: The player data of the new player.
	 * @param reset: Do we want to reset the new player's hp/fuel/ammo etc?
	 * @param spectator: is this player created as a spectator?
	 * @param justJoined: Is this a newly connecting client or a newly respawned one?
	 * @param startPoint: The start point to spawn the new client player at
	 */
	public void createNewClientPlayer(final PlayState ps, final int connID, final String name, final Loadout loadout,
	  final PlayerBodyData data, final boolean reset, final boolean spectator, boolean justJoined, final Event startPoint) {

		ps.addPacketEffect(() -> {

			//Update that player's fields or give them new ones if they are a new client
			User user;
			if (users.containsKey(connID)) {
				user = users.get(connID);
			} else {
				user = new User(null, new SavedPlayerFields(name, connID), new SavedPlayerFieldsExtra());
				users.put(connID, user);
				user.setTeamFilter(loadout.team);
			}

			Event newSave = null;
			if (startPoint != null) {
				newSave = startPoint;
			}

			//set the client as a spectator if requested
			if (spectator) {
				ps.startSpectator(user, connID);
			} else {
				//Create a new player with the designated fields and give them a mouse pointer.
				Player newPlayer = ps.createPlayer(newSave, name, loadout, data, connID, user, reset, false, justJoined,
						user.getHitBoxFilter().getFilter());

				user.setPlayer(newPlayer);
				user.setSpectator(false);
			}
		});
	}
	
	/**
	 * This gets the server's playstate. This allows the server to make changes to a playstate underneath a pausestate.
	 * @return server's playstate and null if there isn't one
	 */
	public PlayState getPlayState() {
		if (!gsm.getStates().empty()) {
			GameState currentState = gsm.getStates().peek();
			if (currentState instanceof PlayState) {
				return (PlayState) currentState;
			} else if (currentState instanceof PauseState) {
				return ((PauseState) currentState).getPs();
			} else if (currentState instanceof SettingState) {
				return ((SettingState) currentState).getPlayState();
			} else if (currentState instanceof AboutState) {
				return ((AboutState) currentState).getPlayState();
			}else if (currentState instanceof ResultsState) {
				return ((ResultsState) currentState).getPs();
			}
		}
		return null;
	}
	
	/**
	 * This makes a client display a notification
	 * @param connID: id of the client to send the notification to
	 * @param name: name giving the notification
	 * @param text: notification text
	 * @param override: Can this notification be overriden by other notifications?
	 * @param type: type of dialog (dialog, system msg, etc)
	 */
	public void sendNotification(int connID, String name, String text, boolean override, final DialogType type) {
		sendToTCP(connID, new Packets.ServerNotification(name, text, override, type));
	}
	
	/**
	 * This adds a notification all clients and also the server themselves
	 * @param ps: server's current playstate
	 * @param name: name giving the notification
	 * @param text: notification text
	 * @param override: Can this notification be overriden by other notifications?
	 * @param type: type of dialog (dialog, system msg, etc)
	 */
	public void addNotificationToAll(final PlayState ps, final String name, final String text, final boolean override, final DialogType type) {
		if (ps.getDialogBox() != null && server != null) {
			server.sendToAllTCP(new Packets.ServerNotification(name, text, override, type));
			Gdx.app.postRunnable(() -> ps.getDialogBox().addDialogue(name, text, "", true, true, true, 3.0f, null, null, type));
		}
	}

	/**
	 * This sends a chat message to all clients and also the server themselves
	 * @param ps: server's current playstate
	 * @param text: the content of the chat message
	 * @param type: type of chat (system, normal chat)
	 * @param connID: connID of the player sending the chat message
	 */
	public void addChatToAll(final PlayState ps, final String text, final DialogType type, final int connID) {
		if (ps.getMessageWindow() != null && server != null) {
			server.sendToAllTCP(new Packets.ServerChat(text, type, connID));
			Gdx.app.postRunnable(() -> ps.getMessageWindow().addText(text, type, connID));
		}
	}
	
	/**
	 * This makes all client display a notification and also self, excluding one client
	 * @param ps: server's current playstate
	 * @param connID: id of the client to exclude
	 * @param name: name giving the notification
	 * @param text: notification text
	 * @param override: Can this notification be overriden by other notifications?
	 * @param type: type of dialog (dialog, system msg, etc)
	 */
	public void addNotificationToAllExcept(final PlayState ps, int connID, final String name, final String text, boolean override, DialogType type) {
		if (ps.getDialogBox() != null && server != null) {
			server.sendToAllExceptTCP(connID, new Packets.ServerNotification(name, text, override, type));
			Gdx.app.postRunnable(() -> ps.getDialogBox().addDialogue(name, text, "", true, true, true, 3.0f, null, null, type));
		}
	}
	
	/**
	 * This returns the number of non-spectator, non-bot players. used to determine whether the server is full or not.
	 */
	public int getNumPlayers() {
		int playerNum = 0;
		
		for (ObjectMap.Entry<Integer, User> conn : users.iterator()) {
			if (!conn.value.isSpectator() && conn.key >= 0.0f) {
				playerNum++;
			}
		}
		return playerNum;
	}
	
	private void registerPackets() {
		Kryo kryo = server.getKryo();
		Packets.allPackets(kryo);
	}
	
	public void sendToAllTCP(Object p) {
		if (server != null) {
			server.sendToAllTCP(p);
		}
	}

	public void sendToAllUDP(Object p) {
		if (server != null) {
			server.sendToAllUDP(p);
		}
	}

	public void sendToAllExceptTCP(int connID, Object p) {
		if (server != null) {
			server.sendToAllExceptTCP(connID, p);
		}
	}

	public void sendToAllExceptUDP(int connID, Object p) {
		if (server != null) {
			server.sendToAllExceptUDP(connID, p);
		}
	}
	
	public void sendToTCP(int connID, Object p) {
		if (server != null) {
			server.sendToTCP(connID, p);
		}
	}
	
	public void sendToUDP(int connID, Object p) {
		if (server != null) {
			server.sendToUDP(connID, p);
		}
	}

	/**
	 * This boots the designated player from the game
	 */
	public void kickPlayer(PlayState ps, User user, int connID) {
		if (server != null) {
			if (user.getPlayer() != null) {
				addNotificationToAll(ps,"", UIText.KICKED.text(user.getPlayer().getName()), true, DialogType.SYSTEM);
			}
			sendToTCP(connID, new Packets.ClientYeet());
		}
	}

	public Server getServer() {	return server; }

	public ObjectMap<Integer, User> getUsers() { return users; }

	public void setServerName(String serverName) { this.serverName = serverName; }
}
