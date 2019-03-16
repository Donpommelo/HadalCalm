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
			public void disconnected(Connection c) {
				
				final PlayState ps = getPlayState();
				
				if (ps != null) {
					Player p = players.get(c.getID());
					if (p != null) {
						p.getPlayerData().die(ps.getWorldDummy().getBodyData(), null);
					}
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
						createNewClientPlayer(ps, c.getID(), p.loadout, null);                        
                        server.sendToTCP(c.getID(), new Packets.LoadLevel(ps.getLevel()));
					} else {
						Log.info("Server received PlayerConnect before entering PlayState!");
					}
				}
				
				if (o instanceof Packets.KeyDown) {
					final PlayState ps = getPlayState();
					
					if (ps != null && players.get(c.getID()) != null) {
						Packets.KeyDown p = (Packets.KeyDown) o;
						players.get(c.getID()).getController().keyDown(p.action);
					}	
				}
				
				if (o instanceof Packets.KeyUp) {
					final PlayState ps = getPlayState();
					
					if (ps != null && players.get(c.getID()) != null) {
						Packets.KeyUp p = (Packets.KeyUp) o;
						players.get(c.getID()).getController().keyUp(p.action);
					}
				}
				
				if (o instanceof Packets.MouseMove) {
					final PlayState ps = getPlayState();
					
					if (ps != null && mice.get(c.getID()) != null) {
						Packets.MouseMove p = (Packets.MouseMove) o;
						mice.get(c.getID()).setDesiredLocation(p.x, p.y);
					}
				}
				
				if (o instanceof Packets.ClientLoaded) {

					final PlayState ps = getPlayState();
					
					if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof PauseState) {
						HadalGame.server.server.sendToTCP(c.getID(), new Packets.Paused());
					}
					
					if (ps != null) {
						ps.addPacketEffect(new PacketEffect() {

							@Override
							public void execute() {
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
					
					if (ps != null) {
						createNewClientPlayer(ps, c.getID(), p.loadout, null);
                        switch(p.state) {
						case LOSE:
							break;
						case NEWLEVEL:
						case NEXTSTAGE:
	                        server.sendToTCP(c.getID(), new Packets.LoadLevel(ps.getLevel()));
							break;
						case WIN:
							break;
						default:
							break;
                        }
					}
				}
				
				if (o instanceof Packets.SyncLoadout) {
        			final Packets.SyncLoadout p = (Packets.SyncLoadout) o;
        			Player player = players.get(c.getID());
        			if (p != null) {
        				player.getPlayerData().syncLoadout(p.loadout);
        				player.getPlayerData().syncLoadoutChange();
        			}
        		}
				
				if (o instanceof Packets.Unpaused) {
        			Log.info("GAME UNPAUSED");
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof PauseState) {
        				final PauseState cs = (PauseState) gsm.getStates().peek();
        				cs.setToRemove(true);
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
	
	public void createNewClientPlayer(final PlayState ps, final int connId, final Loadout loadout, final PlayerBodyData data) {

		ps.addPacketEffect(new PacketEffect() {

			@Override
			public void execute() {
				Player newPlayer = new Player(ps, (int)(ps.getStartX() * PPM), (int)(ps.getStartY() * PPM),
						loadout, data);
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
