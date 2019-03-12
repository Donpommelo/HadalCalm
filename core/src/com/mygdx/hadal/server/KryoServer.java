package com.mygdx.hadal.server;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.io.IOException;
import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.KryoSerialization;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.MouseTracker;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

public class KryoServer {
	
	private static final int serverPort = 25565;
	
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
			
			
			public void received(Connection c, Object o) {

				if (o instanceof Packets.PlayerConnect) {
					Log.info("" + (o.getClass().getName()));

					Packets.PlayerConnect p = (Packets.PlayerConnect) o;
					
					if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof PlayState) {
                        PlayState ps = (PlayState) gsm.getStates().peek();
                        Player newPlayer = new Player(ps, (int)(ps.getStartX() * PPM), (int)(ps.getStartY() * PPM),
                        		p.loadout.character, null);
                        MouseTracker newMouse = new MouseTracker(ps, false);
                        newPlayer.setMouse(newMouse);
                        players.put(c.getID(), newPlayer);
                        mice.put(c.getID(), newMouse);
                        
                        server.sendToTCP(c.getID(), new Packets.LoadLevel(ps.getLevel(), newPlayer.getEntityID().toString()));
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
					Log.info("" + (o.getClass().getName()));

					if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof PlayState) {
                        PlayState ps = (PlayState) gsm.getStates().peek();
                        ps.catchUpClient(c.getID());
					}
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
