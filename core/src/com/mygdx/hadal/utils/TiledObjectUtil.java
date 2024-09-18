package com.mygdx.hadal.utils;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.SpriteConstants;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.*;
import com.mygdx.hadal.event.Event.eventSyncTypes;
import com.mygdx.hadal.event.hub.*;
import com.mygdx.hadal.event.modes.*;
import com.mygdx.hadal.event.prefab.*;
import com.mygdx.hadal.event.saves.*;
import com.mygdx.hadal.event.ui.*;
import com.mygdx.hadal.event.utility.*;
import com.mygdx.hadal.schmucks.entities.ClientIllusion;
import com.mygdx.hadal.server.EventDto;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.PlayState.ObjectLayer;

import java.util.UUID;

/**
 * This util parses a Tiled file into an in-game map.
 * @author Fricieweitz Flogory
 */
public class TiledObjectUtil {
	
	/**
	 * Parses objects to create walls and stuff.
	 * @param state: The state to add the created walls to.
	 * @param objects: The list of Tiled objects to parse through
	 */
    public static void parseTiledObjectLayer(PlayState state, MapObjects objects) {
        for (MapObject object : objects) {
            ChainShape shape;

            //Atm, we only parse PolyLines into solid walls and dropthrough walls
            if (object instanceof PolylineMapObject) {
                shape = createPolyline((PolylineMapObject) object);
            } else {
            	continue;
        	}

            if (object.getProperties().get("dropthrough", false, boolean.class)) {
				WallDropthrough wall = new WallDropthrough(state, shape);
				if (state instanceof ClientState clientState) {
					clientState.addEntity(wall.getEntityID(), wall, false, ObjectLayer.STANDARD);
				}
            } else {
				Wall wall = new Wall(state, shape);
				if (state instanceof ClientState clientState) {
					clientState.addEntity(wall.getEntityID(), wall, false, ObjectLayer.STANDARD);
				}
			}
        }
    }

    //these maps keep track of all the triggers and connected events
    private static final ObjectMap<String, Event> triggeredEvents = new ObjectMap<>();
    private static final ObjectMap<Event, String> triggeringEvents = new ObjectMap<>();
    private static final ObjectMap<TriggerMulti, String> multiTriggeringEvents = new ObjectMap<>();
    private static final ObjectMap<TriggerCond, String> condTriggeringEvents = new ObjectMap<>();
    private static final ObjectMap<TriggerRedirect, String> redirectTriggeringEvents = new ObjectMap<>();
    private static final ObjectMap<MovingPoint, String> movePointConnections = new ObjectMap<>();
    private static final ObjectMap<ChoiceBranch, String> choiceBranchOptions = new ObjectMap<>();
	private static final ObjectMap<String, Prefabrication> prefabrications = new ObjectMap<>();

	public static final Array<SpawnerWave> waveSpawners = new Array<>();
	/**
     * Parses Tiled objects into in game events
     * @param state: Current GameState
     * @param objects: The list of Tiled objects to parse into events.
     */
    public static void parseTiledEventLayer(PlayState state, MapObjects objects) {
    	for (MapObject object : objects) {
			Event e = parseTiledEvent(state, object, !state.isServer());

			if (state instanceof ClientState clientState && e != null) {
				clientState.addEntity(e.getEntityID(), e, false, ObjectLayer.STANDARD);
			}
    	}
    }

	private static final Vector2 position = new Vector2();
	private static final Vector2 size = new Vector2();
	/**
     * This parses a single tiled map object into an event
     * @param state: The Playstate that the event will be placed into
     * @param object: The map object to parse
	 * @param checkIndependence: If true, clients skip non-independent events. (when parsing file, but not synced events)
     * @return the parsed event
     */
    public static Event parseTiledEvent(PlayState state, MapObject object, boolean checkIndependence) {
		
    	RectangleMapObject current = (RectangleMapObject) object;
		Rectangle rect = current.getRectangle();
		rect.getCenter(position);
		rect.getSize(size);

		Event e = null;
		if (checkIndependence) {
			if (object.getProperties().get("independent", boolean.class) != null) {
				if (object.getProperties().get("independent", boolean.class)) {
					e = parseTiledEventServerOnly(state, object);
				}
			}
		} else {
			e = parseTiledEventServerOnly(state, object);
		}
		if (null == e) {
			e = parseTiledEventClientIndependent(state, object);
		}

		if (null == e) {
			genPrefab(state, object, rect);
		}

		setParsedTiledEventProperties(e, object);

		return e;
    }

	/**
	 * This parses a single map object into an event and is used for events generated from prefabs or map modes
	 */
	public static Event parseAddTiledEvent(PlayState state, MapObject object) {
		Event e = parseTiledEvent(state, object, false);
		if (state instanceof ClientState clientState && e != null) {
			clientState.addEntity(e.getEntityID(), e, false, ObjectLayer.STANDARD);
		}
		return e;
	}

	/**
	 * This is like parseAddTiledEvent, except for events that are received from the server and require a UUID to receive
	 * packets that sync or reference the event.
	 */
	public static Event parseAddTiledEventWithUUID(PlayState state, MapObject object, UUID entityID, boolean synced) {
		Event e = parseTiledEvent(state, object, false);
		if (state instanceof ClientState clientState && e != null) {
			clientState.addEntity(entityID, e, synced, ObjectLayer.STANDARD);
		}
		return e;
	}

	/**
	 * This creates and adds a specific event corresponding to an object in the map.
	 * This function handles events that are typically only created by the server; usually synced events
	 */
	private static Event parseTiledEventServerOnly(PlayState state, MapObject object) {
		Event e = null;

		//Go through every event type to create events
		switch (object.getName()) {
			case "Destr_Obj" -> e = new DestructableBlock(state, position, size,
					object.getProperties().get("Hp", 100.0f, float.class),
					object.getProperties().get("static", true, boolean.class));
			case "SeeSaw" -> e = new SeeSawPlatform(state, position, size);
			case "Scale" -> e = new ScalePlatform(state, position, size,
					object.getProperties().get("minHeight", -1.0f, float.class),
					object.getProperties().get("density", 0.5f, float.class));
			case "ObjectiveSpawn" -> e = new SpawnerObjective(state, position, size);
		}

		return e;
	}

	/**
	 * This creates and adds a specific event corresponding to an object in the map.
	 * This function handles events that are typically independent between thte client and server.
	 * As of 1.0.9d, this is most of the events in the game.
	 */
	private static Event parseTiledEventClientIndependent(PlayState state, MapObject object) {
		Event e = null;

		//Go through every event type to create events
		switch (object.getName()) {
			case "Dropthrough" -> e = new DropThroughPlatform(state, position, size);
			case "Platform" -> e = new Platform(state, position, size,
					object.getProperties().get("restitution", 0.0f, float.class),
					object.getProperties().get("wall", true, boolean.class),
					object.getProperties().get("player", true, boolean.class),
					object.getProperties().get("hbox", true, boolean.class),
					object.getProperties().get("event", true, boolean.class),
					object.getProperties().get("enemy", true, boolean.class),
					object.getProperties().get("teamIndex", -1, Integer.class));
			case "Displacer" -> {
				Vector2 power = new Vector2(
						object.getProperties().get("displaceX", 0.0f, float.class),
						object.getProperties().get("displaceY", 0.0f, float.class));
				e = new Displacer(state, position, size, power);
			}
			case "Current" -> {
				Vector2 power = new Vector2(
						object.getProperties().get("currentX", 0.0f, float.class),
						object.getProperties().get("currentY", 0.0f, float.class));
				e = new Currents(state, position, size, power);
			}
			case "Spring" -> {
				Vector2 power = new Vector2(
						object.getProperties().get("springX", 0.0f, float.class),
						object.getProperties().get("springY", 0.0f, float.class));
				e = new Spring(state, position, size, power);
			}
			case "Buzzsaw" -> e = new Buzzsaw(state, position, size,
					object.getProperties().get("damage", 0.0f, float.class),
					object.getProperties().get("filter", (short) 0, short.class));
			case "Poison" -> e = new Poison(state, position, size,
					object.getProperties().get("particle", "POISON", String.class),
					object.getProperties().get("damage", 0.0f, float.class),
					object.getProperties().get("draw", true, boolean.class),
					object.getProperties().get("filter", (short) 0, short.class));
			case "Heal" -> e = new HealingArea(state, position, size,
					object.getProperties().get("heal", 0.0f, float.class),
					object.getProperties().get("filter", (short) 0, short.class));
			case "ParticleField" -> e = new ParticleField(state, position, size,
					Particle.valueOf(object.getProperties().get("particle", "NOTHING", String.class)),
					object.getProperties().get("speed", 1.0f, float.class),
					object.getProperties().get("duration", 1.0f, float.class),
					object.getProperties().get("scale", 1.0f, float.class),
					object.getProperties().get("color", "NOTHING", String.class),
					object.getProperties().get("team", -1, Integer.class));
			case "MovePoint" -> {
				e = new MovingPoint(state, position, size,
						object.getProperties().get("speed", 1.0f, float.class),
						object.getProperties().get("pause", false, boolean.class),
						object.getProperties().get("syncConnected", false, boolean.class));
				movePointConnections.put((MovingPoint) e, object.getProperties().get("connections", "", String.class));
			}
			case "TouchPortal" -> e = new PortalTouch(state, position, size);
			case "Dummy" -> e = new PositionDummy(state, position, size,
					object.getProperties().get("dummyId", "", String.class));
			case "Rotator" -> e = new Rotator(state,
					object.getProperties().get("continuous", true, boolean.class),
					object.getProperties().get("angle", 0.0f, float.class));
			case "Text" -> e = new Text(state, position, size,
					object.getProperties().get("text", String.class),
					object.getProperties().get("scale", 0.5f, float.class));
			case "Start" -> {
				e = new StartPoint(state, position, size,
						object.getProperties().get("startId", "", String.class),
						object.getProperties().get("teamIndex", 0, Integer.class));
				state.addSavePoint((StartPoint) e);

				//As a quirk of start points, their triggered id is set to a unique value based on their location
				//This is so clients will know which start point the server tells them they are spawning at.
				e.setTriggeredID(getStartTriggeredId(position.x, position.y));
				triggeredEvents.put(e.getTriggeredID(), e);
			}
			case "Switch" -> e = new Switch(state, position, size);
			case "Sensor" -> e = new Sensor(state, position, size,
					object.getProperties().get("player", true, boolean.class),
					object.getProperties().get("hbox", false, boolean.class),
					object.getProperties().get("event", false, boolean.class),
					object.getProperties().get("enemy", false, boolean.class),
					object.getProperties().get("gravity", 0.0f, float.class),
					object.getProperties().get("cooldown", 0.0f, float.class),
					object.getProperties().get("collision", false, boolean.class),
					object.getProperties().get("pickup", false, boolean.class));
			case "Timer" -> e = new Timer(state,
					object.getProperties().get("interval", 0.0f, float.class),
					object.getProperties().get("startTime", 0.0f, float.class),
					object.getProperties().get("startOn", true, boolean.class));
			case "Counter" -> e = new Counter(state,
					object.getProperties().get("count", int.class),
					object.getProperties().get("countStart", 0, int.class));
			case "Multitrigger" -> {
				e = new TriggerMulti(state);
				multiTriggeringEvents.put((TriggerMulti) e,
						object.getProperties().get("triggeringId", "", String.class));
			}
			case "Condtrigger" -> {
				e = new TriggerCond(state,
						object.getProperties().get("start", "", String.class));
				condTriggeringEvents.put((TriggerCond) e,
						object.getProperties().get("triggeringId", "", String.class));
			}
			case "Alttrigger" -> e = new TriggerAlt(state,
					object.getProperties().get("message", "", String.class));
			case "Redirecttrigger" -> {
				e = new TriggerRedirect(state);
				redirectTriggeringEvents.put((TriggerRedirect) e,
						object.getProperties().get("blameId", "", String.class));
			}
			case "UI" -> e = new UIChanger(state,
					object.getProperties().get("tags", "", String.class),
					object.getProperties().get("clear", true, boolean.class));
			case "Game" -> e = new GameChanger(state,
					object.getProperties().get("score", 0, int.class),
					object.getProperties().get("timer", 0.0f, float.class),
					object.getProperties().get("timerIncr", 0.0f, float.class),
					object.getProperties().get("changeTimer", true, boolean.class));
			case "Camera" -> e = new CameraChanger(state,
					object.getProperties().get("zoom", 1.0f, float.class),
					object.getProperties().get("offsetX", 0.0f, float.class),
					object.getProperties().get("offsetY", 0.0f, float.class));
			case "Shader" -> e = new ShaderChanger(state,
					object.getProperties().get("shader", String.class));
			case "Bounds" -> e = new CameraBounder(state, position, size,
					object.getProperties().get("right", false, boolean.class),
					object.getProperties().get("left", false, boolean.class),
					object.getProperties().get("up", false, boolean.class),
					object.getProperties().get("down", false, boolean.class),
					object.getProperties().get("spectator", false, boolean.class));
			case "Shake" -> e = new CameraShaker(state,
					object.getProperties().get("shake", 0.0f, float.class),
					object.getProperties().get("duration", 0.0f, float.class),
					object.getProperties().get("interval", 0.1f, float.class));
			case "Sound" -> e = new SoundEmitter(state, position, size,
					object.getProperties().get("sound", String.class),
					object.getProperties().get("float", 1.0f, float.class),
					object.getProperties().get("global", true, boolean.class),
					object.getProperties().get("universal", true, boolean.class));
			case "Objective" -> e = new ObjectiveChanger(state,
					object.getProperties().get("displayOffScreen", false, boolean.class),
					object.getProperties().get("displayOnScreen", false, boolean.class),
					object.getProperties().get("displayClearCircle", false, boolean.class),
					object.getProperties().get("icon", "CLEAR_CIRCLE_ALERT", String.class));
			case "Player" -> e = new PlayerChanger(state,
					object.getProperties().get("hp", 0.0f, float.class),
					object.getProperties().get("fuel", 0.0f, float.class),
					object.getProperties().get("ammo", 0.0f, float.class));
			case "StatChange" -> e = new StatusInflicter(state,
					object.getProperties().get("stat", 0, int.class),
					object.getProperties().get("amount", 0.0f, float.class),
					object.getProperties().get("duration", 0.0f, float.class));
			case "Particle" -> e = new ParticleCreator(state,
					Particle.valueOf(object.getProperties().get("particle", String.class)),
					object.getProperties().get("duration", 0.0f, float.class),
					object.getProperties().get("startOn", false, Boolean.class));
			case "SchmuckSpawn" -> e = new SpawnerSchmuck(state, position, size,
					object.getProperties().get("enemyId", String.class),
					object.getProperties().get("amount", 1, int.class),
					object.getProperties().get("limit", 0, int.class),
					object.getProperties().get("extra", 0, int.class),
					object.getProperties().get("delay", 1.0f, float.class),
					object.getProperties().get("boss", false, boolean.class),
					object.getProperties().get("bossname", "", String.class));
			case "WaveSpawn" -> {
				e = new SpawnerWave(state, position, size,
						object.getProperties().get("extra", 0, int.class),
						object.getProperties().get("tag", "STANDARD", String.class));
				waveSpawners.add((SpawnerWave) e);
			}
			case "WaveSpawnController" -> e = new SpawnerWaveController(state);
			case "HboxSpawn" -> e = new SpawnerHitbox(state, position, size,
					new Vector2(object.getProperties().get("sizeX", float.class),
							object.getProperties().get("sizeY", float.class)),
					object.getProperties().get("lifespan", float.class),
					new Vector2(object.getProperties().get("veloX", 0.0f, float.class),
							object.getProperties().get("veloY", 0.0f, float.class)),
					object.getProperties().get("sensor", true, boolean.class),
					object.getProperties().get("sprite", "NOTHING", String.class),
					object.getProperties().get("particle", "NOTHING", String.class),
					object.getProperties().get("gravity", 1.0f, float.class),
					object.getProperties().get("restitution", 0.0f, float.class),
					object.getProperties().get("friction", 1.0f, float.class),
					object.getProperties().get("damage", 0.0f, float.class),
					object.getProperties().get("knockback", 0.0f, float.class),
					object.getProperties().get("dieWall", true, boolean.class),
					object.getProperties().get("dieSchmuck", true, boolean.class),
					object.getProperties().get("adjustangle", true, boolean.class));
			case "ScrapSpawn" -> e = new SpawnerScrap(state, position, size,
					object.getProperties().get("scrap", 0, int.class));
			case "EventClone" -> e = new EventCloner(state, position, size);
			case "EventDelete" -> e = new EventDeleter(state);
			case "EventMove" -> e = new EventMover(state, position, size,
					object.getProperties().get("gravity", -1.0f, float.class));
			case "SpriteChange" -> e = new SpriteChanger(state,
					object.getProperties().get("newSprite", String.class),
					object.getProperties().get("mode", "NORMAL", String.class),
					object.getProperties().get("still", false, boolean.class),
					object.getProperties().get("frame", 0, int.class),
					object.getProperties().get("speed", 0.8f, float.class),
					object.getProperties().get("align", "NONE", String.class),
					object.getProperties().get("scale", -1.0f, float.class));
			case "QuestChange" -> e = new QuestChanger(state,
					object.getProperties().get("quest", String.class),
					object.getProperties().get("change", 0, int.class));
			case "QuestCheck" -> e = new QuestChecker(state,
					object.getProperties().get("quest", String.class),
					object.getProperties().get("check", 0, int.class));
			case "ItemUnlock" -> e = new ItemUnlocker(state,
					object.getProperties().get("type", String.class),
					object.getProperties().get("item", String.class));
			case "UnlockCheck" -> e = new UnlockChecker(state,
					object.getProperties().get("type", String.class),
					object.getProperties().get("item", String.class),
					object.getProperties().get("unlock", false, Boolean.class));
			case "PlayerMove" -> e = new PlayerMover(state,
					object.getProperties().get("all", false, boolean.class),
					object.getProperties().get("exclude", false, boolean.class),
					object.getProperties().get("respawn", true, boolean.class));
			case "PlayerAlign" -> e = new PlayerAlignmentChanger(state,
					object.getProperties().get("pvp", true, boolean.class),
					object.getProperties().get("filter", (float) BodyConstants.PLAYER_HITBOX, float.class));
			case "WrapPortal" -> e = new PortalWrap(state, position, size,
					object.getProperties().get("axis", true, boolean.class),
					object.getProperties().get("direction", false, boolean.class));
			case "CurrentTemp" -> {
				Vector2 power = new Vector2(
						object.getProperties().get("currentX", 0.0f, float.class),
						object.getProperties().get("currentY", 0.0f, float.class));
				e = new Currents(state, position, size, power,
						object.getProperties().get("duration", 0.0f, float.class));
			}
			case "SpringTemp" -> {
				Vector2 power = new Vector2(
						object.getProperties().get("springX", 0.0f, float.class),
						object.getProperties().get("springY", 0.0f, float.class));
				e = new Spring(state, position, size, power,
						object.getProperties().get("duration", 0.0f, float.class));
			}
			case "Equip" -> e = new PickupEquip(state, position,
					object.getProperties().get("pool", "", String.class));
			case "Dialog" -> e = new Dialog(state,
					object.getProperties().get("textId", String.class),
					object.getProperties().get("dialogType", "DIALOG", String.class));
			case "End" -> e = new End(state,
					object.getProperties().get("text", "", String.class),
					object.getProperties().get("victory", true, boolean.class),
					object.getProperties().get("incrementWins", true, boolean.class));
			case "Warp" -> e = new LevelWarp(state,
					object.getProperties().get("level", String.class),
					object.getProperties().get("reset", false, Boolean.class),
					object.getProperties().get("startId", "", String.class));
			case "PoisonTemp" -> e = new Poison(state, position, size,
					object.getProperties().get("particle", "POISON", String.class),
					object.getProperties().get("damage", 0.0f, float.class),
					object.getProperties().get("duration", 0.0f, float.class),
					state.getWorldDummy(),
					object.getProperties().get("draw", true, boolean.class),
					object.getProperties().get("filter", (short) 0, short.class),
					DamageSource.valueOf(object.getProperties().get("source", "MAP_POISON", String.class)));
			case "HealTemp" -> e = new HealingArea(state, position, size,
					object.getProperties().get("heal", 0.0f, float.class),
					object.getProperties().get("duration", 0.0f, float.class),
					state.getWorldDummy(),
					object.getProperties().get("filter", (short) 0, short.class));
			case "Pusher" -> e = new Pusher(state,
					object.getProperties().get("xPush", 0.0f, float.class),
					object.getProperties().get("yPush", 0.0f, float.class));
			case "FootballGoal" -> e = new FootballGoal(state, position, size,
					object.getProperties().get("teamIndex", 0, Integer.class));
			case "FootballSpawn" -> e = new FootballSpawner(state, position, size);
			case "FlagSpawn" -> e = new FlagSpawner(state, position, size,
					object.getProperties().get("teamIndex", 0, Integer.class));
			case "FlagBlock" -> e = new FlagBlocker(state, position, size,
					object.getProperties().get("teamIndex", 0, Integer.class));
			case "CandySpawn" -> e = new TrickorTreatBucket(state, position, size,
					object.getProperties().get("teamIndex", 0, Integer.class),
					object.getProperties().get("mirror", false, Boolean.class));
			case "PickupDelete" -> e = new PickupDestoyer(state, position, size);
			case "Armory" -> e = new Armory(state, position, size,
					object.getProperties().get("title", "ARMORY", String.class),
					object.getProperties().get("tag", "ARMORY", String.class),
					object.getProperties().get("unlock", true, Boolean.class),
					object.getProperties().get("closeOnLeave", true, Boolean.class));
			case "Reliquary" -> e = new Reliquary(state, position, size,
					object.getProperties().get("title", "RELIQUARY", String.class),
					object.getProperties().get("tag", "RELIQUARY", String.class),
					object.getProperties().get("unlock", true, Boolean.class),
					object.getProperties().get("closeOnLeave", true, Boolean.class));
			case "Arcanery" -> e = new Arcanery(state, position, size,
					object.getProperties().get("title", "ARCANERY", String.class),
					object.getProperties().get("tag", "ARCANERY", String.class),
					object.getProperties().get("unlock", true, Boolean.class),
					object.getProperties().get("closeOnLeave", true, Boolean.class));
			case "Dormitory" -> e = new Dormitory(state, position, size,
					object.getProperties().get("title", "DORMITORY", String.class),
					object.getProperties().get("tag", "DORMITORY", String.class),
					object.getProperties().get("unlock", true, Boolean.class),
					object.getProperties().get("closeOnLeave", true, Boolean.class));
			case "Navigation" -> e = new Navigations(state, position, size,
					object.getProperties().get("title", "NAVIGATIONS", String.class),
					object.getProperties().get("tag", "NAVIGATIONS", String.class),
					object.getProperties().get("level", "", String.class),
					object.getProperties().get("unlock", true, Boolean.class),
					object.getProperties().get("closeOnLeave", true, Boolean.class));
			case "NavigationMultiplayer" -> e = new NavigationsMultiplayer(state, position, size,
					object.getProperties().get("title", "NAVIGATIONS", String.class),
					object.getProperties().get("tag", "NAVIGATIONS", String.class),
					object.getProperties().get("closeOnLeave", true, Boolean.class),
					object.getProperties().get("modes", "", String.class));
			case "Quartermaster" -> e = new Quartermaster(state, position, size,
					object.getProperties().get("title", "QUARTERMASTER", String.class),
					object.getProperties().get("tag", "QUARTERMASTER", String.class),
					object.getProperties().get("unlock", true, Boolean.class),
					object.getProperties().get("closeOnLeave", true, Boolean.class),
					object.getProperties().get("shopId", String.class));
			case "ChoiceBranch" -> {
				e = new ChoiceBranch(state, position, size,
						object.getProperties().get("title", "Choice", String.class),
						object.getProperties().get("optionNames", "", String.class),
						object.getProperties().get("closeAfterSelect", false, boolean.class),
						object.getProperties().get("closeOnLeave", true, Boolean.class));
				choiceBranchOptions.put((ChoiceBranch) e, object.getProperties().get("options", "", String.class));
			}
			case "Painter" -> e = new Painter(state, position, size,
					object.getProperties().get("title", "TEXT_TEAM_COLORS", String.class),
					object.getProperties().get("tag", "PAINTER", String.class),
					object.getProperties().get("unlock", false, boolean.class),
					object.getProperties().get("closeOnLeave", true, Boolean.class));
			case "Wallpaper" -> e = new Wallpaper(state, position, size,
					object.getProperties().get("title", "WALLPAPER", String.class),
					object.getProperties().get("tag", "WALLPAPER", String.class),
					object.getProperties().get("unlock", false, boolean.class),
					object.getProperties().get("closeOnLeave", true, Boolean.class));
			case "Outfitter" -> e = new Outfitter(state, position, size,
					object.getProperties().get("title", "OUTFITTER", String.class),
					object.getProperties().get("tag", "OUTFITTER", String.class),
					object.getProperties().get("unlock", false, boolean.class),
					object.getProperties().get("closeOnLeave", true, Boolean.class));
			case "Haberdasher" -> e = new Haberdasher(state, position, size,
					object.getProperties().get("title", "HABERDASHER", String.class),
					object.getProperties().get("tag", "HABERDASHER", String.class),
					object.getProperties().get("unlock", false, boolean.class),
					object.getProperties().get("closeOnLeave", true, Boolean.class));
			case "Vending" -> e = new Vending(state, position, size,
					object.getProperties().get("title", "VENDING", String.class),
					object.getProperties().get("tag", "VENDING", String.class),
					object.getProperties().get("numWeapons", 0, Integer.class),
					object.getProperties().get("numArtifacts", 6, Integer.class),
					object.getProperties().get("numMagics", 0, Integer.class));
			case "Disposal" -> e = new Disposal(state, position, size,
					object.getProperties().get("title", "DISPOSAL", String.class),
					object.getProperties().get("tag", "DISPOSAL", String.class));
			case "Arcade" -> e = new ArcadeMarquis(state, position, size);
		}

		if (null != e) {
			if (object.getProperties().get("default", true, Boolean.class)) {
				e.setIndependent(true);
			}
		}

		return e;
	}

	/**
	 * This sets a number of properties on a newly created event; usually universal properties
	 */
	private static void setParsedTiledEventProperties(Event e, MapObject object) {

    	if (null == e) { return; }

    	if (null != object.getProperties().get("triggeringId", String.class)) {
			triggeringEvents.put(e, object.getProperties().get("triggeringId", String.class));
		}
		if (null != object.getProperties().get("triggeredId", String.class)) {
			triggeredEvents.put(object.getProperties().get("triggeredId", String.class), e);
			e.setTriggeredID(object.getProperties().get("triggeredId", String.class));
		}
		if (object.getProperties().get("default", true, Boolean.class)) {
			e.loadDefaultProperties();
		}
		if (null != object.getProperties().get("sprite", String.class)) {
			if (null != object.getProperties().get("frame", int.class)) {
				e.setEventSprite(
						Sprite.valueOf(object.getProperties().get("sprite", String.class)),
						true,
						object.getProperties().get("frame", 0, int.class),
						object.getProperties().get("speed", SpriteConstants.SPRITE_ANIMATION_SPEED, float.class),
						PlayMode.valueOf(object.getProperties().get("mode", "NORMAL", String.class)));
			} else {
				e.setEventSprite(Sprite.valueOf(object.getProperties().get("sprite", String.class)));
			}
		}
		if (null != object.getProperties().get("scale", float.class)) {
			e.setScale(object.getProperties().get("scale", float.class));
		}
		if (null != object.getProperties().get("align", String.class)) {
			e.setScaleAlign(ClientIllusion.alignType.valueOf(object.getProperties().get("align", String.class)));
		}
		if (null != object.getProperties().get("syncServer", String.class)) {
			e.setServerSyncType(eventSyncTypes.valueOf(object.getProperties().get("syncServer", String.class)));
		}
		if (null != object.getProperties().get("syncClient", String.class)) {
			e.setClientSyncType(eventSyncTypes.valueOf(object.getProperties().get("syncClient", String.class)));
		}
		if (null != object.getProperties().get("synced", boolean.class)) {
			e.setSynced(object.getProperties().get("synced", boolean.class));
		}
		if (null != object.getProperties().get("cullable", boolean.class)) {
			e.setCullable(object.getProperties().get("cullable", boolean.class));
		}
		if (null != (object.getProperties().get("independent", boolean.class))) {
			e.setIndependent(object.getProperties().get("independent", boolean.class));
		}
		if (null != object.getProperties().get("bot_health_pickup", boolean.class)) {
			e.setBotHealthPickup(object.getProperties().get("bot_health_pickup", boolean.class));
		}
		if (null != object.getProperties().get("gravity", float.class)) {
			e.setGravity(object.getProperties().get("gravity", float.class));
		}
		if (null != object.getProperties().get("particle_amb", String.class)) {
			float offsetX = object.getProperties().get("particle_offsetX", 0.0f, float.class);
			float offsetY = object.getProperties().get("particle_offsetY", 0.0f, float.class);
			e.addAmbientParticle(Particle.valueOf(object.getProperties().get("particle_amb", String.class)),
					offsetX, offsetY);
		}
		if (null != object.getProperties().get("particle_std", String.class)) {
			e.setStandardParticle(Particle.valueOf(object.getProperties().get("particle_std", String.class)));
		}

		//set the event's blueprint and data representation. This is used for sending client event info
		e.setBlueprint((RectangleMapObject) object);
		e.setDto(new EventDto((RectangleMapObject) object));
	}

    /**
     * Generate a prefab combination of events
     * @param state: Play State the events will be created in
     * @param object: MapObject of the prefab
     * @param rect: dimensions of the prefab
     */
    public static void genPrefab(PlayState state, MapObject object, Rectangle rect) {

    	if (!object.getName().equals("Prefab")) { return; }

    	Prefabrication p = switch (object.getProperties().get("prefabId", "", String.class)) {
			case "Door" -> new Door(state, rect.width, rect.height, rect.x, rect.y,
				object.getProperties().get("triggeredId", "", String.class),
				object.getProperties().get("speed", 1.0f, float.class),
				object.getProperties().get("xDisplace", 0, int.class),
				object.getProperties().get("yDisplace", 0, int.class));
			case "Spawner" -> new SpawnerPickupTimed(state, rect.width, rect.height, rect.x, rect.y,
				object.getProperties().get("interval", 1.0f, float.class),
				object.getProperties().get("type", 0, int.class),
				object.getProperties().get("power", 0.0f, float.class));
			case "SpawnerTriggered" -> new SpawnerPickupTriggered(state, rect.width, rect.height, rect.x, rect.y,
				object.getProperties().get("triggeredId", "", String.class),
				object.getProperties().get("type", 0, int.class),
				object.getProperties().get("power", 0.0f, float.class));
			case "SpawnerUnlock" -> new SpawnerUnlockable(state, rect.width, rect.height, rect.x, rect.y,
				object.getProperties().get("triggeredId", "", String.class),
				object.getProperties().get("triggeringId", "", String.class),
				object.getProperties().get("type", "", String.class),
				object.getProperties().get("name", "", String.class));
			case "ScrapCache" -> new ScrapCache(state, rect.width, rect.height, rect.x, rect.y,
				object.getProperties().get("triggeredId", "", String.class),
				object.getProperties().get("triggeringId", "", String.class),
				object.getProperties().get("cacheId", "", String.class),
				object.getProperties().get("amount", 0, int.class));
			case "Camera" -> new CameraPanZone(state, rect.width, rect.height, rect.x, rect.y,
				object.getProperties().get("zoom1", 1.0f, float.class),
				object.getProperties().get("zoom2", 1.0f, float.class),
				object.getProperties().get("align", 0, int.class),
				object.getProperties().get("point1", "", String.class),
				object.getProperties().get("point2", "", String.class));
			case "Alternator" -> new EventAlternatorZone(state,	rect.width, rect.height, rect.x, rect.y,
				object.getProperties().get("align", 0, int.class),
				object.getProperties().get("event1", "", String.class),
				object.getProperties().get("event2", "", String.class));
			case "Limit" -> new Limiter(state,
				object.getProperties().get("triggeredId", "", String.class),
				object.getProperties().get("triggeringId", "", String.class),
				object.getProperties().get("limit", 0, int.class));
			case "Cooldown" -> new Cooldowner(state,
				object.getProperties().get("triggeredId", "", String.class),
				object.getProperties().get("triggeringId", "", String.class),
				object.getProperties().get("cooldown", 0.0f, float.class));
			case "Weapon" -> new SpawnerWeapon(state, rect.width, rect.height, rect.x, rect.y,
				object.getProperties().get("triggeredId", "", String.class),
				object.getProperties().get("triggeringId", "", String.class),
				object.getProperties().get("pool", "", String.class));
			case "LeverActivate" -> new LeverActivate(state, rect.width, rect.height, rect.x, rect.y,
				object.getProperties().get("triggeringId", "", String.class));
			case "LeverActivateOnce" -> new LeverActivateOnce(state, rect.width, rect.height, rect.x, rect.y,
				object.getProperties().get("triggeringId", "", String.class));
			default -> null;
		};

		if (null != p) {
        	p.generateParts();
    	}
    	prefabrications.put(object.getProperties().get("triggeredId", "", String.class), p);
    }

	private static int nextId = 0;
	/**
     * When a prefab is created, its triggerIds are generated dynamically using this to ensure that there are no repeats.
     */
    public static String getPrefabTriggerIdUnsynced() {
    	String id = "prefabTriggerId" + nextId;
    	nextId++;
    	return id;
    }

	/**
	 * If the events within a prefab must be synced, their id must be consistent
	 * We generate it based on location, name and a specific tag designated by the prefab.
	 */
	public static String getPrefabTriggerIdSynced(String prefabId, String tag, float x, float y) {
        return "prefabTriggerId" + prefabId + tag + x + y;
	}

	/**
	 * Similar to synced prefab events, start points require a consistent triggered id between client and server.
	 * We generate one based on event location; don't put multiple start points on the same spot with no id.
	 */
	public static String getStartTriggeredId(float x, float y) {
		return "startTriggerId" + x + y;
	}

    //An event with this id is run when the game timer concludes
	private static final String globalTimer = "runOnGlobalTimerConclude";

	//An event with this id is run when a spectating host presses the interact button
	private static final String globalSpectatorActivation = "globalSpectatorActivation";
    /**
     * This method parses special events from the Tiled map.
     * Certain events have a special id that makes them tagged for special use in the play state.
     * Be sure not to use the above static strings as and for a normal event
     * @param state: this is the state we are adding the newly parsed event to
     */
    public static void parseDesignatedEvents(PlayState state) {
    	for (String key : triggeredEvents.keys()) {
    		if (globalTimer.equals(key)) {
    			state.setGlobalTimer(triggeredEvents.get(key));
    		}
			if (globalSpectatorActivation.equals(key)) {
				state.setSpectatorActivation(triggeredEvents.get(key));
			}
    	}
    }
    
    /**
     * This parses the triggers of all events that have been added to any of the trigger lists
     */
    public static void parseTiledTriggerLayer() {
    	
    	//for all triggering effects, connect them to the event they trigger
    	for (Event key : triggeringEvents.keys()) {
    		if (!"".equals(triggeringEvents.get(key))) {
        		key.setConnectedEvent(triggeredEvents.get(triggeringEvents.get(key), null));
    		}
    	}
    	
    	//for all multi-triggers, connect them to each event that they trigger
    	for (TriggerMulti key : multiTriggeringEvents.keys()) {
    		for (String id : multiTriggeringEvents.get(key).split(",")) {
    			if (!"".equals(id)) {
    				key.addTrigger(triggeredEvents.get(id, null));
    			}
    		}
    	}
    	
    	//for all conditional triggers, connect them to each event that they can possibly trigger
    	for (TriggerCond key : condTriggeringEvents.keys()) {
    		for (String id : condTriggeringEvents.get(key).split(",")) {
    			if (!"".equals(id)) {
    				key.addTrigger(id, triggeredEvents.get(id, null));
    			}
    		}
    	}
    	
    	//for all redirect triggers, connect them to the event that it blames when it triggers another event
    	for (TriggerRedirect key : redirectTriggeringEvents.keys()) {
    		if (!"".equals(redirectTriggeringEvents.get(key))) {
        		key.setBlame(triggeredEvents.get(redirectTriggeringEvents.get(key), null));
    		}
    	}
    	
    	//for all move points, connect them to all events that move along with it
    	for (MovingPoint key : movePointConnections.keys()) {
    		for (String id : movePointConnections.get(key).split(",")) {
    			if (!"".equals(id)) {
        			key.addConnection(triggeredEvents.get(id, null));

        			//for prefabs, connect to the event parts that are specified to be moveable
        			Prefabrication prefab = prefabrications.get(id, null);
        			if (null != prefab) {
        				for (String e : prefab.getConnectedEvents()) {
        					Event movingPrefabPart = triggeredEvents.get(e, null);
        					key.addConnection(movingPrefabPart);
						}
        			}
    			}
    		}
    	}
    	
    	//for all choice branches, connect them to each event that corresponds to a choosable option
    	for (ChoiceBranch branch : choiceBranchOptions.keys()) {
    		String[] options = choiceBranchOptions.get(branch).split(",");
    		for (int i = 0; i < options.length; i++) {
    			if (!"".equals(options[i])) {
        			branch.addOption(branch.getOptionNames()[i], triggeredEvents.get(options[i], null));
    			}
    		}
    	}  
    }
    
    /**
     * This parses a single event's triggers and updates existing events with connections.
     * @param e: Event to add triggers for
     */
    public static void parseTiledSingleTrigger(Event e) {
    	MapObject blueprint = e.getBlueprint();
    	String triggeringId =  blueprint.getProperties().get("triggeringId", "", String.class);
    	String triggeredId =  blueprint.getProperties().get("triggeredId", "", String.class);

    	//connect e to all the events that trigger it
    	for (Event key : triggeringEvents.keys()) {
    		if (!"".equals(triggeringEvents.get(key)) && triggeredId.equals(triggeringEvents.get(key))) {
        		key.setConnectedEvent(e);
    		}
    	}
    	
    	//connect e to the event that it triggers
    	if (!"".equals(triggeringId)) {
    		e.setConnectedEvent(triggeredEvents.get(triggeringId, null));
    	}

		String myId;

		//connect e to any redirect events that blame it for triggers
		for (TriggerRedirect key : redirectTriggeringEvents.keys()) {
			if (!"".equals(redirectTriggeringEvents.get(key)) && triggeredId.equals(redirectTriggeringEvents.get(key))) {
				key.setBlame(e);
			}
		}

		//if e is a redirect trigger, connect it to the event that it blames when it triggers another event
		if (e instanceof TriggerRedirect trigger) {
			myId = redirectTriggeringEvents.get(trigger);
			if (null != myId) {
				if (!myId.isEmpty()) {
					trigger.setBlame(triggeredEvents.get(myId, null));
				}
			}
		}

		//connect e to any multi-triggers that trigger it
		for (TriggerMulti key : multiTriggeringEvents.keys()) {
			for (String id : multiTriggeringEvents.get(key).split(",")) {
				if (!"".equals(id) && triggeredId.equals(id)) {
					key.addTrigger(e);
				}
			}
		}

		//if e is a multi-trigger, connect it to all events that it triggers
		if (e instanceof TriggerMulti trigger) {
			myId = multiTriggeringEvents.get(trigger);
			if (null != myId) {
				for (String id : myId.split(",")) {
					if (!"".equals(id)) {
						trigger.addTrigger(triggeredEvents.get(id, null));
					}
				}
			}
		}

    	//connect e to any conditional triggers that can trigger it
    	for (TriggerCond key : condTriggeringEvents.keys()) {
    		for (String id : condTriggeringEvents.get(key).split(",")) {
    			if (!"".equals(id) && triggeredId.equals(id)) {
    				key.addTrigger(id, triggeredEvents.get(id, null));
    			}
    		}
    	}
    	
    	//if e is a conditional trigger, connect it to each event that it can trigger
		if (e instanceof TriggerCond trigger) {
			myId = condTriggeringEvents.get(trigger);
			if (null != myId) {
				for (String id : myId.split(",")) {
					if (!"".equals(id)) {
						trigger.addTrigger(id, triggeredEvents.get(id, null));
					}
				}
			}
		}

    	//connect e to any move points that connect to it.
		// We don't need a case for when e is a move point b/c the client doesn't process that and we haven't needed to clone move points yet.
    	for (MovingPoint key : movePointConnections.keys()) {
    		for (String id : movePointConnections.get(key).split(",")) {
    			if (!"".equals(id) && triggeredId.equals(id)) {
        			key.addConnection(e);
    			}
    		}
    	} 
    	
    	//connect e to any choice branches that can trigger it
    	for (ChoiceBranch branch : choiceBranchOptions.keys()) {
    		String[] options = choiceBranchOptions.get(branch).split(",");
    		for (int i = 0; i < options.length; i++) {
    			if (!"".equals(options[i]) && triggeredId.equals(options[i])) {
        			branch.addOption(branch.getOptionNames()[i], e);
    			}
    		}
    	} 
    	
    	//if e is a choice branch, connect it to each event that it can trigger
		if (e instanceof ChoiceBranch choice) {
			myId = choiceBranchOptions.get(choice);
			if (null != myId) {
				String[] options = myId.split(",");
				for (int i = 0; i < options.length; i++) {
					if (!"".equals(options[i])) {
						choice.addOption(choice.getOptionNames()[i], triggeredEvents.get(options[i], null));
					}
				}
			}
		}
    }
    
    /**
     * Add a single event to the world, alongside triggers
     * @param state: Playstate to add event to
     * @param object: Map object of the event to add.
     * @return The newly created event
     */
    public static Event parseSingleEventWithTriggers(PlayState state, MapObject object) {
    	Event e = parseAddTiledEvent(state, object);
    	parseTiledSingleTrigger(e);
    	return e;
    }

	/**
	 * Similar to parseSingleEventWithTriggers, except used for synced events received from server that need the UUID
	 * in order to receive sync packets
	 */
	public static Event parseSingleEventWithTriggersWithUUID(PlayState state, MapObject object, UUID entityID, boolean synced) {
		Event e = parseAddTiledEventWithUUID(state, object, entityID, synced);
		parseTiledSingleTrigger(e);
		return e;
	}

    /**
     * Clear all trigger lists. This is done upon initializing a playstate to clear previous triggers.
     */
    public static void clearEvents() {
    	triggeredEvents.clear();
    	triggeringEvents.clear();
    	multiTriggeringEvents.clear();
    	condTriggeringEvents.clear();
    	redirectTriggeringEvents.clear();
    	movePointConnections.clear();
		choiceBranchOptions.clear();
		waveSpawners.clear();
    	prefabrications.clear();
    }

    public static ObjectMap<String, Event> getTriggeredEvents() { return triggeredEvents; }

	/**
     * Helper function for parseTiledObjectLayer that creates line bodies
     * @param polyline: Tiled map object
     * @return Box2d body
     */
    private static ChainShape createPolyline(PolylineMapObject polyline) {
        float[] vertices = polyline.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < worldVertices.length; i++) {
            worldVertices[i] = new Vector2(vertices[i * 2] / Constants.PPM, vertices[i * 2 + 1] / Constants.PPM);
        }
        ChainShape cs = new ChainShape();
        cs.createChain(worldVertices);
        return cs;
    }
}
