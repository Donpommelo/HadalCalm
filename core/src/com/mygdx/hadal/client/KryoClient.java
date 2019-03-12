package com.mygdx.hadal.client;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.KryoSerialization;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.ClientIllusion;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.TitleState;

public class KryoClient {
	
	
	int portSocket = 25565;
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
                Packets.PlayerConnect connected = new Packets.PlayerConnect(name, new Loadout(gsm.getRecord()));
                client.sendTCP(connected);
            }
        	
        	public void received(Connection c, final Object o) {

        		if (o instanceof Packets.LoadLevel) {
            		Log.info("" + (o.getClass().getName()));

        			final Packets.LoadLevel p = (Packets.LoadLevel) o;
        			myId = p.yourId;
        			Gdx.app.postRunnable(new Runnable() {
        				
                        @Override
                        public void run() {
                			gsm.addClientPlayState(p.level, new Loadout(gsm.getRecord()), TitleState.class);
                        }
                    });
        		}
        		
        		if (o instanceof Packets.CreateEntity) {
        			Packets.CreateEntity p = (Packets.CreateEntity) o;
        			
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
        				ClientState cs = (ClientState) gsm.getStates().peek();
        				cs.addEntity(p.entityID, new ClientIllusion(cs, p.size.x, p.size.y, p.sprite));
        			}
        		}
        		
        		if (o instanceof Packets.DeleteEntity) {
        			Packets.DeleteEntity p = (Packets.DeleteEntity) o;
        			
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
        				ClientState cs = (ClientState) gsm.getStates().peek();
        				cs.removeEntity(p.entityID, null);
        			}
        		}
        		
        		if (o instanceof Packets.CreatePlayer) {

        			Packets.CreatePlayer p = (Packets.CreatePlayer) o;
            		Log.info("" + (o.getClass().getName()) + " " + p.entityID);

        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
        				ClientState cs = (ClientState) gsm.getStates().peek();
        				Log.info("" + p.entityID + " " + myId);
        				
        				if (!p.entityID.equals(myId)) {
            				Player newPlayer = new Player(cs, 0, 0, p.loadout.character, null);
            				cs.addEntity(p.entityID, newPlayer);
        				} else {        					
        					cs.addEntity(p.entityID, cs.getPlayer());
        				}
        			}
        		}
        		
        		if (o instanceof Packets.SyncEntity) {
        			Packets.SyncEntity p = (Packets.SyncEntity) o;
        			
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
        				ClientState cs = (ClientState) gsm.getStates().peek();
        				cs.syncObject(p.entityID, p);
        			}
        		}
        		
        		if (o instanceof Packets.SyncPlayer) {
        			Packets.SyncPlayer p = (Packets.SyncPlayer) o;
        			
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ClientState) {
        				ClientState cs = (ClientState) gsm.getStates().peek();
        				cs.syncObject(p.entityID, p);
        			}
        		}
        	}
        });
        
        if (!reconnect) {
            // Request the host from the user.
            String input = (String) JOptionPane.showInputDialog(null, "Host:", "Connect to game server", JOptionPane.QUESTION_MESSAGE,
                    null, null, "localhost");
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
                    client.connect(5000, hostIP, portSocket);
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
