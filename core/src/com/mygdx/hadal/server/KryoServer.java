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
import com.mygdx.hadal.event.PickupEquip;
import com.mygdx.hadal.event.hub.Vending;
import com.mygdx.hadal.managers.StateManager;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.map.SettingArcade;
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
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.users.UserManager;
import com.mygdx.hadal.utils.TiledObjectUtil;
import com.mygdx.hadal.utils.UnlocktoItem;

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

	public final UserManager usm;

	//name of the server to be displayed in the lobby state
	private String serverName = "";

	public KryoServer(UserManager userManager) {
		this.usm = userManager;
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

		//reset used teams. This is needed to prevent all the usable "alignments" from being used up if server is remade
		AlignmentFilter.resetUsedAlignments();

		usm.resetUsers();
		usm.setConnID(0);
		usm.getUsers().put(0, new User(0, JSONManager.loadout.getName(), new Loadout(JSONManager.loadout)));

		if (!start) { return; }

		Listener packetListener = new Listener() {
			
			@Override
			public void disconnected(final Connection c) {
				final PlayState ps = getPlayState();
				if (ps != null) {
					ps.addPacketEffect(() -> {
						//Identify the player that disconnected
						User user = usm.getUsers().get(c.getID());
						if (user != null) {

							//free up the disconnected user's player slot
							user.getHitboxFilter().setUsed(false);

							Player player = user.getPlayer();
							if (player != null) {
								//Inform all that the player disconnected and kill the player
								if (player.getPlayerData() != null) {
									player.getPlayerData().die(ps.getWorldDummy().getBodyData(), DamageSource.DISCONNECT);
								}
								addNotificationToAll(ps, "", UIText.CLIENT_DISCONNECTED.text(player.getName()),
										true, DialogType.SYSTEM);
							}

							//remove disconnecting player from users
							usm.getUsers().remove(c.getID());
							ps.getScoreWindow().syncScoreTable();
							sendToAllTCP(new Packets.RemoveScore(c.getID()));
						}
					});
				}

				//If in a victory state, count a disconnect as ready so disconnected players don't prevent return to hub.
				if (!StateManager.states.empty() && StateManager.states.peek() instanceof final ResultsState vs) {
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
					if (ps != null) {
						ps.addPacketEffect(() -> {
							User user = usm.getUsers().get(c.getID());
							if (user != null) {
								Player player = user.getPlayer();
								if (player != null) {
									player.onReceiveSync(p, p.timestamp);
								}
							}
						});
					}
				}

				/*
				 * This packet is sent periodically to inform the server of the client's inputs
				 * this includes what buttons the client is holding as well as their mouse position
				 */
				if (o instanceof final Packets.SyncKeyStrokes p) {
					final PlayState ps = getPlayState();
					if (ps != null) {
						ps.addPacketEffect(() -> {
							User user = usm.getUsers().get(c.getID());
							if (user != null) {
								Player player = user.getPlayer();
								if (player != null) {
									if (player.getController() != null) {
										player.getController().syncClientKeyStrokes(p.mouseX, p.mouseY,
												p.playerX, p.playerY, p.actions, p.timestamp);
									}
								}
							}
						});
					}
				}

				/*
				 * These are for different types of SyncedAttack packets that server should echo.
				 * "Extra" indicates the packet contains more information to be used for the attack
				 * Independent refers to attacks that produce a hbox that does not need to have same uuid as client version.
				 */
				if (o instanceof final PacketsAttacks.SingleClientIndependent p) {
					final PlayState ps = getPlayState();
					if (ps != null) {
						ps.addPacketEffect(() -> {
							User user = usm.getUsers().get(c.getID());
							if (user != null) {
								Player player = user.getPlayer();
								if (player != null) {
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
								}
							}
						});
					}
				}

				/*
				 * Like single version except it produces an ordered list of hboxes
				 */
				if (o instanceof final PacketsAttacks.MultiClientIndependent p) {
					final PlayState ps = getPlayState();
					if (ps != null) {
						ps.addPacketEffect(() -> {
							User user = usm.getUsers().get(c.getID());
							if (user != null) {
								Player player = user.getPlayer();
								if (player != null) {
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
								}
							}
						});
					}
				}

				/*
				 * For synced attacks that produce no hitbox, we just run the designated attack
				 */
				if (o instanceof final PacketsAttacks.SyncedAttackNoHbox p) {
					final PlayState ps = getPlayState();
					if (ps != null) {
						ps.addPacketEffect(() -> {
							User user = usm.getUsers().get(c.getID());
							if (user != null) {
								Player player = user.getPlayer();
								if (player != null) {
									if (p instanceof PacketsAttacks.SyncedAttackNoHboxExtra p1) {
										p.attack.initiateSyncedAttackNoHbox(ps, player, p.pos, c.getID(), false, p.independent, p1.extraFields);
									} else {
										p.attack.initiateSyncedAttackNoHbox(ps, player, p.pos, c.getID(), false, p.independent);
									}
								}
							}
						});
					}
				}

				/*
				 * The Client has connected.
				 * Notify clients and create a new player for the client. Also, tell the new client what level to load
				 */
				else if (o instanceof final Packets.PlayerConnect p) {
					final PlayState ps = getPlayState();
					if (ps != null) {
						HashMap<String, Integer> modeSettings = JSONManager.setting.getModeSettings(ps.getMode());
						if (p.firstTime) {
							
							//reject clients with wrong version
							if (!HadalGame.VERSION.equals(p.version)) {
								sendToTCP(c.getID(), new Packets.ConnectReject(UIText.INCOMPATIBLE.text(HadalGame.VERSION)));
								return;
							}

							//if no server password, the client connects.
							if (!"".equals(JSONManager.setting.getServerPassword())) {
								//password being null indicates the client just attempted to connect.
								//otherwise, we check whether the password entered matches
								if (p.password == null) {
									sendToTCP(c.getID(), new Packets.PasswordRequest());
									return;
								} else if (!JSONManager.setting.getServerPassword().equals(p.password)) {
									sendToTCP(c.getID(), new Packets.ConnectReject(UIText.INCORRECT_PASSWORD.text()));
									return;
								}
							}
							addNotificationToAllExcept(ps, c.getID(), "", UIText.CLIENT_CONNECTED.text(p.name), true, DialogType.SYSTEM);

							//clients joining full servers or in the middle of matches join as spectators
							if (usm.getNumPlayers() >= JSONManager.setting.getMaxPlayers() + 1) {
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
							User user = usm.getUsers().get(c.getID());
							if (user != null) {

								//set the client's loadout. Active loadout is only reset for new levels
								user.getLoadoutManager().setSavedLoadout(p.loadout);
								if (ps.isReset()) {
									user.getLoadoutManager().setActiveLoadout(p.loadout);
								}

								//if the player is told to start as a spectator or was a spectator prior to the match, they join as a spectator
								if (user.isSpectator() || spectator) {
									createNewClientSpectator(ps, c.getID(), p.name, p.loadout);
								} else {
									Player player = user.getPlayer();
									if (player != null) {

										//alive check prevents duplicate players if entering/respawning simultaneously
										if (!player.isAlive()) {
											spawnNewUser(ps, user, ps.isReset());
										}
									} else {
										spawnNewUser(ps, user, true);
									}
								}
							} else {
								if (spectator) {
									createNewClientSpectator(ps, c.getID(), p.name, p.loadout);
								} else {
									spawnNewUser(ps, c.getID(), p.name, p.loadout, true);
								}
							}

							//this just updates user's "last primary weapon" which is only used for a single artifact rn
							User userUpdated = usm.getUsers().get(c.getID());
							if (userUpdated != null) {
								userUpdated.getLoadoutManager().setLastEquippedPrimary(p.loadout.multitools[0]);
							}

							//sync client ui elements
							sendToTCP(c.getID(), new Packets.SyncUI(ps.getUiExtra().getMaxTimer(), ps.getUiExtra().getTimer(),
									ps.getUiExtra().getTimerIncr(),	AlignmentFilter.currentTeams, AlignmentFilter.teamScores));
							sendToTCP(c.getID(), new Packets.SyncSharedSettings(JSONManager.sharedSetting));
						});
					}
				}
				
				/*
				 * The Client has loaded the level.
				 * sync the client's loadout and activate the event connected to the start point.
				 */
				else if (o instanceof Packets.ClientPlayerCreated) {
					User user = usm.getUsers().get(c.getID());
					if (user != null) {
						Player player = user.getPlayer();
						if (player != null) {
							if (player.getPlayerData() != null) {
								HadalGame.server.sendToAllTCP(new PacketsLoadout.SyncWholeLoadout(
										c.getID(), user.getLoadoutManager().getActiveLoadout(), false));
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
					if (ps != null) {
						ps.addPacketEffect(() -> {
							User user = usm.getUsers().get(c.getID());
							if (user != null) {
								Player player = user.getPlayer();
								if (player != null && player.getPlayerData() != null) {
                                    switch (p) {
                                        case PacketsLoadout.SyncEquipClient s -> {
                                            player.getEquipHelper().syncEquip(s.equip);
                                            player.getEquipHelper().syncServerEquipChangeEcho(c.getID(), s.equip);
                                        }
                                        case PacketsLoadout.SyncArtifactAddClient s ->
                                                player.getArtifactHelper().addArtifact(s.artifactAdd, false, s.save);
                                        case PacketsLoadout.SyncArtifactRemoveClient s ->
                                                player.getArtifactHelper().removeArtifact(s.artifactRemove, false);
                                        case PacketsLoadout.SyncActiveClient s -> {
                                            player.getMagicHelper().syncMagic(s.active);
                                            player.getMagicHelper().syncServerMagicChangeEcho(c.getID(), s.active);
                                        }
                                        case PacketsLoadout.SyncCharacterClient s -> {
                                            player.getCosmeticsHelper().setCharacter(s.character);
                                            player.getCosmeticsHelper().syncServerCharacterChange(s.character);
                                        }
                                        case PacketsLoadout.SyncTeamClient s -> {
                                            player.getCosmeticsHelper().setTeam(s.team);
                                            player.getCosmeticsHelper().syncServerTeamChange(s.team);
                                        }
                                        case PacketsLoadout.SyncCosmeticClient s -> {
                                            player.getCosmeticsHelper().setCosmetic(s.cosmetic);
                                            player.getCosmeticsHelper().syncServerCosmeticChange(s.cosmetic);
                                        }
										case PacketsLoadout.SyncVendingArtifact s -> {
											Vending.checkUnlock(ps, s.artifact, user);
											user.setScoreUpdated(true);
										}
										case PacketsLoadout.SyncVendingScrapSpend s -> {
											user.getScoreManager().setCurrency(user.getScoreManager().getCurrency() - s.scrap);
											user.setScoreUpdated(true);
										}
                                        default -> {}
                                    }
								}
							}
						});
					}
        		}

				/*
				 * The client has sen an entire loadout to replace with. Occurs from using Outfitter
				 */
				else if (o instanceof final PacketsLoadout.SyncWholeLoadout p) {
					final PlayState ps = getPlayState();
					if (ps != null) {
						ps.addPacketEffect(() -> {
							User user = usm.getUsers().get(c.getID());
							if (user != null) {
								Player player = user.getPlayer();
								if (player != null) {
									if (player.getPlayerData() != null) {
										player.getLoadoutHelper().syncLoadout(p.loadout, false, false);
										player.getLoadoutHelper().syncServerWholeLoadoutChange();
									}
								}
							}
						});
					}
				}

				/*
				 * A Client has unpaused the game
				 * Return to the PlayState and inform everyone who unpaused.
				 */
				else if (o instanceof Packets.Unpaused) {
        			if (!StateManager.states.empty()) {
						User user = usm.getUsers().get(c.getID());
						if (user != null) {
							Player player = user.getPlayer();

							//if pauses are enabled, unpause and remove pause state (and setting state)
							if (player != null && JSONManager.setting.isMultiplayerPause()) {
								if (StateManager.states.peek() instanceof final PauseState ps) {
									addNotificationToAll(ps.getPs(), "", UIText.SERVER_UNPAUSED.text(player.getName()), true, DialogType.SYSTEM);
									ps.setToRemove(true);
								}
								if (StateManager.states.peek() instanceof final SettingState ss) {
									addNotificationToAll(ss.getPlayState(), "", UIText.SERVER_UNPAUSED.text(player.getName()), true, DialogType.SYSTEM);
									ss.setToRemove(true);
								}
								if (StateManager.states.peek() instanceof final AboutState as) {
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
						ps.addPacketEffect(() -> {
							if (JSONManager.setting.isMultiplayerPause()) {
								StateManager.addPauseState(ps, p.pauser, PlayState.class, true);
							}
						});
					}
				}

				/*
				 * Respond to this packet sent from the client periodically so the client knows their latency.
				 */
				else if (o instanceof final Packets.LatencySyn p) {
					final PlayState ps = getPlayState();
					if (ps != null) {
						ps.addPacketEffect(() -> {
							User user = usm.getUsers().get(c.getID());
							if (user != null) {
								if (user.getPing() != p.latency) {
									user.setPing(p.latency);
									user.setScoreUpdated(true);
								}
								server.sendToUDP(c.getID(), new Packets.LatencyAck(ps.getTimer(), p.timestamp));
							}
						});
					}
				}

				/*
				 * The client tried to emote. make them emote, if possible
				 */
				else if (o instanceof final Packets.SyncEmote p) {
					final PlayState ps = getPlayState();
					if (ps != null) {
						ps.addPacketEffect(() -> {
							User user = usm.getUsers().get(c.getID());
							if (user != null) {
								Player player = user.getPlayer();
								if (player != null) {
									ps.getChatWheel().emote(player, p.emoteIndex, c.getID());
								}
							}
						});
					}
				}

				/*
				 * A Client has said they want to enter spectator mode
				 */
				else if (o instanceof Packets.StartSpectate) {
					final PlayState ps = getPlayState();
					if (ps != null) {
						ps.addPacketEffect(() -> {
							User user = usm.getUsers().get(c.getID());
							if (user != null) {
								ps.becomeSpectator(user, true);
							}
						});
					}
				}
				
				/*
				 * A Client has said they want to exit spectator mode
				 */
				else if (o instanceof final Packets.EndSpectate p) {
					final PlayState ps = getPlayState();
					if (ps != null) {
						ps.addPacketEffect(() -> {
							usm.getUsers().get(c.getID()).getLoadoutManager().setActiveLoadout(p.loadout);
							ps.exitSpectator(usm.getUsers().get(c.getID()));
						});
					}
				}
				
				/*
				 * A Client has said they are ready to return to hub state from results state
				 * Ready that player in the results state.
				 */
				else if (o instanceof Packets.ClientReady) {
					if (!StateManager.states.empty()) {
						if (StateManager.states.peek() instanceof final ResultsState vs) {
							Gdx.app.postRunnable(() -> vs.readyPlayer(c.getID()));
						} else if (StateManager.states.peek() instanceof final PlayState ps) {
							if (ps.getMode().equals(GameMode.ARCADE)) {
								SettingArcade.readyUp(ps, c.getID());
							}
						}
					}
				}

				/*
				 * A Client has typed /killme and wants their player to be killed (not disconnected)
				 */
				else if (o instanceof Packets.ClientYeet) {
					final PlayState ps = getPlayState();
					if (null != ps) {
						ps.addPacketEffect(() -> {
							User user = usm.getUsers().get(c.getID());
							if (user != null) {
								Player player = user.getPlayer();
								if (player != null) {
									if (player.getPlayerData() != null) {
										player.getPlayerData().receiveDamage(9999, new Vector2(),
												player.getPlayerData(), false, null, DamageSource.MISC);
									}
								}
							}
						});
					}
				}

				/*
				 * Client has died in their own world. This counts as an kill that should be echoed to other client
				 */
				else if (o instanceof final Packets.DeleteClientSelf p) {
					final PlayState ps = getPlayState();
					if (null != ps) {
						ps.addPacketEffect(() -> {
							User vic = usm.getUsers().get(c.getID());
							if (null != vic) {
								if (null != vic.getPlayer()) {
									HadalEntity perp = ps.findEntity(p.uuidMSB, p.uuidLSB);
									if (perp instanceof Schmuck schmuck) {
										vic.getPlayer().getPlayerData().die(schmuck.getBodyData(), p.source, p.tags);
									} else {
										vic.getPlayer().getPlayerData().die(ps.getWorldDummy().getBodyData(), p.source, p.tags);
									}
								}
							}
						});
					}
				}

				/*
				 * Client has activated an event in their world and the server should echo that activation
				 */
				else if (o instanceof Packets.ActivateEvent p) {
					final PlayState ps = getPlayState();
					if (null != ps) {
						ps.addPacketEffect(() -> {
							User user = usm.getUsers().get(c.getID());
							if (user != null) {
								Player player = user.getPlayer();
								if (player != null) {
									HadalEntity entity = ps.findEntity(p.uuidMSB, p.uuidLSB);
									if (entity != null) {
										if (entity instanceof Event event) {
											event.getEventData().preActivate(null, player);
										}
									}
								}
							}
						});
					}
				}

				/*
				 * Like ActivateEvent, except for an unsynced event that does not have the same UUID.
				 * These events should always have a consistent, non-null triggeredID that we use to echo the activation
				 */
				else if (o instanceof Packets.ActivateEventByTrigger p) {
					final PlayState ps = getPlayState();
					if (null != ps) {
						ps.addPacketEffect(() -> {
							User user = usm.getUsers().get(c.getID());
							if (user != null) {
								Player player = user.getPlayer();
								if (player != null) {
									Event event = TiledObjectUtil.getTriggeredEvents().get(p.triggeredID);
									if (event != null) {
										event.getEventData().preActivate(null, player);
									}
								}
							}
						});
					}
				}

				/*
				 * The client has picked up a weapon from an event and the server should echo that pickup
				 */
				else if (o instanceof Packets.SyncPickup p) {
					final PlayState ps = getPlayState();
					if (null != ps) {
						ps.addPacketEffect(() -> {
							HadalEntity entity = ps.findEntity(p.uuidMSB, p.uuidLSB);
							if (null != entity) {
								if (entity instanceof PickupEquip pickupEquip) {
									pickupEquip.setEquip(UnlocktoItem.getUnlock(p.newPickup, null));
								}
							}
						});
					}
				}

				/*
				 * Like SyncPickup, except for an unsynced pickup (non-drop weapon) that does not have the same UUID
				 * These events should always have a consistent, non-null triggeredID that we use to echo the activation
				 */
				else if (o instanceof Packets.SyncPickupTriggered p) {
					final PlayState ps = getPlayState();
					if (null != ps) {
						ps.addPacketEffect(() -> {
							Event event = TiledObjectUtil.getTriggeredEvents().get(p.triggeredID);
							if (null != event) {
								if (event instanceof PickupEquip pickupEquip) {
									pickupEquip.setEquip(UnlocktoItem.getUnlock(p.newPickup, null));
									pickupEquip.setEquipChanged(true);
								}
							}
						});
					}
				}

				/*
				 * For events whose positions must be synced at the start of the level, the client will send this packet
				 * to let the server know that they are ready to perform the adjustment.
				 */
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
			server.bind(JSONManager.setting.getPortNumber(), JSONManager.setting.getPortNumber());
		} catch (IOException e) {
			if (StateManager.states.peek() instanceof LobbyState lobby) {
				lobby.setNotification(UIText.PORT_FAIL.text(Integer.toString(JSONManager.setting.getPortNumber())));
			}
		}	
		registerPackets();
		server.start();
	}

	/**
	 * This is run when a player is created for a connID. We want to check their existing User or create a new one
	 * We also set the user's loadout here
	 */
	public User checkNewUser(int connID, String name, Loadout loadout) {
		User user;
		if (usm.getUsers().containsKey(connID)) {
			user = usm.getUsers().get(connID);
		} else {
			user = new User(connID, name, loadout);
			usm.getUsers().put(connID, user);
			user.setTeamFilter(loadout.team);
		}
		user.getLoadoutManager().setActiveLoadout(loadout);
		return user;
	}

	/**
	 * A new player is spawned for a connID.
	 * First we check the user, then spawn a new player for them using transition manager
	 */
	public void spawnNewUser(PlayState ps, int connID, String name, Loadout loadout, boolean reset) {
		User user = checkNewUser(connID, name, loadout);
		spawnNewUser(ps, user, reset);
	}

	public void spawnNewUser(PlayState ps, User user, boolean reset) {
		user.getTransitionManager().levelStartSpawn(ps, reset);
	}

	/**
	 * Create a new spectator for a client.
	 * First we check the user, then make them transition to spectator.
	 */
	public void createNewClientSpectator(PlayState ps, int connID, String name, Loadout loadout) {
		User user = checkNewUser(connID, name, loadout);
		ps.addPacketEffect(() -> ps.startSpectator(user));
	}

	/**
	 * This is called whenever the server creates a new player for a client
	 * @param ps: This is the server's current play state
	 * @param data: The player data of the new player.
	 * @param reset: Do we want to reset the new player's hp/fuel/ammo etc?
	 * @param startPoint: The start point to spawn the new client player at
	 */
	public void createNewClientPlayer(final PlayState ps, final User user, final PlayerBodyData data, final boolean reset, final Event startPoint) {

		ps.addPacketEffect(() -> {

			Event newSave = null;
			if (startPoint != null) {
				newSave = startPoint;
			}

			//Create a new player with the designated fields.
			ps.createPlayer(newSave, user.getStringManager().getName(), user.getLoadoutManager().getActiveLoadout(),
					data, user, reset, false, user.getHitboxFilter().getFilter());

			user.setSpectator(false);
		});
	}
	
	/**
	 * This gets the server's playstate. This allows the server to make changes to a playstate underneath a pausestate.
	 * @return server's playstate and null if there isn't one
	 */
	public PlayState getPlayState() {
		if (!StateManager.states.empty()) {
			GameState currentState = StateManager.states.peek();
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

	public void dispose() throws IOException {
		if (server != null) {
			server.stop();
			server.dispose();
			server = null;
		}
	}

	public Server getServer() {	return server; }

	public void setServerName(String serverName) { this.serverName = serverName; }
}
