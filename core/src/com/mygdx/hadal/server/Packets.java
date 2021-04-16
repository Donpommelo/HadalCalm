package com.mygdx.hadal.server;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryo.Kryo;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.PlayerSpriteHelper.DespawnType;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.save.*;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.schmucks.bodies.ClientIllusion.alignType;
import com.mygdx.hadal.schmucks.bodies.enemies.EnemyType;
import com.mygdx.hadal.server.User.UserDto;
import com.mygdx.hadal.states.ClientState.ObjectSyncLayers;
import com.mygdx.hadal.states.PlayState.TransitionState;
import com.mygdx.hadal.statuses.DamageTypes;

import java.util.ArrayList;

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
		 * This is done when the server is full, or if the server is in the middle of a game. (until we implement spectator mode or player blocking)
		 * 
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
		 * 
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
		public boolean firstTime;
		public boolean spectator;
		public LoadLevel() {}
		
		/**
		 * A LoadLevel is sent from the Server to the Client to tell the Client to transition to a new level.
		 * This is done when the Server receives a Client's PlayerConnect to tell them what world to load.
		 * It is also done when the Client sends a ClientFinishedTransition packet if the Client should load a new level.
		 * 
		 * @param level: Level that the Client will load.
		 * @param firstTime: Is this the client's first time? Or is this sent as level transition. Checked when displaying notifications.
		 * @param spectator: Is this client connecting as a spectator?
		 */
		public LoadLevel(UnlockLevel level, boolean firstTime, boolean spectator) {
			this.level = level;
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
		 * 
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
	
	public static class SyncClientLoadout {

		public UnlockEquip equip;
		public UnlockArtifact artifactAdd;
		public UnlockArtifact artifactRemove;
		public UnlockActives active;
		public UnlockCharacter character;
		public AlignmentFilter team;
		
		public SyncClientLoadout() {}
		
		/**
		 * A SyncClientWeapon is sent from the Client to the Server when the client attempts to change their loadout in the hub.
		 * 
		 * @param equip: An equip to be switched to this client's loadout
		 * @param artifactAdd: An artifact to be added to this client's loadout
		 * @param artifactRemove: An artifact to be removed to this client's loadout
		 * @param active: An active item to be switched to this client's loadout
		 * @param character: A character skin to be switched to this client's loadout
		 * @param team: the team alignment/color to be switched to this client's loadout
		 */
		public SyncClientLoadout(UnlockEquip equip, UnlockArtifact artifactAdd, UnlockArtifact artifactRemove,
								 UnlockActives active, UnlockCharacter character, AlignmentFilter team) {
			this.equip = equip;
			this.artifactAdd = artifactAdd;
			this.artifactRemove= artifactRemove;
			this.active = active;
			this.character = character;
			this.team = team;
		}
	}
	
	public static class ClientStartTransition {
		public TransitionState state;
		public boolean override;
		public String resultsText;
		public float fadeSpeed;
		public float fadeDelay;
		public ClientStartTransition() {}
		
		/**
		 * A ClientStartTransition is sent from the Server to the Client upon beginning a transition to another state to tell the Client
		 * to transition as well.
		 * Clients receiving this begin fading to black the same way the Server does.
		 * @param state: Are we transitioning to a new level, a gameover screen or whatever else?
		 * @param override: Should this override other transitions?
		 * @param resultsText: If transitioning to a results screen, what text should be displayed?
		 * @param fadeSpeed: speed of the fade transition
		 * @param fadeDelay: Amount of delay before transition
		 */
		public ClientStartTransition(TransitionState state, boolean override, String resultsText, float fadeSpeed, float fadeDelay) {
			this.state = state;
			this.override = override;
			this.resultsText = resultsText;
			this.fadeSpeed = fadeSpeed;
			this.fadeDelay = fadeDelay;
		}
	}
	
	public static class ClientFinishRespawn {
		public Loadout loadout;

		public ClientFinishRespawn() {}

		/**
		 * A ClientFinishRespawn is sent from the Client to the Server upon finishing their fade-to-black after receiving a ClientStartTransition Packet
		 * If the client is respawning, the server creates the new client player upon receiving this message.
		 * @param loadout: the loadout that the client will be respawning with
		 *
		 */
		public ClientFinishRespawn(Loadout loadout) {
			this.loadout = loadout;
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
		 *
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
		 *
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
		public PlayerAction[] actions;
		public float timestamp;

		public SyncKeyStrokes() {}

		public SyncKeyStrokes(PlayerAction[] actions, float timestamp) {
			this.actions = actions;
			this.timestamp = timestamp;
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
		public int connID;
		public String name;
		public int wins;
		public int kills;
		public int deaths;
		public int score;
		public int lives;
		public int ping;

		public SyncScore() {}
		
		/**
		 * This is sent from the server to the clients to give them their scores for all players
		 * @param connID: id of the player whose score is being updated.
		 */
		public SyncScore(int connID, String name, int wins, int kills, int deaths, int score, int lives, int ping) {
			this.connID = connID;
			this.name = name;
			this.wins = wins;
			this.kills = kills;
			this.deaths = deaths;
			this.score = score;
			this.lives = lives;
			this.ping = ping;
		}
	}

	public static class CreateEntity {
		public String entityID;
		public Vector2 pos;
		public float angle;
        public Vector2 size;
        public Sprite sprite;
        public boolean synced;
        public ObjectSyncLayers layer;
        public alignType align;
		public CreateEntity() {}
		
		/**
		 * A CreateEntity is sent from the Server to the Client to tell the Client to create a new Entity.
		 * 
		 * @param entityID: ID of the new entity
		 * @param size: Size of the new entity
		 * @param pos: position of the new entity
		 * @param angle: starting angle of the new entity
		 * @param sprite: entity's sprite
		 * @param synced: should this entity receive a sync packet regularly?
		 * @param layer: Hitbox or Standard layer? (Hitboxes are rendered underneath other entities)
		 * @param align: The new object's align type. Used to determine how the client illusion should be rendered
		 */
		public CreateEntity(String entityID, Vector2 size, Vector2 pos, float angle, Sprite sprite, boolean synced, ObjectSyncLayers layer, alignType align) {
			this.entityID = entityID;
			this.pos = pos;
			this.angle = angle;
            this.size = size;
            this.sprite = sprite;
            this.synced = synced;
            this.layer = layer;
            this.align = align;
        }
	}
	
	public static class CreateEnemy {
		public String entityID;
		public Vector2 pos;
		public EnemyType type;
		public boolean boss;
		public String name;
		public CreateEnemy() {}
		
		/**
		 * A CreateEnemy is sent from the Server to the Client to tell the Client to create a new Enemy.
		 * @param entityID: ID of the new Enemy.
		 * @param pos: position of the enemy
		 * @param type: Enemy Type
		 * @param boss: is this a boss enemy?
		 * @param name: if a boss, what name shows up in the ui?
		 */
		public CreateEnemy(String entityID, Vector2 pos, EnemyType type, boolean boss, String name) {
            this.entityID = entityID;
            this.pos = pos;
            this.type = type;
            this.boss = boss;
            this.name = name;
        }
	}
	
	public static class DeleteEntity {
		public String entityID;
		public float timestamp;
		public DeleteEntity() {}
		
		/**
		 * A Delete Entity is sent from the Server to the Client to tell the Client to delete an Entity.
		 * @param entityID: ID of the entity to be deleted.
		 * @param timestamp: when this deletion occurred. Used to handle the possibility of packet loss.
		 */
		public DeleteEntity(String entityID, float timestamp) {
			this.entityID = entityID;
			this.timestamp = timestamp;
        }
	}

	public static class DeletePlayer {
		public String entityID;
		public float timestamp;
		public DespawnType type;
		public DeletePlayer() {}

		/**
		 * A Delete Player is sent from the Server to the Client to tell the Client to delete a player.
		 * This is separate from delete entity to pass along info about the type of death.
		 * @param entityID: ID of the entity to be deleted.
		 * @param timestamp: when this deletion occurred. Used to handle the possibility of packet loss.
		 * @param type: type of deletion for animation purpose (death, disconnect teleport etc)
		 */
		public DeletePlayer(String entityID, float timestamp, DespawnType type) {
			this.entityID = entityID;
			this.timestamp = timestamp;
			this.type = type;
		}
	}

	public static class CreatePlayer {
		public String entityID;
		public int connID;
		public Vector2 startPosition;
		public String name;
		public Loadout loadout;
		public short hitboxFilter;
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
		 */
		public CreatePlayer(String entityID, int connID, Vector2 startPosition, String name, Loadout loadout, short hitboxFilter) {
            this.entityID = entityID;
            this.connID = connID;
            this.startPosition = startPosition;
            this.name = name;
            this.loadout = loadout;
            this.hitboxFilter = hitboxFilter;
        }
	}
	
	public static class CreateEvent {
		public String entityID;
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
		public CreateEvent(String entityID, EventDto blueprint, boolean synced) {
            this.entityID = entityID;
            this.blueprint = blueprint;
            this.synced = synced;
        }
	}
	
	public static class CreatePickup {
		public String entityID;
        public Vector2 pos;
        public String newPickup;
        public boolean synced;
        public CreatePickup() {}
        
        /**
         * A CreatePickup is sent from the Server to the Client to tell the client to create a new Pickup Event.
		 * This is for Pickups because they have some custom logic to synchronize what drop they represent.
		 * 
		 * @param entityID: ID of the new Pickup.
		 * @param pos: position of the new Pickup
		 * @param newPickup: The pickup that this event should start with.
		 * @param synced: should this entity receive a sync packet regularly?
         */
		public CreatePickup(String entityID, Vector2 pos, String newPickup, boolean synced) {
			this.entityID = entityID;
            this.pos = pos;
            this.newPickup = newPickup;
            this.synced = synced;
		}
	}
	
	public static class CreateRagdoll {
		public String entityID;
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
         */
        public CreateRagdoll(String entityID, Vector2 pos, Vector2 size, Sprite sprite, Vector2 velocity, float duration, float gravity, boolean setVelo, boolean sensor) {
        	this.entityID = entityID;
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
	
	public static class SyncEntity {
		public String entityID;
        public Vector2 pos;
        public Vector2 velocity;
        public float angle;
        public float age;
        public float timestamp;
        public boolean instant;
		public SyncEntity() {}
		
		/**
		 * A SyncEntity is sent from the Server to the Client for every synchronized entity every engine tick.
		 * This packet (and similar packets) just tell the client how to change their version of the entity.
		 * Most basic version just transforms the entity's body.
		 * 
		 * @param entityID: ID of the entity to synchronize
		 * @param pos: position of the entity
		 * @param velocity: linear velocity of the entity
		 * @param angle: body angle of the new entity.
		 * @param age: age of the entity. (used by client to determine if they missed a packet)
		 * @param timestamp: time of sync. Used for client prediction.
		 * @param instant: should the client entity instantly copy the server or interpolate?
		 */
		public SyncEntity(String entityID, Vector2 pos, Vector2 velocity, float angle, float age, float timestamp, boolean instant) {
            this.entityID = entityID;
            this.pos = pos;
            this.velocity = velocity;
            this.angle = angle;
            this.age = age;
            this.timestamp = timestamp;
            this.instant = instant;
        }
	}
	
	public static class SyncPickup {
		public String entityID;
        public String newPickup;
		public float age;
		public float timestamp;
        public SyncPickup() {}
        
        /**
         * A SyncPickup is sent from the Server to the Client when a Pickup is activated.
         * Clients receiving this adjust their version of the pickup to hold the new pickup
         * 
         * @param entityID: ID of the activated Pickup
         * @param newPickup: enum name of the new pickup.
		 * @param age: age of the entity. (used by client to determine if they missed a packet)
		 * @param timestamp: time of sync. Used for client prediction.
         */
		public SyncPickup(String entityID, String newPickup, float age, float timestamp) {
			this.entityID = entityID;
            this.newPickup = newPickup;
			this.age = age;
			this.timestamp = timestamp;
		}
	}
	
	public static class ActivateEvent {
		public String entityID;
		public int connID;
		public ActivateEvent() {}
		
		/**
		 * A ActivateEvent is sent from the Server to the Client when an Event is activated (For synchronized events).
         * This is used for events that are activated on the Client's end as well. (CameraChangers, Hub Events ... etc)
         * 
		 * @param entityID: ID of the activated Pickup
		 */
		public ActivateEvent(String entityID, int connID) {
            this.entityID = entityID;
            this.connID = connID;
        }
	}
	
	public static class SyncSchmuck {
		public String entityID;
		public MoveState moveState;
		public float hpPercent;
		public float timestamp;
		
		public SyncSchmuck() {}
		
		/**
		 * A SyncSchmuck is sent from the Server to the Client for every synchronized schmuck every engine tick.
		 * This packet (and similar packets) just tell the client how to change their version of the schmuck.
		 * This adjusts the Schmuck's stats and visual information
		 * 
		 * @param entityID: ID of the Schmuck to be synced
		 * @param moveState: The State of the Schmuck. Used for animations on the Client's end
		 * @param hpPercent: The percent of remaining hp this schmuck has.
		 * @param timestamp: time of sync. Used for client prediction.
		 */
		public SyncSchmuck(String entityID, MoveState moveState, float hpPercent, float timestamp) {
			this.entityID = entityID;
			this.moveState = moveState;
			this.hpPercent = hpPercent;
			this.timestamp = timestamp;
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
		 * This packet (and similar packets) just tells the client how to change their own Player for their purpose of their own ui.
		 * @param fuelPercent: The client player's current fuel amount.
		 * @param currentClip: The client player's current clip amount.
		 * @param currentAmmo: The client player's current ammo amount.
		 * @param activeCharge: The client player's current active item charge amount.
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
        public Vector2 attackAngle;
        public MoveState moveState;
        public boolean grounded;
        public int currentSlot;
        public boolean reloading;
        public float reloadPercent;
        public boolean charging;
        public float chargePercent;
        public boolean outOfAmmo;
        public short maskBits;
        public boolean invisible;
        public float timestamp;
        
		public SyncPlayerAll() {}
		
		/**
		 * A SyncPlayerAll is sent from the Server to the Client for every synchronized Player every engine tick.
		 * This packet (and similar packets) just tell the client how to change their version of each Player.
		 * This long list of fields is just the Player-specific information needed for Clients to properly render other players.
		 */
		public SyncPlayerAll(String entityID, Vector2 attackAngle, Boolean grounded, int currentSlot, boolean reloading, float reloadPercent, boolean charging, float chargePercent, 
				boolean outOfAmmo, short maskBits, boolean invisible, float timestamp) {
            this.entityID = entityID;
            this.attackAngle = attackAngle;
            this.grounded = grounded;
            this.currentSlot = currentSlot;
            this.reloading = reloading;
            this.reloadPercent = reloadPercent;
            this.charging = charging;
            this.chargePercent = chargePercent;
            this.outOfAmmo = outOfAmmo;
            this.maskBits = maskBits;
            this.invisible = invisible;
            this.timestamp = timestamp;
        }
	}
	
	public static class SyncPlayerStats {
		public int maxClip;
        public float maxHp;
        public float maxFuel;
        public float airblastCost;
        public int weaponSlots;
        public int artifactSlots;
        public float healthVisible;
        public SyncPlayerStats() {}
        
        /**
         * A SyncPlayerStats is sent from the server to the client whenever their stats change.
         * This long list of fields is just the Player-specific information needed for Clients to properly render their own ui.
         */
        public SyncPlayerStats(int maxClip, float maxHp, float maxFuel, float airblastCost, int weaponSlots, int artifactSlots, float healthVisible) {
        	 this.maxClip = maxClip;
             this.maxHp = maxHp;
             this.maxFuel = maxFuel;
             this.airblastCost = airblastCost;
             this.weaponSlots = weaponSlots;
             this.artifactSlots = artifactSlots;
             this.healthVisible = healthVisible;
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
		 * @param entityID: ID of the player to change
		 * @param loadout: Player's new loadout
		 */
		public SyncServerLoadout(String entityID, Loadout loadout) {
			this.entityID = entityID;
			this.loadout = loadout;
		}
	}
	
	public static class CreateParticles {
		public String entityID;
		public String attachedID;
        public Vector2 pos;
        public Vector2 offset;
        public boolean attached;
		public String particle;
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
		 * 
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
		public CreateParticles(String entityID, String attachedID, Vector2 pos, boolean attached, String particle, boolean startOn, float linger,
			float lifespan, float scale, boolean rotate, float velocity, boolean synced, Vector3 color) {
			this.entityID = entityID;
			this.attachedID = attachedID;
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
	
	public static class SyncParticles {
		public String entityID;
		public Vector2 pos;
		public Vector2 offset;
        public boolean on;
        public float age;
        public float timestamp;
		public SyncParticles() {}
		
		/**
		 * A SyncParticles is sent from the Server to the Client every engine tick for every ParticleEntity of the TICKSYNC type.
		 * Particles of this nature are dynamically turned on and off in the Server, thus needing this packet.
		 * 
		 * @param entityID: ID of the Particle Effect to turn on/off
		 * @param pos: position of the synced particle effect
		 * @param offset: if connected to another entity, this is the offset from that entity's position
		 * @param on: Is the Server's version of this effect on or off?
		 * @param age: age of the entity. (used by client to determine if they missed a packet)
		 * @param timestamp: time of sync. Used for client prediction.
		 */
		public SyncParticles(String entityID, Vector2 pos, Vector2 offset, boolean on, float age, float timestamp) {
			this.entityID = entityID;
			this.pos = pos;
			this.offset = offset;
			this.on = on;
			this.age = age;
			this.timestamp = timestamp;
		}
	}
	
	public static class SyncParticlesExtra {
		public String entityID;
		public Vector2 pos;
		public Vector2 offset;
        public boolean on;
        public float age;
        public float timestamp;
		public float scale;
		public Vector3 color;
		
		public SyncParticlesExtra() {}
		
		/**
		 * This sync packet is used for particles that sync the extra fields; color and scale.
		 */
		public SyncParticlesExtra(String entityID, Vector2 pos, Vector2 offset, boolean on, float age, float timestamp, float scale, Vector3 color) {
			this.entityID = entityID;
			this.pos = pos;
			this.offset = offset;
			this.on = on;
			this.age = age;
			this.timestamp = timestamp;
			this.scale = scale;
			this.color = color;
		}
	}
	
	public static class SyncUI {
		public String uiTags;
		public float timer;
		public float timerIncr;
		public AlignmentFilter[] teams;
		public int[] scores;
		public SyncUI() {}
		
		/**
		 * A SyncUI is sent from the Server to the Client whenever the ui is updated.
		 * The client updates their ui to represent the changes.
		 * @param uiTags: list of ui elements to add
		 * @param timer: what to set the global game timer to
		 * @param timerIncr: How much should the timer be incrementing by (probably +-1 or 0)
		 */
		public SyncUI(String uiTags, float timer, float timerIncr, AlignmentFilter[] teams, int[] scores) {
			this.uiTags = uiTags;
			this.timer = timer;
			this.timerIncr = timerIncr;
			this.teams = teams;
			this.scores = scores;
		}
	}
	
	public static class SyncShader {
		public String entityID;
		public Shader shader;
		public float shaderCount;
		public SyncShader() {}
		
		/**
		 * A SyncShader is sent from the Server to the Client whenever a new shader is implemented.
		 * @param entityID: schmuck whose shader to change. (if null, change shader for whole playstate)
		 * @param shader: enum of the new shader
		 * @param shaderCount: duration of shader
		 */
		public SyncShader(String entityID, Shader shader, float shaderCount) {
			this.entityID = entityID;
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
		public String entityID;
		public String attachedID;
		public String sound;
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
		 * @param attachedID: schmuck id of the entity that the SchmuckEntity is to attach to
		 * @param sound: The sound effect to play
		 * @param volume: volume of the sound. 1.0f = full volume.
		 * @param pitch: pitch of the sound. 1.0f - default pitch.
		 * @param looped: does the sound loop?
		 * @param on: does the sound start off on?
		 * @param synced: should this entity receive a sync packet regularly?
		 */
		public CreateSound(String entityID, String attachedID, String sound, float volume, float pitch, boolean looped, boolean on, boolean synced) {
			this.entityID = entityID;
			this.attachedID = attachedID;
			this.sound = sound;
			this.volume = volume;
			this.pitch = pitch;
			this.looped = looped;
			this.on = on;
			this.synced = synced;
		}
	}
	
	public static class SyncSound {
		public String entityID;
		public Vector2 pos;
		public float volume;
		public boolean on;
		public float age;
		public float timestamp;
		
		public SyncSound() {}
		
		/**
		 * A SyncSound synchronizes a single sound entity and is sent from the server to the client every world-sync.
		 * @param entityID: schmuck id of the SoundEntity
		 * @param pos: new position of the soundentity
		 * @param volume: new volume of the soundentity
		 * @param on: is the soundentity on?
		 * @param age: age of the entity. (used by client to determine if they missed a packet)
		 * @param timestamp: time of sync. Used for client prediction.
		 */
		public SyncSound(String entityID, Vector2 pos, float volume, boolean on, float age, float timestamp) {
			this.entityID = entityID;
			this.pos = pos;
			this.volume = volume;
			this.on = on;
			this.age = age;
			this.timestamp = timestamp;
		}
	}
	
	public static class SyncMusic {
		public String music;
		public float volume;
		
		public SyncMusic() {}
		
		/**
		 * A SyncMusic is sent from the server to the client to tell them to begin playing a certain music track.
		 * @param music: the enum name of the music track to play
		 * @param volume: volume to play the music at
		 */
		public SyncMusic(String music, float volume) {
			this.music = music;
			this.volume = volume;
		}
	}
	
	public static class SyncHitSound {
		public boolean large;
		/**
		 * A SyncHitSound is a simple packet that just tells the client to play their hitsound.
		 * large: is this hitsound pitched up as a result of high damage or being fatal?
		 */
		public SyncHitSound() {}
		
		public SyncHitSound(boolean large) {
			this.large = large;
		}
	}
	
	public static class MissedCreate {
		public String entityID;
		
		public MissedCreate() {}
		
		/**
		 * A MissedCreate is sent from the client to the server when the client is told to sync an object it didn't create.
		 * The implication is that the client missed a create entity packet from the server.
		 * @param entityID: the entity id of the entity the client was told to sync
		 */
		public MissedCreate(String entityID) {
			this.entityID = entityID;
		}
	}
	
	public static class MissedDelete {
		public String entityID;
		
		public MissedDelete() {}
		
		/**
		 * A MissedDelete is sent from the client to the server when the client has not been told to sync an object that should be receiving syncs.
		 * The implication is that the client missed a delete entity packet from the server.
		 * @param entityID: the entity id of the entity the client expected to be synced
		 */
		public MissedDelete(String entityID) {
			this.entityID = entityID;
		}
	}
	
	public static class StartSpectate {
		
		/**
		 * A StartSpectate is sent from the client to the server when the client chooses to spectate a game 
		 */
		public StartSpectate() {}
	}
	
	public static class EndSpectate {
		
		/**
		 * An EndSpectate is sent from the client to the server when the client chooses to stop spectating a game 
		 */
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
		
		/**
		 * A LatencyAck is sent from the server to the client as a response to a LatencySyn. The time is used to calculate the client's ping.
		 */
		public LatencyAck() {}
	}
	
	public static class SyncExtraResultsInfo {
		public UserDto[] users;

		public SyncExtraResultsInfo() {}
		
		/**
		 * A SyncExtraResultsInfo is sent from the server to the client when they enter the results screen.
		 * @param users: This contains information about the match performance
		 */
		public SyncExtraResultsInfo(UserDto[] users) {
			this.users = users;
		}
	}
	
	public static class SyncTyping {
		public String entityID;
		
		public SyncTyping() {}
		
		/**
		 * A LatencyAck is sent from the client to the server when they type in the message window.
		 * This is also sent from the server to the client to indicate which players are currently typing.
		 * @param entityID: this is the id of the player that is currently typing.
		 */
		public SyncTyping(String entityID) {
			this.entityID = entityID;
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

	public static class SyncAssignedTeams {
		public AlignmentFilter[] teams;

		public SyncAssignedTeams() {}

		/**
		 * A SyncAssignedTeams is sent from the server to the client when a new level is loaded with auto-assigned teams.
		 * The client must receive the team assignments to process their ui
		 * @param teams: an array of teams
		 */
		public SyncAssignedTeams(AlignmentFilter[] teams) {
			this.teams = teams;
		}
	}

	public static class SyncObjectiveMarker {
		public String entityID;
		public boolean displayOnScreen;
		public boolean displayOffScreen;
		public Sprite icon;

		public SyncObjectiveMarker() {}

		/**
		 * A SyncObjectiveMarker is sent from the server to the client when the objective marker is set to a non-event
		 * entity. The client sets their objective marker to match the packet
		 * @param entityID: ID of the entity to mark
		 * @param displayOffScreen: should the marker be displayed when the target is off screen?
		 * @param displayOnScreen: should the marker be displayed when the target is on screen?
		 * @param icon: what icon should be used for the marker?
		 */
		public SyncObjectiveMarker(String entityID, boolean displayOffScreen, boolean displayOnScreen, Sprite icon) {
			this.entityID = entityID;
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
    	kryo.register(MouseMove.class);
    	kryo.register(LoadLevel.class);
    	kryo.register(ClientLoaded.class);
    	kryo.register(ClientPlayerCreated.class);
    	kryo.register(ClientStartTransition.class);
    	kryo.register(ClientFinishRespawn.class);
    	kryo.register(SyncScore.class);
    	kryo.register(CreateEntity.class);
    	kryo.register(CreateEnemy.class);
		kryo.register(DeleteEntity.class);
		kryo.register(DeletePlayer.class);
    	kryo.register(CreateEvent.class);
    	kryo.register(CreatePickup.class);
    	kryo.register(SyncPickup.class);
    	kryo.register(ActivateEvent.class);
    	kryo.register(CreatePlayer.class);
		kryo.register(SyncPlayerStats.class);
		kryo.register(SyncServerLoadout.class);
    	kryo.register(SyncClientLoadout.class);
    	kryo.register(CreateParticles.class);
    	kryo.register(CreateRagdoll.class);
    	kryo.register(SyncEntity.class);
    	kryo.register(SyncSchmuck.class);
    	kryo.register(SyncPlayerSelf.class);
    	kryo.register(SyncPlayerAll.class);
    	kryo.register(SyncParticles.class);
    	kryo.register(SyncParticlesExtra.class);
    	kryo.register(SyncUI.class);
    	kryo.register(SyncShader.class);
    	kryo.register(SyncSoundSingle.class);
    	kryo.register(CreateSound.class);
    	kryo.register(SyncSound.class);
    	kryo.register(SyncMusic.class);
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
		kryo.register(SyncAssignedTeams.class);
		kryo.register(SyncObjectiveMarker.class);

		kryo.register(int[].class);
		kryo.register(Vector2.class);
		kryo.register(Vector3.class);
		kryo.register(UnlockLevel.class);
		kryo.register(UnlockArtifact.class);
		kryo.register(UnlockArtifact[].class);
		kryo.register(UnlockActives.class);
		kryo.register(UnlockCharacter.class);
		kryo.register(UnlockEquip.class);
		kryo.register(UnlockEquip[].class);
		kryo.register(AlignmentFilter.class);
		kryo.register(AlignmentFilter[].class);
		kryo.register(Loadout.class);
		kryo.register(TransitionState.class);
		kryo.register(DialogType.class);
		kryo.register(PlayerAction.class);
		kryo.register(PlayerAction[].class);
		kryo.register(ObjectSyncLayers.class);
		kryo.register(alignType.class);
		kryo.register(EnemyType.class);
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

		kryo.register(ArrayList.class);
	}
}
