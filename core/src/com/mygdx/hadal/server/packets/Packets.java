package com.mygdx.hadal.server.packets;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.PlayerSpriteHelper.DespawnType;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.*;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.schmucks.bodies.ClientIllusion.alignType;
import com.mygdx.hadal.schmucks.bodies.enemies.EnemyType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.SyncedAttack;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.EventDto;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.server.SavedPlayerFieldsExtra;
import com.mygdx.hadal.server.User.UserDto;
import com.mygdx.hadal.states.ClientState.ObjectSyncLayers;
import com.mygdx.hadal.states.PlayState.TransitionState;
import com.mygdx.hadal.statuses.DamageTypes;

import java.util.HashMap;
import java.util.UUID;

/**
 * These are packets sent between the Server and Client.
 * @author Wogganov Wrarnando
 */
public class Packets {

	public static class ConnectReject {
		public String msg;
		public ConnectReject() {}
		
		/**
		 * ConnectReject is sent from the Server to the Client to reject a connection.
		 * This is done when the server is full, or if the server is in the middle of a game.
		 * @param msg: message to be displayed by the client
		 */
		public ConnectReject(String msg) {
			this.msg = msg;
		}
	}

	public static class PasswordRequest {
		/**
		 * PasswordRequest is sent from the Server to the Client when the client attempts to connect if the server has a pw
		 */
		public PasswordRequest() {}
	}

	public static class PlayerConnect {
		public boolean firstTime;
		public String name;
		public String version;
		public String password;
		public PlayerConnect() {}
		
		/**
		 * PlayerConnect is sent from the Client to the Server whenever a Player connects to the world.
		 * Sent when a client first connects to the server, as well as when the client connects to a new world after level transition.
		 * Treat this as Client: "Create a Player for me in the Server's world."
		 * @param firstTime: Is this the client's first time? Or is this sent as level transition. Checked when displaying notifications.
		 * @param name: Client's selected name and name of their new Player.
		 * @param version: the version of the game to make sure we are compatible with the host.
		 * @param password: if the client is entering a password, this is it (otherwise null)
		 */
		public PlayerConnect(boolean firstTime, String name, String version, String password) {
			this.firstTime = firstTime;
			this.name = name;
			this.version = version;
			this.password = password;
		}
	}
	
	public static class LoadLevel {
		public UnlockLevel level;
		public GameMode mode;
		public HashMap<String, Integer> modeSettings;
		public boolean firstTime;
		public boolean spectator;
		public LoadLevel() {}
		
		/**
		 * A LoadLevel is sent from the Server to the Client to tell the Client to transition to a new level.
		 * This is done when the Server receives a Client's PlayerConnect to tell them what world to load.
		 * It is also done when the Client sends a ClientFinishedTransition packet if the Client should load a new level.
		 * @param level: Level that the Client will load.
		 * @param mode: mode that the Client will load.
		 * @param modeSettings: Server mode settings for the level to load. Used for client processing of things like ui
		 * @param firstTime: Is this the client's first time? Or is this sent as level transition. Checked when displaying notifications.
		 * @param spectator: Is this client connecting as a spectator?
		 */
		public LoadLevel(UnlockLevel level, GameMode mode, HashMap<String, Integer> modeSettings, boolean firstTime, boolean spectator) {
			this.level = level;
			this.mode = mode;
			this.modeSettings = modeSettings;
			this.firstTime = firstTime;
			this.spectator = spectator;
		}
	}
	
	public static class ClientLoaded {
		public boolean firstTime;
		public boolean lastSpectator;
		public boolean spectator;
		public String name;
		public Loadout loadout;
		public ClientLoaded() {}
		
		/**
		 * A ClientLoaded is sent from the Client to the Server when the Client finishes initializing their ClientState as a result of
		 * receiving a LoadLevel packet from the Server.
		 * Server receiving this should welcome the new Client and give them the down-low about the world they just entered.
		 * @param firstTime: Is this the client's first time? Or is this sent as level transition. Checked when displaying notifications.
		 * @param lastSpectator: was this client previously a spectator? If so, they will still be a spectator (unless transitioning to a hub)
		 * @param spectator: Is this client being forced to be a spectator? (by joining mid-session or full server)
		 * @param name: The client's name
		 * @param loadout: the client's starting loadout
		 */
		public ClientLoaded(boolean firstTime, boolean lastSpectator, boolean spectator, String name, Loadout loadout) {
			this.firstTime = firstTime;
			this.lastSpectator = lastSpectator;
			this.spectator = spectator;
			this.name = name;
			this.loadout = loadout;
		}
	}
	
	public static class ClientPlayerCreated {
		
		/**
		 * ClientPlayerCreated is sent from the Client to the server when they create their own player.
		 * This prompts the server to sync all fields that require a player to already have been created.
		 * At the moment, this just includes the client's loadout
		 */
		public ClientPlayerCreated() {}
	}
	
	public static class ClientStartTransition {
		public TransitionState state;
		public float fadeSpeed;
		public float fadeDelay;
		public ClientStartTransition() {}
		
		/**
		 * A ClientStartTransition is sent from the Server to the Client upon beginning a transition to another state to tell the Client
		 * to transition as well.
		 * Clients receiving this begin fading to black the same way the Server does.
		 * @param state: Are we transitioning to a new level, a gameover screen or whatever else?
		 * @param fadeSpeed: speed of the fade transition
		 * @param fadeDelay: Amount of delay before transition
		 */
		public ClientStartTransition(TransitionState state, float fadeSpeed, float fadeDelay) {
			this.state = state;
			this.fadeSpeed = fadeSpeed;
			this.fadeDelay = fadeDelay;
		}
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
		 * Clients send this to the server is multiplayer is enabled.
		 * @param pauser: This is the name of the Player who paused.
		 */
		public Paused(String pauser) {
			this.pauser = pauser;
		}
	}
	
	public static class Unpaused {

		/**
		 * Unpaused is sent from Clients to Server and vice-versa whenever any Player unpauses.
		 * This is different from Paused, because Clients unpause in their own worlds and must send a packet to the Server when they do so.
		 * If the Server unpauses, or receives an Unpause from any Client, it sends an Unpaused to all clients.
		 * Clients receiving an Unpause simply unpause their games.
		 * Note that stuff like Players connecting/disconnecting during an unpause will all occur at once after unpausing.
		 */
		public Unpaused() {}
	}
	
	public static class ServerNotification {
		public String name;
		public String text;
		public DialogType type;
		public ServerNotification() {}
		
		/**
		 * A ServerNotification is sent from the Server to the Client to make a notification window appear in their dialog box.
		 * A Notification is sent from the Client to the Server to tell it to relay the message to all clients.
		 * @param name: The name that will be displayed in the notification
		 * @param text: The text displayed in the notification
		 * @param type: type of dialog (dialog, system msg, etc)
		 */
		public ServerNotification(String name, String text, DialogType type) {
			this.name = name;
			this.text = text;
			this.type = type;
		}
	}

	public static class ServerChat {
		public String text;
		public DialogType type;
		public int connID;
		public ServerChat() {}

		/**
		 * A ServerChat is sent from the Server to the Client to make a text appear in message window.
		 * A Notification is sent from the Client to the Server to tell it to relay the message to all clients.
		 * @param text: The text displayed in the notification
		 * @param type: type of dialog (dialog, system msg, etc)
		 * @param connID: user that sent the chat
		 */
		public ServerChat(String text, DialogType type, int connID) {
			this.text = text;
			this.type = type;
			this.connID = connID;
		}
	}

	public static class ClientChat {
		public String text;
		public DialogType type;
		public ClientChat() {}

		/**
		 * A ClientChat is sent from the Client to the Server to tell it to relay the message to all clients.
		 * @param text: The text displayed in the notification
		 * @param type: type of dialog (dialog, system msg, etc)
		 */
		public ClientChat(String text, DialogType type) {
			this.text = text;
			this.type = type;
		}
	}

	public static class ClientReady {
		public int playerID;
		public ClientReady() {}
		
		/**
		 * This is sent from the server to all clients to indicate that a client is ready to return to the hub
 		 * @param playerID: the id of the client who is ready
		 */
		public ClientReady(int playerID) {
			this.playerID = playerID;
		}
	}
	
	public static class SyncKeyStrokes {
		public float mouseX, mouseY, playerX, playerY;
		public PlayerAction[] actions;
		public float timestamp;

		public SyncKeyStrokes() {}

		/**
		 * This is sent from thte client to the server periodically to indicate what keys are currently held
		 * This also includes mouse information
		 * @param mouseX: X-coordinate of client's mouse
		 * @param mouseY: Y-coordinate of client's mouse
		 * @param playerX: X-coordinate of client's player (to the client)
		 * @param playerY: Y-coordinate of client's player (to the client)
		 * @param actions: Array of actions whose button is currently held
		 * @param timestamp: Time that this input snapshot was sent
		 */
		public SyncKeyStrokes(float mouseX, float mouseY, float playerX, float playerY, PlayerAction[] actions, float timestamp) {
			this.mouseX = mouseX;
			this.mouseY = mouseY;
			this.playerX = playerX;
			this.playerY = playerY;
			this.actions = actions;
			this.timestamp = timestamp;
		}
	}
	
	public static class SyncScore {
		public int connID;
		public String name;
		public int wins, kills, deaths, score, lives, ping;
		public boolean spectator;

		public SyncScore() {}
		
		/**
		 * This is sent from the server to the clients to give them their scores for a player whose score changed
		 * @param connID: id of the player whose score is being updated.
		 */
		public SyncScore(int connID, String name, int wins, int kills, int deaths, int score, int lives, int ping, boolean spectator) {
			this.connID = connID;
			this.name = name;
			this.wins = wins;
			this.kills = kills;
			this.deaths = deaths;
			this.score = score;
			this.lives = lives;
			this.ping = ping;
			this.spectator = spectator;
		}
	}

	public static class CreateEntity {
		public long uuidMSB, uuidLSB;
		public Vector2 size;
		public Vector2 pos;
		public float angle;
        public Sprite sprite;
		public boolean synced;
		public boolean instant;
        public ObjectSyncLayers layer;
        public alignType align;
		public CreateEntity() {}
		
		/**
		 * A CreateEntity is sent from the Server to the Client to tell the Client to create a new Entity.
		 * @param entityID: ID of the new entity
		 * @param size: Size of the new entity
		 * @param pos: position of the new entity
		 * @param angle: starting angle of the new entity
		 * @param sprite: entity's sprite
		 * @param synced: should this entity receive a sync packet regularly?
		 * @param instant: should this entity copy the server location instantly?
		 * @param layer: Hitbox or Standard layer? (Hitboxes are rendered underneath other entities)
		 * @param align: The new object's align type. Used to determine how the client illusion should be rendered
		 */
		public CreateEntity(UUID entityID, Vector2 size, Vector2 pos, float angle, Sprite sprite, boolean synced, boolean instant,
							ObjectSyncLayers layer, alignType align) {
			this.uuidLSB = entityID.getLeastSignificantBits();
			this.uuidMSB = entityID.getMostSignificantBits();
			this.pos = pos;
			this.angle = angle;
            this.size = size;
            this.sprite = sprite;
            this.synced = synced;
            this.instant = instant;
            this.layer = layer;
            this.align = align;
        }
	}

	public static class CreateSyncedAttackSingle {
		public long uuidMSB, uuidLSB;
		public long uuidMSBCreator, uuidLSBCreator;
		public Vector2 pos, velo;
		public SyncedAttack attack;

		public CreateSyncedAttackSingle() {}

		/**
		 * A CreateSyncedAttackSingle is sent from server to client to inform them that an attack was executed
		 * For most weapons, this packages multiple fields of a single attack to send fewer packets
		 * @param entityID: The entityID of the hitbox this attack will create
		 * @param creatorID: The entityID of the player that is executing the attack
		 * @param pos: The starting position of the hbox this attack will create
		 * @param velo: The starting velocity/trajectory of the hbox this attack will create
		 * @param attack: the type of attack that is being executed
		 */
		public CreateSyncedAttackSingle(UUID entityID, UUID creatorID, Vector2 pos, Vector2 velo, SyncedAttack attack) {
			this.uuidLSB = entityID.getLeastSignificantBits();
			this.uuidMSB = entityID.getMostSignificantBits();
			this.uuidLSBCreator = creatorID.getLeastSignificantBits();
			this.uuidMSBCreator = creatorID.getMostSignificantBits();
			this.pos = pos;
			this.velo = velo;
			this.attack = attack;
		}
	}

	public static class CreateSyncedAttackSingleExtra extends CreateSyncedAttackSingle {
		public float[] extraFields;

		public CreateSyncedAttackSingleExtra() {}

		/**
		 * A CreateSyncedAttackSingleExtra is like a CreateSyncedAttackSingle except it carries extra information
		 * so the client can process things like charge levels
		 * @param extraFields: extra information needed to execute this specific attack
		 */
		public CreateSyncedAttackSingleExtra(UUID entityID, UUID creatorID, Vector2 pos, Vector2 velo, float[] extraFields, SyncedAttack attack) {
			super(entityID, creatorID, pos, velo, attack);
			this.extraFields = extraFields;
		}
	}

	public static class CreateSyncedAttackMulti {
		public long[] uuidMSB, uuidLSB;
		public long uuidMSBCreator, uuidLSBCreator;
		public Vector2[] pos, velo;
		public SyncedAttack attack;

		public CreateSyncedAttackMulti() {}

		/**
		 * A CreateSyncedAttackMulti is like a CreateSyncedAttackSingle except it executes an attack that creates several
		 * hitboxes like the flounderbuss or party popper.
		 * @param entityID: The entityID of the hitbox this attack will create
		 * @param creatorID: The entityID of the player that is executing the attack
		 * @param pos: The starting positions of the hboxes this attack will create
		 * @param velo: The starting velocities/trajectories of the hboxes this attack will create
		 * @param attack: the type of attack that is being executed
		 */
		public CreateSyncedAttackMulti(UUID[] entityID, UUID creatorID, Vector2[] pos, Vector2[] velo, SyncedAttack attack) {
			this.uuidLSB = new long[entityID.length];
			this.uuidMSB = new long[entityID.length];
			for (int i = 0; i < entityID.length; i++) {
				uuidLSB[i] = entityID[i].getLeastSignificantBits();
				uuidMSB[i] = entityID[i].getMostSignificantBits();
			}
			this.uuidLSBCreator = creatorID.getLeastSignificantBits();
			this.uuidMSBCreator = creatorID.getMostSignificantBits();
			this.pos = pos;
			this.velo = velo;
			this.attack = attack;
		}
	}

	public static class CreateSyncedAttackMultiExtra extends CreateSyncedAttackMulti {
		public float[] extraFields;

		public CreateSyncedAttackMultiExtra() {}

		/**
		 * A CreateSyncedAttackMultiExtra is like a CreateSyncedAttackMulti except it carries extra information
		 * so thee client can process things like charge levels
		 * @param extraFields: extra information needed to execute this specific attack
		 */
		public CreateSyncedAttackMultiExtra(UUID[] entityID, UUID creatorID, Vector2[] pos, Vector2[] velo, float[] extraFields, SyncedAttack attack) {
			super(entityID, creatorID, pos, velo, attack);
			this.extraFields = extraFields;
		}
	}

	public static class CreateEnemy {
		public long uuidMSB, uuidLSB;
		public Vector2 pos;
		public EnemyType type;
		public short hitboxFilter;
		public boolean boss;
		public String name;
		public CreateEnemy() {}
		
		/**
		 * A CreateEnemy is sent from the Server to the Client to tell the Client to create a new Enemy.
		 * @param entityID: ID of the new Enemy.
		 * @param pos: position of the enemy
		 * @param type: Enemy Type
		 * @param hitboxFilter: Enemy team filter. Used because some "enemies" may be player summons
		 * @param boss: is this a boss enemy?
		 * @param name: if a boss, what name shows up in the ui?
		 */
		public CreateEnemy(UUID entityID, Vector2 pos, EnemyType type, short hitboxFilter, boolean boss, String name) {
			this.uuidLSB = entityID.getLeastSignificantBits();
			this.uuidMSB = entityID.getMostSignificantBits();
            this.pos = pos;
            this.type = type;
            this.hitboxFilter = hitboxFilter;
            this.boss = boss;
            this.name = name;
        }
	}
	
	public static class DeleteEntity {
		public long uuidMSB, uuidLSB;
		public float timestamp;
		public DeleteEntity() {}
		
		/**
		 * A DeleteEntity is sent from the Server to the Client to tell the Client to delete an Entity.
		 * @param entityID: ID of the entity to be deleted.
		 * @param timestamp: when this deletion occurred. Used to handle the possibility of packet loss.
		 */
		public DeleteEntity(UUID entityID, float timestamp) {
			this.uuidLSB = entityID.getLeastSignificantBits();
			this.uuidMSB = entityID.getMostSignificantBits();
			this.timestamp = timestamp;
        }
	}

	public static class DeletePlayer {
		public long uuidMSB, uuidLSB;
		public float timestamp;
		public DespawnType type;
		public DeletePlayer() {}

		/**
		 * A DeletePlayer is sent from the Server to the Client to tell the Client to delete a player.
		 * This is separate from delete entity to pass along info about the type of death.
		 * @param entityID: ID of the entity to be deleted.
		 * @param timestamp: when this deletion occurred. Used to handle the possibility of packet loss.
		 * @param type: type of deletion for animation purpose (death, disconnect teleport etc)
		 */
		public DeletePlayer(UUID entityID, float timestamp, DespawnType type) {
			this.uuidLSB = entityID.getLeastSignificantBits();
			this.uuidMSB = entityID.getMostSignificantBits();
			this.timestamp = timestamp;
			this.type = type;
		}
	}

	public static class CreatePlayer {
		public long uuidMSB, uuidLSB;
		public int connID;
		public Vector2 startPosition;
		public String name;
		public Loadout loadout;
		public short hitboxFilter;
		public float scaleModifier;
		public boolean dontMoveCamera;
		public CreatePlayer() {}
		
		/**
		 * A CreatePlayer is sent from the Server to the Client to tell the client to create a new Player.
		 * Clients should create this new player, unless it is themselves, in which case they should synchronize it.
		 * This is because the Client reuses the same ClientState.getPlayer() which they already created.
		 * 
		 * @param entityID: ID of the new Player
		 * @param connID: conn id of the client who controls this player
		 * @param startPosition: location of new player. Used by client to focus camera
		 * @param name: name of the new Player
		 * @param loadout: loadout of the new Player
		 * @param hitboxFilter: collision filter of the new player
		 * @param scaleModifier: player body size modification
		 * @param dontMoveCamera: should the client's camera be constant when they die (used for matryoshka mode respawn)
		 */
		public CreatePlayer(UUID entityID, int connID, Vector2 startPosition, String name, Loadout loadout,
					short hitboxFilter, float scaleModifier, boolean dontMoveCamera) {
			this.uuidLSB = entityID.getLeastSignificantBits();
			this.uuidMSB = entityID.getMostSignificantBits();
            this.connID = connID;
            this.startPosition = startPosition;
            this.name = name;
            this.loadout = loadout;
            this.hitboxFilter = hitboxFilter;
            this.scaleModifier = scaleModifier;
            this.dontMoveCamera = dontMoveCamera;
        }
	}
	
	public static class CreateEvent {
		public long uuidMSB, uuidLSB;
        public EventDto blueprint;
        public boolean synced;
		public CreateEvent() {}
		
		/**
		 * A CreateEvent is sent from the Server to the Client to tell the client to create a new Event.
		 * These events are the ones parsed from Tiled Map.
		 * 
		 * @param entityID: ID of the new event
		 * @param blueprint: MapObject of the event to be parsed in the TiledObjectUtils
		 * @param synced: should this entity receive a sync packet regularly?
		 */
		public CreateEvent(UUID entityID, EventDto blueprint, boolean synced) {
			this.uuidLSB = entityID.getLeastSignificantBits();
			this.uuidMSB = entityID.getMostSignificantBits();
            this.blueprint = blueprint;
            this.synced = synced;
        }
	}
	
	public static class CreatePickup {
		public long uuidMSB, uuidLSB;
        public Vector2 pos;
        public UnlockEquip newPickup;
        public boolean synced;
        public CreatePickup() {}
        
        /**
         * A CreatePickup is sent from the Server to the Client to tell the client to create a new Pickup Event.
		 * This is for Pickups because they have some custom logic to synchronize what drop they represent.
		 * 
		 * @param entityID: ID of the new Pickup.
		 * @param pos: position of the new Pickup
		 * @param newPickup: The pickup that this event should start with.
		 * @param synced: should this entity receive a sync packet regularly? (for weapon drops from players)
         */
		public CreatePickup(UUID entityID, Vector2 pos, UnlockEquip newPickup, boolean synced) {
			this.uuidLSB = entityID.getLeastSignificantBits();
			this.uuidMSB = entityID.getMostSignificantBits();
            this.pos = pos;
            this.newPickup = newPickup;
            this.synced = synced;
		}
	}
	
	public static class CreateRagdoll {
		public long uuidMSB, uuidLSB;
        public Vector2 pos;
        public Vector2 size;
        public Sprite sprite;
        public Vector2 velocity;
        public float duration;
        public float gravity;
        public boolean setVelo;
        public boolean sensor;
        public CreateRagdoll() {}
        
        /**
         * A CreateRagdoll is sent from the server to the client to tell the client to create a ragdoll with the contained data.
         * Ragdolls are not synced between server and client.
		 * @param entityID: entityID of the player to create ragdoll for
		 * @param pos: position to create the new ragdoll
		 * @param size: size of the ragdolls (in case the player has any size modifiers)
		 * @param sprite: sprite of the ragdoll to create
		 * @param velocity: starting velocity of the ragdoll
		 * @param duration: lifespan of the ragdoll
		 * @param gravity: effect of gravity on the ragdoll
		 * @param setVelo: whether to set the velocity of newly created ragdolls
		 * @param sensor: should the ragdoll not have collisions with other objects?
         */
        public CreateRagdoll(UUID entityID, Vector2 pos, Vector2 size, Sprite sprite, Vector2 velocity, float duration, float gravity, boolean setVelo, boolean sensor) {
			this.uuidLSB = entityID.getLeastSignificantBits();
			this.uuidMSB = entityID.getMostSignificantBits();
        	this.pos = pos;
        	this.size = size;
        	this.sprite = sprite;
        	this.velocity = velocity;
        	this.duration = duration;
        	this.gravity = gravity;
        	this.setVelo = setVelo;
        	this.sensor = sensor;
        }
	}
	
	public static class SyncPickup {
		public long uuidMSB, uuidLSB;
        public UnlockEquip newPickup;
		public float age;
		public float timestamp;
        public SyncPickup() {}
        
        /**
         * A SyncPickup is sent from the Server to the Client when a Pickup is activated.
         * Clients receiving this adjust their version of the pickup to hold the new pickup
         * @param entityID: ID of the activated Pickup
         * @param newPickup: enum name of the new pickup.
		 * @param age: age of the entity. (used by client to determine if they missed a packet)
		 * @param timestamp: time of sync. Used for client prediction.
         */
		public SyncPickup(UUID entityID, UnlockEquip newPickup, float age, float timestamp) {
			this.uuidLSB = entityID.getLeastSignificantBits();
			this.uuidMSB = entityID.getMostSignificantBits();
            this.newPickup = newPickup;
			this.age = age;
			this.timestamp = timestamp;
		}
	}
	
	public static class ActivateEvent {
		public long uuidMSB, uuidLSB;
		public int connID;
		public ActivateEvent() {}
		
		/**
		 * A ActivateEvent is sent from the Server to the Client when an Event is activated (For synchronized events).
         * This is used for events that are activated on the Client's end as well. (CameraChangers, Hub Events ... etc)
		 * @param entityID: ID of the activated Pickup
		 * @param connID: The connection id of the player that activated this event
		 */
		public ActivateEvent(UUID entityID, int connID) {
			this.uuidLSB = entityID.getLeastSignificantBits();
			this.uuidMSB = entityID.getMostSignificantBits();
            this.connID = connID;
        }
	}

	public static class SyncServerLoadout {
		public long uuidMSB, uuidLSB;
		public Loadout loadout;
		public boolean save;
		public SyncServerLoadout() {}
		
		/**
		 * A SyncLoadout is sent from the Server to the Client when any Player in the world changes their loadout.
		 * Upon receiving this packet, clients adjust their versions of that Player to have the new loadout.
		 * @param entityID: ID of the player to change
		 * @param loadout: Player's new loadout
		 * @param save: do we save this loadout change in records?
		 */
		public SyncServerLoadout(UUID entityID, Loadout loadout, boolean save) {
			this.uuidLSB = entityID.getLeastSignificantBits();
			this.uuidMSB = entityID.getMostSignificantBits();
			this.loadout = loadout;
			this.save = save;
		}
	}
	
	public static class CreateParticles {
		public long uuidMSB, uuidLSB;
		public long uuidMSBAttached, uuidLSBAttached;
        public Vector2 pos;
        public boolean attached;
		public Particle particle;
		public boolean startOn;
		public float linger;
		public float lifespan;
		public float scale;
		public boolean rotate;
		public float velocity;
		public boolean synced;
		public Vector3 color;
		public CreateParticles() {}
		
		/**
		 * A CreateParticles is sent from the Server to the Client whenever a synced ParticlesEntity is created.
		 * Clients simply create the desired particle entity with all of the listed fields.
		 * Attached information is useful for making most particles sync on create, instead of every engine tick.(unless needed)
		 * @param entityID: ID of the newly created ParticlesEntity
		 * @param attachedID: ID of attached entity if it exists and null otherwise
		 * @param pos: Position of particle entity if not attached to another entity. If attached to an entity. this is the offset from the entity's position
		 * @param attached: Is this particleEntity attached to another entity?
		 * @param particle: Particle Effect to be created.
		 * @param startOn: Does this effect start turned on?
		 * @param linger: How long does an attached Particle Entity persist after its attached entity dies?
		 * @param lifespan: Duration of a non-attached entity.
		 * @param scale: The size multiplier of the particle effect
		 * @param rotate: should this entity rotate to match an attached entity?
		 * @param velocity: the velocity of the particles. (0 means to set the as the default)
		 * @param synced: should this entity receive a sync packet regularly?
		 * @param color: the color tint of the particle
		 */
		public CreateParticles(UUID entityID, UUID attachedID, Vector2 pos, boolean attached, Particle particle, boolean startOn, float linger,
			float lifespan, float scale, boolean rotate, float velocity, boolean synced, Vector3 color) {
			this.uuidLSB = entityID.getLeastSignificantBits();
			this.uuidMSB = entityID.getMostSignificantBits();
			this.uuidLSBAttached = attachedID.getLeastSignificantBits();
			this.uuidMSBAttached = attachedID.getMostSignificantBits();
			this.pos = pos;
			this.attached = attached;
			this.particle = particle;
			this.startOn = startOn;
			this.linger = linger;
			this.lifespan = lifespan;
			this.scale = scale;
			this.rotate = rotate;
			this.velocity = velocity;
			this.synced = synced;
			this.color = color;
		}
	}
	
	public static class SyncUI {
		public float timer;
		public float timerIncr;
		public AlignmentFilter[] teams;
		public int[] scores;
		public SyncUI() {}
		
		/**
		 * A SyncUI is sent from the Server to the Client whenever the ui is updated.
		 * The client updates their ui to represent the changes.
		 * @param timer: what to set the global game timer to
		 * @param timerIncr: How much should the timer be incrementing by (probably +-1 or 0)
		 * @param teams: the list of teams currently active for the match
		 * @param scores: list of scores for each team
		 */
		public SyncUI(float timer, float timerIncr, AlignmentFilter[] teams, int[] scores) {
			this.timer = timer;
			this.timerIncr = timerIncr;
			this.teams = teams;
			this.scores = scores;
		}
	}
	
	public static class SyncShader {
		public long uuidMSB, uuidLSB;
		public Shader shader;
		public float shaderCount;
		public SyncShader() {}
		
		/**
		 * A SyncShader is sent from the Server to the Client whenever a new shader is implemented.
		 * @param entityID: schmuck whose shader to change. (if null, change shader for whole playstate)
		 * @param shader: enum of the new shader
		 * @param shaderCount: duration of shader
		 */
		public SyncShader(UUID entityID, Shader shader, float shaderCount) {
			this.uuidLSB = entityID.getLeastSignificantBits();
			this.uuidMSB = entityID.getMostSignificantBits();
			this.shader = shader;
			this.shaderCount = shaderCount;
		}
	}
	
	public static class SyncSoundSingle {
		public SoundEffect sound;
		public Vector2 worldPos;
		public float volume;
		public float pitch;
		public boolean singleton;
		
		public SyncSoundSingle() {}
		
		/**
		 * A SyncSound is sent from the Server to the Client to tell the client to play a specific sound effect.
		 * @param sound: The sound effect to play
		 * @param worldPos: This is the world location of the source of the sound. used to manage sound pan (null if not sourced to an entity)
		 * @param volume: volume of the sound. 1.0f = full volume.
		 * @param pitch: pitch of the sound. 1.0f = default pitch.
		 * @param singleton: is there only one instance of this sound? (stops other instances of the sound before playing)
		 */
		public SyncSoundSingle(SoundEffect sound, Vector2 worldPos, float volume, float pitch, boolean singleton) {
			this.sound = sound;
			this.worldPos = worldPos;
			this.volume = volume;
			this.pitch = pitch;
			this.singleton = singleton;
		}
	}
	
	public static class CreateSound {
		public long uuidMSB, uuidLSB;
		public long uuidMSBAttached, uuidLSBAttached;
		public SoundEffect sound;
		public float volume;
		public float pitch;
		public boolean looped;
		public boolean on;
		public boolean synced;
		
		public CreateSound() {}
		
		/**
		 * A CreateSound is sent from the Server to the Client to tell the client to create a SoundEntity.
		 * This is distinct from SyncSoundSingle because the sound is attached to an entity that can move/be destroyed etc.
		 * The volume and pan of the sound is dependent on the relative position of the entity.
		 * @param entityID: schmuck id of the SoundEntity
		 * @param attachedID: schmuck id of the entity that the SchmuckEntity is to attached to
		 * @param sound: The sound effect to play
		 * @param volume: volume of the sound. 1.0f = full volume.
		 * @param pitch: pitch of the sound. 1.0f - default pitch.
		 * @param looped: does the sound loop?
		 * @param on: does the sound start off on?
		 * @param synced: should this entity receive a sync packet regularly?
		 */
		public CreateSound(UUID entityID, UUID attachedID, SoundEffect sound, float volume, float pitch, boolean looped,
						   boolean on, boolean synced) {
			this.uuidLSB = entityID.getLeastSignificantBits();
			this.uuidMSB = entityID.getMostSignificantBits();
			this.uuidLSBAttached = attachedID.getLeastSignificantBits();
			this.uuidMSBAttached = attachedID.getMostSignificantBits();
			this.sound = sound;
			this.volume = volume;
			this.pitch = pitch;
			this.looped = looped;
			this.on = on;
			this.synced = synced;
		}
	}
	
	public static class SyncSound {
		public long uuidMSB, uuidLSB;
		public float volume;
		public boolean on;
		public float age;
		public float timestamp;
		
		public SyncSound() {}
		
		/**
		 * A SyncSound synchronizes a single sound entity and is sent from the server to the client every world-sync.
		 * @param entityID: schmuck id of the SoundEntity
		 * @param volume: new volume of the soundentity
		 * @param on: is the soundentity on?
		 * @param age: age of the entity. (used by client to determine if they missed a packet)
		 * @param timestamp: time of sync. Used for client prediction.
		 */
		public SyncSound(UUID entityID, float volume, boolean on, float age, float timestamp) {
			this.uuidLSB = entityID.getLeastSignificantBits();
			this.uuidMSB = entityID.getMostSignificantBits();
			this.volume = volume;
			this.on = on;
			this.age = age;
			this.timestamp = timestamp;
		}
	}
	
	public static class SyncHitSound {
		public boolean large;
		public SyncHitSound() {}

		/**
		 * A SyncHitSound is a simple packet that just tells the client to play their hitsound.
		 * @param large: is this hitsound pitched up as a result of high damage or being fatal?
		 */
		public SyncHitSound(boolean large) {
			this.large = large;
		}
	}
	
	public static class MissedCreate {
		public long uuidMSB, uuidLSB;
		
		public MissedCreate() {}
		
		/**
		 * A MissedCreate is sent from the client to the server when the client is told to sync an object it didn't create.
		 * The implication is that the client missed a create entity packet from the server.
		 * @param entityID: the entity id of the entity the client was told to sync
		 */
		public MissedCreate(UUID entityID) {
			this.uuidLSB = entityID.getLeastSignificantBits();
			this.uuidMSB = entityID.getMostSignificantBits();
		}
	}
	
	public static class MissedDelete {
		public long uuidMSB, uuidLSB;
		
		public MissedDelete() {}
		
		/**
		 * A MissedDelete is sent from the client to the server when the client has not been told to sync an object that should be receiving syncs.
		 * The implication is that the client missed a delete entity packet from the server.
		 * @param entityID: the entity id of the entity the client expected to be synced
		 */
		public MissedDelete(UUID entityID) {
			this.uuidLSB = entityID.getLeastSignificantBits();
			this.uuidMSB = entityID.getMostSignificantBits();
		}
	}
	
	public static class StartSpectate {
		
		/**
		 * A StartSpectate is sent from the client to the server when the client chooses to spectate a game 
		 */
		public StartSpectate() {}
	}
	
	public static class EndSpectate {
		public Loadout loadout;
		/**
		 * An EndSpectate is sent from the client to the server when the client chooses to stop spectating a game
		 * @param loadout: the client's chosen loadout. sent so the server can give the right loadout to new player
		 */
		public EndSpectate(Loadout loadout) {
			this.loadout = loadout;
		}

		public EndSpectate() {}
	}

	public static class SyncSharedSettings {
		public SharedSetting settings;
		
		public SyncSharedSettings() {}
		
		/**
		 * A SyncSharedSettings is sent from the server to the client when the client connects, or when settings are changed.
		 * @param settings: the host settings to be displayed in the score window ui
		 */
		public SyncSharedSettings(SharedSetting settings) {
			this.settings = settings;
		}
	}
	
	public static class LatencySyn {
		public int latency;
		
		public LatencySyn() {}
		
		/**
		 * A LatencySyn is sent from the client to the server periodically to check the quality of the network connection.
		 * @param latency: the client's last synced latency
		 */
		public LatencySyn(int latency) {
			this.latency = latency;
		}
	}

	public static class LatencyAck {
		public float timestamp;

		public LatencyAck() {}

		/**
		 * A LatencyAck is sent from the server to the client as a response to a LatencySyn. The time is used to calculate the client's ping.
		 * @param timestamp: is the server's time at time of response
		 */
		public LatencyAck(float timestamp) { this.timestamp = timestamp; }
	}
	
	public static class SyncExtraResultsInfo {
		public UserDto[] users;
		public String resultsText;

		public SyncExtraResultsInfo() {}
		
		/**
		 * A SyncExtraResultsInfo is sent from the server to the client when they enter the results screen.
		 * @param users: This contains information about the match performance
		 * @param resultsText: the text to be displayed as the title of the results state
		 */
		public SyncExtraResultsInfo(UserDto[] users, String resultsText) {
			this.users = users;
			this.resultsText = resultsText;
		}
	}
	
	public static class SyncTyping {
		public long uuidMSB, uuidLSB;
		
		public SyncTyping() {}
		
		/**
		 * A LatencyAck is sent from the client to the server when they type in the message window.
		 * This is also sent from the server to the client to indicate which players are currently typing.
		 * @param entityID: this is the id of the player that is currently typing.
		 */
		public SyncTyping(UUID entityID) {
			this.uuidLSB = entityID.getLeastSignificantBits();
			this.uuidMSB = entityID.getMostSignificantBits();
		}
	}

	public static class RemoveScore {
		public int connID;

		public RemoveScore() {}

		/**
		 * A RemoveScore is sent from the server to each client when another client disconnects.
		 * This instructs them to remove that user from the score table
		 * @param connID: this is the id of the player that disconnected.
		 */
		public RemoveScore(int connID) {
			this.connID = connID;
		}
	}

	public static class ClientYeet {

		/**
		 * A ClientYeet packet is sent from server to client to disconnect the client
		 * It is also sent from client to server to tell the server to kill the client's player.
		 * Why use the same packet for completely different purposes? no reason.
		 */
		public ClientYeet() {}
	}

	public static class SyncEmote {
		public int emoteIndex;

		public SyncEmote() {}

		/**
		 * A SyncEmote is sent from the client to the server to indicate that they want to use a emote
		 * @param emoteIndex: index in list of emotes the client wishes to use
		 */
		public SyncEmote(int emoteIndex) {
			this.emoteIndex = emoteIndex;
		}
	}

	public static class SyncKillMessage {
		public int perpConnID;
		public int vicConnID;
		public EnemyType enemyType;
		public DamageTypes[] tags;

		public SyncKillMessage() {}

		/**
		 * A SyncKillMessage is sent from the server to the client when a player dies.
		 * This provides the client with the needed information to display a kill message in their feed
		 * @param perpConnID: the connID of the perp (-1 if no player perp)
		 * @param vicConnID: the connID of the vic
		 * @param enemyType: the type of enemy that killed the player (null if not an enemy kill)
		 * @param tags: damage tags of the last instance of damage.
		 */
		public SyncKillMessage(int perpConnID, int vicConnID, EnemyType enemyType, DamageTypes... tags) {
			this.perpConnID = perpConnID;
			this.vicConnID = vicConnID;
			this.enemyType = enemyType;
			this.tags = tags;
		}
	}

	public static class SyncNotification {
		public String message;

		public SyncNotification() {}

		/**
		 * A SyncNotification is sent from the server to the client to tell them to display a notification
		 * @param message: the string notification to be displayed
		 */
		public SyncNotification(String message) {
			this.message = message;
		}
	}

	public static class SyncObjectiveMarker {
		public long uuidMSB, uuidLSB;
		public Vector3 color;
		public boolean displayOnScreen;
		public boolean displayOffScreen;
		public Sprite icon;

		public SyncObjectiveMarker() {}

		/**
		 * A SyncObjectiveMarker is sent from the server to the client when the objective marker is set to a non-event
		 * entity. The client sets their objective marker to match the packet
		 * @param entityID: ID of the entity to mark
		 * @param color: rgb color for the objective marker (1, 1, 1 for no change)
		 * @param displayOffScreen: should the marker be displayed when the target is off screen?
		 * @param displayOnScreen: should the marker be displayed when the target is on screen?
		 * @param icon: what icon should be used for the marker?
		 */
		public SyncObjectiveMarker(UUID entityID, Vector3 color, boolean displayOffScreen, boolean displayOnScreen, Sprite icon) {
			this.uuidLSB = entityID.getLeastSignificantBits();
			this.uuidMSB = entityID.getMostSignificantBits();
			this.color = color;
			this.displayOffScreen = displayOffScreen;
			this.displayOnScreen = displayOnScreen;
			this.icon = icon;
		}
	}

	/**
     * REGISTER ALL THE CLASSES FOR KRYO TO SERIALIZE AND SEND
     * @param kryo The kryo object
     */
    public static void allPackets(Kryo kryo) {
		kryo.register(ConnectReject.class);
		kryo.register(PasswordRequest.class);
    	kryo.register(PlayerConnect.class);
    	kryo.register(ServerLoaded.class);
    	kryo.register(Paused.class);
    	kryo.register(Unpaused.class);
		kryo.register(ServerNotification.class);
		kryo.register(ServerChat.class);
		kryo.register(ClientChat.class);
    	kryo.register(ClientReady.class);
		kryo.register(SyncKeyStrokes.class);
    	kryo.register(LoadLevel.class);
    	kryo.register(ClientLoaded.class);
    	kryo.register(ClientPlayerCreated.class);
    	kryo.register(ClientStartTransition.class);
    	kryo.register(SyncScore.class);
    	kryo.register(CreateEntity.class);
    	kryo.register(CreateEnemy.class);
		kryo.register(DeleteEntity.class);
		kryo.register(DeletePlayer.class);
    	kryo.register(CreateEvent.class);
    	kryo.register(CreatePickup.class);
		kryo.register(CreateSyncedAttackSingle.class);
		kryo.register(CreateSyncedAttackSingleExtra.class);
		kryo.register(CreateSyncedAttackMulti.class);
		kryo.register(CreateSyncedAttackMultiExtra.class);
    	kryo.register(SyncPickup.class);
    	kryo.register(ActivateEvent.class);
    	kryo.register(CreatePlayer.class);
		kryo.register(SyncServerLoadout.class);
    	kryo.register(CreateParticles.class);
    	kryo.register(CreateRagdoll.class);

    	kryo.register(SyncUI.class);
    	kryo.register(SyncShader.class);
    	kryo.register(SyncSoundSingle.class);
    	kryo.register(CreateSound.class);
    	kryo.register(SyncSound.class);
    	kryo.register(SyncHitSound.class);
    	kryo.register(MissedCreate.class);
    	kryo.register(MissedDelete.class);
    	kryo.register(StartSpectate.class);
		kryo.register(EndSpectate.class);
    	kryo.register(SyncSharedSettings.class);
    	kryo.register(LatencySyn.class);
    	kryo.register(LatencyAck.class);
    	kryo.register(SyncExtraResultsInfo.class);
		kryo.register(SyncTyping.class);
		kryo.register(RemoveScore.class);
		kryo.register(ClientYeet.class);
		kryo.register(SyncEmote.class);
		kryo.register(SyncKillMessage.class);
		kryo.register(SyncNotification.class);
		kryo.register(SyncObjectiveMarker.class);

		kryo.register(PacketsSync.SyncEntity.class);
		kryo.register(PacketsSync.SyncEntityAngled.class);
		kryo.register(PacketsSync.SyncSchmuck.class);
		kryo.register(PacketsSync.SyncSchmuckAngled.class);
		kryo.register(PacketsSync.SyncPlayer.class);
		kryo.register(PacketsSync.SyncPlayerSelf.class);
		kryo.register(PacketsSync.SyncParticles.class);
		kryo.register(PacketsSync.SyncParticlesExtra.class);

		kryo.register(PacketsLoadout.SyncLoadoutClient.class);
		kryo.register(PacketsLoadout.SyncEquipClient.class);
		kryo.register(PacketsLoadout.SyncArtifactAddClient.class);
		kryo.register(PacketsLoadout.SyncArtifactRemoveClient.class);
		kryo.register(PacketsLoadout.SyncActiveClient.class);
		kryo.register(PacketsLoadout.SyncCharacterClient.class);
		kryo.register(PacketsLoadout.SyncTeamClient.class);
		kryo.register(PacketsLoadout.SyncEquipServer.class);
		kryo.register(PacketsLoadout.SyncArtifactServer.class);
		kryo.register(PacketsLoadout.SyncActiveServer.class);
		kryo.register(PacketsLoadout.SyncCharacterServer.class);
		kryo.register(PacketsLoadout.SyncTeamServer.class);

		kryo.register(int[].class);
		kryo.register(float[].class);
		kryo.register(long[].class);
		kryo.register(Vector2.class);
		kryo.register(Vector2[].class);
		kryo.register(Vector3.class);
		kryo.register(Particle.class);
		kryo.register(SoundEffect.class);
		kryo.register(UnlockLevel.class);
		kryo.register(UnlockArtifact.class);
		kryo.register(UnlockArtifact[].class);
		kryo.register(UnlockActives.class);
		kryo.register(UnlockCharacter.class);
		kryo.register(UnlockEquip.class);
		kryo.register(UnlockEquip[].class);
		kryo.register(AlignmentFilter.class);
		kryo.register(AlignmentFilter[].class);
		kryo.register(GameMode.class);
		kryo.register(Loadout.class);
		kryo.register(TransitionState.class);
		kryo.register(DialogType.class);
		kryo.register(PlayerAction.class);
		kryo.register(PlayerAction[].class);
		kryo.register(ObjectSyncLayers.class);
		kryo.register(alignType.class);
		kryo.register(EnemyType.class);
		kryo.register(SyncedAttack.class);
		kryo.register(DespawnType.class);
		kryo.register(MoveState.class);
		kryo.register(Shader.class);
		kryo.register(Sprite.class);
		kryo.register(SoundEffect.class);
		kryo.register(EventDto.class);
		kryo.register(EventDto.Pair.class);
		kryo.register(SharedSetting.class);
		kryo.register(DamageTypes.class);
		kryo.register(DamageTypes[].class);
		kryo.register(UserDto.class);
		kryo.register(UserDto[].class);
		kryo.register(SavedPlayerFields.class);
		kryo.register(SavedPlayerFieldsExtra.class);

		kryo.register(Array.class);
		kryo.register(Object[].class);
		kryo.register(HashMap.class);
	}
}
