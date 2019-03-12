package com.mygdx.hadal.server;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.MoveStates;

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
			this.action = a;
		}
	}
	
	public static class MouseMove {
		public int x, y;
		public MouseMove() {}
		public MouseMove(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	public static class LoadLevel {
		public UnlockLevel level;
		public String yourId;
		public LoadLevel() {}
		public LoadLevel(UnlockLevel level, String yourId) {
			this.level = level;
			this.yourId = yourId;
		}
	}
	
	public static class ClientLoaded {
		public ClientLoaded() {}
	}
	
	public static class CreateEntity {
		public String entityID;
        public Vector2 size;
        public Sprite sprite;
		public CreateEntity() {}
		public CreateEntity(String entityID, Vector2 size, Sprite sprite) {
			this.entityID = entityID;
            this.size = size;
            this.sprite = sprite;
        }
	}
	
	public static class DeleteEntity {
		public String entityID;
		public DeleteEntity() {}
		public DeleteEntity(String entityID) {
			this.entityID = entityID;
        }
	}
	
	public static class CreatePlayer {
		public String entityID;
		public Loadout loadout;
		public CreatePlayer() {}
		public CreatePlayer(String entityID, Loadout loadout) {
            this.entityID = entityID;
            this.loadout = loadout;
        }
	}
	
	public static class SyncEntity {
		public String entityID;
        public Vector2 pos;
        public float angle;
		public SyncEntity() {}
		public SyncEntity(String entityID, Vector2 pos, float a) {
            this.entityID = entityID;
            this.pos = pos;
            this.angle = a;
        }
	}
	
	//TODO: transmit status/stat information
	public static class SyncPlayer {
		public String entityID;
		public Loadout loadout;
        public Vector2 pos;
        public float attackAngle;
        public MoveStates moveState;
        public boolean grounded;
        public int currentSlot;
        public int currentClip;
        public String character;
        
		public SyncPlayer() {}
		public SyncPlayer(String entityID, Loadout loadout, Vector2 pos, float a, MoveStates moveState, Boolean grounded,
				int currentSlot, int currentClip, String character) {
            this.entityID = entityID;
            this.loadout = loadout;
            this.pos = pos;
            this.attackAngle = a;
            this.moveState = moveState;
            this.grounded = grounded;
            this.currentSlot = currentSlot;
            this.currentClip = currentClip;
            this.character = character;
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
    	kryo.register(MouseMove.class);
    	kryo.register(LoadLevel.class);
    	kryo.register(ClientLoaded.class);
    	kryo.register(CreateEntity.class);
    	kryo.register(DeleteEntity.class);
    	kryo.register(CreatePlayer.class);
    	kryo.register(SyncEntity.class);
    	kryo.register(SyncPlayer.class);
    }
}