package com.mygdx.hadal.client;

import java.io.IOException;
import java.net.InetAddress;

import javax.swing.JOptionPane;

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
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.managers.GameStateManager;
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
import com.mygdx.hadal.states.ClientState.ObjectSyncLayers;

public class KryoClient {
	
	
	public static final int tcpPortSocket = 25565;
	public static final int udpPortSocket = 54777;
	String ipAddress = "localhost";
	
	public Client client;
	public GameStateManager gsm;
	
	public static String hostIP, name;
    public static final int timeout = 5000;
    
    public static String myId;
    
    public KryoClient(GameStateManager gameStateManager) {
    	this.gsm = gameStateManager;
    }
    	
	public void init(boolean reconnect) {
		Kryo kryo = new Kryo();
        kryo.setReferences(true);
        KryoSerialization serialization = new KryoSerialization(kryo);
        this.client = new Client(16384, 2048, serialization);
        client.start();
        
        registerPackets();
        
        client.addListener(new Listener() {
        	
        	public void connected(Connection c) {
        		Log.info("CLIENT CONNECTED");
                Packets.PlayerConnect connected = new Packets.PlayerConnect(name, new Loadout(gsm.getRecord()));
                client.sendTCP(connected);
            }
        	
        	public void received(Connection c, final Object o) {

        		if (o instanceof Packets.NewClientPlayer) {
        			final Packets.NewClientPlayer p = (Packets.NewClientPlayer) o;
        			Log.info("CLIENT RECEIVED NEW ID: " + p.yourId);
        			myId = p.yourId;
        		}
        		
        		if (o instanceof Packets.ServerLoaded) {
        			Log.info("SERVER LOADED");
        			Packets.PlayerConnect connected = new Packets.PlayerConnect(name, new Loadout(gsm.getRecord()));
                    client.sendTCP(connected);
        		}
        		
        		if (o instanceof Packets.LoadLevel) {
        			final Packets.LoadLevel p = (Packets.LoadLevel) o;
            		Log.info("CLIENT LOADED LEVEL: " + p.level);

        			Gdx.app.postRunnable(new Runnable() {
        				
                        @Override
                        public void run() {
                        	gsm.removeState(ClientState.class);
                			gsm.addClientPlayState(p.level, new Loadout(gsm.getRecord()), TitleState.class);
                	        HadalGame.client.client.sendTCP(new Packets.ClientLoaded());
                        }
                    });
        		}
        		
        		if (o instanceof Packets.ClientStartTransition) {
        			final Packets.ClientStartTransition p = (Packets.ClientStartTransition) o;
        			Log.info("CLIENT INSTRUCTED TO TRANSITION: ");

        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
        				final ClientState cs = (ClientState) gsm.getStates().peek();
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
        			
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
        				final ClientState cs = (ClientState) gsm.getStates().peek();
        				cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
                				cs.addEntity(p.entityID, new ClientIllusion(cs, p.size.x, p.size.y, p.sprite), p.layer);

        					}
        				});
        			}
        		}
        		
        		if (o instanceof Packets.CreateEnemy) {
        			final Packets.CreateEnemy p = (Packets.CreateEnemy) o;
        			
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
        				final ClientState cs = (ClientState) gsm.getStates().peek();
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
        			
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
        				final ClientState cs = (ClientState) gsm.getStates().peek();
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
            		
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
        				Log.info("CLIENT CREATED PLAYER: " + " " + p.entityID);
        				final ClientState cs = (ClientState) gsm.getStates().peek();
        				
        				cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						if (!p.entityID.equals(myId)) {
                    				Player newPlayer = new Player(cs, 0, 0, p.loadout, null);
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
            		
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
        				final ClientState cs = (ClientState) gsm.getStates().peek();
        				
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
            		
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
        				final ClientState cs = (ClientState) gsm.getStates().peek();
        				
        				cs.addPacketEffect(new PacketEffect() {
        					
        					@Override
        					public void execute() {
        						Poison poison = new Poison(cs, (int)p.size.x, (int)p.size.y, (int)p.pos.x, (int)p.pos.y, 
        								0, p.draw, (short)0);
        						cs.addEntity(p.entityID, poison, ObjectSyncLayers.STANDARD);
            				}
    					});
        			}
        		}
        		
        		if (o instanceof Packets.ActivateEvent) {
        			final Packets.ActivateEvent p = (Packets.ActivateEvent) o;
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
        				final ClientState cs = (ClientState) gsm.getStates().peek();
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
        			
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
        				ClientState cs = (ClientState) gsm.getStates().peek();
        				cs.syncEntity(p.entityID, p);
        			}
        		}
        		
        		if (o instanceof Packets.SyncSchmuck) {
        			Packets.SyncSchmuck p = (Packets.SyncSchmuck) o;
        			
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
        				ClientState cs = (ClientState) gsm.getStates().peek();
        				cs.syncEntity(p.entityID, p);
        			}
        		}
        		
        		if (o instanceof Packets.SyncPlayer) {
        			Packets.SyncPlayer p = (Packets.SyncPlayer) o;
        			
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
        				ClientState cs = (ClientState) gsm.getStates().peek();
        				cs.syncEntity(p.entityID, p);
        			}
        		}
        		
        		if (o instanceof Packets.SyncLoadout) {
        			final Packets.SyncLoadout p = (Packets.SyncLoadout) o;
        			Log.info("LOADOUT SYNC: " + p.entityId);
        			
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
        				final ClientState cs = (ClientState) gsm.getStates().peek();
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
            		
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
        				final ClientState cs = (ClientState) gsm.getStates().peek();
        				
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
        			
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
        				ClientState cs = (ClientState) gsm.getStates().peek();
        				cs.syncEntity(p.entityID, p);
        			}
        		}
        	}
        });
        
        if (!reconnect) {
            
        	InetAddress address = client.discoverHost(54777, 5000);
        	String start = "IT PUTS INTO IP";
        	if (address != null) {
        		start = address.getHostAddress();
        	}
        	
        	// Request the host from the user.
            String input = (String) JOptionPane.showInputDialog(null, "Host:", "Connect to game server", JOptionPane.QUESTION_MESSAGE,
                    null, null, start);
            if (input == null || input.trim().length() == 0) System.exit(1);
            hostIP = input.trim();

            // Request the user's name.
            input = (String) JOptionPane.showInputDialog(null, "Name:", "Connect to game server", JOptionPane.QUESTION_MESSAGE, null,
                    null, "Test");
            while (input == null || input.trim().length() == 0) {
                input = (String) JOptionPane.showInputDialog(null, "Name can't be nothing. Try again:", "Connect to game server", JOptionPane.QUESTION_MESSAGE, null,
                        null, "Test");
            }
            name = input.trim();
        }
        
        new Thread("Connect") {
            public void run () {
                try {
                    client.connect(5000, hostIP,tcpPortSocket, udpPortSocket);
                    // Server communication after connection can go here, or in Listener#connected().
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showConfirmDialog(null, "Couldn't connect to server.", "Server connection issue", JOptionPane.OK_OPTION);
                }
            }
        }.start();
	}
    
	private void registerPackets() {
		Kryo kryo = client.getKryo();
		Packets.allPackets(kryo);
	}
}
