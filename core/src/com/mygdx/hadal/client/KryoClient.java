package com.mygdx.hadal.client;

import java.net.InetAddress;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.KryoSerialization;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.mods.WeaponMod;
import com.mygdx.hadal.event.*;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.*;
import com.mygdx.hadal.schmucks.bodies.ClientIllusion;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.enemies.*;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy.enemyType;
import com.mygdx.hadal.server.PacketEffect;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.TitleState;
import com.mygdx.hadal.states.ResultsState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.TiledObjectUtil;
import com.mygdx.hadal.utils.UnlocktoItem;
import com.mygdx.hadal.states.ClientState.ObjectSyncLayers;
import com.mygdx.hadal.states.PauseState;

/**
 * This is the client of the game
 * @author Zachary Tu
 *
 */
public class KryoClient {
	
	//Static fields for ports to be used
	public static final int tcpPortSocket = 25565;
	public static final int udpPortSocket = 54777;
	
	//Me Client
	public Client client;
	
	//This is the gsm of the client
	public GameStateManager gsm;
    
	//This is the id of the client's player
    public static String myID;
    
    //This is a list of scores for display purposes
    public HashMap<Integer, SavedPlayerFields> scores;
    
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
        this.client = new Client(16384, 2048, serialization);
        client.start();
        
        registerPackets();
        
        client.addListener(new Listener() {
        	
        	/**
        	 * Upon connecting to server, send a playerConnect packet with out name and loadout
        	 */
        	@Override
        	public void connected(Connection c) {
        		Log.info("CLIENT CONNECTED");
                client.sendTCP(new Packets.PlayerConnect(true, gsm.getRecord().getName()));
                connID = c.getID();
            }
        	
        	/**
        	 * Upon disconnecting to server, return to title state
        	 */
        	@Override
        	public void disconnected(Connection c) {
        		Log.info("HOST DISCONNECTED");
        		final ClientState cs = getClientState();
				
				if (cs != null) {
					addNotification(cs, "HOST", "DISCONNECTED!");
				}
        		Gdx.app.postRunnable(new Runnable() {
    				
                    @Override
                    public void run() {
                    	gsm.removeState(ResultsState.class);
                    	gsm.removeState(PauseState.class);
                    	
                    	if (cs != null) {
                    		cs.returnToTitle();
                    	} else {
                    		gsm.removeState(ClientState.class);
                    	}
                    }
                });
            }
        	
        	@Override
        	public void received(Connection c, final Object o) {

        		/*
        		 * The Server has created our Player and tells us to load the level.
        		 * Load the level and tell the server we finished.
        		 */
        		if (o instanceof Packets.LoadLevel) {
        			final Packets.LoadLevel p = (Packets.LoadLevel) o;
            		Log.info("CLIENT LOADED LEVEL: " + p.level);

        			Gdx.app.postRunnable(new Runnable() {
        				
                        @Override
                        public void run() {
                        	gsm.removeState(ResultsState.class);
                        	gsm.removeState(ClientState.class);
                			gsm.addClientPlayState(p.level, new Loadout(gsm.getRecord()), TitleState.class);
                	        HadalGame.client.client.sendTCP(new Packets.ClientLoaded(p.firstTime, gsm.getRecord().getName(), new Loadout(gsm.getRecord())));
                        }
                    });
        		}
        		
        		/*
        		 * The Server has seen our connect request and created a new Player for us.
        		 * We receive the id of our new player.
        		 */
        		if (o instanceof Packets.NewClientPlayer) {
        			final Packets.NewClientPlayer p = (Packets.NewClientPlayer) o;
        			Log.info("CLIENT RECEIVED NEW ID: " + p.yourId);
        			myID = p.yourId;
        		}
        		
        		/*
        		 * The Server has finished loading its play state.
        		 * If we haven't connected to the world yet, we send a message now.
        		 */
        		if (o instanceof Packets.ServerLoaded) {
        			Log.info("SERVER LOADED");
        			Packets.PlayerConnect connected = new Packets.PlayerConnect(false, gsm.getRecord().getName());
                    client.sendTCP(connected);
        		}
        		
        		/*
        		 * The game has been paused. (By server or any client)
        		 * We go to the pause state.
        		 */
        		if (o instanceof Packets.Paused) {
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
        		if (o instanceof Packets.Unpaused) {
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof PauseState) {
        				final PauseState cs = (PauseState) gsm.getStates().peek();
        				cs.setToRemove(true);
        			}
        		}
        		
        		/*
        		 * We have received a notification from the server.
        		 * Display the notification
        		 */
        		if (o instanceof Packets.Notification) {
        			final Packets.Notification p = (Packets.Notification) o;
        			
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						addNotification(cs, p.name, p.text);
					}
        		}
        		
        		/*
        		 * The Server informs us that a player is ready in the results screen.
        		 * Update that player's readiness in the results screen
        		 */
        		if (o instanceof Packets.ClientReady) {
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
        		 * The Server tells us to transition to a new state
        		 * Begin transitioning to the specified state.
        		 */
        		if (o instanceof Packets.ClientStartTransition) {
        			final Packets.ClientStartTransition p = (Packets.ClientStartTransition) o;
        			Log.info("CLIENT INSTRUCTED TO TRANSITION: ");

        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						cs.beginTransition(p.state, p.override, p.resultsText);
        					}
        				});
					}
        		}
        		
        		/*
        		 * The Server tells us the new score after scores change.
        		 * Update our scores to the ones specified
        		 */
        		if (o instanceof Packets.SyncScore) {
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
        		 * The Server tells us to create a new entity.
        		 * Create a Client Illusion with the speified dimentions
        		 */
        		if (o instanceof Packets.CreateEntity) {
        			final Packets.CreateEntity p = (Packets.CreateEntity) o;
        			
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
                				cs.addEntity(p.entityID, new ClientIllusion(cs, p.size.x, p.size.y, (int)p.pos.x, (int)p.pos.y, p.sprite, p.align), p.layer);
        					}
        				});
					}
        		}
        		
        		/*
        		 * The Server tells us to create a new enemy entity
        		 * Create the enemy based on server spefications
        		 */
        		if (o instanceof Packets.CreateEnemy) {
        			final Packets.CreateEnemy p = (Packets.CreateEnemy) o;
        			
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						
        						Enemy enemy = null;
        						switch(p.type) {
								case MISC:
									break;
								case SCISSORFISH:
									enemy = new Scissorfish(cs, 0, 0, Constants.ENEMY_HITBOX, null);
									break;
								case SPITTLEFISH:
									enemy = new Spittlefish(cs, 0, 0, Constants.ENEMY_HITBOX, null);
									break;
								case TORPEDOFISH:
									enemy = new Torpedofish(cs, 0, 0, Constants.ENEMY_HITBOX, null);
									break;
								case TURRET_FLAK:
								case TURRET_VOLLEY:
									enemy = new Turret(cs, 0, 0, p.type, 0, Constants.ENEMY_HITBOX, null);
									break;
								case BOSS:
									enemy = new Boss1(cs, 0, 0, enemyType.BOSS, Constants.ENEMY_HITBOX, null);
									break;
								default:
									break;
        						
        						}
        						if (enemy != null) {
        							cs.addEntity(p.entityID, enemy, ObjectSyncLayers.STANDARD);
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
        		 * The Server tells us to delete an entity
        		 * Delete the entity
        		 */
        		if (o instanceof Packets.DeleteEntity) {
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
        		 * The server tells us to create a new player
        		 * Create the player, unless it is ourselves(based on our given id)
        		 * If the player is ourselves, we attach our state's player (ourselves) to the entity.
        		 * Essentially, we create new "other players" but always reuse our state's player for ourselves
        		 */
        		if (o instanceof Packets.CreatePlayer) {
        			final Packets.CreatePlayer p = (Packets.CreatePlayer) o;
            		
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						if (!p.entityID.equals(myID)) {
                    				Player newPlayer = cs.createPlayer(0, 0, p.name, p.loadout, null, 0, true, true);
                    				cs.addEntity(p.entityID, newPlayer, ObjectSyncLayers.STANDARD);
                				} else {
                					cs.getPlayer().setStartLoadout(p.loadout);
                					cs.addEntity(p.entityID, cs.getPlayer(), ObjectSyncLayers.STANDARD);
                				}
        					}
        				});
					} else {
        				Log.info("CLIENT ATTEMPTED TO CREATE PLAYER: " + " " + p.entityID + " BUT WAS NOT LOADED YET.");
        			}
        		}
        		
        		/*
        		 * The Server tells us to create a new event
        		 * We create the event using its provided map object blueprint
        		 */
        		if (o instanceof Packets.CreateEvent) {
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
        		 * Event Creation for specific Poison event.
        		 */
        		if (o instanceof Packets.CreatePoison) {
        			final Packets.CreatePoison p = (Packets.CreatePoison) o;
            		
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						Poison poison = new Poison(cs, (int)p.size.x, (int)p.size.y, (int)(p.pos.x), (int)(p.pos.y), 
        								0, p.draw, (short)0);
        						cs.addEntity(p.entityID, poison, ObjectSyncLayers.STANDARD);
            				}
    					});
					}
        		}
        		
        		/*
        		 * Event Creation for specific Pickup event.
        		 */
        		if (o instanceof Packets.CreatePickup) {
        			final Packets.CreatePickup p = (Packets.CreatePickup) o;
            		
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						Event pickup = null;
        						switch(p.type) {
								case ACTIVE:
									pickup = new PickupActive(cs, (int)(p.pos.x), (int)(p.pos.y), "");
									((PickupActive)pickup).setActive(UnlocktoItem.getUnlock(UnlockActives.valueOf(p.startPickup), null));
									break;
								case ARTIFACT:
									pickup = new PickupArtifact(cs, (int)p.pos.x, (int)p.pos.y, "");
									((PickupArtifact)pickup).setArtifact(UnlockArtifact.valueOf(p.startPickup));
									break;
								case MOD:
									pickup = new PickupWeaponMod(cs, (int)p.pos.x, (int)p.pos.y, "");
									((PickupWeaponMod)pickup).setWeaponMod(WeaponMod.valueOf(p.startPickup));
									break;
								case WEAPON:
									pickup = new PickupEquip(cs, (int)(p.pos.x), (int)(p.pos.y), 0, "");
									((PickupEquip)pickup).setEquip(UnlocktoItem.getUnlock(UnlockEquip.valueOf(p.startPickup), null));
									((PickupEquip)pickup).setMods(p.mods);
									break;
        						}
        						
        						if (pickup != null) {
        							pickup.loadDefaultProperties();
            						cs.addEntity(p.entityID, pickup, ObjectSyncLayers.STANDARD);
        						}
            				}
    					});
					}
        		}
        		
        		/*
        		 * The server has activated an event.
        		 * If we have a copy of that event in our world, we want to activate it as well.
        		 */
        		if (o instanceof Packets.ActivateEvent) {
        			final Packets.ActivateEvent p = (Packets.ActivateEvent) o;
        			
        			final ClientState cs = getClientState();
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
	    					
	    					@Override
	    					public void execute() {
	    						HadalEntity entity = cs.findEntity(p.entityID);
	    						if (entity != null) {
	    							if (entity instanceof Event) {
	    								((Event)entity).getEventData().onActivate(null, null);
	    							}
	    						}
	    					}
    					});
					}
        		}
        		
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
        		
        		if (o instanceof Packets.SyncSchmuck) {
        			Packets.SyncSchmuck p = (Packets.SyncSchmuck) o;
        			
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.syncEntity(p.entityID, p);
					}
        		}
        		
        		if (o instanceof Packets.SyncPlayer) {
        			Packets.SyncPlayer p = (Packets.SyncPlayer) o;
        			
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.syncEntity(p.entityID, p);
					}
        		}
        		
        		if (o instanceof Packets.SyncPickup) {
        			Packets.SyncPickup p = (Packets.SyncPickup) o;
        			
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.syncEntity(p.entityID, p);
					}
        		}
        		
        		if (o instanceof Packets.SyncParticles) {
        			Packets.SyncParticles p = (Packets.SyncParticles) o;
        			
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.syncEntity(p.entityID, p);
					}
        		}
        		
        		/*
        		 * The Server tells us that a player changed their loadout.
        		 * Change their loadout on the client side
        		 */
        		if (o instanceof Packets.SyncServerLoadout) {
        			final Packets.SyncServerLoadout p = (Packets.SyncServerLoadout) o;
        			Log.info("LOADOUT SYNC: " + p.entityID);
        			
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
	    					
	    					@Override
	    					public void execute() {
	    						HadalEntity entity = cs.findEntity(p.entityID);
	    						
	    						if (entity != null) {
	    							if (entity instanceof Player) {
	    								((Player)entity).getPlayerData().syncLoadout(p.loadout);
	    							}
	    						}
	    					}
    					});
					}
        		}
        		
        		/*
        		 * The Server tells us to create a particle entity.
        		 * Create the designated particles and attatch it accordingly
        		 */
        		if (o instanceof Packets.CreateParticles) {
        			final Packets.CreateParticles p = (Packets.CreateParticles) o;
            		
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						if (p.attached) {
        							ParticleEntity entity = new ParticleEntity(cs, null,
        									Particle.valueOf(p.particle), p.linger, p.lifespan, p.startOn, particleSyncType.NOSYNC);
        							entity.setAttachedId(p.attachedID);
        							cs.addEntity(p.entityID, entity, ObjectSyncLayers.STANDARD);
        						} else {
        							ParticleEntity entity = new ParticleEntity(cs, p.pos.x, p.pos.y,
        									Particle.valueOf(p.particle), p.lifespan, p.startOn, particleSyncType.NOSYNC);
            						cs.addEntity(p.entityID, entity, ObjectSyncLayers.STANDARD);
        						}
            				}
    					});
					}
        		}
        		
        		/*
        		 * The Server tells us to focus out camera on a specific location or ourselves
        		 * This is run upon spawning a new player to ensure their camera is centered correctly. 
        		 */
        		if (o instanceof Packets.SyncCamera) {
        			final Packets.SyncCamera p = (Packets.SyncCamera) o;
            		
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {

							@Override
							public void execute() {
								cs.setZoom(p.zoom);
								cs.setCameraTarget(p.zoomPos);
							}
						});
					}
        		}
        		/*
        		 * The Server tells us to despawn a boss. (spawning is taken care of in the CreateEnemy packet)
        		 */
        		if (o instanceof Packets.SyncBoss) {
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {

							@Override
							public void execute() {
								cs.clearBoss();
							}
						});
					} else {
						Log.info("CS NOT LOADED");
					}
        		}
        		if (o instanceof Packets.SyncUI) {
        			final Packets.SyncUI p = (Packets.SyncUI) o;
        			
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {

							@Override
							public void execute() {
								cs.getUiExtra().changeTypes(p.uiTags, true);
							}
						});
					} else {
						Log.info("CS NOT LOADED");
					}
        		}
        	}
        });       
	}
	
	/**
	 * Search for nearby servers.
	 * @return: The address of the server if found and a "Nope" otherwise
	 */
	public String searchServer() {
		if (client == null) {
			init();
		}
		
		InetAddress address = client.discoverHost(54777, 5000);
		
		String start = "NO IP FOUND";
    	if (address != null) {
    		start = address.getHostAddress();
    	}
    	
    	return start;
	}
	
	/**
	 * Similar to getPlayState for server. This returns the ClientState, even if it is underneath a pause.
	 * @return: The current clientstate
	 */
	public ClientState getClientState() {
		
		if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
			return (ClientState) gsm.getStates().peek();
		}
		if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof PauseState) {
			return (ClientState)(((PauseState) gsm.getStates().peek()).getPs());
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
		cs.getPlayStateStage().addDialogue(name, text, "", true, true, true, 3.0f, null, null);
	}
	
	private void registerPackets() {
		Kryo kryo = client.getKryo();
		Packets.allPackets(kryo);
	}
	
	public HashMap<Integer, SavedPlayerFields> getScores() {
		return scores;
	}
}
