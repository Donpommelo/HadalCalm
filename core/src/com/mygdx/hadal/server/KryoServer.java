package com.mygdx.hadal.server;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.io.IOException;
import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.KryoSerialization;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

public class KryoServer {
	
	private static final int serverPort = 25565;
	
	public Server server;
	public GameStateManager gsm;
	
	private HashMap<Integer, Player> players;
	
	public KryoServer(GameStateManager gameStateManager) {
		Kryo kryo = new Kryo();
		kryo.setReferences(true);
		KryoSerialization serialization = new KryoSerialization(kryo);
		this.server = new Server(16384, 2048, serialization);
		this.gsm = gameStateManager;
		this.players = new HashMap<Integer, Player>();
		
		server.addListener(new Listener() {
			
			
			public void received(Connection c, Object o) {
				
				if (o instanceof Packets.PlayerConnect) {
					Packets.PlayerConnect p = (Packets.PlayerConnect) o;
					
					if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof PlayState) {
                        PlayState ps = (PlayState) gsm.getStates().peek();
                        players.put(c.getID(), new Player(ps, (int)(ps.getStartX() * PPM), (int)(ps.getStartY() * PPM),
                        		p.loadout.character, null));
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
			}
		});
		
		try {
			server.bind(serverPort);
		} catch (IOException e) {
			e.printStackTrace();
		}

		registerPackets();

		server.start();
	}
	
	private void registerPackets() {
		Kryo kryo = server.getKryo();
		Packets.allPackets(kryo);
	}
}
