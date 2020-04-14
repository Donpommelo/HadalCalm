package com.mygdx.hadal.client;

import java.net.InetAddress;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.KryoSerialization;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.MusicTrack;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.*;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.bodies.ClientIllusion;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.SoundEntity.soundSyncType;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.SoundEntity;
import com.mygdx.hadal.schmucks.bodies.enemies.*;
import com.mygdx.hadal.server.PacketEffect;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.TitleState;
import com.mygdx.hadal.states.ResultsState;
import com.mygdx.hadal.states.SettingState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.TiledObjectUtil;
import com.mygdx.hadal.utils.UnlocktoItem;
import com.mygdx.hadal.states.ClientState.ObjectSyncLayers;
import com.mygdx.hadal.states.GameState;
import com.mygdx.hadal.states.PauseState;

/**
 * This is the client of the game
 * @author Zachary Tu
 *
 */
public class KryoClient {
	
	//Me Client
	private Client client;
	
	//This is the gsm of the client
	public GameStateManager gsm;
    
	//This is the id of the client's player
    public static String myID;
    
    //This is a mapping of connIds to corresponding scores for display purposes
    public HashMap<Integer, SavedPlayerFields> scores;
    
    //this is the client's connection id
    public int connID;
    
    public KryoClient(GameStateManager gameStateManager) {
    	this.gsm = gameStateManager;
    	scores = new HashMap<Integer, SavedPlayerFields>();
    }
    
    /**
	 * This is called upon starting a new client. initialize client and whatever
	 */
	public void init() {
		Kryo kryo = new Kryo();
        kryo.setReferences(true);
        KryoSerialization serialization = new KryoSerialization(kryo);
        this.client = new Client(30000, 30000, serialization);
        client.start();
        
        registerPackets();

        Listener packetListener = new Listener() {
        	
        	/**
        	 * Upon connecting to server, send a playerConnect packet with your name.
        	 */
        	@Override
        	public void connected(Connection c) {
                sendTCP(new Packets.PlayerConnect(true, gsm.getLoadout().getName()));
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
					addNotification(cs, "", "DISCONNECTED!");
				}
				
				//return to the title. (if our client state is still there, we can do a fade out transition first.
        		Gdx.app.postRunnable(new Runnable() {
    				
                    @Override
                    public void run() {
                    	gsm.removeState(ResultsState.class);
                    	gsm.removeState(PauseState.class);
                    	
                    	if (cs != null) {
                    		cs.returnToTitle(1.5f);
                    	} else {
                    		gsm.removeState(ClientState.class);
                    	}
                    }
                });
            }
        	
        	/**
        	 * Note that the order of these if/elses is according to approximate frequency of packets.
        	 * This might have a (very minor) effect on performance or something idk
        	 */
        	@Override
        	public void received(Connection c, final Object o) {

        		/*
        		 * SyncEntity packets are received for each synced entity every engine tick.
        		 * Sync the specified entity.
        		 */
        		if (o instanceof Packets.SyncEntity) {
        			Packets.SyncEntity p = (Packets.SyncEntity) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.syncEntity(p.entityID, p);
					}
        		}
        		
        		else if (o instanceof Packets.SyncSchmuck) {
        			Packets.SyncSchmuck p = (Packets.SyncSchmuck) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.syncEntity(p.entityID, p);
					}
        		}
        		
        		else if (o instanceof Packets.SyncPlayerAll) {
        			Packets.SyncPlayerAll p = (Packets.SyncPlayerAll) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.syncEntity(p.entityID, p);
					}
        		}
        		
        		else if (o instanceof Packets.SyncPickup) {
        			Packets.SyncPickup p = (Packets.SyncPickup) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.syncEntity(p.entityID, p);
					}
        		}
        		
        		else if (o instanceof Packets.SyncParticles) {
        			Packets.SyncParticles p = (Packets.SyncParticles) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.syncEntity(p.entityID, p);
					}
        		}
        		
        		else if (o instanceof Packets.SyncSound) {
        			Packets.SyncSound p = (Packets.SyncSound) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.syncEntity(p.entityID, p);
					}
        		}
        		
        		/*
        		 * The Server tells us to create a new entity.
        		 * Create a Client Illusion with the specified dimentions
        		 */
        		else if (o instanceof Packets.CreateEntity) {
        			final Packets.CreateEntity p = (Packets.CreateEntity) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						ClientIllusion illusion = new ClientIllusion(cs, p.pos, p.size, p.sprite, p.align);
                				cs.addEntity(p.entityID, illusion, p.layer);
        					}
        				});
					}
        		}
        		
        		/*
        		 * The Server tells us to delete an entity
        		 * Delete the entity
        		 */
        		else if (o instanceof Packets.DeleteEntity) {
        			final Packets.DeleteEntity p = (Packets.DeleteEntity) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
                				cs.removeEntity(p.entityID);
        					}
        				});
					}
        		}
        		
        		/*
        		 * The Server tells us to create a particle entity.
        		 * Create the designated particles and attatch it accordingly
        		 */
        		else if (o instanceof Packets.CreateParticles) {
        			final Packets.CreateParticles p = (Packets.CreateParticles) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						if (p.attached) {
        							ParticleEntity entity = new ParticleEntity(cs, null, Particle.valueOf(p.particle), p.linger, p.lifespan, p.startOn, particleSyncType.NOSYNC, p.pos);
        							entity.setAttachedId(p.attachedID);
        							cs.addEntity(p.entityID, entity, ObjectSyncLayers.STANDARD);
        							entity.setScale(p.scale);
        						} else {
        							ParticleEntity entity = new ParticleEntity(cs, p.pos, Particle.valueOf(p.particle), p.lifespan, p.startOn, particleSyncType.NOSYNC);
            						cs.addEntity(p.entityID, entity, ObjectSyncLayers.STANDARD);
            						entity.setScale(p.scale);
        						}
            				}
    					});
					}
        		}
        		
        		/*
        		 * Server tells us to create a SoundEntity to play a sound
        		 * Create entity and set is attachedId so that it will connect once it is created.
        		 */
        		else if (o instanceof Packets.CreateSound) {
        			final Packets.CreateSound p = (Packets.CreateSound) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						
        						SoundEntity entity = new SoundEntity(cs, null, SoundEffect.valueOf(p.sound), p.volume, p.looped, p.on, soundSyncType.NOSYNC);
        						entity.setAttachedId(p.attachedID);
    							cs.addEntity(p.entityID, entity, ObjectSyncLayers.STANDARD);
            				}
    					});
					}
        		}
        		
        		/*
        		 * The server tells up to update our player's stats when we get buffs
        		 * Change override values that are displayed in our ui
        		 */
        		else if (o instanceof Packets.SyncPlayerStats) {
        			Packets.SyncPlayerStats p = (Packets.SyncPlayerStats) o;
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
        		 * The client is told to update its own stats.
        		 * These are stats only relevant to one client.
        		 */
        		else if (o instanceof Packets.SyncPlayerSelf) {
        			Packets.SyncPlayerSelf p = (Packets.SyncPlayerSelf) o;
        			final ClientState cs = getClientState();
					
        			if (cs != null) {
        				if (cs.getUiPlay() != null) {
        					cs.getUiPlay().setOverrideFuelPercent(p.fuelPercent);
    						cs.getUiPlay().setOverrideClipLeft(p.currentClip);
    						cs.getUiPlay().setOverrideAmmoSize(p.currentAmmo);
    						cs.getUiPlay().setOverrideActivePercent(p.activeCharge);
        				}
					}
        		}
        		
        		/*
        		 * A sound is played on the server side that should be echoed to all clients
        		 */
        		else if (o instanceof Packets.SyncSoundSingle) {
        			final Packets.SyncSoundSingle p = (Packets.SyncSoundSingle) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {

							@Override
							public void execute() {
								if (p.worldPos != null) {
									p.sound.playSourced(cs, p.worldPos, p.volume, p.singleton);
								} else {
									p.sound.play(gsm, p.volume, p.singleton);
								}
							}
							
						});
					}
        		}
        		
        		/*
        		 * The Server tells us the new score after scores change.
        		 * Update our scores to the ones specified
        		 */
        		else if (o instanceof Packets.SyncScore) {
        			final Packets.SyncScore p = (Packets.SyncScore) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						scores = p.scores;
        						cs.getScoreWindow().syncTable();
        					}
        				});
					}
        		}
        		
        		/*
        		 * The Server tells us to create a new enemy entity
        		 * Create the enemy based on server spefications
        		 */
        		else if (o instanceof Packets.CreateEnemy) {
        			final Packets.CreateEnemy p = (Packets.CreateEnemy) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						
        						Enemy enemy = p.type.generateEnemy(cs, new Vector2(), Constants.ENEMY_HITBOX, 0, null);
        						if (enemy != null) {
        							cs.addEntity(p.entityID, enemy, ObjectSyncLayers.STANDARD);
        							enemy.setBoss(p.boss);
        							if (p.boss) {
        								enemy.setName(p.name);
            							cs.setBoss(enemy);
            						}
        						}
        					}
        				});
					}
        		}
        		
        		/*
        		 * The server tells us to create a new player
        		 * Create the player, unless it is ourselves(based on our given id)
        		 * If the player is ourselves, we attach our state's player (ourselves) to the entity.
        		 * Essentially, we create new "other players" but always reuse our state's player for ourselves
        		 */
        		else if (o instanceof Packets.CreatePlayer) {
        			final Packets.CreatePlayer p = (Packets.CreatePlayer) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						if (!p.entityID.equals(myID)) {
                    				Player newPlayer = cs.createPlayer(null, p.name, p.loadout, null, 0, true);
                    				cs.addEntity(p.entityID, newPlayer, ObjectSyncLayers.STANDARD);
                				} else {
                					cs.getPlayer().setStartLoadout(p.loadout);
                					cs.addEntity(p.entityID, cs.getPlayer(), ObjectSyncLayers.STANDARD);
                					
                					//set camera to look at new client player.
                    				cs.camera.position.set(new Vector3(p.startPosition.x, p.startPosition.y, 0));
                				}
        					}
        				});
					}
        		}
        		
        		/*
        		 * The Server tells us to create a new event
        		 * We create the event using its provided map object blueprint
        		 */
        		else if (o instanceof Packets.CreateEvent) {
        			final Packets.CreateEvent p = (Packets.CreateEvent) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						MapObject blueprint = p.blueprint;
        						blueprint.getProperties().put("sync", "USER");
        						cs.addEntity(p.entityID, TiledObjectUtil.parseSingleEventWithTriggers(cs, blueprint), ObjectSyncLayers.STANDARD);
            				}
    					});
					}
        		}
        		
        		/*
        		 * Event Creation for specific Pickup event.
        		 */
        		else if (o instanceof Packets.CreatePickup) {
        			final Packets.CreatePickup p = (Packets.CreatePickup) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						PickupEquip pickup = new PickupEquip(cs, p.pos, "");
        						pickup.setEquip(UnlocktoItem.getUnlock(UnlockEquip.valueOf(p.newPickup), null));
        						cs.addEntity(p.entityID, pickup, ObjectSyncLayers.STANDARD);
            				}
    					});
					}
        		}
        		
        		/*
        		 * The server has activated an event.
        		 * If we have a copy of that event in our world, we want to activate it as well.
        		 */
        		else if (o instanceof Packets.ActivateEvent) {
        			final Packets.ActivateEvent p = (Packets.ActivateEvent) o;
        			final ClientState cs = getClientState();
        			
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
	    					
	    					@Override
	    					public void execute() {
	    						HadalEntity entity = cs.findEntity(p.entityID);
	    						if (entity != null) {
	    							if (entity instanceof Event) {
	    								((Event) entity).getEventData().onActivate(null, null);
	    							}
	    						}
	    					}
    					});
					}
        		}
        		
        		/*
        		 * Server rejects out connection. Display msg on title screen.
        		 */
        		else if (o instanceof Packets.ConnectReject) {
        			final Packets.ConnectReject p = (Packets.ConnectReject) o;
        			if (!gsm.getStates().isEmpty()) {
        				if (gsm.getStates().peek() instanceof TitleState) {
        					((TitleState)gsm.getStates().peek()).setNotification(p.msg);
        					((TitleState)gsm.getStates().peek()).setInputDisabled(false);
        				}
        			}
        			client.stop();
        		}
        		
        		/*
        		 * The Server tells us to load the level.
        		 * Load the level and tell the server we finished.
        		 */
        		else if (o instanceof Packets.LoadLevel) {
        			final Packets.LoadLevel p = (Packets.LoadLevel) o;

        			Gdx.app.postRunnable(new Runnable() {
        				
                        @Override
                        public void run() {
                        	gsm.getApp().fadeOut();
                        	gsm.getApp().setRunAfterTransition(new Runnable() {

								@Override
								public void run() {
									gsm.removeState(ResultsState.class);
		                        	gsm.removeState(ClientState.class);
		                			gsm.addClientPlayState(p.level, new Loadout(gsm.getLoadout()), TitleState.class);
		                	        HadalGame.client.sendTCP(new Packets.ClientLoaded(p.firstTime, gsm.getLoadout().getName(), new Loadout(gsm.getLoadout())));
								}
                        	});
                        }
                    });
        		}
        		
        		/*
        		 * The Server has seen that we finished loading the level and created a new Player for us.
        		 * We receive the id of our new player.
        		 */
        		else if (o instanceof Packets.NewClientPlayer) {
        			Packets.NewClientPlayer p = (Packets.NewClientPlayer) o;
        			myID = p.yourId;
        		}
        		
        		/*
        		 * The Server has finished loading its play state.
        		 * Ask the server to let us connect
        		 */
        		else if (o instanceof Packets.ServerLoaded) {
        			Packets.PlayerConnect connected = new Packets.PlayerConnect(false, gsm.getLoadout().getName());
                    sendTCP(connected);
        		}
        		
        		/*
        		 * The Server tells us to transition to a new state
        		 * Begin transitioning to the specified state.
        		 */
        		else if (o instanceof Packets.ClientStartTransition) {
        			final Packets.ClientStartTransition p = (Packets.ClientStartTransition) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						cs.beginTransition(p.state, p.override, p.resultsText, p.fadeSpeed, p.fadeDelay);
        					}
        				});
					}
        		}
        		
        		/*
        		 * The game has been paused. (By server or any client)
        		 * We go to the pause state.
        		 */
        		else if (o instanceof Packets.Paused) {
        			final Packets.Paused p = (Packets.Paused) o;
        			
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
        				final ClientState cs = (ClientState) gsm.getStates().peek();
        				cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						cs.getGsm().addPauseState(cs, p.pauser, ClientState.class);
        					}
        				});
        			}
        		}
        		
        		/*
        		 * The game has been unpaused. (By server or any client)
        		 * We return to the ClientState
        		 */
        		else if (o instanceof Packets.Unpaused) {
        			if (!gsm.getStates().empty()) {
        				if (gsm.getStates().peek() instanceof PauseState) {
        					final PauseState ps = (PauseState) gsm.getStates().peek();
            				ps.setToRemove(true);
        				}
        				if (gsm.getStates().peek() instanceof SettingState) {
            				final SettingState ss = (SettingState) gsm.getStates().peek();
            				ss.setToRemove(true);
        				}
        			}
        		}
        		
        		/*
        		 * We have received a notification from the server.
        		 * Display the notification
        		 */
        		else if (o instanceof Packets.Notification) {
        			final Packets.Notification p = (Packets.Notification) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						Gdx.app.postRunnable(new Runnable() {
	        				
	                        @Override
	                        public void run() {
	                        	addNotification(cs, p.name, p.text);
	                        }
						});
					}
        		}
        		
        		/*
        		 * The Server informs us that a player is ready in the results screen.
        		 * Update that player's readiness in the results screen
        		 */
        		else if (o instanceof Packets.ClientReady) {
        			final Packets.ClientReady p = (Packets.ClientReady) o;
        			
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ResultsState) {
						final ResultsState vs =  (ResultsState) gsm.getStates().peek();
						Gdx.app.postRunnable(new Runnable() {
	        				
	                        @Override
	                        public void run() {
	                        	vs.readyPlayer(p.playerId);
	                        }
						});
					}
        		}
        		
        		
        		/*
        		 * The Server tells us that a player changed their loadout.
        		 * Change their loadout on the client side
        		 */
        		else if (o instanceof Packets.SyncServerLoadout) {
        			final Packets.SyncServerLoadout p = (Packets.SyncServerLoadout) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
	    					
	    					@Override
	    					public void execute() {
	    						HadalEntity entity = cs.findEntity(p.entityID);
	    						
	    						if (entity != null) {
	    							if (entity instanceof Player) {
	    								((Player)entity).getPlayerData().syncLoadout(p.loadout);
	    								cs.getUiHub().refreshHub();
	    							}
	    						}
	    					}
    					});
					}
        		}
        		
        		/*
        		 * When a client player is spawned, we are told which ui elements to fill our uiExtra with.
        		 */
        		else if (o instanceof Packets.SyncUI) {
        			final Packets.SyncUI p = (Packets.SyncUI) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {

							@Override
							public void execute() {
								cs.getUiExtra().changeTypes(p.uiTags, true);
								cs.getUiExtra().setTimer(p.timer);
								cs.getUiExtra().setTimerIncr(p.timerIncr);
							}
						});
					}
        		}
        		
        		/*
        		 * When a shader in the server changes, we are told to echo that change.
        		 */
        		else if (o instanceof Packets.SyncShader) {
        			final Packets.SyncShader p = (Packets.SyncShader) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {

							@Override
							public void execute() {
								
								if (p.entityID == null) {
									cs.setShaderBase(p.shader);
								} else {
									HadalEntity entity = cs.findEntity(p.entityID);
		    						
		    						if (entity != null) {
		    							entity.setShader(p.shader, p.shaderCount);
		    						}
								}
							}
						});
					}
        		}
        		
        		/*
        		 * we are told by the server to play a new music track
        		 * atm, this only happens when we are in the playstate.
        		 */
        		else if (o instanceof Packets.SyncMusic) {
        			final Packets.SyncMusic p = (Packets.SyncMusic) o;
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {

							@Override
							public void execute() {
								HadalGame.musicPlayer.playSong(MusicTrack.valueOf(p.music), p.volume);
							}
						});
					}
        		}
        	}
        };
        
//       client.addListener(new Listener.LagListener(100, 100, packetListener));
        client.addListener(packetListener);
	}
	
	/**
	 * Search for nearby servers.
	 * @return: The address of the server if found and a "Nope" otherwise
	 */
	public String searchServer() {
		if (client == null) {
			init();
		}
		
		InetAddress address = client.discoverHost(gsm.getSetting().getPortNumber(), 5000);
		
		String start = "NO IP FOUND";
    	if (address != null) {
    		start = address.getHostAddress();
    	}
    	
    	return start;
	}
	
	/**
	 * Similar to getPlayState for server. This returns the ClientState, even if it is underneath a pause or setting stat.
	 * @return: The current clientstate
	 */
	public ClientState getClientState() {
		if (!gsm.getStates().empty()) {
			GameState currentState = gsm.getStates().peek();
			if (currentState instanceof ClientState) {
				return (ClientState) currentState;
			}
			if (currentState instanceof PauseState) {
				return (ClientState) (((PauseState) currentState).getPs());
			}
			if (currentState instanceof SettingState) {
				return (ClientState) (((SettingState) currentState).getPs());
			}
		}
		return null;
	}
    
	/**
	 * This adds a notification to the client's dialog box
	 * @param cs: Clients current clientstate
	 * @param name: name giving the notification
	 * @param text: notification text
	 */
	public void addNotification(ClientState cs, String name, String text) {
		cs.getDialogBox().addDialogue(name, text, "", true, true, true, 3.0f, null, null);
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

	public HashMap<Integer, SavedPlayerFields> getScores() { return scores; }
}
