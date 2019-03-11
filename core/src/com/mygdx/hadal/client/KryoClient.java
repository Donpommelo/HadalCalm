package com.mygdx.hadal.client;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.KryoSerialization;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.server.Packets;

public class KryoClient {
	
	
	int portSocket = 25565;
	String ipAddress = "localhost";
	
	public Client client;
	public GameStateManager gsm;
	
	public static String hostIP, name;
    public static final int timeout = 5000;
    
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
