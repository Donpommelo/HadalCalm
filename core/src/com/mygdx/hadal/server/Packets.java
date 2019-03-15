package com.mygdx.hadal.server;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.MoveStates;
import com.mygdx.hadal.states.ClientState.ObjectSyncLayers;

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
		public LoadLevel() {}
		public LoadLevel(UnlockLevel level) {
			this.level = level;
		}
	}
	
	public static class NewClientPlayer {
		public String yourId;
		public NewClientPlayer() {}
		public NewClientPlayer(String yourId) {
			this.yourId = yourId;
		}
	}
	
	public static class ClientLoaded {
		public ClientLoaded() {}
	}
	
	public static class ClientStartTransition {
		public boolean won;
		public ClientStartTransition() {}
		public ClientStartTransition(boolean won) {
			this.won = won;
		}
	}
	
	public static class ClientFinishTransition {
		public Loadout loadout;
		public ClientFinishTransition() {}
		public ClientFinishTransition(Loadout loadout) {
			this.loadout = loadout;
		}
	}
	
	public static class CreateEntity {
		public String entityID;
        public Vector2 size;
        public Sprite sprite;
        public ObjectSyncLayers layer;
		public CreateEntity() {}
		public CreateEntity(String entityID, Vector2 size, Sprite sprite, ObjectSyncLayers layer) {
			this.entityID = entityID;
            this.size = size;
            this.sprite = sprite;
            this.layer = layer;
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
	
	public static class CreateEvent {
		public String entityID;
        public MapObject blueprint;
		public CreateEvent() {}
		public CreateEvent(String entityID, MapObject blueprint) {
            this.entityID = entityID;
            this.blueprint = blueprint;
        }
	}
	
	public static class CreatePoison {
		public String entityID;
        public Vector2 pos;
        public Vector2 size;
        public boolean draw;
		public CreatePoison() {}
		public CreatePoison(String entityID, Vector2 pos, Vector2 size, boolean draw) {
            this.entityID = entityID;
            this.pos = pos;
            this.size = size;
            this.draw = draw;
        }
	}
	
	public static class ActivateEvent {
		public String entityID;
		public ActivateEvent() {}
		public ActivateEvent(String entityID) {
            this.entityID = entityID;
        }
	}
	
	public static class SyncSchmuck {
		public String entityID;
        public float currentHp;
        public float currentFuel;
        public float flashDuration;

		public SyncSchmuck() {}
		public SyncSchmuck(String entityID, float currentHp, float currentFuel, float flashDuration) {
			this.entityID = entityID;
			this.currentHp = currentHp;
			this.currentFuel = currentFuel;
			this.flashDuration = flashDuration;
		}
	}
	
	public static class SyncPlayer {
		public String entityID;
        public float attackAngle;
        public MoveStates moveState;
        public boolean grounded;
        public int currentSlot;
        public int currentClip;
        public int maxClip;
        public float maxHp;
        public float maxFuel;
        public float airblastCost;
        public float activeCharge;
        public boolean reloading;
        public float reloadPercent;
        
		public SyncPlayer() {}
		public SyncPlayer(String entityID, float a, MoveStates moveState, Boolean grounded,
				int currentSlot, int currentClip, int maxClip, float maxHp, float maxFuel,
				float airblastCost, float activeCharge, boolean reloading, float reloadPercent) {
            this.entityID = entityID;
            this.attackAngle = a;
            this.moveState = moveState;
            this.grounded = grounded;
            this.currentSlot = currentSlot;
            this.currentClip = currentClip;
            this.maxClip = maxClip;
            this.maxHp = maxHp;
            this.maxFuel = maxFuel;
            this.airblastCost = airblastCost;
            this.activeCharge = activeCharge;
            
            this.reloading = reloading;
            this.reloadPercent = reloadPercent;
        }
	}
	
	public static class SyncLoadout {
		public String entityId;
		public Loadout loadout;
		public SyncLoadout() {}
		public SyncLoadout(String entityId, Loadout loadout) {
			this.entityId = entityId;
			this.loadout = loadout;
		}
	}
	
	public static class CreateParticles {
		public String entityID;
		public String attachedID;
        public Vector2 pos;
        public boolean attached;
		public String particle;
		public boolean startOn;
		public float linger;
		public float lifespan;
		public CreateParticles() {}
		public CreateParticles(String entityID, String attachedID, Vector2 pos, boolean attached, String particle, boolean startOn,
				float linger, float lifespan) {
			this.entityID = entityID;
			this.attachedID = attachedID;
			this.pos = pos;
			this.attached = attached;
			this.particle = particle;
			this.startOn = startOn;
			this.linger = linger;
			this.lifespan = lifespan;
		}
	}
	
	public static class SyncParticles {
		public String entityID;
        public boolean on;
		public SyncParticles() {}
		public SyncParticles(String entityID, boolean on) {
			this.entityID = entityID;
			this.on = on;
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
    	kryo.register(ClientStartTransition.class);
    	kryo.register(ClientFinishTransition.class);
    	kryo.register(NewClientPlayer.class);
    	kryo.register(CreateEntity.class);
    	kryo.register(DeleteEntity.class);
    	kryo.register(CreateEvent.class);
    	kryo.register(CreatePoison.class);
    	kryo.register(ActivateEvent.class);
    	kryo.register(CreatePlayer.class);
    	kryo.register(SyncLoadout.class);
    	kryo.register(CreateParticles.class);
    	kryo.register(SyncEntity.class);
    	kryo.register(SyncSchmuck.class);
    	kryo.register(SyncPlayer.class);
    	kryo.register(SyncParticles.class);
    }
}
