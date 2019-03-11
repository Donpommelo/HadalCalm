package com.mygdx.hadal.server;

import com.esotericsoftware.kryo.Kryo;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.input.PlayerAction;

public class Packets {

	public static class PlayerConnect {
		public String name;
		public Loadout loadout;
		public PlayerConnect() {}
		public PlayerConnect(String m, Loadout loadout) {
			this.name = m;
			this.loadout = loadout;
		}
	}
	
	public static class KeyDown {
		public PlayerAction action;
		public KeyDown() {}
		public KeyDown(PlayerAction a) {
			this. action = a;
		}
	}
	
	public static class KeyUp {
		public PlayerAction action;
		public KeyUp() {}
		public KeyUp(PlayerAction a) {
			this. action = a;
		}
	}
	
	/**
     * REGISTER ALL THE CLASSES FOR KRYO TO SERIALIZE AND SEND
     * @param kryo The kryo object
     */
    public static void allPackets(Kryo kryo) {
    	kryo.register(PlayerConnect.class);
    	kryo.register(KeyDown.class);
    	kryo.register(KeyUp.class);
    }
}
