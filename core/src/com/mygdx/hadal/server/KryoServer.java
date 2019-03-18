package com.mygdx.hadal.server;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.KryoSerialization;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.client.KryoClient;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.MouseTracker;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PauseState;
import com.mygdx.hadal.states.PlayState;

public class KryoServer {
	
	public Server server;
	public GameStateManager gsm;
	
	private HashMap<Integer, Player> players;
	private HashMap<Integer, MouseTracker> mice;
	
	public KryoServer(GameStateManager gameStateManager) {
		this.gsm = gameStateManager;
	}
	
	public void init() {
		Kryo kryo = new Kryo();
		kryo.setReferences(true);
		KryoSerialization serialization = new KryoSerialization(kryo);
		this.server = new Server(16384, 2048, serialization);
		this.players = new HashMap<Integer, Player>();
		this.mice = new HashMap<Integer, MouseTracker>();
		
		server.addListener(new Listener() {
			
			@Override
			public void disconnected(final Connection c) {
				
				final PlayState ps = getPlayState();
				
				if (ps != null) {
					final Player p = players.get(c.getID());
					ps.addPacketEffect(new PacketEffect() {

						@Override
						public void execute() {
							if (p != null) {
								addNotificationToAllExcept(ps, c.getID(), p.getName(), "PLAYER DISCONNECTED!");
								p.getPlayerData().die(ps.getWorldDummy().getBodyData(), null);
							}
						}
					});
				}
				
				players.remove(c.getID());
				mice.remove(c.getID());
			}
			
			public void received(final Connection c, Object o) {

				if (o instanceof Packets.PlayerConnect) {
					Log.info("NEW CLIENT CONNECTED: " + c.getID());
					Packets.PlayerConnect p = (Packets.PlayerConnect) o;
					
					final PlayState ps = getPlayState();
					
					if (ps != null) {
						if (p.firstTime) {
							addNotificationToAllExcept(ps, c.getID(), p.name, "PLAYER CONNECTED!");
						}
						
						createNewClientPlayer(ps, c.getID(), p.name, p.loadout, null);                        
                        server.sendToTCP(c.getID(), new Packets.LoadLevel(ps.getLevel(), p.firstTime));
					} else {
						Log.info("Server received PlayerConnect before entering PlayState!");
					}
				}
				
				
				if (o instanceof Packets.ClientLoaded) {
					final Packets.ClientLoaded p = (Packets.ClientLoaded) o;
					
					final PlayState ps = getPlayState();
					
					if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof PauseState) {
						PauseState pss = ((PauseState)gsm.getStates().peek());
						HadalGame.server.server.sendToTCP(c.getID(), new Packets.Paused(pss.getPauser()));
					}
					
					if (ps != null) {
						ps.addPacketEffect(new PacketEffect() {

							@Override
							public void execute() {
								
								if (p.firstTime) {
									sendNotification(ps, c.getID(), ps.getPlayer().getName(), "JOINED SERVER!");
								}
								
		                        ps.catchUpClient(c.getID());
							}
						});
					} else {
						Log.info("CLIENT LOADED BEFORE SERVER. OOPS");
					}
				}
				
				if (o instanceof Packets.ClientFinishTransition) {
					Packets.ClientFinishTransition p = (Packets.ClientFinishTransition) o;
					Log.info("CLIENT FINISHED TRANSITIONING");

					final PlayState ps = getPlayState();
					String playerName = "";
					PlayerBodyData data = null;
					Player player = players.get(c.getID());
					if (player != null) {
						playerName = player.getName();
						data = player.getPlayerData();
					}
					
					if (ps != null) {
						
						//TODO: logic for client transition upon won/lose
                        switch(p.state) {
						case LOSE:
							createNewClientPlayer(ps, c.getID(), playerName, p.loadout, data);
							break;
						case NEWLEVEL:
							createNewClientPlayer(ps, c.getID(), playerName, p.loadout, null);
	                        server.sendToTCP(c.getID(), new Packets.LoadLevel(ps.getLevel(), false));
							break;
						case NEXTSTAGE:
							createNewClientPlayer(ps, c.getID(), playerName, p.loadout, data);
	                        server.sendToTCP(c.getID(), new Packets.LoadLevel(ps.getLevel(), false));
							break;
						case WIN:
							createNewClientPlayer(ps, c.getID(), playerName, p.loadout, data);
							break;
						default:
							break;
                        }
					} else {
						Log.info("CLIENT FINISHED TRANSITIONING BEFORE SERVER");
					}
				}
				
				if (o instanceof Packets.SyncLoadout) {
        			final Packets.SyncLoadout p = (Packets.SyncLoadout) o;
        			final Player player = players.get(c.getID());
        				final PlayState ps = getPlayState();
        				if (ps != null && player != null) {
        					ps.addPacketEffect(new PacketEffect() {
        						
        						@Override
    							public void execute() {
        							player.getPlayerData().syncLoadout(p.loadout);
                    				player.getPlayerData().syncServerLoadoutChange();
        						}
            				});
        				}
        		}
				
				if (o instanceof Packets.Unpaused) {
        			Log.info("GAME UNPAUSED");
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof PauseState) {
        				
        				Player p = players.get(c.getID());
        				if (p != null) {
        					final PauseState cs = (PauseState) gsm.getStates().peek();
            				cs.setToRemove(true);
            				HadalGame.server.server.sendToAllExceptTCP(c.getID(), new Packets.Unpaused(p.getName()));
        				}
        			}
				}
				
				if (o instanceof Packets.KeyDown) {
					final Packets.KeyDown p = (Packets.KeyDown) o;
					final PlayState ps = getPlayState();
					
					if (ps != null && players.get(c.getID()) != null) {
						
						ps.addPacketEffect(new PacketEffect() {
    						
    						@Override
							public void execute() {
    							players.get(c.getID()).getController().keyDown(p.action);
    						}
        				});
					}	
				}
				
				if (o instanceof Packets.KeyUp) {
					final Packets.KeyUp p = (Packets.KeyUp) o;
					final PlayState ps = getPlayState();
					
					if (ps != null && players.get(c.getID()) != null) {
						ps.addPacketEffect(new PacketEffect() {
    						
    						@Override
							public void execute() {
    							players.get(c.getID()).getController().keyUp(p.action);
    						}
        				});
					}
				}
				
				if (o instanceof Packets.MouseMove) {
					final Packets.MouseMove p = (Packets.MouseMove) o;
					final PlayState ps = getPlayState();
					
					if (ps != null && mice.get(c.getID()) != null) {
						mice.get(c.getID()).setDesiredLocation(p.x, p.y);
					}
				}
			}
		});
		
		try {
			server.bind(KryoClient.tcpPortSocket, KryoClient.udpPortSocket);
		} catch (IOException e) {
			e.printStackTrace();
		}

		registerPackets();

		server.start();
	}
	
	public void createNewClientPlayer(final PlayState ps, final int connId, final String name, 
			final Loadout loadout, final PlayerBodyData data) {

		ps.addPacketEffect(new PacketEffect() {

			@Override
			public void execute() {
				Player newPlayer = new Player(ps, (int)(ps.getStartX() * PPM), (int)(ps.getStartY() * PPM),
						name, loadout, data);
		        MouseTracker newMouse = new MouseTracker(ps, false);
		        newPlayer.setMouse(newMouse);
		        players.put(connId, newPlayer);
		        mice.put(connId, newMouse);
		        
		        server.sendToTCP(connId, new Packets.NewClientPlayer(newPlayer.getEntityID().toString()));
			}
		});
	}
	
	public void sendPacketToPlayer(Player p, Object o) {
		for (Entry<Integer, Player> conn: players.entrySet()) {
			if (conn.getValue().equals(p)) {
				server.sendToTCP(conn.getKey(), o);
				break;
			}
		}
	}
	
	public PlayState getPlayState() {
		
		if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof PlayState) {
			return (PlayState) gsm.getStates().peek();
		}
		if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof PauseState) {
			return ((PauseState) gsm.getStates().peek()).getPs();
		}
		return null;
	}
	
	public void addNotification(PlayState ps, String name, String text) {
		if (ps.getStage() != null) {
			ps.getStage().addDialogue(name, text, "", true, true, true, 3.0f, null, null);
		}
	}
	
	public void sendNotification(PlayState ps, int connId, String name, String text) {
        server.sendToTCP(connId, new Packets.Notification(name, text));	
	}
	
	public void addNotificationToAll(PlayState ps, String name, String text) {
		if (ps.getStage() != null) {
			ps.getStage().addDialogue(name, text, "", true, true, true, 3.0f, null, null);
	        server.sendToAllTCP(new Packets.Notification(name, text));	
		}
	}
	
	public void addNotificationToAllExcept(PlayState ps, int connId, String name, String text) {
		if (ps.getStage() != null) {
			ps.getStage().addDialogue(name, text, "", true, true, true, 3.0f, null, null);
	        server.sendToAllExceptTCP(connId, new Packets.Notification(name, text));
		}
	}
	
	public HashMap<Integer, Player> getPlayers() {
		return players;
	}

	public HashMap<Integer, MouseTracker> getMice() {
		return mice;
	}

	private void registerPackets() {
		Kryo kryo = server.getKryo();
		Packets.allPackets(kryo);
	}
}
