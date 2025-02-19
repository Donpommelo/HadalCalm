package com.mygdx.hadal.server.packets;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.MoveState;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.managers.TransitionManager.TransitionState;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.SoundCreate;
import com.mygdx.hadal.save.*;
import com.mygdx.hadal.schmucks.entities.ClientIllusion.alignType;
import com.mygdx.hadal.schmucks.entities.enemies.EnemyType;
import com.mygdx.hadal.schmucks.entities.helpers.PlayerSpriteHelper.DespawnType;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.EventDto;
import com.mygdx.hadal.users.ScoreManager;
import com.mygdx.hadal.users.StatsManager;
import com.mygdx.hadal.users.User.UserDto;

import java.util.HashMap;

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

	public static class ClientLevelRequest {
		public UnlockLevel level;
		public GameMode mode;
		public HashMap<String, Integer> modeSettings;

		public ClientLevelRequest() {}

		/**
		 * A ClientLevelRequest is sent from Client to Server when a client host wants to transition to a new level
		 * @param level: the new level we transition to
		 */
		public ClientLevelRequest(UnlockLevel level, GameMode mode, HashMap<String, Integer> modeSettings) {
			this.level = level;
			this.mode = mode;
			this.modeSettings = modeSettings;
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
		public boolean skipFade;
		public Vector2 startPosition;
		public ClientStartTransition() {}
		
		/**
		 * A ClientStartTransition is sent from the Server to the Client upon beginning a transition to another state to tell the Client
		 * to transition as well.
		 * Clients receiving this begin fading to black the same way the Server does.
		 * @param state: Are we transitioning to a new level, a gameover screen or whatever else?
		 * @param fadeSpeed: speed of the fade transition
		 * @param fadeDelay: Amount of delay before transition
		 * @param skipFade: Should the client skip fading to the next state? (true for
		 * @param startPosition: For respawning, this is the new spawn location for camera focusing purposes.
		 */
		public ClientStartTransition(TransitionState state, float fadeSpeed, float fadeDelay, boolean skipFade, Vector2 startPosition) {
			this.state = state;
			this.fadeSpeed = fadeSpeed;
			this.fadeDelay = fadeDelay;
			this.skipFade = skipFade;
			this.startPosition = startPosition;
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
		public boolean override;
		public DialogType type;
		public ServerNotification() {}
		
		/**
		 * A ServerNotification is sent from the Server to the Client to make a notification window appear in their dialog box.
		 * A Notification is sent from the Client to the Server to tell it to relay the message to all clients.
		 * @param name: The name that will be displayed in the notification
		 * @param text: The text displayed in the notification
		 * @param override: Can this notification be overriden by other notifications? Just used for initial connect to avoid being overridden by tip
		 * @param type: type of dialog (dialog, system msg, etc)
		 */
		public ServerNotification(String name, String text, boolean override, DialogType type) {
			this.name = name;
			this.text = text;
			this.override = override;
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

	public static class ServerNextMapRequest {

		/**
		 * This is sent from a headless server to the host client when exiting result state.
		 * This prompts the host client to inform the server whether to go to the next map or return to the hub
		 */
		public ServerNextMapRequest() {}
	}

	public static class ClientNextMapResponse {
		public boolean returnToHub;
		public UnlockLevel nextMap;
		public ClientNextMapResponse() {}

		/**
		 * This is sent from a host client to a headless server after exiting results state and being prompted by the server.
		 * This prompts the server to transition from the results state into the next playstate
		 */
		public ClientNextMapResponse(boolean returnToHub, UnlockLevel nextMap) {
			this.returnToHub = returnToHub;
			this.nextMap = nextMap;
		}
	}

	public static class SyncScore {
		public int connID;
		public String name;
		public Loadout loadout;
		public int wins, kills, deaths, assists, score, extraModeScore, lives, currency, ping;
		public boolean spectator;

		public SyncScore() {}
		
		/**
		 * This is sent from the server to the clients to give them their scores for a player whose score changed
		 * @param connID: id of the player whose score is being updated.
		 */
		public SyncScore(int connID, String name, Loadout loadout, int wins, int kills, int deaths, int assists, int score,
						 int extraModeScore, int lives, int currency, int ping, boolean spectator) {
			this.connID = connID;
			this.name = name;
			this.loadout = loadout;
			this.wins = wins;
			this.kills = kills;
			this.deaths = deaths;
			this.assists = assists;
			this.score = score;
			this.extraModeScore = extraModeScore;
			this.lives = lives;
			this.currency = currency;
			this.ping = ping;
			this.spectator = spectator;
		}
	}

	public static class CreateEntity {
		public int entityID;
		public Vector2 size;
		public Vector2 pos;
		public float angle;
        public Sprite sprite;
		public boolean synced;
		public boolean instant;
        public ObjectLayer layer;
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
		public CreateEntity(int entityID, Vector2 size, Vector2 pos, float angle, Sprite sprite, boolean synced, boolean instant,
                            ObjectLayer layer, alignType align) {
			this.entityID = entityID;
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

	public static class CreateEnemy {
		public int entityID;
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
		public CreateEnemy(int entityID, Vector2 pos, EnemyType type, short hitboxFilter, boolean boss, String name) {
			this.entityID = entityID;
            this.pos = pos;
            this.type = type;
            this.hitboxFilter = hitboxFilter;
            this.boss = boss;
            this.name = name;
        }
	}
	
	public static class DeleteEntity {
		public int entityID;
		public float timestamp;
		public DeleteEntity() {}
		
		/**
		 * A DeleteEntity is sent from the Server to the Client to tell the Client to delete an Entity.
		 * @param entityID: ID of the entity to be deleted.
		 * @param timestamp: when this deletion occurred. Used to handle the possibility of packet loss.
		 */
		public DeleteEntity(int entityID, float timestamp) {
			this.entityID = entityID;
			this.timestamp = timestamp;
        }
	}

	public static class DeleteSchmuck extends DeleteEntity {
		public int perpID;
		public DamageSource source;
		public DamageTag[] tags;
		public DeleteSchmuck() {}

		/**
		 * A DeletePlayer is sent from the Server to the Client to tell the Client to delete a player.
		 * This is separate from delete entity to pass along info about the type of death.
		 * @param entityID: ID of the entity to be deleted.
		 * @param perpID: ID of the perp that killed this entity.
		 * @param timestamp: when this deletion occurred. Used to handle the possibility of packet loss.
		 * @param source: source of fatal damage (for death message)
		 * @param tags: tags associated with fatal damage (for death message
		 */
		public DeleteSchmuck(int entityID, int perpID, float timestamp, DamageSource source, DamageTag[] tags) {
			super(entityID, timestamp);
			this.perpID = perpID;
			this.source = source;
			this.tags = tags;
		}
	}

	public static class DeleteClientSelf {
		public int entityID;
		public DamageSource source;
		public DamageTag[] tags;
		public DeleteClientSelf() {}

		/**
		 * A DeleteClientSelf is sent from client to server upon death and cuases the world to count them as dead.
		 * @param entityID: ID of the entity that killed this client.
		 * @param source: The source of damage that got the last hit; for kill text
		 * @param tags: Tags of the damage instance. Used for kill text
		 */
		public DeleteClientSelf(int entityID, DamageSource source, DamageTag[] tags) {
			this.entityID = entityID;
			this.source = source;
			this.tags = tags;
		}
	}

	public static class CreatePlayer {
		public int entityID;
		public int connID;
		public Vector2 startPosition;
		public String name;
		public Loadout loadout;
		public short hitboxFilter;
		public float scaleModifier;
		public boolean dontMoveCamera;
		public boolean pvpOverride;
		public String startTriggeredId;
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
		 * @param pvpOverride: should this player's hitbox always be set to their alignment? (used for hub training room)
		 * @param startTriggeredId: id of the start event to trigger upon creating player
		 */
		public CreatePlayer(int entityID, int connID, Vector2 startPosition, String name, Loadout loadout,
					short hitboxFilter, float scaleModifier, boolean dontMoveCamera, boolean pvpOverride,
					String startTriggeredId) {
			this.entityID = entityID;
            this.connID = connID;
            this.startPosition = startPosition;
            this.name = name;
            this.loadout = loadout;
            this.hitboxFilter = hitboxFilter;
            this.scaleModifier = scaleModifier;
			this.dontMoveCamera = dontMoveCamera;
			this.pvpOverride = pvpOverride;
			this.startTriggeredId = startTriggeredId;
        }
	}
	
	public static class CreateEvent {
		public int entityID;
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
		public CreateEvent(int entityID, EventDto blueprint, boolean synced) {
			this.entityID = entityID;
            this.blueprint = blueprint;
            this.synced = synced;
        }
	}
	
	public static class CreatePickup {
		public int entityID;
        public Vector2 pos;
        public UnlockEquip newPickup;
		public float lifespan;
        public CreatePickup() {}
        
        /**
         * A CreatePickup is sent from the Server to the Client to tell the client to create a new Pickup Event.
		 * This is for Pickups because they have some custom logic to synchronize what drop they represent.
		 * 
		 * @param entityID: ID of the new Pickup.
		 * @param pos: position of the new Pickup
		 * @param newPickup: The pickup that this event should start with.
		 * @param lifespan: lifespan of pickup (only used if pickup is synced)
         */
		public CreatePickup(int entityID, Vector2 pos, UnlockEquip newPickup, float lifespan) {
			this.entityID = entityID;
            this.pos = pos;
            this.newPickup = newPickup;
            this.lifespan = lifespan;
		}
	}

	public static class SyncPickup {
		public int entityID;
		public UnlockEquip newPickup;
		public SyncPickup() {}

		/**
		 * A SyncPickup is sent from the Server to the Client when a Pickup is activated.
		 * Clients receiving this adjust their version of the pickup to hold the new pickup
		 * @param entityID: ID of the activated Pickup
		 * @param newPickup: enum name of the new pickup.
		 */
		public SyncPickup(int entityID, UnlockEquip newPickup) {
			this.entityID = entityID;
			this.newPickup = newPickup;
		}
	}

	public static class SyncPickupTriggered {
		public String triggeredID;
        public UnlockEquip newPickup;
        public SyncPickupTriggered() {}
        
        /**
         * A SyncPickup is sent from the Server to the Client when a Pickup is activated.
         * Clients receiving this adjust their version of the pickup to hold the new pickup
         * @param triggeredID: ID of the activated Pickup
         * @param newPickup: enum name of the new pickup.
         */
		public SyncPickupTriggered(String triggeredID, UnlockEquip newPickup) {
			this.triggeredID = triggeredID;
            this.newPickup = newPickup;
		}
	}

	public static class ActivateEvent {
		public int entityID;
		public int connID;
		public ActivateEvent() {}
		
		/**
		 * A ActivateEvent is sent from the Server to the Client when an Event is activated (For synchronized events).
         * This is used for events that are activated on the Client's end as well. (CameraChangers, Hub Events ... etc)
		 * @param entityID: ID of the activated Pickup
		 * @param connID: The connection id of the player that activated this event
		 */
		public ActivateEvent(int entityID, int connID) {
			this.entityID = entityID;
            this.connID = connID;
        }
	}

	public static class ActivateEventByTrigger {
		public String triggeredID;
		public int connID;
		public ActivateEventByTrigger() {}

		/**
		 * A ActivateEventByTrigger is like an ActivateEvent, but it uses the event's triggeredID instead of entity id
		 * @param triggeredID: triggered ID of the activated Pickup
		 * @param connID: The connection id of the player that activated this event
		 */
		public ActivateEventByTrigger(String triggeredID, int connID) {
			this.triggeredID = triggeredID;
			this.connID = connID;
		}
	}

	public static class CreateParticles {
		public int attachedID;
        public Vector2 pos;
        public boolean attached;
		public Particle particle;
		public boolean startOn;
		public float lifespan;
		public float scale;
		public boolean rotate;
		public float velocity;
		public Vector3 color;
		public CreateParticles() {}
		
		/**
		 * A CreateParticles is sent from the Server to the Client whenever a synced ParticlesEntity is created.
		 * Clients simply create the desired particle entity with all of the listed fields.
		 * Attached information is useful for making most particles sync on create, instead of every engine tick.(unless needed)
		 * @param attachedID: ID of attached entity if it exists and null otherwise
		 * @param pos: Position of particle entity if not attached to another entity. If attached to an entity. this is the offset from the entity's position
		 * @param attached: Is this particleEntity attached to another entity?
		 * @param particle: Particle Effect to be created.
		 * @param startOn: Does this effect start turned on?
		 * @param lifespan: Duration of a non-attached entity.
		 * @param scale: The size multiplier of the particle effect
		 * @param rotate: should this entity rotate to match an attached entity?
		 * @param velocity: the velocity of the particles. (0 means to set the as the default)
		 * @param color: the color tint of the particle
		 */
		public CreateParticles(int attachedID, Vector2 pos, boolean attached, Particle particle, boolean startOn,
		   	float lifespan, float scale, boolean rotate, float velocity, Vector3 color) {
			this.attachedID = attachedID;
			this.pos = pos;
			this.attached = attached;
			this.particle = particle;
			this.startOn = startOn;
			this.lifespan = lifespan;
			this.scale = scale;
			this.rotate = rotate;
			this.velocity = velocity;
			this.color = color;
		}

		public CreateParticles(ParticleCreate particleCreate) {
			if (particleCreate.getAttachedEntity() != null) {
				this.attachedID = particleCreate.getAttachedEntity().getEntityID();
				this.attached = true;
			}
			this.pos = particleCreate.getPosition();
			this.particle = particleCreate.getParticle();
			this.startOn = particleCreate.isStartOn();
			this.lifespan = particleCreate.getLifespan();
			this.scale = particleCreate.getScale();
			this.rotate = particleCreate.isRotate();
			this.velocity = particleCreate.getVelocity();
			this.color = particleCreate.getColorRGB();
		}
	}

	public static class CreateFlag {
		public int entityID;
		public Vector2 pos;
		public int teamIndex;

		public CreateFlag() {}

		/**
		 * A CreateFlag is sent from server to client to create a Flag event for CTF mode.
		 * @param entityID: ID of the newly created Crown
		 * @param pos: The starting position of this event
		 * @param teamIndex: The team alignment that owns this flag
		 */
		public CreateFlag(int entityID, Vector2 pos, int teamIndex) {
			this.entityID = entityID;
			this.pos = pos;
			this.teamIndex = teamIndex;
		}
	}

	public static class CreateCrown {
		public int entityID;
		public Vector2 pos;

		public CreateCrown() {}

		/**
		 * A CreateCrown is sent from server to client to create a Crown event for Kingmaker mode.
		 * @param entityID: ID of the newly created Crown
		 * @param pos: The starting position of this event
		 */
		public CreateCrown(int entityID, Vector2 pos) {
			this.entityID = entityID;
			this.pos = pos;
		}
	}

	public static class CreateGrave {
		public int entityID;
		public int connID;
		public Vector2 pos;
		public float returnMaxTimer;

		public CreateGrave() {}

		/**
		 * A CreateGrave is sent from server to client to create a Gravestone event for coop modes or Resurrectionist modes.
		 * @param entityID: ID of the newly created Gravestone
		 * @param connID: The connID of the user that died and would be resurrected from this event
		 * @param pos: The starting position of this event
		 * @param returnMaxTimer: The amount of time required to revive the player
		 */
		public CreateGrave(int entityID, int connID, Vector2 pos, float returnMaxTimer) {
			this.entityID = entityID;
			this.connID = connID;
			this.pos = pos;
			this.returnMaxTimer = returnMaxTimer;
		}
	}

	public static class RequestStartSyncedEvent {
		public String triggeredID;

		public RequestStartSyncedEvent() {}

		/**
		 * A RequestStartSyncedEvent is sent from the Client to the Server after Rotators or Moving Points are created.
		 * This is so that the Client can receive the event's current position instead of being synced constantly
		 * @param triggeredID: the triggeredID of the event to sync
		 */
		public RequestStartSyncedEvent(String triggeredID) {
			this.triggeredID = triggeredID;
		}
	}

	public static class CreateStartSyncedEvent {
		public float timer;
		public String triggeredID, targetTriggeredID;
		public Vector2 pos, velo;

		public CreateStartSyncedEvent() {}

		/**
		 * A CreateStartSyncedEvent is sent from the Server to the Client after the latter informs through a RequestStartSyncedEvent
		 * that the event was created.
		 * This packet contains the info needed for the client to set the initial state of the event.
		 * @param timer: Server timestamp
		 * @param triggeredID: the triggeredID of the event to sync
		 * @param targetTriggeredID: the event's connected event's id. Used for moving points so they move towards right point.
		 * @param pos: position of the Start-Synced Event
		 * @param velo: Velocity of the Start-Synced Event
		 */
		public CreateStartSyncedEvent(float timer, String triggeredID, String targetTriggeredID, Vector2 pos, Vector2 velo) {
			this.timer = timer;
			this.triggeredID = triggeredID;
			this.targetTriggeredID = targetTriggeredID;
			this.pos = pos;
			this.velo = velo;
		}
	}
	
	public static class SyncUI {
		public float maxTimer;
		public float timer;
		public float timerIncr;
		public int hostID;
		public AlignmentFilter[] teams;
		public int[] scores;
		public SyncUI() {}
		
		/**
		 * A SyncUI is sent from the Server to the Client when the client loads into a level. (To handle mid-round joins)
		 * After joining, the client can handle things like the timer themselves
		 * @param maxTimer: what to set the global game timer to (max)
		 * @param timer: what to set the global game timer to (current)
		 * @param timerIncr: How much should the timer be incrementing by (probably +-1 or 0)
		 * @param hostID: connection id of the current host (can be this client, since server spawns new user before sending this)
		 * @param teams: the list of teams currently active for the match
		 * @param scores: list of scores for each team
		 */
		public SyncUI(float maxTimer, float timer, float timerIncr, int hostID, AlignmentFilter[] teams, int[] scores) {
			this.maxTimer = maxTimer;
			this.timer = timer;
			this.timerIncr = timerIncr;
			this.hostID = hostID;
			this.teams = teams;
			this.scores = scores;
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
		public int attachedID;
		public SoundEffect sound;
		public float lifespan;
		public float volume;
		public float pitch;
		public boolean looped;
		public boolean on;

		public CreateSound() {}
		
		/**
		 * A CreateSound is sent from the Server to the Client to tell the client to create a SoundEntity.
		 * This is distinct from SyncSoundSingle because the sound is attached to an entity that can move/be destroyed etc.
		 * The volume and pan of the sound is dependent on the relative position of the entity.
		 * @param attachedID: schmuck id of the entity that the SchmuckEntity is to attached to
		 * @param sound: The sound effect to play
		 * @param lifespan: duration of sound (if looping and not dependent on attached entity)
		 * @param volume: volume of the sound. 1.0f = full volume.
		 * @param pitch: pitch of the sound. 1.0f - default pitch.
		 * @param looped: does the sound loop?
		 * @param on: does the sound start off on?
		 */
		public CreateSound(int attachedID, SoundEffect sound, float lifespan, float volume, float pitch,
						   boolean looped, boolean on) {
			this.attachedID = attachedID;
			this.sound = sound;
			this.lifespan = lifespan;
			this.volume = volume;
			this.pitch = pitch;
			this.looped = looped;
			this.on = on;
		}

		public CreateSound(SoundCreate soundCreate) {
			this.attachedID = soundCreate.getAttachedEntity().getEntityID();
			this.sound = soundCreate.getSound();
			this.lifespan = soundCreate.getLifespan();
			this.volume = soundCreate.getVolume();
			this.pitch = soundCreate.getPitch();
			this.looped = soundCreate.isLooped();
			this.on = soundCreate.isStartOn();
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
		 * This is also sent from client host to a headless server when settings are changed
		 * @param settings: the host settings to be displayed in the score window ui
		 */
		public SyncSharedSettings(SharedSetting settings) {
			this.settings = settings;
		}
	}

	public static class SyncInitialHeadlessSettings extends SyncSharedSettings {
		public String serverName;

		public SyncInitialHeadlessSettings() {}

		/**
		 * A SyncInitialHeadlessSettings is sent from a host client to the headless server after connecting.
		 * This ensures the starting settings are synced, and also gives the server name
		 */
		public SyncInitialHeadlessSettings(SharedSetting settings, String serverName) {
			super(settings);
			this.serverName = serverName;
		}

	}

	public static class HeadlessHostRequest {

		/**
		 * A HeadlessHostRequest is sent from a headless server to a client host after they connect.
		 * This prompts the client host to send the server their settings and the server name
		 */
		public HeadlessHostRequest() {}
	}

	public static class LatencySyn {
		public float timestamp;
		public int latency;
		
		public LatencySyn() {}
		
		/**
		 * A LatencySyn is sent from the client to the server periodically to check the quality of the network connection.
		 * @param latency: the client's last synced latency
		 * @param timestamp: the client's clientPingTimer. Used to determine elapsed time
		 */
		public LatencySyn(int latency, float timestamp) {
			this.latency = latency;
			this.timestamp = timestamp;
		}
	}

	public static class LatencyAck {
		public float serverTimestamp;
		public float clientTimestamp;

		public LatencyAck() {}

		/**
		 * A LatencyAck is sent from the server to the client as a response to a LatencySyn. The time is used to calculate the client's ping.
		 * @param serverTimestamp: is the server's time at time of response
		 * @param clientTimestamp: the client's timer when they first sent the sync packet
		 */
		public LatencyAck(float serverTimestamp, float clientTimestamp) {
			this.serverTimestamp = serverTimestamp;
			this.clientTimestamp = clientTimestamp;
		}
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
		public int connID;
		public ClientYeet() {}
		/**
		 * A ClientYeet packet is sent from server to client to disconnect the client
		 * It is also sent from a client host to a headless server to indicate a kick
		 * @param connID: this is the id of the player that is getting kicked.
		 */
		public ClientYeet(int connID) {
			this.connID = connID;
		}
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
		public int entityID;
		public HadalColor color;
		public boolean displayOnScreen;
		public boolean displayOffScreen;
		public boolean displayClearCircle;
		public Sprite icon;

		public SyncObjectiveMarker() {}

		/**
		 * A SyncObjectiveMarker is sent from the server to the client when the objective marker is set to a non-event
		 * entity. The client sets their objective marker to match the packet
		 * @param entityID: ID of the entity to mark
		 * @param color: color for the objective marker (1, 1, 1 for no change)
		 * @param displayOffScreen: should the marker be displayed when the target is off screen?
		 * @param displayOnScreen: should the marker be displayed when the target is on screen?
		 * @param icon: what icon should be used for the marker?
		 */
		public SyncObjectiveMarker(int entityID, HadalColor color, boolean displayOffScreen, boolean displayOnScreen,
								   boolean displayClearCircle, Sprite icon) {
			this.entityID = entityID;
			this.color = color;
			this.displayOffScreen = displayOffScreen;
			this.displayOnScreen = displayOnScreen;
			this.displayClearCircle = displayClearCircle;
			this.icon = icon;
		}
	}

	public static class SyncArcadeModeChoices {
		public String[] modeChoices;
		public String[] mapChoices;

		public SyncArcadeModeChoices() {}

		public SyncArcadeModeChoices(String[] modeChoices, String[] mapChoices) {
			this.modeChoices = modeChoices;
			this.mapChoices = mapChoices;
		}
	}

	public static class SyncClientModeVote {
		public int vote;

		public SyncClientModeVote() {}

		public SyncClientModeVote(int vote) {
			this.vote = vote;
		}
	}

	public static class ServerNewHost {
		public int hostID;

		public ServerNewHost() {}

		public ServerNewHost(int hostID) {
			this.hostID = hostID;
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
    	kryo.register(LoadLevel.class);
		kryo.register(ClientLoaded.class);
		kryo.register(ClientLevelRequest.class);
    	kryo.register(ClientPlayerCreated.class);
    	kryo.register(ClientStartTransition.class);
		kryo.register(ServerNextMapRequest.class);
		kryo.register(ClientNextMapResponse.class);
		kryo.register(SyncScore.class);
    	kryo.register(CreateEntity.class);
    	kryo.register(CreateEnemy.class);
		kryo.register(DeleteEntity.class);
		kryo.register(DeleteSchmuck.class);
		kryo.register(DeleteClientSelf.class);
    	kryo.register(CreateEvent.class);
    	kryo.register(CreatePickup.class);
		kryo.register(SyncPickup.class);
		kryo.register(SyncPickupTriggered.class);
		kryo.register(ActivateEvent.class);
		kryo.register(ActivateEventByTrigger.class);
    	kryo.register(CreatePlayer.class);
		kryo.register(CreateParticles.class);
		kryo.register(CreateFlag.class);
		kryo.register(CreateCrown.class);
		kryo.register(CreateGrave.class);
		kryo.register(RequestStartSyncedEvent.class);
		kryo.register(CreateStartSyncedEvent.class);

    	kryo.register(SyncUI.class);
    	kryo.register(SyncSoundSingle.class);
    	kryo.register(CreateSound.class);
    	kryo.register(StartSpectate.class);
		kryo.register(EndSpectate.class);
		kryo.register(SyncSharedSettings.class);
		kryo.register(SyncInitialHeadlessSettings.class);
		kryo.register(HeadlessHostRequest.class);
		kryo.register(LatencySyn.class);
    	kryo.register(LatencyAck.class);
    	kryo.register(SyncExtraResultsInfo.class);
		kryo.register(RemoveScore.class);
		kryo.register(ClientYeet.class);
		kryo.register(SyncEmote.class);
		kryo.register(SyncNotification.class);
		kryo.register(SyncObjectiveMarker.class);
		kryo.register(SyncArcadeModeChoices.class);
		kryo.register(SyncClientModeVote.class);
		kryo.register(ServerNewHost.class);

		kryo.register(PacketsSync.SyncEntity.class);
		kryo.register(PacketsSync.SyncEntityAngled.class);
		kryo.register(PacketsSync.SyncSchmuck.class);
		kryo.register(PacketsSync.SyncSchmuckAngled.class);
		kryo.register(PacketsSync.SyncSchmuckColor.class);
		kryo.register(PacketsSync.SyncPlayerSnapshot.class);
		kryo.register(PacketsSync.SyncClientSnapshot.class);
		kryo.register(PacketsSync.SyncFlag.class);
		kryo.register(PacketsSync.SyncFlagAttached.class);

		kryo.register(PacketsLoadout.SyncWholeLoadout.class);
		kryo.register(PacketsLoadout.SyncLoadoutClient.class);
		kryo.register(PacketsLoadout.SyncEquipClient.class);
		kryo.register(PacketsLoadout.SyncArtifactAddClient.class);
		kryo.register(PacketsLoadout.SyncArtifactRemoveClient.class);
		kryo.register(PacketsLoadout.SyncActiveClient.class);
		kryo.register(PacketsLoadout.SyncCharacterClient.class);
		kryo.register(PacketsLoadout.SyncTeamClient.class);
		kryo.register(PacketsLoadout.SyncCosmeticClient.class);
		kryo.register(PacketsLoadout.SyncEquipServer.class);
		kryo.register(PacketsLoadout.SyncArtifactServer.class);
		kryo.register(PacketsLoadout.SyncActiveServer.class);
		kryo.register(PacketsLoadout.SyncCharacterServer.class);
		kryo.register(PacketsLoadout.SyncTeamServer.class);
		kryo.register(PacketsLoadout.SyncCosmeticServer.class);
		kryo.register(PacketsLoadout.SyncVendingArtifact.class);
		kryo.register(PacketsLoadout.SyncDisposalArtifact.class);
		kryo.register(PacketsLoadout.SyncVendingScrapSpend.class);

		kryo.register(PacketsAttacks.SingleClientIndependent.class);
		kryo.register(PacketsAttacks.SingleClientDependent.class);
		kryo.register(PacketsAttacks.SingleServerIndependent.class);
		kryo.register(PacketsAttacks.SingleServerDependent.class);
		kryo.register(PacketsAttacks.SingleClientIndependentExtra.class);
		kryo.register(PacketsAttacks.SingleClientDependentExtra.class);
		kryo.register(PacketsAttacks.SingleServerIndependentExtra.class);
		kryo.register(PacketsAttacks.SingleServerDependentExtra.class);

		kryo.register(PacketsAttacks.MultiClientIndependent.class);
		kryo.register(PacketsAttacks.MultiClientDependent.class);
		kryo.register(PacketsAttacks.MultiServerIndependent.class);
		kryo.register(PacketsAttacks.MultiServerDependent.class);
		kryo.register(PacketsAttacks.MultiClientIndependentExtra.class);
		kryo.register(PacketsAttacks.MultiClientDependentExtra.class);
		kryo.register(PacketsAttacks.MultiServerIndependentExtra.class);
		kryo.register(PacketsAttacks.MultiServerDependentExtra.class);

		kryo.register(PacketsAttacks.SyncedAttackNoHbox.class);
		kryo.register(PacketsAttacks.SyncedAttackNoHboxServer.class);
		kryo.register(PacketsAttacks.SyncedAttackNoHboxExtra.class);
		kryo.register(PacketsAttacks.SyncedAttackNoHboxExtraServer.class);

		kryo.register(int[].class);
		kryo.register(float[].class);
		kryo.register(long[].class);
		kryo.register(String[].class);
		kryo.register(Vector2.class);
		kryo.register(Vector2[].class);
		kryo.register(Vector3.class);
		kryo.register(Particle.class);
		kryo.register(HadalColor.class);
		kryo.register(SoundEffect.class);
		kryo.register(UnlockLevel.class);
		kryo.register(UnlockLevel[].class);
		kryo.register(UnlockArtifact.class);
		kryo.register(UnlockArtifact[].class);
		kryo.register(UnlockActives.class);
		kryo.register(UnlockCharacter.class);
		kryo.register(UnlockEquip.class);
		kryo.register(UnlockEquip[].class);
		kryo.register(AlignmentFilter.class);
		kryo.register(AlignmentFilter[].class);
		kryo.register(UnlockCosmetic.class);
		kryo.register(UnlockCosmetic[].class);
		kryo.register(GameMode.class);
		kryo.register(Loadout.class);
		kryo.register(TransitionState.class);
		kryo.register(DialogType.class);
		kryo.register(PlayerAction.class);
		kryo.register(PlayerAction[].class);
		kryo.register(ObjectLayer.class);
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
		kryo.register(DamageSource.class);
		kryo.register(DamageTag.class);
		kryo.register(DamageTag[].class);
		kryo.register(UserDto.class);
		kryo.register(UserDto[].class);
		kryo.register(ScoreManager.class);
		kryo.register(StatsManager.class);

		kryo.register(Array.class);
		kryo.register(Object[].class);
		kryo.register(HashMap.class);
	}
}
