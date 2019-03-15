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
import com.mygdx.hadal.client.KryoClient;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.MouseTracker;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class KryoServer {
	
	public Server server;
	public GameStateManager gsm;
	
	private HashMap<Integer, Player> players;
	private HashMap<Integer, MouseTracker> mice;
	
	public KryoServer(GameStateManager gameStateManager) {
		Kryo kryo = new Kryo();
		kryo.setReferences(true);
		KryoSerialization serialization = new KryoSerialization(kryo);
		this.server = new Server(16384, 2048, serialization);
		this.gsm = gameStateManager;
		this.players = new HashMap<Integer, Player>();
		this.mice = new HashMap<Integer, MouseTracker>();
		
		server.addListener(new Listener() {
			
			public void received(final Connection c, Object o) {

				if (o instanceof Packets.PlayerConnect) {
					Log.info("NEW CLIENT CONNECTED: " + c.getID());
					Packets.PlayerConnect p = (Packets.PlayerConnect) o;
					
					if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof PlayState) {
                        PlayState ps = (PlayState) gsm.getStates().peek();
                        createNewClientPlayer(ps, c.getID(), p.loadout, null);                        
                        server.sendToTCP(c.getID(), new Packets.LoadLevel(ps.getLevel()));
					} else {
						Log.info("Server received PlayerConnect before entering PlayState!");
					}
				}
				
				if (o instanceof Packets.KeyDown) {
					Packets.KeyDown p = (Packets.KeyDown) o;
					players.get(c.getID()).getController().keyDown(p.action);
				}
				
				if (o instanceof Packets.KeyUp) {
					Packets.KeyUp p = (Packets.KeyUp) o;
					players.get(c.getID()).getController().keyUp(p.action);
				}
				
				if (o instanceof Packets.MouseMove) {
					Packets.MouseMove p = (Packets.MouseMove) o;
					mice.get(c.getID()).setDesiredLocation(p.x, p.y);
				}
				
				if (o instanceof Packets.ClientLoaded) {

					if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof PlayState) {
						final PlayState ps = (PlayState) gsm.getStates().peek();
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

					if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof PlayState) {
                        PlayState ps = (PlayState) gsm.getStates().peek();
                        createNewClientPlayer(ps, c.getID(), p.loadout, null);
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
