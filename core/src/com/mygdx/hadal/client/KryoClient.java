package com.mygdx.hadal.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.serialization.KryoSerialization;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.PickupEquip;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.bodies.*;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.SoundEntity.soundSyncType;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.server.*;
import com.mygdx.hadal.server.User.UserDto;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.*;
import com.mygdx.hadal.states.ClientState.ObjectSyncLayers;
import com.mygdx.hadal.states.PlayState.TransitionState;
import com.mygdx.hadal.text.HText;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.TiledObjectUtil;
import com.mygdx.hadal.utils.UnlocktoItem;

import java.util.HashMap;
import java.util.Objects;

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
    public HashMap<Integer, User> users;

    //this is the client's connection id
    public int connID;
    
    public Listener packetListener;
    
    public KryoClient(GameStateManager gameStateManager) { this.gsm = gameStateManager; }
    
    /**
	 * This is called upon starting a new client. initialize client and whatever
	 */
	public void init() {
		Kryo kryo = new Kryo();
        kryo.setReferences(true);

        KryoSerialization serialization = new KryoSerialization(kryo);
        this.client = new Client(65536, 32768, serialization);
		users = new HashMap<>();
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
					addNotification(cs, "", HText.DISCONNECTED.text(), DialogType.SYSTEM);
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
        
//       client.addListener(new Listener.LagListener(100, 100, packetListener));
       client.addListener(packetListener);
	}

	/**
	 * this processes all the packets that do not fall into the other categories (add/remove/sync)
	 */
	public void receiveOtherPacket(Object o) {

		/*
		 * The server tells up to update our player's stats when we get buffs
		 * Change override values that are displayed in our ui
		 */
		if (o instanceof Packets.SyncPlayerStats p) {
			final ClientState cs = getClientState();

			if (cs != null) {
				if (cs.getUiPlay() != null) {
					cs.getUiPlay().setOverrideClipSize(p.maxClip);
					cs.getUiPlay().setOverrideMaxHp(p.maxHp);
					cs.getUiPlay().setOverrideMaxFuel(p.maxFuel);
					cs.getUiPlay().setOverrideAirblastCost(p.airblastCost);
					cs.getUiPlay().setOverrideWeaponSlots(p.weaponSlots);
					cs.getUiPlay().setOverrideArtifactSlots(p.artifactSlots);
					cs.getUiPlay().setOverrideHealthVisibility(p.healthVisible);
				}
			}
		}

		/*
		 * A sound is played on the server side that we should echo
		 */
		else if (o instanceof final Packets.SyncSoundSingle p) {
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
						user.setScoreUpdated(true);
					} else {
						score = new SavedPlayerFields(p.name, p.connID);
						users.put(p.connID, new User(null, score, new SavedPlayerFieldsExtra()));
					}
					score.setWins(p.wins);
					score.setKills(p.kills);
					score.setDeaths(p.deaths);
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
					HadalEntity entity = cs.findEntity(p.entityID);
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
					cs.syncLatency(p.timestamp);
				});
			}
		}

		/*
		 * Server tells us a player started typing. Make typing bubble appear above their head.
		 */
		else if (o instanceof final Packets.SyncTyping p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				HadalEntity entity = cs.findEntity(p.entityID);
				if (entity != null) {
					if (entity instanceof Player player) {
						player.startTyping();
					}
				}
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

					if (p.modeSettings != null) {
						for (String key: p.modeSettings.keySet()) {
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
						Gdx.app.postRunnable(() -> cs.getKillFeed().addMessage(perp.getPlayer(), vic.getPlayer(), p.enemyType, p.tags));
					} else {
						Gdx.app.postRunnable(() -> cs.getKillFeed().addMessage(null, vic.getPlayer(), p.enemyType, p.tags));
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
				Gdx.app.postRunnable(() -> cs.getKillFeed().addNotification(p.message, false));
			}
		}

		/*
		 * Server sends a chat message to the client
		 */
		else if (o instanceof final Packets.ServerChat p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				Gdx.app.postRunnable(() -> cs.getMessageWindow().addText(p.text, p.type, p.connID));
			}
		}

		/*
		 * We have received a notification from the server.
		 * Display the notification
		 */
		else if (o instanceof final Packets.ServerNotification p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				Gdx.app.postRunnable(() -> addNotification(cs, p.name, p.text, p.type));
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
		 * The Server tells us that a player changed their loadout.
		 * Change their loadout on the client side
		 */
		else if (o instanceof final Packets.SyncServerLoadout p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					HadalEntity entity = cs.findEntity(p.entityID);
					if (entity != null) {
						if (entity instanceof Player player) {
							player.getPlayerData().syncLoadout(p.loadout, p.save);
							cs.getUiHub().refreshHub();
						}
					}
				});
			}
		}

		/*
		 * When a client player is spawned, we are told which ui elements to fill our uiExtra with.
		 */
		else if (o instanceof final Packets.SyncUI p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					AlignmentFilter.currentTeams = p.teams;
					AlignmentFilter.teamScores = p.scores;
					cs.getUiExtra().setTimer(p.timer);
					cs.getUiExtra().setTimerIncr(p.timerIncr);
					cs.getUiExtra().changeTypes(p.uiTags, true);
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
						.addObjectiveClient(p.entityID, p.icon, p.color, p.displayOffScreen, p.displayOnScreen));
			}
		}

		/*
		 * When a shader in the server changes, we are told to echo that change.
		 */
		else if (o instanceof final Packets.SyncShader p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {

					//no entity info means use the shader on the state background
					if (p.entityID == null) {
						cs.setShaderBase(p.shader);
					} else {
						HadalEntity entity = cs.findEntity(p.entityID);
						if (entity != null) {
							entity.setShader(p.shader, p.shaderCount);
						}
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
					HashMap<Integer, User> usersTemp = new HashMap<>(users);
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
					cs.addEntity(p.entityID, illusion, p.synced, p.layer);
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
				cs.addPacketEffect(() -> cs.syncEntity(p.entityID, p, 0.0f, p.timestamp));
			}
			return true;
		}

		else if (o instanceof final Packets.DeletePlayer p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> cs.syncEntity(p.entityID, p, 0.0f, p.timestamp));
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
						entity = new ParticleEntity(cs, null, Particle.valueOf(p.particle), p.linger, p.lifespan, p.startOn, particleSyncType.NOSYNC, p.pos);
						entity.setAttachedId(p.attachedID);
					} else {
						entity = new ParticleEntity(cs, p.pos, Particle.valueOf(p.particle), p.lifespan, p.startOn, particleSyncType.NOSYNC);
					}
					cs.addEntity(p.entityID, entity, p.synced, ObjectSyncLayers.EFFECT);
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
		 * Server tells us to create a SoundEntity to play a sound
		 * Create entity and set its attachedId so that it will connect once it is created.
		 */
		else if (o instanceof final Packets.CreateSound p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.addPacketEffect(() -> {
					SoundEntity entity = new SoundEntity(cs, null, SoundEffect.valueOf(p.sound), p.volume, p.pitch, p.looped, p.on, soundSyncType.NOSYNC);
					entity.setAttachedId(p.attachedID);
					cs.addEntity(p.entityID, entity, p.synced, ObjectSyncLayers.STANDARD);
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
					Enemy enemy = p.type.generateEnemy(cs, p.pos, Constants.ENEMY_HITBOX, 0, null);
					enemy.serverPos.set(p.pos).scl(1 / PPM);
					cs.addEntity(p.entityID, enemy, true, ObjectSyncLayers.STANDARD);
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

					Player newPlayer = cs.createPlayer(null, p.name, p.loadout, null, 0, true, p.connID == connID, p.hitboxFilter);

					newPlayer.serverPos.set(p.startPosition).scl(1 / PPM);
					newPlayer.setStartPos(p.startPosition);
					newPlayer.setConnId(p.connID);
					newPlayer.setScaleModifier(p.scaleModifier);
					cs.addEntity(p.entityID, newPlayer, true, ObjectSyncLayers.STANDARD);

					if (p.connID == connID) {
						cs.setPlayer(newPlayer);

						if (!p.dontMoveCamera) {
							//set camera to look at new client player.
							cs.getCamera().position.set(new Vector3(p.startPosition.x, p.startPosition.y, 0));
							cs.getCameraFocusAim().set(p.startPosition);
						}
					}

					//attach new player to respective user (or create if nonexistent)
					if (users.containsKey(p.connID)) {
						users.get(p.connID).setPlayer(newPlayer);
					} else {
						users.put(p.connID, new User(newPlayer, new SavedPlayerFields(p.name, p.connID), new SavedPlayerFieldsExtra()));
					}
					users.get(p.connID).setTeamFilter(p.loadout.team);
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
					for (EventDto.Pair pair: dto.getProperties()) {
						blueprint.getProperties().put(pair.getKey(), pair.getValue());
					}

					Event e = TiledObjectUtil.parseSingleEventWithTriggers(cs, blueprint);
					e.serverPos.set(e.getStartPos()).scl(1 / PPM);
					cs.addEntity(p.entityID, e, p.synced, ObjectSyncLayers.STANDARD);
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
					Ragdoll entity = new Ragdoll(cs, p.pos, p.size, p.sprite, p.velocity, p.duration, p.gravity, p.setVelo, p.sensor, false);
					cs.addEntity(p.entityID, entity, false, ObjectSyncLayers.STANDARD);
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
					PickupEquip pickup = new PickupEquip(cs, p.pos, "");
					pickup.setEquip(Objects.requireNonNull(UnlocktoItem.getUnlock(UnlockEquip.getByName(p.newPickup),null)));
					pickup.serverPos.set(pickup.getStartPos()).scl(1 / PPM);

					cs.addEntity(p.entityID, pickup, p.synced, ObjectSyncLayers.STANDARD);
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
				cs.syncEntity(p.entityID, p, 0.0f, p.timestamp);
				if (cs.getUiPlay() != null) {
					cs.getUiPlay().setOverrideFuelPercent(p.fuelPercent);
					cs.getUiPlay().setOverrideClipLeft(p.currentClip);
					cs.getUiPlay().setOverrideAmmoSize(p.currentAmmo);
					cs.getUiPlay().setOverrideActivePercent(p.activeCharge);
					if (cs.getPlayer() != null) {
						cs.getPlayer().setBlinded(p.blinded);
					}
				}
			}
			return true;
		}

		else if (o instanceof PacketsSync.SyncEntity p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.syncEntity(p.entityID, p, p.age, p.timestamp);
			}
			return true;
		}
		
		else if (o instanceof Packets.SyncSound p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.syncEntity(p.entityID, p, p.age, p.timestamp);
			}
			return true;
		}

		else if (o instanceof Packets.SyncPickup p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				cs.syncEntity(p.entityID, p, p.age, p.timestamp);
			}
			return true;
		}

		else if (o instanceof Packets.SyncHitSound p) {
			final ClientState cs = getClientState();
			if (cs != null) {
				SoundEffect.playHitSound(cs.getGsm(), p.large);
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
			if (currentState instanceof ClientState) {
				return (ClientState) currentState;
			} else if (currentState instanceof PauseState) {
				return (ClientState) (((PauseState) currentState).getPs());
			} else if (currentState instanceof SettingState) {
				return (ClientState) (((SettingState) currentState).getPlayState());
			} else if (currentState instanceof AboutState) {
				return (ClientState) (((AboutState) currentState).getPlayState());
			} else if (currentState instanceof ResultsState) {
				return (ClientState) (((ResultsState) currentState).getPs());
			}
		}
		return null;
	}
    
	/**
	 * This adds a notification to the client's dialog box
	 * @param cs: Client's current clientstate
	 * @param name: name giving the notification
	 * @param text: notification text
	 * @param type: the type of dialog (system message, story dialog, etc)
	 */
	public void addNotification(ClientState cs, String name, String text, DialogType type) {
		cs.getDialogBox().addDialogue(name, text, "", true, true, true, 3.0f, null, null, type);
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

	public HashMap<Integer, User> getUsers() { return users; }
}
