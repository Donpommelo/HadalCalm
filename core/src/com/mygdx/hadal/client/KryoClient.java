package com.mygdx.hadal.client;

import java.net.InetAddress;

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
import com.mygdx.hadal.server.PacketEffect;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.TitleState;
import com.mygdx.hadal.utils.TiledObjectUtil;
import com.mygdx.hadal.utils.UnlocktoItem;
import com.mygdx.hadal.states.ClientState.ObjectSyncLayers;
import com.mygdx.hadal.states.PauseState;

public class KryoClient {
	
	
	public static final int tcpPortSocket = 25565;
	public static final int udpPortSocket = 54777;
	String ipAddress = "localhost";
	
	public Client client;
	public GameStateManager gsm;
	
	public static String hostIP;
    public static final int timeout = 5000;
    
    public static String myId;
    
    public KryoClient(GameStateManager gameStateManager) {
    	this.gsm = gameStateManager;
    }
    	
	public void init() {
		Kryo kryo = new Kryo();
        kryo.setReferences(true);
        KryoSerialization serialization = new KryoSerialization(kryo);
        this.client = new Client(16384, 2048, serialization);
        client.start();
        
        registerPackets();
        
        client.addListener(new Listener() {
        	
        	@Override
        	public void connected(Connection c) {
        		Log.info("CLIENT CONNECTED");
                client.sendTCP(new Packets.PlayerConnect(true, gsm.getRecord().getName(), new Loadout(gsm.getRecord())));
            }
        	
        	@Override
        	public void disconnected(Connection c) {
        		Log.info("HOST DISCONNECTED");
        		final ClientState cs = getClientState();
				
				if (cs != null) {
					addNotification(cs, "HOST", "SERVER DISCONNECTED!");
				}
        		Gdx.app.postRunnable(new Runnable() {
    				
                    @Override
                    public void run() {
                    	gsm.removeState(PauseState.class);
                    	gsm.removeState(ClientState.class);
                    }
                });
            }
        	
        	@Override
        	public void received(Connection c, final Object o) {

        		if (o instanceof Packets.NewClientPlayer) {
        			final Packets.NewClientPlayer p = (Packets.NewClientPlayer) o;
        			Log.info("CLIENT RECEIVED NEW ID: " + p.yourId);
        			myId = p.yourId;
        		}
        		
        		if (o instanceof Packets.ServerLoaded) {
        			Log.info("SERVER LOADED");
        			Packets.PlayerConnect connected = new Packets.PlayerConnect(false, gsm.getRecord().getName(), new Loadout(gsm.getRecord()));
                    client.sendTCP(connected);
        		}
        		
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
        		
        		if (o instanceof Packets.Unpaused) {
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof PauseState) {
        				final PauseState cs = (PauseState) gsm.getStates().peek();
        				cs.setToRemove(true);
        			}
        		}
        		
        		if (o instanceof Packets.Notification) {
        			final Packets.Notification p = (Packets.Notification) o;
        			
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						addNotification(cs, p.name, p.text);
					}
        		}
        		
        		if (o instanceof Packets.LoadLevel) {
        			final Packets.LoadLevel p = (Packets.LoadLevel) o;
            		Log.info("CLIENT LOADED LEVEL: " + p.level);

        			Gdx.app.postRunnable(new Runnable() {
        				
                        @Override
                        public void run() {
                        	gsm.removeState(ClientState.class);
                			gsm.addClientPlayState(p.level, new Loadout(gsm.getRecord()), TitleState.class);
                	        HadalGame.client.client.sendTCP(new Packets.ClientLoaded(p.firstTime));
                        }
                    });
        		}
        		
        		if (o instanceof Packets.ClientStartTransition) {
        			final Packets.ClientStartTransition p = (Packets.ClientStartTransition) o;
        			Log.info("CLIENT INSTRUCTED TO TRANSITION: ");

        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						cs.beginTransition(p.state);
        					}
        				});
					}
        		}
        		
        		if (o instanceof Packets.CreateEntity) {
        			final Packets.CreateEntity p = (Packets.CreateEntity) o;
        			
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
                				cs.addEntity(p.entityID, new ClientIllusion(cs, p.size.x, p.size.y, (int)p.pos.x, (int)p.pos.y, p.sprite), p.layer);
        					}
        				});
					}
        		}
        		
        		if (o instanceof Packets.CreateEnemy) {
        			final Packets.CreateEnemy p = (Packets.CreateEnemy) o;
        			
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						switch(p.type) {
								case MISC:
									break;
								case SCISSORFISH:
									cs.addEntity(p.entityID, new Scissorfish(cs, 0, 0), ObjectSyncLayers.STANDARD);
									break;
								case SPITTLEFISH:
									cs.addEntity(p.entityID, new Spittlefish(cs, 0, 0), ObjectSyncLayers.STANDARD);
									break;
								case TORPEDOFISH:
									cs.addEntity(p.entityID, new Torpedofish(cs, 0, 0), ObjectSyncLayers.STANDARD);
									break;
								case TURRET_FLAK:
								case TURRET_VOLLEY:
									cs.addEntity(p.entityID, new Turret(cs, 0, 0, p.type, 0), ObjectSyncLayers.STANDARD);
									break;
								default:
									break;
        						
        						}
        					}
        				});
					}
        		}
        		
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
        		
        		if (o instanceof Packets.CreatePlayer) {
        			final Packets.CreatePlayer p = (Packets.CreatePlayer) o;
            		
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						if (!p.entityID.equals(myId)) {
                    				Player newPlayer = new Player(cs, 0, 0, p.name, p.loadout, null);
                    				cs.addEntity(p.entityID, newPlayer, ObjectSyncLayers.STANDARD);
                				} else {        					
                					cs.addEntity(p.entityID, cs.getPlayer(), ObjectSyncLayers.STANDARD);
                				}
        					}
        				});
					} else {
        				Log.info("CLIENT ATTEMPTED TO CREATE PLAYER: " + " " + p.entityID + " BUT WAS NOT LOADED YET.");
        			}
        		}
        		
        		if (o instanceof Packets.CreateEvent) {
        			final Packets.CreateEvent p = (Packets.CreateEvent) o;
            		
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						MapObject blueprint = p.blueprint;
        						blueprint.getProperties().put("sync", 1);
        						Event event = TiledObjectUtil.parseTiledEvent(cs, blueprint);
        						cs.addEntity(p.entityID, event, ObjectSyncLayers.STANDARD);
        						TiledObjectUtil.parseTiledSingleTrigger(event);
            				}
    					});
					}
        		}
        		
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
        		
        		if (o instanceof Packets.SyncLoadout) {
        			final Packets.SyncLoadout p = (Packets.SyncLoadout) o;
        			Log.info("LOADOUT SYNC: " + p.entityId);
        			
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.addPacketEffect(new PacketEffect() {
	    					
	    					@Override
	    					public void execute() {
	    						HadalEntity entity = cs.findEntity(p.entityId);
	    						
	    						if (entity != null) {
	    							if (entity instanceof Player) {
	    								((Player)entity).getPlayerData().syncLoadout(p.loadout);
	    							}
	    						}
	    					}
    					});
					}
        		}
        		
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
        		
        		if (o instanceof Packets.SyncParticles) {
        			Packets.SyncParticles p = (Packets.SyncParticles) o;
        			
        			final ClientState cs = getClientState();
					
					if (cs != null) {
						cs.syncEntity(p.entityID, p);
					}
        		}
        	}
        });       
	}
	
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
	
	public ClientState getClientState() {
		
		if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
			return (ClientState) gsm.getStates().peek();
		}
		if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof PauseState) {
			return (ClientState)(((PauseState) gsm.getStates().peek()).getPs());
		}
		return null;
	}
    
	public void addNotification(ClientState cs, String name, String text) {
		cs.getStage().addDialogue(name, text, "", true, true, true, 3.0f, null, null);
	}
	
	private void registerPackets() {
		Kryo kryo = client.getKryo();
		Packets.allPackets(kryo);
	}
}
