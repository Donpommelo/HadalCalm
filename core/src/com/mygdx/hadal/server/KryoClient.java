package com.mygdx.hadal.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.serialization.KryoSerialization;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.PickupEquip;
import com.mygdx.hadal.event.modes.ArcadeMarquis;
import com.mygdx.hadal.event.modes.CrownHoldable;
import com.mygdx.hadal.event.modes.FlagCapturable;
import com.mygdx.hadal.event.modes.ReviveGravestone;
import com.mygdx.hadal.managers.*;
import com.mygdx.hadal.managers.TransitionManager.TransitionState;
import com.mygdx.hadal.map.SettingArcade;
import com.mygdx.hadal.map.SettingSave;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.SoundCreate;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.entities.*;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.packets.PacketsAttacks;
import com.mygdx.hadal.server.packets.PacketsLoadout;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.server.util.PacketManager;
import com.mygdx.hadal.server.util.SocketManager;
import com.mygdx.hadal.states.*;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.users.ScoreManager;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.users.User.UserDto;
import com.mygdx.hadal.users.UserManager;
import com.mygdx.hadal.utils.TiledObjectUtil;
import com.mygdx.hadal.utils.UnlocktoItem;

import java.io.IOException;

import static com.mygdx.hadal.constants.Constants.PPM;

/**
 * This is the client of the game
 * @author Ghulliam Grapplejack
 */
public class KryoClient {
	
	//Me Client
	private Client client;

	private final HadalGame app;

	public final UserManager usm;

    public Listener packetListener;

	private String instanceID = "";
    
    public KryoClient(HadalGame app, UserManager userManager) {
		this.app = app;
		this.usm = userManager;
    }
    
    /**
	 * This is called upon starting a new client. initialize client and whatever
	 */
	public void init() {
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
        this.client = new Client(65536, 32768, serialization);
		client.start();

		usm.resetUsers();

		PacketManager.clientPackets(client);

        packetListener = new Listener() {
        	
        	/**
        	 * Upon connecting to server, send a playerConnect packet with your name and version.
        	 */
        	@Override
        	public void connected(Connection c) {
				PacketManager.clientTCP(new Packets.PlayerConnect(true, JSONManager.loadout.getName(), HadalGame.VERSION, null));
                usm.setConnID(c.getID());
				usm.addUser(new User(c.getID(), JSONManager.loadout.getName(), new Loadout(JSONManager.loadout)));
			}
        	
        	/**
        	 * Upon disconnecting to server, return to title state
        	 */
        	@Override
        	public void disconnected(Connection c) {
        		final ClientState cs = getClientState();
				
				//return to the lobby state. (if our client state is still there, we can do a fade out transition first.
        		Gdx.app.postRunnable(() -> {

					if (cs != null) {

						//If our client state is still here, the server closed
						addNotification(cs, "", UIText.DISCONNECTED.text(), false, DialogType.SYSTEM);
						cs.getTransitionManager().returnToTitle(1.0f);
					} else {
						StateManager.removeState(ResultsState.class);
						StateManager.removeState(SettingState.class, false);
						StateManager.removeState(AboutState.class, false);
						StateManager.removeState(PauseState.class, false);
						StateManager.removeState(ClientState.class);
					}
				});
            }
        	
        	/**
        	 * Note that the order of these if/else is according to approximate frequency of packets.
        	 * This might have a (very minor) effect on performance or something idk
        	 */
        	@Override
        	public void received(Connection c, final Object o) {
				//first check for sync packets, then create/delete ones
				if (!HadalGame.client.receiveSyncPacket(o)) {
					if (!HadalGame.client.receiveAddRemovePacket(o)) {
						HadalGame.client.receiveOtherPacket(o);
					}
				}
			}
        };

//		client.addListener(new Listener.LagListener(150, 150, packetListener));
       client.addListener(packetListener);
	}

	/**
	 * this processes all the packets that do not fall into the other categories (add/remove/sync)
	 */
	public void receiveOtherPacket(Object o) {

		/*
		 * A sound is played on the server side that we should echo
		 */
		if (o instanceof final Packets.SyncSoundSingle p) {
			final ClientState cs = getClientState();

			if (cs != null) {
				cs.addPacketEffect(() -> {
					if (p.worldPos != null) {
						SoundManager.play(cs, new SoundLoad(p.sound)
								.setVolume(p.volume)
								.setPitch(p.pitch)
								.setPosition(p.worldPos));
					} else {
						SoundManager.play(new SoundLoad(p.sound)
								.setVolume(p.volume)
								.setPitch(p.pitch)
								.setSingleton(p.singleton));
					}
				});
			}
		}

		/*
		 * The Server tells us the new score after scores change.
		 * Update our scores to the ones specified
		 */
		else if (o instanceof final Packets.SyncScore p) {

			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					ScoreManager score;

					//update score or create a new user if existing score not found
					if (usm.getUsers().containsKey(p.connID)) {
						User user = usm.getUsers().get(p.connID);
						score = user.getScoreManager();
						user.setSpectator(p.spectator);
						user.setPing(p.ping);
						user.setScoreUpdated(true);
					} else {
						score = new ScoreManager();
						User user = new User(p.connID, p.name, p.loadout);
						user.setSpectator(p.spectator);
						user.setPing(p.ping);
						usm.addUser(user);
					}
					score.setWins(p.wins);
					score.setKills(p.kills);
					score.setDeaths(p.deaths);
					score.setAssists(p.assists);
					score.setLives(p.lives);
					score.setScore(p.score);
					score.setExtraModeScore(p.extraModeScore);

					//refresh hub if server validating vending purchase updates currency
					boolean currencyChange = score.getCurrency() != p.currency;
					score.setCurrency(p.currency);

					if (p.connID == usm.getConnID() && currencyChange) {
						cs.getUIManager().getUiHub().refreshHub(null);
					}
				});
			}
		}

		/*
		 * The server has activated an event.
		 * If we have a copy of that event in our world, we want to activate it as well.
		 */
		else if (o instanceof final Packets.ActivateEvent p) {
			final ClientState cs = getClientState();

			if (cs != null) {
				cs.addPacketEffect(() -> {
					HadalEntity entity = cs.findEntity(p.entityID);
					if (entity != null) {
						if (entity instanceof Event event) {
							if (usm.getUsers().containsKey(p.connID)) {
								User user = usm.getUsers().get(p.connID);
								Player player = user.getPlayer();
								event.getEventData().onActivate(null, player);
							} else {
								event.getEventData().onActivate(null, null);
							}
						}
					}
				});
			}
		}

		/*
		 * This version of event activation uses a trigger id instead of a uuid.
		 */
		else if (o instanceof final Packets.ActivateEventByTrigger p) {
			final ClientState cs = getClientState();

			if (cs != null) {
				cs.addPacketEffect(() -> {
					Event event = TiledObjectUtil.getTriggeredEvents().get(p.triggeredID);
					if (event != null) {
						if (usm.getUsers().containsKey(p.connID)) {
							User user = usm.getUsers().get(p.connID);
							Player player = user.getPlayer();
							event.getEventData().onActivate(null, player);
						} else {
							event.getEventData().onActivate(null, null);
						}
					}
				});
			}
		}

		/*
		 * A PickupEquip event has changed on the server side
		 */
		else if (o instanceof Packets.SyncPickup p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					HadalEntity entity = cs.findEntity(p.entityID);
					if (entity != null) {
						if (entity instanceof PickupEquip pickupEquip) {
							pickupEquip.setEquip(UnlocktoItem.getUnlock(p.newPickup, null));
						}
					}
				});
			}
		}

		/*
		 * This version of pickup activation uses a trigger id instead of a uuid.
		 */
		else if (o instanceof Packets.SyncPickupTriggered p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					Event event = TiledObjectUtil.getTriggeredEvents().get(p.triggeredID);
					if (event != null) {
						if (event instanceof PickupEquip pickupEquip) {
							pickupEquip.setEquip(UnlocktoItem.getUnlock(p.newPickup, null));
						}
					}
				});
			}
		}

		/*
		 * Server responds to our latency checking packet. record the time and update our latency.
		 */
		else if (o instanceof final Packets.LatencyAck p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> cs.syncLatency(p.serverTimestamp, p.clientTimestamp));
			}
		}

		/*
		 * Server tells us a player has disconnected so we must remove them from user list.
		 */
		else if (o instanceof final Packets.RemoveScore p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> usm.removeUser(p.connID));
			}
		}

		/*
		 * Server rejects our connection. Display msg on lobby screen.
		 */
		else if (o instanceof final Packets.ConnectReject p) {
			if (!StateManager.states.isEmpty()) {
				if (StateManager.states.peek() instanceof LobbyState lobby) {
					lobby.setNotification(p.msg);
					lobby.setInputDisabled(false);
				}
			}
			client.stop();
		}

		/*
		 * Server requests a password. Display password field on lobby screen.
		 */
		else if (o instanceof Packets.PasswordRequest) {
			if (!StateManager.states.isEmpty()) {
				if (StateManager.states.peek() instanceof LobbyState lobby) {
					lobby.openPasswordRequest();
				}
			}
		}

		/*
		 * The Server tells us to load the level.
		 * Load the level and tell the server we finished.
		 */
		else if (o instanceof final Packets.LoadLevel p) {
			final ClientState cs = getClientState();

			//we must set the playstate's next state so that other transitions (respawns) do not override this transition
			if (cs != null) {
				cs.getTransitionManager().setNextState(TransitionState.NEWLEVEL);
			}
			Gdx.app.postRunnable(() -> {
				FadeManager.fadeOut();
				FadeManager.setRunAfterTransition(() -> {
					StateManager.removeState(ResultsState.class, false);
					StateManager.removeState(SettingState.class, false);
					StateManager.removeState(AboutState.class, false);
					StateManager.removeState(PauseState.class, false);

					boolean spectator = cs != null && cs.getSpectatorManager().isSpectatorMode();
					StateManager.removeState(ClientState.class, false);

					//set mode settings according to what the server sends
					if (p.modeSettings != null) {
						for (String key : p.modeSettings.keySet()) {
							JSONManager.setting.setModeSetting(p.mode, SettingSave.getByName(key), p.modeSettings.get(key));
						}
					}

					StateManager.addClientPlayState(app, p.level, p.mode, LobbyState.class);
					PacketManager.clientTCP(new Packets.ClientLoaded(p.firstTime, spectator, p.spectator,
							JSONManager.loadout.getName(), new Loadout(JSONManager.loadout)));
				});
			});
		}

		/*
		 * The Server has finished loading its play state.
		 * Ask the server to let us connect
		 */
		else if (o instanceof Packets.ServerLoaded) {
			Packets.PlayerConnect connected = new Packets.PlayerConnect(false, JSONManager.loadout.getName(), HadalGame.VERSION, null);
			PacketManager.clientTCP(connected);
		}

		/*
		 * The Server tells us to transition to a new state
		 * Begin transitioning to the specified state.
		 */
		else if (o instanceof final Packets.ClientStartTransition p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
						cs.getTransitionManager().beginTransition(p.state, p.fadeSpeed, p.fadeDelay, p.skipFade);
						if (p.startPosition != null) {
							cs.getCameraManager().setCameraPosition(p.startPosition);
							cs.getCameraManager().setCameraTarget(p.startPosition);
							cs.getCameraManager().getCameraFocusAimVector().setZero();
						}
					}
				);
			}
		}

		/*
		 * The game has been paused. (By server or any client)
		 * We go to the pause state.
		 */
		else if (o instanceof final Packets.Paused p) {
			if (!StateManager.states.empty() && StateManager.states.peek() instanceof final ClientState cs) {
				cs.addPacketEffect(() -> StateManager.addPauseState(cs, p.pauser, ClientState.class, true));
			}
		}

		/*
		 * The game has been unpaused. (By server or any client)
		 * We return to the ClientState
		 */
		else if (o instanceof Packets.Unpaused) {
			if (!StateManager.states.empty()) {
				if (StateManager.states.peek() instanceof final PauseState ps) {
					ps.setToRemove(true);
				}
				if (StateManager.states.peek() instanceof final SettingState ss) {
					ss.setToRemove(true);
				}
				if (StateManager.states.peek() instanceof final AboutState as) {
					as.setToRemove(true);
				}
			}
		}

		/*
		 * Server sends a kill feed notification to the client. Display it
		 */
		else if (o instanceof final Packets.SyncNotification p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> cs.getUIManager().getKillFeed().addNotification(p.message, false));
			}
		}

		/*
		 * Server sends a chat message to the client
		 */
		else if (o instanceof final Packets.ServerChat p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> cs.getUIManager().getMessageWindow().addText(p.text, p.type, p.connID));
			}
		}

		/*
		 * We have received a notification from the server.
		 * Display the notification
		 */
		else if (o instanceof final Packets.ServerNotification p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> addNotification(cs, p.name, p.text, p.override, p.type));
			}
		}

		/*
		 * The Server tells us the new settings after settings change.
		 * Update our settings to the ones specified
		 */
		else if (o instanceof final Packets.SyncSharedSettings p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					JSONManager.hostSetting = p.settings;
					cs.getUIManager().getScoreWindow().syncSettingTable();
				});
			}
		}

		/*
		 * The Server informs us that a player is ready in the results screen.
		 * Update that player's readiness in the results screen or arcade break room
		 */
		else if (o instanceof final Packets.ClientReady p) {
			if (!StateManager.states.empty()) {
				if (StateManager.states.peek() instanceof final ResultsState vs) {
					vs.getPs().addPacketEffect(() -> vs.readyPlayer(p.playerID));
				} else if (StateManager.states.peek() instanceof final PlayState ps) {
					ps.addPacketEffect(() -> SettingArcade.readyUp(ps, p.playerID));
				}
			}
		}

		/*
		 * Headless server has readied up in the results state and the client host decides the next map
		 */
		else if (o instanceof Packets.ServerNextMapRequest) {
			if (!StateManager.states.empty()) {
				if (StateManager.states.peek() instanceof final ResultsState vs) {
					Gdx.app.postRunnable(() -> PacketManager.clientTCP(
							new Packets.ClientNextMapResponse(vs.isReturnToHub(), vs.getNextMap())));
				} else {
					Gdx.app.postRunnable(() -> PacketManager.clientTCP(
							new Packets.ClientNextMapResponse(true, UnlockLevel.HUB_MULTI)));
				}
			}
		}

		/*
		 * The Server tells us that a player has received a new loadout after spawning.
		 * Change their loadout on the client side
		 */
		else if (o instanceof final PacketsLoadout.SyncWholeLoadout p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					User user = usm.getUsers().get(p.connID);
					if (user != null) {
						Player player = user.getPlayer();
						if (player != null) {
							if (player.getPlayerData() != null) {
								player.getLoadoutHelper().syncLoadout(p.loadout, true, p.save);
							}
						}
					}
				});
			}
		}

		/*
		 * The Server tells us that a player changed one part of their loadout.
		 * Change their loadout on the client side
		 */
		else if (o instanceof final PacketsLoadout.SyncLoadoutServer p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					User user = usm.getUsers().get(p.connID);
					if (user != null) {
						Player player = user.getPlayer();
						if (player != null) {
							if (player.getPlayerData() != null) {
                                switch (p) {
                                    case PacketsLoadout.SyncEquipServer s -> player.getEquipHelper().syncEquip(s.equip);
                                    case PacketsLoadout.SyncArtifactServer s -> {
                                        player.getArtifactHelper().syncArtifact(s.artifact, true, s.save);
										if (user.equals(usm.getOwnUser())) {
											cs.getUIManager().getUiHub().refreshHub(null);
											cs.getUIManager().getUiHub().refreshHubOptions();
										}
									}
                                    case PacketsLoadout.SyncActiveServer s ->
                                            player.getMagicHelper().syncMagic(s.active);
                                    case PacketsLoadout.SyncCharacterServer s ->
                                            player.getCosmeticsHelper().setCharacter(s.character);
                                    case PacketsLoadout.SyncTeamServer s -> player.getCosmeticsHelper().setTeam(s.team);
                                    case PacketsLoadout.SyncCosmeticServer s ->
                                            player.getCosmeticsHelper().setCosmetic(s.cosmetic);
                                    default -> {}
                                }
							}
						}
					}
				});
			}
		}

		/*
		 * When a client player is spawned, we are told some ui elements like the current time and team setup
		 */
		else if (o instanceof final Packets.SyncUI p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					AlignmentFilter.currentTeams = p.teams;
					AlignmentFilter.teamScores = p.scores;
					cs.getTimerManager().setMaxTimer(p.maxTimer);
					cs.getTimerManager().setTimer(p.timer);
					cs.getTimerManager().setTimerIncr(p.timerIncr);
					usm.setHostID(p.hostID);
				});
			}
		}

		/*
		 * When the server wants us to track a non-event entity, it sends us the id of the target
		 * (events can use a global objective setter event)
		 */
		else if (o instanceof final Packets.SyncObjectiveMarker p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> cs.getUIManager().getUiObjective()
						.addObjectiveClient(p.entityID, p.icon, p.color, p.displayOffScreen,
								p.displayOnScreen, p.displayClearCircle));
			}
		}

		/*
		 * Server has loaded arcade break room and sent next mode choices
		 */
		else if (o instanceof final Packets.SyncArcadeModeChoices p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> ArcadeMarquis.updateClientChoices(p.modeChoices, p.mapChoices));
			}
		}

		/*
		 * We are told by the server each player's extra score info.
		 * Set it so we can display in the results state.
		 */
		else if (o instanceof final Packets.SyncExtraResultsInfo p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {

					cs.getEndgameManager().setResultsText(p.resultsText);

					//temporarily store user info so we can attach old player to updated user
					ObjectMap<Integer, User> usersTemp = new ObjectMap<>(usm.getUsers());
					usm.getUsers().clear();

					for (int i = 0; i < p.users.length; i++) {
						UserDto user = p.users[i];
						User updatedUser;
						if (user != null) {
							updatedUser = new User(user.connID, user.name, user.loadout);
							updatedUser.setScoreManager(user.scores);
							updatedUser.setStatsManager(user.stats);
							updatedUser.setSpectator(user.spectator);
							updatedUser.setTeamFilter(user.loadout.team);

							User userOld = usersTemp.get(user.connID);
							if (userOld != null) {
								updatedUser.setPlayer(userOld.getPlayer());
							}
							usm.addUser(updatedUser);
						}
					}
				});
			}
		}

		/*
		 * Server has kicked client. Get yeeted.
		 */
		else if (o instanceof Packets.ClientYeet) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.getTransitionManager().returnToTitle(0.0f);
			}
		}

		/*
		 * Headless server host has changed; update host indicator
		 */
		else if (o instanceof Packets.ServerNewHost p) {
			HadalGame.usm.setHostID(p.hostID);
		}

		else if (o instanceof Packets.HeadlessHostRequest) {
			Gdx.app.postRunnable(() -> PacketManager.clientTCP(
					new Packets.SyncInitialHeadlessSettings(JSONManager.sharedSetting, SocketManager.getServerName())));
		}
	}

	/**
	 * this processes all the packets that add or remove entities from the world.
	 * returns true if any packet is processed. (if the input o is an add or remove packet)
	 */
	public boolean receiveAddRemovePacket(Object o) {
		
		/*
		 * The Server tells us to create a new entity.
		 * Create a Client Illusion with the specified dimensions
		 */
		if (o instanceof final Packets.CreateEntity p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					ClientIllusion illusion = new ClientIllusion(cs, p.pos, p.size, p.angle, p.sprite, p.align);
					illusion.serverPos.set(p.pos).scl(1 / PPM);
					illusion.serverAngle.setAngleRad(p.angle);
					illusion.copyServerInstantly = p.instant;
					cs.addEntity(p.entityID, illusion, p.synced, p.layer);
				});
			}
			return true;
		}

		/*
		 * Server tells us to execute a synced attack. Do so with the parameters provided
		 */
		if (o instanceof final PacketsAttacks.SingleServerIndependent p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					HadalEntity creator = cs.findEntity(p.creatorID);
					if (creator == null) {
						creator = cs.getWorldDummy();
					}
					if (creator instanceof Schmuck schmuck) {
						Hitbox hbox;
						if (p instanceof PacketsAttacks.SingleServerDependent p1) {
							if (p instanceof PacketsAttacks.SingleServerDependentExtra p2) {
								hbox = p.attack.initiateSyncedAttackSingle(cs, schmuck, p.pos, p.velo, 0, 0, false, p2.extraFields);
							} else {
								hbox = p.attack.initiateSyncedAttackSingle(cs, schmuck, p.pos, p.velo, 0, 0, false);
							}
							hbox.serverPos.set(p.pos).scl(1 / PPM);
							hbox.serverVelo.set(p.velo);
							cs.addEntity(p1.entityID, hbox, true, ObjectLayer.HBOX);
						} else {
							if (p instanceof PacketsAttacks.SingleServerIndependentExtra p2) {
								hbox = p.attack.initiateSyncedAttackSingle(cs, schmuck, p.pos, p.velo, 0, 0, false, p2.extraFields);
							} else {
								hbox = p.attack.initiateSyncedAttackSingle(cs, schmuck, p.pos, p.velo, 0, 0, false);
							}
							hbox.serverPos.set(p.pos).scl(1 / PPM);
							hbox.serverVelo.set(p.velo);
							cs.addEntity(hbox.getEntityID(), hbox, false, ObjectLayer.HBOX);
						}
					}
				});
			}
			return true;
		}

		/*
		 * Server tells us to execute a synced attack that creates multiple hitboxes. Do so with the parameters provided
		 */
		if (o instanceof final PacketsAttacks.MultiServerIndependent p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					HadalEntity creator = cs.findEntity(p.creatorID);
					if (creator == null) {
						creator = cs.getWorldDummy();
					}
					if (creator instanceof Schmuck schmuck) {
						Hitbox[] hboxes;

						if (p instanceof PacketsAttacks.MultiServerDependent p1) {
							if (p instanceof PacketsAttacks.MultiServerDependentExtra p2) {
								hboxes = p.attack.initiateSyncedAttackMulti(cs, schmuck, p.weaponVelo, p.pos, p.velo,
										0, null, false, p2.extraFields);
							} else {
								hboxes = p.attack.initiateSyncedAttackMulti(cs, schmuck, p.weaponVelo, p.pos, p.velo,
										0, null, false);
							}
							for (int i = 0; i < hboxes.length; i++) {
								if (p.pos.length > i) {
									hboxes[i].serverPos.set(p.pos[i]).scl(1 / PPM);
								}
								if (p.velo.length > i) {
									hboxes[i].serverVelo.set(p.velo[i]);
								}
								if (p1.entityID.length > i) {
									cs.addEntity(p1.entityID[i], hboxes[i], true, ObjectLayer.HBOX);
								}
							}
						} else {
							if (p instanceof PacketsAttacks.MultiServerIndependentExtra p1) {
								hboxes = p.attack.initiateSyncedAttackMulti(cs, schmuck, p.weaponVelo, p.pos, p.velo,
										0, null, false, p1.extraFields);
							} else {
								hboxes = p.attack.initiateSyncedAttackMulti(cs, schmuck, p.weaponVelo, p.pos, p.velo,
										0, null, false);
							}
							for (int i = 0; i < hboxes.length; i++) {
								if (p.pos.length > i) {
									hboxes[i].serverPos.set(p.pos[i]).scl(1 / PPM);
								}
								if (p.velo.length > i) {
									hboxes[i].serverVelo.set(p.velo[i]);
								}
								cs.addEntity(hboxes[i].getEntityID(), hboxes[i], false, ObjectLayer.HBOX);
							}
						}
					}
				});
			}
			return true;
		}

		/*
		 * Server tells us to execute a synced attack that creates no hitbox. Do so with the parameters provided
		 */
		if (o instanceof final PacketsAttacks.SyncedAttackNoHboxServer p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					HadalEntity creator = cs.findEntity(p.creatorID);
					if (creator == null) {
						creator = cs.getWorldDummy();
					}
					if (creator instanceof Schmuck schmuck) {
						if (p instanceof PacketsAttacks.SyncedAttackNoHboxExtraServer p1) {
							p.attack.initiateSyncedAttackNoHbox(cs, schmuck, p.pos, 0, false, true, p1.extraFields);
						} else {
							p.attack.initiateSyncedAttackNoHbox(cs, schmuck, p.pos, 0, false, true);
						}
					}
				});
			}
			return true;
		}

		/*
		 * The Server tells us to delete an entity and we delete it
		 * Delete packets go into the sync packets list. This is so they are carried out according to their timestamp to avoid deleting stuff too early.
		 */
		else if (o instanceof final Packets.DeleteEntity p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> cs.syncEntity(p.entityID, p, p.timestamp));
			}
			return true;
		}

		/*
		 * The Server tells us to create a particle entity.
		 * Create the designated particles and set its attachedId so that it will connect once it is created.
		 */
		else if (o instanceof final Packets.CreateParticles p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					ParticleEntity entity;
					ParticleCreate request;
					if (p.attached) {
						request = new ParticleCreate(p.particle, (HadalEntity) null);
					} else {
						request = new ParticleCreate(p.particle, p.pos);
					}
					request.setLifespan(p.lifespan).setStartOn(p.startOn);
					entity = EffectEntityManager.getParticle(cs, request);

					if (p.attached) {
						entity.setAttachedId(p.attachedID);
						entity.setOffset(p.pos.x, p.pos.y);
					}

					entity.setScale(p.scale);
					entity.setRotate(p.rotate);
					if (p.velocity != 0) {
						entity.setParticleVelocity(p.velocity);
					}
					if (!p.color.isZero()) {
						entity.setColor(p.color);
					}
				});
			}
			return true;
		}

		/*
		 * Create flag event for capture the flag mode
		 */
		else if (o instanceof final Packets.CreateFlag p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					FlagCapturable entity = new FlagCapturable(cs, p.pos, null, p.teamIndex);
					cs.addEntity(p.entityID, entity, true, ObjectLayer.HBOX);
				});
			}
			return true;
		}

		/*
		 * Create crown event for kingmaker mode
		 */
		else if (o instanceof final Packets.CreateCrown p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					CrownHoldable entity = new CrownHoldable(cs, p.pos);
					cs.addEntity(p.entityID, entity, true, ObjectLayer.HBOX);
				});
			}
			return true;
		}

		/*
		 * Create grave event for pve or resurrectionist mode
		 */
		else if (o instanceof final Packets.CreateGrave p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					User user = usm.getUsers().get(p.connID);
					if (user != null) {
						ReviveGravestone grave = new ReviveGravestone(cs, p.pos, user, p.returnMaxTimer, null);
						cs.addEntity(p.entityID, grave, true, ObjectLayer.HBOX);
					}
				});
			}
			return true;
		}

		/*
		 * Server tells us to create a SoundEntity to play a sound
		 * Create entity and set its attachedId so that it will connect once it is created.
		 */
		else if (o instanceof final Packets.CreateSound p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					SoundEntity entity = EffectEntityManager.getSound(cs, new SoundCreate(p.sound, null)
							.setLifespan(p.lifespan)
							.setVolume(p.volume)
							.setPitch(p.pitch)
							.setLooped(p.looped)
							.setStartOn(p.on));
					entity.setAttachedID(p.attachedID);
				});
			}
			return true;
		}
		
		/*
		 * The Server tells us to create a new enemy entity
		 * Create the enemy based on server specifications
		 */
		else if (o instanceof final Packets.CreateEnemy p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					Enemy enemy = p.type.generateEnemy(cs, p.pos, p.hitboxFilter, 0);
					enemy.serverPos.set(p.pos).scl(1 / PPM);
					cs.addEntity(p.entityID, enemy, true, ObjectLayer.STANDARD);
					enemy.setBoss(p.boss);
					if (p.boss) {
						enemy.setName(p.name);
						cs.getUIManager().setBoss(enemy);
					}
				});
			}
			return true;
		}
		
		/*
		 * The server tells us to create a new player
		 * Create the player and set its position fields.
		 * If it is ourselves, we set the camera to face it and update the state's player field
		 */
		else if (o instanceof final Packets.CreatePlayer p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {

					//Add new user if it doesn't exist
					User user;
					if (usm.getUsers().containsKey(p.connID)) {
						user = usm.getUsers().get(p.connID);
					} else {
						user = new User(p.connID, p.name, p.loadout);
						usm.addUser(user);
					}
					user.setTeamFilter(p.loadout.team);

					Event event = null;
					if (p.startTriggeredId != null) {
						event = TiledObjectUtil.getTriggeredEvents().get(p.startTriggeredId);
					}

					//if override is on, set hitbox filter to user's filter
					//(needed since user filters are not consistent between server/client)
					short hitboxFilterOverride = p.pvpOverride ? user.getHitboxFilter().getFilter() : p.hitboxFilter;

					Player newPlayer = cs.getSpawnManager().createPlayer(event, p.name, p.loadout, null, user,
							true, p.connID == usm.getConnID(), hitboxFilterOverride);

					newPlayer.serverPos.set(p.startPosition).scl(1 / PPM);
					newPlayer.setStartPos(p.startPosition);
					newPlayer.setHitboxFilter(hitboxFilterOverride);
					newPlayer.changeScaleModifier(p.scaleModifier);
					cs.addEntity(p.entityID, newPlayer, true, ObjectLayer.STANDARD);

					if (p.connID == usm.getConnID()) {
						usm.setOwnPlayer(newPlayer);

						if (!p.dontMoveCamera) {
							//set camera to look at new client player.
							cs.getCameraManager().setCameraTarget(null);
							cs.getCameraManager().setCameraPosition(p.startPosition);
							cs.getCameraManager().getCameraFocusAimVector().setZero();
						}
					}
				});
			}
			return true;
		}
		
		/*
		 * The Server tells us to create a new event
		 * We create the event using its provided map object blueprint
		 */
		else if (o instanceof final Packets.CreateEvent p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					EventDto dto = p.blueprint;
					MapObject blueprint = new RectangleMapObject(dto.getX(), dto.getY(), dto.getWidth(), dto.getHeight());
					blueprint.setName(dto.getName());
					for (EventDto.Pair pair : dto.getProperties()) {
						blueprint.getProperties().put(pair.getKey(), pair.getValue());
					}

					Event e = TiledObjectUtil.parseSingleEventWithTriggersWithUUID(cs, blueprint, p.entityID, p.synced);
					e.serverPos.set(e.getStartPos()).scl(1 / PPM);
				});
			}
			return true;
		}
		
		/*
		 * Event Creation for specific Pickup event.
		 */
		else if (o instanceof final Packets.CreatePickup p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					PickupEquip pickup;
					if (p.lifespan != 0.0f) {
						pickup = new PickupEquip(cs, p.pos, p.newPickup, p.lifespan);
					} else {
						pickup = new PickupEquip(cs, p.pos, "");
						pickup.setEquip(UnlocktoItem.getUnlock(p.newPickup,null));
					}
					pickup.serverPos.set(pickup.getStartPos()).scl(1 / PPM);

					cs.addEntity(p.entityID, pickup, true, ObjectLayer.STANDARD);
				});
			}
			return true;
		}

		/*
		 * Create packet for events that need their current position on the server for mid-game joining
		 */
		else if (o instanceof final Packets.CreateStartSyncedEvent p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					Event event = TiledObjectUtil.getTriggeredEvents().get(p.triggeredID);
					if (event != null && p.targetTriggeredID != null) {
						HadalEntity target = TiledObjectUtil.getTriggeredEvents().get(p.targetTriggeredID);
						if (target instanceof Event targetEvent) {
							event.onClientSyncInitial(p.timer, targetEvent, p.pos, p.velo);
						}
					}
				});
			}
		}
		
		//if none of the packets match, return false to indicate the packet was not an add/create packet
		return false;
	}
	
	/**
	 * this processes all the packets that sync entities in the world.
	 * returns true if any packet is processed. (if the input o is a sync packet)
	 */
	public boolean receiveSyncPacket(Object o) {

		if (o instanceof final PacketsSync.SyncPlayerSnapshot p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					User user = usm.getUsers().get((int) p.connID);
					if (user != null) {
						Player player = user.getPlayer();
						if (player != null) {
							player.onReceiveSync(p, p.timestamp);
						}
					}
				});
			}
		}

		if (o instanceof PacketsSync.SyncEntity p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> cs.syncEntity(p.entityID, p, p.timestamp));
			}
			return true;
		}
		
		//if none of the packets match, return false to indicate the packet was not a sync packet
		return false;
	}
	
	/**
	 * Similar to getPlayState for server. This returns the ClientState, even if it is underneath a pause or setting state.
	 * @return The current clientstate
	 */
	public ClientState getClientState() {
		if (!StateManager.states.empty()) {
			GameState currentState = StateManager.states.peek();
			if (currentState instanceof ClientState clientState) {
				return clientState;
			} else if (currentState instanceof PauseState pauseState) {
				return (ClientState) (pauseState.getPs());
			} else if (currentState instanceof SettingState settingState) {
				return (ClientState) (settingState.getPlayState());
			} else if (currentState instanceof AboutState aboutState) {
				return (ClientState) (aboutState.getPlayState());
			} else if (currentState instanceof ResultsState resultsState) {
				return (ClientState) (resultsState.getPs());
			}
		}
		return null;
	}
    
	/**
	 * This adds a notification to the client's dialog box
	 * @param cs: Client's current clientstate
	 * @param name: name giving the notification
	 * @param text: notification text
	 * @param override: can this notification be overridden by other notifications
	 * @param type: the type of dialog (system message, story dialog, etc)
	 */
	public void addNotification(ClientState cs, String name, String text, boolean override, DialogType type) {
		cs.getUIManager().getDialogBox().addDialogue(name, text, "", true, override, true, 3.0f, null, null, type);
	}

	public void dispose() throws IOException {
		if (client != null) {
			client.stop();
			client.dispose();
			client = null;
		}
	}

	public Client getClient() {	return client; }

	public void setInstanceID(String instanceID) { this.instanceID = instanceID; }

	public String getInstanceID() { return instanceID; }
}
