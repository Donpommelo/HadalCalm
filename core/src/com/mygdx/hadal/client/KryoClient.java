package com.mygdx.hadal.client;

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
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.PickupEquip;
import com.mygdx.hadal.event.modes.CrownHoldable;
import com.mygdx.hadal.event.modes.FlagCapturable;
import com.mygdx.hadal.event.modes.ReviveGravestone;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.*;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.server.*;
import com.mygdx.hadal.server.User.UserDto;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.packets.PacketsLoadout;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.*;
import com.mygdx.hadal.states.PlayState.ObjectLayer;
import com.mygdx.hadal.states.PlayState.TransitionState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.utils.TiledObjectUtil;
import com.mygdx.hadal.utils.UnlocktoItem;

import java.util.Objects;
import java.util.UUID;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * This is the client of the game
 * @author Ghulliam Grapplejack
 */
public class KryoClient {
	
	//Me Client
	private Client client;
	
	//This is the gsm of the client
	public final GameStateManager gsm;
    
    //This is a mapping of connIds to corresponding users
    public ObjectMap<Integer, User> users;

    //this is the client's connection id
    public int connID;
    
    public Listener packetListener;
    
    public KryoClient(GameStateManager gameStateManager) { this.gsm = gameStateManager; }
    
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
		users = new ObjectMap<>();
        client.start();
        
        registerPackets();

        packetListener = new Listener() {
        	
        	/**
        	 * Upon connecting to server, send a playerConnect packet with your name and version.
        	 */
        	@Override
        	public void connected(Connection c) {
                sendTCP(new Packets.PlayerConnect(true, gsm.getLoadout().getName(), HadalGame.Version, null));
                connID = c.getID();
            }
        	
        	/**
        	 * Upon disconnecting to server, return to title state
        	 */
        	@Override
        	public void disconnected(Connection c) {
        		final ClientState cs = getClientState();
				
        		//If our client state is still here, the server closed
				if (cs != null) {
					addNotification(cs, "", UIText.DISCONNECTED.text(), false, DialogType.SYSTEM);
				}
				
				//return to the lobby state. (if our client state is still there, we can do a fade out transition first.
        		Gdx.app.postRunnable(() -> {

					if (cs != null) {
						cs.returnToTitle(1.0f);
					} else {
						gsm.removeState(ResultsState.class);
						gsm.removeState(SettingState.class, false);
						gsm.removeState(AboutState.class, false);
						gsm.removeState(PauseState.class, false);
						gsm.removeState(ClientState.class);
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

//		client.addListener(new Listener.LagListener(50, 100, packetListener));
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
						p.sound.playSourced(cs, p.worldPos, p.volume, p.pitch);
					} else {
						p.sound.play(gsm, p.volume, p.singleton);
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
					SavedPlayerFields score;

					//update score or create a new user if existing score not found
					if (users.containsKey(p.connID)) {
						User user = users.get(p.connID);
						score = user.getScores();
						user.setSpectator(p.spectator);
						user.setScoreUpdated(true);
					} else {
						score = new SavedPlayerFields(p.name, p.connID);
						User user = new User(null, score, new SavedPlayerFieldsExtra());
						user.setSpectator(p.spectator);
						users.put(p.connID, user);
					}
					score.setWins(p.wins);
					score.setKills(p.kills);
					score.setDeaths(p.deaths);
					score.setAssists(p.assists);
					score.setLives(p.lives);
					score.setScore(p.score);
					score.setPing(p.ping);
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
					HadalEntity entity = cs.findEntity(p.uuidMSB, p.uuidLSB);
					if (entity != null) {
						if (entity instanceof Event event) {
							if (users.containsKey(p.connID)) {
								User user = users.get(p.connID);
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
		 * Server responds to our latency checking packet. record the time and update our latency.
		 */
		else if (o instanceof final Packets.LatencyAck p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					cs.syncLatency(p.serverTimestamp, p.clientTimestamp);
				});
			}
		}

		/*
		 * Server tells us a player started typing. Make typing bubble appear above their head.
		 */
		else if (o instanceof final Packets.SyncTyping p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					HadalEntity entity = cs.findEntity(p.uuidMSB, p.uuidLSB);
					if (entity != null) {
						if (entity instanceof Player player) {
							player.startTyping();
						}
					}
				});
			}
		}

		/*
		 * Server tells us a player has disconnected so we must remove them from user list.
		 */
		else if (o instanceof final Packets.RemoveScore p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> users.remove(p.connID));
			}
		}

		/*
		 * Server rejects our connection. Display msg on lobby screen.
		 */
		else if (o instanceof final Packets.ConnectReject p) {
			if (!gsm.getStates().isEmpty()) {
				if (gsm.getStates().peek() instanceof LobbyState lobby) {
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
			if (!gsm.getStates().isEmpty()) {
				if (gsm.getStates().peek() instanceof LobbyState lobby) {
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
				cs.setNextState(TransitionState.NEWLEVEL);
			}
			Gdx.app.postRunnable(() -> {
				gsm.getApp().fadeOut();
				gsm.getApp().setRunAfterTransition(() -> {
					gsm.removeState(ResultsState.class, false);
					gsm.removeState(SettingState.class, false);
					gsm.removeState(AboutState.class, false);
					gsm.removeState(PauseState.class, false);

					boolean spectator = false;
					if (cs != null) {
						spectator = cs.isSpectatorMode();
					}

					gsm.removeState(ClientState.class, false);

					//set mode settings according to what the server sends
					if (p.modeSettings != null) {
						for (String key : p.modeSettings.keySet()) {
							gsm.getSetting().setModeSetting(p.mode, key, p.modeSettings.get(key));
						}
					}

					gsm.addClientPlayState(p.level, p.mode, new Loadout(gsm.getLoadout()), LobbyState.class);
					HadalGame.client.sendTCP(new Packets.ClientLoaded(p.firstTime, spectator, p.spectator, gsm.getLoadout().getName(), new Loadout(gsm.getLoadout())));
				});
			});
		}

		/*
		 * The Server has finished loading its play state.
		 * Ask the server to let us connect
		 */
		else if (o instanceof Packets.ServerLoaded) {
			Packets.PlayerConnect connected = new Packets.PlayerConnect(false, gsm.getLoadout().getName(), HadalGame.Version, null);
			sendTCP(connected);
		}

		/*
		 * The Server tells us to transition to a new state
		 * Begin transitioning to the specified state.
		 */
		else if (o instanceof final Packets.ClientStartTransition p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					cs.beginTransition(p.state, p.fadeSpeed, p.fadeDelay);
				});
			}
		}

		/*
		 * The game has been paused. (By server or any client)
		 * We go to the pause state.
		 */
		else if (o instanceof final Packets.Paused p) {
			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof final ClientState cs) {
				cs.addPacketEffect(() -> cs.getGsm().addPauseState(cs, p.pauser, ClientState.class, true));
			}
		}

		/*
		 * The game has been unpaused. (By server or any client)
		 * We return to the ClientState
		 */
		else if (o instanceof Packets.Unpaused) {
			if (!gsm.getStates().empty()) {
				if (gsm.getStates().peek() instanceof final PauseState ps) {
					ps.setToRemove(true);
				}
				if (gsm.getStates().peek() instanceof final SettingState ss) {
					ss.setToRemove(true);
				}
				if (gsm.getStates().peek() instanceof final AboutState as) {
					as.setToRemove(true);
				}
			}
		}

		/*
		 * We have received a kill message from the server.
		 * Display the message
		 */
		else if (o instanceof final Packets.SyncKillMessage p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				User vic = users.get(p.vicConnID);
				if (vic != null) {
					User perp = users.get(p.perpConnID);
					if (perp != null) {
						cs.addPacketEffect(() -> cs.getKillFeed().addMessage(perp.getPlayer(), vic.getPlayer(),
							p.enemyType, p.source, p.tags));
					} else {
						cs.addPacketEffect(() -> cs.getKillFeed().addMessage(null, vic.getPlayer(),
							p.enemyType, p.source, p.tags));
					}
				}
			}
		}

		/*
		Server sends a notification to the client. Display it
		 */
		else if (o instanceof final Packets.SyncNotification p) {
			final ClientState cs = getClientState();

			if (cs != null) {
				cs.addPacketEffect(() -> cs.getKillFeed().addNotification(p.message, false));
			}
		}

		/*
		 * Server sends a chat message to the client
		 */
		else if (o instanceof final Packets.ServerChat p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> cs.getMessageWindow().addText(p.text, p.type, p.connID));
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
					gsm.setHostSetting(p.settings);
					cs.getScoreWindow().syncSettingTable();
				});
			}
		}

		/*
		 * The Server informs us that a player is ready in the results screen.
		 * Update that player's readiness in the results screen
		 */
		else if (o instanceof final Packets.ClientReady p) {
			if (!gsm.getStates().empty()) {
				if (gsm.getStates().peek() instanceof final ResultsState vs) {
					Gdx.app.postRunnable(() -> vs.readyPlayer(p.playerID));
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
				User user = users.get(p.connID);
				if (user != null && cs != null) {
					Player player = user.getPlayer();
					if (player != null) {
						cs.addPacketEffect(() -> {
							if (player.getPlayerData() != null) {
								player.getPlayerData().syncLoadout(p.loadout, true, p.save);
							}
						});
					}
				}
			}
		}

		/*
		 * The Server tells us that a player changed one part of their loadout.
		 * Change their loadout on the client side
		 */
		else if (o instanceof final PacketsLoadout.SyncLoadoutServer p) {
			final ClientState cs = getClientState();

			User user = users.get(p.connID);
			if (user != null && cs != null) {
				Player player = user.getPlayer();
				if (player != null) {
					cs.addPacketEffect(() -> {
						if (player.getPlayerData() != null) {
							if (p instanceof PacketsLoadout.SyncEquipServer s) {
								player.getPlayerData().syncEquip(s.equip);
							}
							else if (p instanceof PacketsLoadout.SyncArtifactServer s) {
								player.getPlayerData().syncArtifact(s.artifact, true, s.save);
								cs.getUiHub().refreshHub(null);
							}
							else if (p instanceof PacketsLoadout.SyncActiveServer s) {
								player.getPlayerData().syncActive(s.active);
							}
							else if (p instanceof PacketsLoadout.SyncCharacterServer s) {
								player.getPlayerData().setCharacter(s.character);
							}
							else if (p instanceof PacketsLoadout.SyncTeamServer s) {
								player.getPlayerData().setTeam(s.team);
							}
							else if (p instanceof PacketsLoadout.SyncCosmeticServer s) {
								player.getPlayerData().setCosmetic(s.cosmetic);
							}
						}
					});
				}
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
					cs.getUiExtra().setTimer(p.timer);
					cs.getUiExtra().setTimerIncr(p.timerIncr);
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
				cs.addPacketEffect(() -> cs.getUiObjective()
						.addObjectiveClient(new UUID(p.uuidMSB, p.uuidLSB), p.icon, p.color, p.displayOffScreen, p.displayOnScreen));
			}
		}

		/*
		 * When a shader in the server changes, we are told to echo that change.
		 */
		else if (o instanceof final Packets.SyncShader p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					HadalEntity entity = cs.findEntity(p.uuidMSB, p.uuidLSB);
					if (entity != null) {
						entity.setShader(p.shader, p.shaderCount, false);
					}
				});
			}
		}

		/*
		 * We are told by the server each player's extra score info. Set it so we can display in the results state.
		 */
		else if (o instanceof final Packets.SyncExtraResultsInfo p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {

					cs.setResultsText(p.resultsText);

					//temporarily store user info so we can attach old player to updated user
					ObjectMap<Integer, User> usersTemp = new ObjectMap<>(users);
					users.clear();

					for (int i = 0; i < p.users.length; i++) {
						UserDto user = p.users[i];
						User updatedUser;
						if (user != null) {
							updatedUser = new User(null, user.scores, user.scoresExtra);
							updatedUser.setSpectator(user.spectator);

							User userOld = usersTemp.get(user.scores.getConnID());
							if (userOld != null) {
								updatedUser.setPlayer(userOld.getPlayer());
							}
							if (user.scoresExtra.getLoadout() != null) {
								updatedUser.setTeamFilter(user.scoresExtra.getLoadout().team);
							}
							users.put(user.scores.getConnID(), updatedUser);
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
				cs.returnToTitle(0.0f);
			}
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
					cs.addEntity(p.uuidMSB, p.uuidLSB, illusion, p.synced, p.layer);
				});
			}
			return true;
		}

		/*
		 * Server tells us to execute a synced attack. Do so with the parameters provided
		 */
		if (o instanceof final Packets.CreateSyncedAttackSingle p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					HadalEntity creator = cs.findEntity(new UUID(p.uuidMSBCreator, p.uuidLSBCreator));
					if (creator == null) {
						creator = cs.getWorldDummy();
					}
					if (creator != null) {
						if (creator instanceof Schmuck schmuck) {
							Hitbox hbox;
							if (p instanceof Packets.CreateSyncedAttackSingleExtra p1) {
								hbox = p.attack.initiateSyncedAttackSingle(cs, schmuck, p.pos, p.velo, p1.extraFields);
							} else {
								hbox = p.attack.initiateSyncedAttackSingle(cs, schmuck, p.pos, p.velo);
							}
							hbox.serverPos.set(p.pos).scl(1 / PPM);
							hbox.serverVelo.set(p.velo);
							cs.addEntity(p.uuidMSB, p.uuidLSB, hbox, true, ObjectLayer.HBOX);
						}
					}
				});
			}
			return true;
		}

		/*
		 * Server tells us to execute a synced attack that creates multiple hitboxes. Do so with the parameters provided
		 */
		if (o instanceof final Packets.CreateSyncedAttackMulti p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				HadalEntity creator = cs.findEntity(new UUID(p.uuidMSBCreator, p.uuidLSBCreator));
				if (creator != null) {
					if (creator instanceof Schmuck schmuck) {
						cs.addPacketEffect(() -> {
							Hitbox[] hboxes;
							if (p instanceof Packets.CreateSyncedAttackMultiExtra p1) {
								hboxes = p.attack.initiateSyncedAttackMulti(cs, schmuck, p.weaponVelo, p.pos, p.velo, p1.extraFields);
							} else {
								hboxes = p.attack.initiateSyncedAttackMulti(cs, schmuck, p.weaponVelo, p.pos, p.velo);
							}

							for (int i = 0; i < hboxes.length; i++) {
								if (p.pos.length > i) {
									hboxes[i].serverPos.set(p.pos[i]).scl(1 / PPM);
								}
								if (p.velo.length > i) {
									hboxes[i].serverVelo.set(p.velo[i]);
								}
								if (p.uuidMSB.length > i && p.uuidLSB.length > 1) {
									cs.addEntity(p.uuidMSB[i], p.uuidLSB[i], hboxes[i], true, ObjectLayer.HBOX);
								}
							}
						});
					}
				}
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
				cs.addPacketEffect(() -> cs.syncEntity(p.uuidMSB, p.uuidLSB, p, 0.0f, p.timestamp));
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
					if (p.attached) {
						entity = new ParticleEntity(cs, null, p.particle, p.linger, p.lifespan, p.startOn, SyncType.NOSYNC);
						entity.setAttachedId(new UUID(p.uuidMSBAttached, p.uuidLSBAttached));
						entity.setOffset(p.pos.x, p.pos.y);
					} else {
						entity = new ParticleEntity(cs, p.pos, p.particle, p.lifespan, p.startOn, SyncType.NOSYNC);
					}
					cs.addEntity(p.uuidMSB, p.uuidLSB, entity, p.synced, ObjectLayer.EFFECT);
					entity.setScale(p.scale);
					entity.setRotate(p.rotate);
					entity.setPrematureOff(p.prematureOff);
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

		else if (o instanceof final Packets.CreateFlag p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					FlagCapturable entity = new FlagCapturable(cs, p.pos, null, p.teamIndex);
					cs.addEntity(p.uuidMSB, p.uuidLSB, entity, true, ObjectLayer.HBOX);
				});
			}
			return true;
		}

		else if (o instanceof final Packets.CreateCrown p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					CrownHoldable entity = new CrownHoldable(cs, p.pos);
					cs.addEntity(p.uuidMSB, p.uuidLSB, entity, true, ObjectLayer.HBOX);
				});
			}
			return true;
		}

		else if (o instanceof final Packets.CreateGrave p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				User user = users.get(p.connID);
				if (user != null) {
					cs.addPacketEffect(() -> {
						ReviveGravestone grave = new ReviveGravestone(cs, p.pos, user, p.connID, p.returnMaxTimer, null);
						cs.addEntity(p.uuidMSB, p.uuidLSB, grave, true, ObjectLayer.HBOX);
					});
				}
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
					SoundEntity entity = new SoundEntity(cs, null, p.sound, p.lifespan, p.volume, p.pitch, p.looped, p.on, SyncType.NOSYNC);
					entity.setAttachedId(new UUID(p.uuidMSBAttached, p.uuidLSBAttached));
					cs.addEntity(p.uuidMSB, p.uuidLSB, entity, p.synced, ObjectLayer.STANDARD);
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
					cs.addEntity(p.uuidMSB, p.uuidLSB, enemy, true, ObjectLayer.STANDARD);
					enemy.setBoss(p.boss);
					if (p.boss) {
						enemy.setName(p.name);
						cs.setBoss(enemy);
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

					Player newPlayer = cs.createPlayer(null, p.name, p.loadout, null, 0, null,
							true, p.connID == connID, false, p.hitboxFilter);

					newPlayer.serverPos.set(p.startPosition).scl(1 / PPM);
					newPlayer.setStartPos(p.startPosition);
					newPlayer.setConnId(p.connID);
					newPlayer.setHitboxfilter(p.hitboxFilter);
					newPlayer.setScaleModifier(p.scaleModifier);
					cs.addEntity(p.uuidMSB, p.uuidLSB, newPlayer, true, ObjectLayer.STANDARD);

					if (p.connID == connID) {
						cs.setPlayer(newPlayer);

						if (!p.dontMoveCamera) {
							//set camera to look at new client player.
							cs.getCamera().position.set(new Vector3(p.startPosition.x, p.startPosition.y, 0));
							cs.getCameraFocusAimVector().setZero();
						}
					}

					//attach new player to respective user (or create if nonexistent)
					if (users.containsKey(p.connID)) {
						users.get(p.connID).setPlayer(newPlayer);
					} else {
						users.put(p.connID, new User(newPlayer, new SavedPlayerFields(p.name, p.connID), new SavedPlayerFieldsExtra()));
					}
					users.get(p.connID).setTeamFilter(p.loadout.team);
					newPlayer.setUser(users.get(p.connID));
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

					Event e = TiledObjectUtil.parseSingleEventWithTriggers(cs, blueprint);
					e.serverPos.set(e.getStartPos()).scl(1 / PPM);
					cs.addEntity(p.uuidMSB, p.uuidLSB, e, p.synced, ObjectLayer.STANDARD);
				});
			}
			return true;
		}
		
		/*
		 * Server tells us to create a ragdoll. Ragdolls are not synced.
		 */
		else if (o instanceof final Packets.CreateRagdoll p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					Ragdoll entity = new Ragdoll(cs, p.pos, p.size, p.sprite, p.velocity, p.duration, p.gravity, p.setVelo,
							p.sensor, false, p.fade);
					cs.addEntity(p.uuidMSB, p.uuidLSB, entity, false, ObjectLayer.STANDARD);
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
						pickup.setEquip(Objects.requireNonNull(UnlocktoItem.getUnlock(p.newPickup,null)));
					}
					pickup.serverPos.set(pickup.getStartPos()).scl(1 / PPM);

					cs.addEntity(p.uuidMSB, p.uuidLSB, pickup, p.synced, ObjectLayer.STANDARD);
				});
			}
			return true;
		}
		
		//if none of the packets match, return false to indicate the packet was not an add/create packet
		return false;
	}
	
	/**
	 * this processes all the packets that sync entities in the world.
	 * returns true if any packet is processed. (if the input o is a sync packet)
	 */
	public boolean receiveSyncPacket(Object o) {

		/*
		 * The client is told to update its own stats.
		 * These are stats only relevant to one client.
		 */
		if (o instanceof PacketsSync.SyncPlayerSelf p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					cs.syncEntity(p.uuidMSB, p.uuidLSB, p, 0.0f, p.timestamp);
					if (cs.getUiPlay() != null) {
						cs.getUiPlay().setOverrideClipLeft(p.currentClip);
						cs.getUiPlay().setOverrideAmmoSize(p.currentAmmo);
						cs.getUiPlay().setOverrideActivePercent(p.activeCharge);
						if (cs.getPlayer() != null) {
							if (cs.getPlayer().getPlayerData() != null) {
								cs.getPlayer().getPlayerData().setCurrentFuel(p.currentFuel);
							}
							cs.getPlayer().setBlinded(p.blinded);
						}
					}
				});
			}
			return true;
		}

		else if (o instanceof PacketsSync.SyncEntity p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					cs.syncEntity(p.uuidMSB, p.uuidLSB, p, p.age, p.timestamp);
				});
			}
			return true;
		}
		
		else if (o instanceof Packets.SyncSound p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					cs.syncEntity(p.uuidMSB, p.uuidLSB, p, p.age, p.timestamp);
				});
			}
			return true;
		}

		else if (o instanceof Packets.SyncPickup p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					cs.syncEntity(p.uuidMSB, p.uuidLSB, p, p.age, p.timestamp);
				});
			}
			return true;
		}

		else if (o instanceof Packets.SyncHitSound p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					SoundEffect.playHitSound(cs.getGsm(), p.large);
				});
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
		if (!gsm.getStates().empty()) {
			GameState currentState = gsm.getStates().peek();
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
	 * @param override: ncan this notification be overridden by other notifications
	 * @param type: the type of dialog (system message, story dialog, etc)
	 */
	public void addNotification(ClientState cs, String name, String text, boolean override, DialogType type) {
		cs.getDialogBox().addDialogue(name, text, "", true, override, true, 3.0f, null, null, type);
	}

	/**
	 * This returns the number of non-spectator, non-bot players. used to determine boss hp scaling.
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
		Kryo kryo = client.getKryo();
		Packets.allPackets(kryo);
	}
	
	public void sendTCP(Object p) {
		if (client != null) {
			client.sendTCP(p);
		}
	}
	
	public void sendUDP(Object p) {
		if (client != null) {
			client.sendUDP(p);
		}
	}
	
	public Client getClient() {	return client; }

	public ObjectMap<Integer, User> getUsers() { return users; }
}
