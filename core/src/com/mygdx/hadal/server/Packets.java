package com.mygdx.hadal.server;

import java.util.HashMap;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.SchmuckMoveStates;
import com.mygdx.hadal.schmucks.bodies.ClientIllusion.alignType;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy.enemyType;
import com.mygdx.hadal.states.ClientState.ObjectSyncLayers;
import com.mygdx.hadal.states.PlayState.transitionState;

/**
 * These are packets sent between the Server and Client.
 * 
 * @author Zachary Tu
 *
 */
public class Packets {

	public static class PlayerConnect {
		public boolean firstTime;
		public String name;
		public PlayerConnect() {}
		
		/**
		 * PlayerConnect is sent from the Client to the Server whenever a Player connects to the world.
		 * Sent when a client first connects to the server, as well as when the client connects to a new world after level transition.
		 * Treat this as Client: "Create a Player for me in the Server's world."
		 * 
		 * @param firstTime: Is this the client's first time? Or is this sent as level transition. Checked when displaying notifications.
		 * @param name: Client's selected name and name of their new Player.
		 */
		public PlayerConnect(boolean firstTime, String name) {
			this.firstTime = firstTime;
			this.name = name;
		}
	}
	
	public static class LoadLevel {
		public UnlockLevel level;
		public boolean firstTime;
		public LoadLevel() {}
		
		/**
		 * A LoadLevel is sent from the Server to the Client to tell the Client to transition to a new level.
		 * This is done when the Server receives a Client's PlayerConnect to tell them what world to load.
		 * It is also done when the Client sends a ClientFinishedTransition packet if the Client should load a new level.
		 * 
		 * @param level: Level that the Client will load.
		 * @param firstTime: Is this the client's first time? Or is this sent as level transition. Checked when displaying notifications.
		 */
		public LoadLevel(UnlockLevel level, boolean firstTime) {
			this.level = level;
			this.firstTime = firstTime;
		}
	}
	
	public static class ClientLoaded {
		public boolean firstTime;
		public String name;
		public Loadout loadout;
		public ClientLoaded() {}
		
		/**
		 * A ClientLoaded is sent from the Client to the Server when the Client finishes initializing their ClientState as a result of
		 * receiving a LoadLevel packet from the Server.
		 * Server receiving this should welcome the new Client and give them the down-low about the world they just entered.
		 * 
		 * @param firstTime: Is this the client's first time? Or is this sent as level transition. Checked when displaying notifications.
		 */
		public ClientLoaded(boolean firstTime, String name, Loadout loadout) {
			this.firstTime = firstTime;
			this.name = name;
			this.loadout = loadout;
		}
	}
	
	public static class ClientPlayerCreated {
		
		/**
		 * ClientPlayerCreated is sent from the Client to the server when they create their own player. This prompts the server to sync all fields that require a player to already have been created.
		 * At the moment, this just includes the client's loadout
		 */
		public ClientPlayerCreated() {}
	}
	
	public static class NewClientPlayer {
		public String yourId;
		public NewClientPlayer() {}
		
		/**
		 * A New ClientPlayer is sent from the Server to the Client whenever the Server initializes a new Player for that Client.
		 * This packet is only sent to the corresponding client. Not all clients.
		 * This packet tells the Client the new Player's ID. 
		 * Clients store this ID so that when the Player is actually created in the PlayState, they know its themselves and can sync accordingly.
		 * 
		 * @param yourId: entityId of the newly created Player.
		 */
		public NewClientPlayer(String yourId) {
			this.yourId = yourId;
		}
	}
	
	public static class SyncClientLoadout {

		public UnlockEquip equip;
		public UnlockArtifact artifactAdd;
		public UnlockArtifact artifactRemove;
		public UnlockActives active;
		public UnlockCharacter character;
		
		public SyncClientLoadout() {}
		
		/**
		 * A SyncClientWeapon is sent from the Client to the Server when the client attempts to change their loadout in the hub.
		 * 
		 * @param entityId: ID of the player to change
		 * @param loadout: Player's new loadout
		 */
		public SyncClientLoadout(UnlockEquip equip, UnlockArtifact artifactAdd, UnlockArtifact artifactRemove, UnlockActives active, UnlockCharacter character) {
			this.equip = equip;
			this.artifactAdd = artifactAdd;
			this.artifactRemove= artifactRemove;
			this.active = active;
			this.character = character;
		}
	}
	
	public static class ClientStartTransition {
		public transitionState state;
		public boolean override;
		public String resultsText;
		public ClientStartTransition() {}
		
		/**
		 * A ClientStartTransition is sent from the Server to the Client upon beginning a transition to another state to tell the Client
		 * to transition as well.
		 * Clients receiving this begin fading to black the same way the Server does.
		 * @param state: Are we transitioning to a new level, a gameover screen or whatever else?
		 */
		public ClientStartTransition(transitionState state, boolean override, String resultsText) {
			this.state = state;
			this.override = override;
			this.resultsText = resultsText;
		}
	}
	
	public static class ClientFinishRespawn {
		/**
		 * A ClientFinishRespawn is sent from the Client to the Server upon finishing their fade-to-black after receiving a ClientStartTransition Packet.
		 * 
		 * If the client is respawning, the server creates the new client player upon receiving this message.
		 * 		 
		 */
		public ClientFinishRespawn() {}
	}
	
	public static class ServerLoaded {
		
		/**
		 * ServerLoaded is sent from the Server to the Client whenever the server finishes loading its Playstate (On first engine tick)
		 * This is used by a connected Client so they know when to send their PlayerConnect.
		 * This is intended to deal with the Client loading before the Server.
		 */
		public ServerLoaded() {}
	}
	
	public static class Paused {
		public String pauser;
		public Paused() {}
		
		/**
		 * Paused is sent from the Server to the Client to indicate that the game has been paused.
		 * Clients never send this to Server, because "their pauses" are carried out by the Player they control in the Server's world.
		 * @param pauser: This is the name of the Player who paused.
		 */
		public Paused(String pauser) {
			this.pauser = pauser;
		}
	}
	
	public static class Unpaused {
		public String unpauser;
		public Unpaused() {}
		
		/**
		 * Unpaused is sent from Clients to Server and vice-versa whenever any Player unpauses.
		 * This is different from Paused, because Clients unpause in their own worlds and must send a packet to the Server when they do so.
		 * If the Server unpauses, or receives an Unpause from any Client, it sends an Unpaused to all clients.
		 * Clients receiving an Unpause simply unpause their games.
		 * Note that stuff like Players connecting/disconnecting during an unpause will all occur at once after unpausing.
		 * @param unpauser: This is the name of the Player who unpaused. Not using it yet, but maybe eventually.
		 */
		public Unpaused(String unpauser) {
			this.unpauser = unpauser;
		}
	}
	
	public static class Notification {
		public String name;
		public String text;
		public Notification() {}
		
		/**
		 * A Notification is sent from the Server to the Client to make a notification window appear in their dialog box.
		 * A Notification is sent from the Client to the Server to tell it to relay the message to all clients.
		 * @param name: The name that will be displayed in the notification
		 * @param text: The text displayed in the notification
		 */
		public Notification(String name, String text) {
			this.name = name;
			this.text = text;
		}
	}
	
	public static class ClientReady {
		public int playerId;
		public ClientReady() {}
		
		/**
		 * This is sent from the client to the server at the results screen to indicate the client is ready to return.
		 * @param: the id of the client who is ready
		 */
		public ClientReady(int playerId) {
			this.playerId = playerId;
		}
	}
	
	public static class KeyDown {
		public PlayerAction action;
		public KeyDown() {}
		
		/**
		 * A KeyDown is sent from the Client to the Server whenever they press a key down that results in some action being taken.
		 * The Server takes these actions and makes the client's Player execute them.
		 * @param a: The action taken by the client.
		 */
		public KeyDown(PlayerAction a) {
			this. action = a;
		}
	}
	
	public static class KeyUp {
		public PlayerAction action;
		public KeyUp() {}
		
		/**
		 * A KeyDown is sent from the Client to the Server whenever they release a key that results in some action being taken.
		 * The Server takes these actions and makes the client's Player execute them.
		 * 
		 * @param a: The action taken by the client.
		 */
		public KeyUp(PlayerAction a) {
			this.action = a;
		}
	}
	
	public static class MouseMove {
		public float x, y;
		public MouseMove() {}
		
		/**
		 * A MouseMove is sent from the Client to the Server every engine tick to update location of their mouse.
		 * The Server uses these packets to synchronize each player's mouse pointer so sprites point weapons right directions.
		 * 
		 * @param x: X position of the client's mouse.
		 * @param y: Y position of the client's mouse.
		 */
		public MouseMove(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}
	
	public static class SyncScore {
		public HashMap<Integer, SavedPlayerFields> scores;
		public SyncScore() {}
		
		/**
		 * This is sent from the server to the clients to give them their scores for all players
		 * @param score: mapping of each players connId to their score
		 */
		public SyncScore(HashMap<Integer, SavedPlayerFields> scores) {
			this.scores = scores;
		}
	}
	
	public static class CreateEntity {
		public String entityID;
		public Vector2 pos;
        public Vector2 size;
        public Sprite sprite;
        public ObjectSyncLayers layer;
        public alignType align;
		public CreateEntity() {}
		
		/**
		 * A CreateEntity is sent from the Server to the Client to tell the Client to create a new Entity.
		 * 
		 * @param entityID: ID of the new entity
		 * @param size: Size of the new entity
		 * @param pos: position of the new entity
		 * @param sprite: entity's sprite
		 * @param layer: Hitbox or Standard layer? (Hitboxes are rendered underneath other entities)
		 * @param align: The new object's align type. Used to determine how the client illusioin should be rendered
		 */
		public CreateEntity(String entityID, Vector2 size, Vector2 pos, Sprite sprite, ObjectSyncLayers layer, alignType align) {
			this.entityID = entityID;
			this.pos = pos;
            this.size = size;
            this.sprite = sprite;
            this.layer = layer;
            this.align = align;
        }
	}
	
	public static class CreateEnemy {
		public String entityID;
		public enemyType type;
		public boolean boss;
		public String name;
		public CreateEnemy() {}
		
		/**
		 * A CreateEnemy is sent from the Server to the Client to tell the Client to create a new Enemy.
		 * @param entityID: ID of the new Enemy.
		 * @param type: Enemy Type
		 * @param boss: is this a boss enemy?
		 * @param name: if a boss, what name shows up in the ui?
		 */
		public CreateEnemy(String entityID, enemyType type, boolean boss, String name) {
            this.entityID = entityID;
            this.type = type;
            this.boss = boss;
            this.name = name;
        }
	}
	
	public static class DeleteEntity {
		public String entityID;
		public DeleteEntity() {}
		
		/**
		 * A Delete Entity is sent from the Server to the Client to tell the Client to delete an Entity.
		 * @param entityID: ID of the entity to be deleted.
		 */
		public DeleteEntity(String entityID) {
			this.entityID = entityID;
        }
	}
	
	public static class CreatePlayer {
		public String entityID;
		public String name;
		public Loadout loadout;
		public CreatePlayer() {}
		
		/**
		 * A CreatePlayer is sent fro mthe Server to the Client to tell the client to create a new Player.
		 * Clients should create this new player, unless it is themselves, in which case they should synchronize it.
		 * This is because the Client reuses the same ClientState.getPlayer() which they already created.
		 * 
		 * @param entityID: ID of the new Player
		 * @param name: name of the new Player
		 * @param loadout: loadout of the new Player
		 */
		public CreatePlayer(String entityID, String name, Loadout loadout) {
            this.entityID = entityID;
            this.name = name;
            this.loadout = loadout;
        }
	}
	
	public static class CreateEvent {
		public String entityID;
        public MapObject blueprint;
		public CreateEvent() {}
		
		/**
		 * A CreateEvent is sent from the Server to the Client to tell the client to create a new Event.
		 * These events are the ones parsed from Tiled Map.
		 * 
		 * @param entityID: ID of the new event
		 * @param blueprint: MapObject of the event to be parsed in the TiledObjectUtils
		 */
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
		
		/**
		 * A CreatePoison is sent from the Server to the Client to tell the client to create a new Poison Event.
		 * This is for dynamically created Poison that doesn't have a Blueprint.
		 * 
		 * @param entityID: ID of the new Poison.
		 * @param pos: position of the new poison
		 * @param size: size of the new poison
		 * @param draw: Whether the new poison should be drawn or not.
		 */
		public CreatePoison(String entityID, Vector2 pos, Vector2 size, boolean draw) {
            this.entityID = entityID;
            this.pos = pos;
            this.size = size;
            this.draw = draw;
        }
	}
	
	public static class CreatePickup {
		public String entityID;
        public Vector2 pos;
        public CreatePickup() {}
        
        /**
         * A CreatePickup is sent from the Server to the Client to tell the client to create a new Pickup Event.
		 * This is for Pickups because they have some custom logic to synchronize what drop they represent.
		 * 
		 * @param entityID: ID of the new Pickup.
		 * @param pos: position of the new Pickup
         */
		public CreatePickup(String entityID, Vector2 pos) {
			this.entityID = entityID;
            this.pos = pos;
		}
	}
	
	public static class SyncEntity {
		public String entityID;
        public Vector2 pos;
        public float angle;
		public SyncEntity() {}
		
		/**
		 * A SyncEntity is sent from the Server to the Client for every synchronized entity every engine tick.
		 * This packet (and similar packets) just tell the client how to change their version of the entity.
		 * Most basic version just transforms the entity's body.
		 * 
		 * @param entityID: ID of the entity to synchronize
		 * @param pos: position of the entity
		 * @param a: body angle of the new entity.
		 */
		public SyncEntity(String entityID, Vector2 pos, float a) {
            this.entityID = entityID;
            this.pos = pos;
            this.angle = a;
        }
	}
	
	public static class SyncPickup {
		public String entityID;
        public String newPickup;
        public SyncPickup() {}
        
        /**
         * A SyncPickup is sent from the Server to the Client when a Pickup is activated.
         * Clients receiving this adjust their version of the pickup to hold the new pickup
         * 
         * @param entityID: ID of the activated Pickup
         * @param newPickup: enum name of the new pickup.
         */
		public SyncPickup(String entityID, String newPickup) {
			this.entityID = entityID;
            this.newPickup = newPickup;
		}
	}
	
	public static class ActivateEvent {
		public String entityID;
		public ActivateEvent() {}
		
		/**
		 * A ActivateEvent is sent from the Server to the Client when an Event is activated (For synchronized events).
         * This is used for events that are activated on the Client's end as well. (CameraChangers, Hub Events ... etc)
         * 
		 * @param entityID: ID of the activated Pickup
		 */
		public ActivateEvent(String entityID) {
            this.entityID = entityID;
        }
	}
	
	public static class SyncSchmuck {
		public String entityID;
		public SchmuckMoveStates moveState;
        public float flashDuration;

		public SyncSchmuck() {}
		
		/**
		 * A SyncSchmuck is sent from the Server to the Client for every synchronized schmuck every engine tick.
		 * This packet (and similar packets) just tell the client how to change their version of the schmuck.
		 * This adjusts the Schmuck's stats and visual information
		 * 
		 * @param entityID: ID of the Schmuck to be synced
		 * @param moveState: The State of the Schmuck. Used for animations on the Client's end
		 * @param flashDuration: Is the Schmuck flashing? Used so schmuck's flash upon damage for Clients.
		 */
		public SyncSchmuck(String entityID, SchmuckMoveStates moveState, float flashDuration) {
			this.entityID = entityID;
			this.moveState = moveState;
			this.flashDuration = flashDuration;
		}
	}
	
	public static class SyncBoss {
        public float hpPercent;

		public SyncBoss() {}
		
		/**
		 * A SyncBoss is sent from the Server to the Client by the boss every engine tick.
		 * atm, this just accounts for the boss' hp to be displayed in the client's ui
		 */
		public SyncBoss(float hpPercent) {
			this.hpPercent = hpPercent;
		}
	}
	
	public static class SyncPlayerSelf {

        public float fuelPercent;
        public int currentClip;
        public int currentAmmo;
        public float activeCharge;
        
		public SyncPlayerSelf() {}
		
		/**
		 * A SyncPlayer is sent from the Server to the Client every engine tick.
		 * This packet (and similar packets) just tells the client how to change their own Player for ther purpose of their own ui.
		 */
		public SyncPlayerSelf(float fuelPercent, int currentClip, int currentAmmo, float activeCharge) {
            this.fuelPercent = fuelPercent;
            this.currentClip = currentClip;
            this.currentAmmo = currentAmmo;
            this.activeCharge = activeCharge;
        }
	}
	
	public static class SyncPlayerAll {
		public String entityID;
        public float attackAngle;
        public float hpPercent;
        public SchmuckMoveStates moveState;
        public boolean grounded;
        public int currentSlot;
        public boolean reloading;
        public float reloadPercent;
        public boolean charging;
        public float chargePercent;
        
		public SyncPlayerAll() {}
		
		/**
		 * A SyncPlayerAll is sent from the Server to the Client for every synchronized Player every engine tick.
		 * This packet (and similar packets) just tell the client how to change their version of each Player.
		 * This long list of fields is just the Player-specific information needed for Clients to properly render other players.
		 */
		public SyncPlayerAll(String entityID, float a, float hpPercent, Boolean grounded, int currentSlot, boolean reloading, float reloadPercent, boolean charging, float chargePercent) {
            this.entityID = entityID;
            this.attackAngle = a;
            this.hpPercent = hpPercent;
            this.grounded = grounded;
            this.currentSlot = currentSlot;
            
            this.reloading = reloading;
            this.reloadPercent = reloadPercent;
            this.charging = charging;
            this.chargePercent = chargePercent;
        }
	}
	
	public static class SyncPlayerStats {
		public int maxClip;
        public float maxHp;
        public float maxFuel;
        public float airblastCost;
        public int weaponSlots;
        public int artifactSlots;
        public SyncPlayerStats() {}
        
        /**
         * 
         * @param maxClip
         * @param maxHp
         * @param maxFuel
         * @param airblastCost
         * @param weaponSlots
         */
        public SyncPlayerStats(int maxClip, float maxHp, float maxFuel, float airblastCost, int weaponSlots, int artifactSlots) {
        	 this.maxClip = maxClip;
             this.maxHp = maxHp;
             this.maxFuel = maxFuel;
             this.airblastCost = airblastCost;
             this.weaponSlots = weaponSlots;
             this.artifactSlots = artifactSlots;
        }
	}
	
	public static class SyncServerLoadout {
		public String entityID;
		public Loadout loadout;
		public SyncServerLoadout() {}
		
		/**
		 * A SyncLoadout is sent from the Server to the Client when any Player in the world changes their loadout.
		 * Upon receiving this packet, clients adjust their versions of that Player to have the new loadout.
		 * 
		 * 
		 * @param entityId: ID of the player to change
		 * @param loadout: Player's new loadout
		 */
		public SyncServerLoadout(String entityId, Loadout loadout) {
			this.entityID = entityId;
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
		
		/**
		 * A CreateParticles is sent from the Server to the Client whenever a synced ParticlesEntity is created.
		 * Clients simply create the desired particle entity with all of the listed fields.
		 * Attached information is useful for making most particles sync on create, instead of every engine tick.(unless needed)
		 * 
		 * @param entityID: ID of the newly created ParticlesEntity
		 * @param attachedID: ID of attached entity if it exists and null otherwise
		 * @param pos: Position of particle entity if not attached to another entity
		 * @param attached: Is this particleEntity attached to another entity?
		 * @param particle: Particle Effect to be created.
		 * @param startOn: Does this effect start turned on?
		 * @param linger: How long does an attached Particleentity persist after its attached entity dies?
		 * @param lifespan: Duration of a non-attached entity.
		 */
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
		public Vector2 pos;
        public boolean on;
		public SyncParticles() {}
		
		/**
		 * A SyncParticles is sent from the Server to the Client every engine tick for every ParticleEntity of the TICKSYNC type.
		 * Particles of this nature are dynamically turned on and off in the Server, thus needing this packet.
		 * 
		 * @param entityID: ID of the Particle Effect to turn on/off
		 * @param pos: position of the synced particle effect
		 * @param on: Is the Server's version of this effect on or off?
		 */
		public SyncParticles(String entityID, Vector2 pos, boolean on) {
			this.entityID = entityID;
			this.pos = pos;
			this.on = on;
		}
	}
	
	public static class SyncCamera {
		public Vector2 zoomPos;
		public float zoom;
		public float[] cameraBounds;
		public boolean[] cameraBounded;
		
		public SyncCamera() {}
		
		/**
		 * A SyncCamera is sent from the Server to the Client when the client respawns.
		 * This is done incase the client's camera has changed target/zoom before dying and needs to respaen with a different target/zoom matching their spawn location.
		 * @param zoomPos: The location that the client's camera should focus on. Focus on self if null
		 * @param zoom: How much should the client's camera be zoomed in
		 * @param cameraBounds: what are the camera bounds? [right, left, up, down]
		 * @param cameraBounded: does the camera obey each aformentioned bound?
		 */
		public SyncCamera(Vector2 zoomPos, float zoom, float[] cameraBounds, boolean[] cameraBounded) {
			this.zoomPos = zoomPos;
			this.zoom = zoom;
			this.cameraBounds = cameraBounds;
			this.cameraBounded = cameraBounded;
		}
	}
	
	public static class SyncUI {
		public String uiTags;
		public float timer;
		public float timerIncr;
		public SyncUI() {}
		
		/**
		 * A SyncUI is sent from the Server to the Client whenever the ui is updated.
		 * The client updates their ui to represent the changes.
		 * @param uiTags: list of ui elements to add
		 * @param timer: what to set the global game timer to
		 * @param timerIncr: How much should the timer be incrementing by (probably +-1 or 0)
		 */
		public SyncUI(String uiTags, float timer, float timerIncr) {
			this.uiTags = uiTags;
			this.timer = timer;
			this.timerIncr = timerIncr;
		}
	}
	
	/**
     * REGISTER ALL THE CLASSES FOR KRYO TO SERIALIZE AND SEND
     * @param kryo The kryo object
     */
    public static void allPackets(Kryo kryo) {
    	kryo.register(PlayerConnect.class);
    	kryo.register(ServerLoaded.class);
    	kryo.register(Paused.class);
    	kryo.register(Unpaused.class);
    	kryo.register(Notification.class);
    	kryo.register(ClientReady.class);
    	kryo.register(KeyDown.class);
    	kryo.register(KeyUp.class);
    	kryo.register(MouseMove.class);
    	kryo.register(LoadLevel.class);
    	kryo.register(ClientLoaded.class);
    	kryo.register(ClientPlayerCreated.class);
    	kryo.register(ClientStartTransition.class);
    	kryo.register(ClientFinishRespawn.class);
    	kryo.register(NewClientPlayer.class);
    	kryo.register(SyncScore.class);
    	kryo.register(CreateEntity.class);
    	kryo.register(CreateEnemy.class);
    	kryo.register(DeleteEntity.class);
    	kryo.register(CreateEvent.class);
    	kryo.register(CreatePoison.class);
    	kryo.register(CreatePickup.class);
    	kryo.register(SyncPickup.class);
    	kryo.register(ActivateEvent.class);
    	kryo.register(CreatePlayer.class);
    	kryo.register(SyncServerLoadout.class);
    	kryo.register(SyncClientLoadout.class);
    	kryo.register(CreateParticles.class);
    	kryo.register(SyncEntity.class);
    	kryo.register(SyncSchmuck.class);
    	kryo.register(SyncPlayerSelf.class);
    	kryo.register(SyncPlayerAll.class);
    	kryo.register(SyncParticles.class);
    	kryo.register(SyncCamera.class);
    	kryo.register(SyncBoss.class);
    	kryo.register(SyncUI.class);
    }
}
