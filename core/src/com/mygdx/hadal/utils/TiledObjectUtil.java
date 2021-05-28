package com.mygdx.hadal.utils;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.*;
import com.mygdx.hadal.event.Event.eventSyncTypes;
import com.mygdx.hadal.event.hub.*;
import com.mygdx.hadal.event.prefab.*;
import com.mygdx.hadal.event.saves.*;
import com.mygdx.hadal.event.ui.*;
import com.mygdx.hadal.event.utility.*;
import com.mygdx.hadal.server.EventDto;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.ClientState.ObjectSyncLayers;
import com.mygdx.hadal.states.PlayState;

import java.util.HashMap;
import java.util.Map;

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
                new WallDropthrough(state, shape);
            } else {
                new Wall(state, shape);
            }
        }
    }

	/**
	 * Client version of object parsing is identical except different method of adding event to world.
	 */
	public static void parseTiledObjectLayerClient(ClientState state, MapObjects objects) {
        for (MapObject object : objects) {
            ChainShape shape;

            //Atm, we only parse PolyLines into solid walls and dropthrough walls
            if(object instanceof PolylineMapObject) {
                shape = createPolyline((PolylineMapObject) object);
            } else {
            	continue;
        	}

            if (object.getProperties().get("dropthrough", false, boolean.class)) {
            	WallDropthrough wall = new WallDropthrough(state, shape);
                state.addEntity(wall.getEntityID().toString(), wall, false, ObjectSyncLayers.STANDARD);
            } else {
            	Wall wall = new Wall(state, shape);
                state.addEntity(wall.getEntityID().toString(), wall, false, ObjectSyncLayers.STANDARD);
            }
        }
    }

    //these maps keep track of all the triggers and connected events
    private static final Map<String, Event> triggeredEvents = new HashMap<>();
    private static final Map<Event, String> triggeringEvents = new HashMap<>();
    private static final Map<TriggerMulti, String> multiTriggeringEvents = new HashMap<>();
    private static final Map<TriggerCond, String> condTriggeringEvents = new HashMap<>();
    private static final Map<TriggerRedirect, String> redirectTriggeringEvents = new HashMap<>();
    private static final Map<MovingPoint, String> movePointConnections = new HashMap<>();
    private static final Map<ChoiceBranch, String> choiceBranchOptions = new HashMap<>();
    private static final Map<String, Prefabrication> prefabrications = new HashMap<>();

    /**
     * Parses Tiled objects into in game events
     * @param state: Current GameState
     * @param objects: The list of Tiled objects to parse into events.
     */
    public static void parseTiledEventLayer(PlayState state, MapObjects objects) {
    	for (MapObject object : objects) {
    		parseTiledEvent(state, object);
    	}
    }

	/**
	 * client's version of this method only adds events that are marked as "independent"
	 * This usually applies to static events like springs or currents
	 */
	public static void parseTiledEventLayerClient(ClientState state, MapObjects objects) {
		for (MapObject object : objects) {
			if (object.getProperties().get("independent", boolean.class) != null) {
				if (object.getProperties().get("independent", boolean.class)) {
					Event e = parseTiledEvent(state, object);
					if (e != null) {
						state.addEntity(e.getEntityID().toString(), e, false, ObjectSyncLayers.STANDARD);
					}
				}
			}
		}
	}

    /**
     * This parses a single tiled map object into an event
     * @param state: The Playstate that the event will be placed into
     * @param object: The map object to parse
     * @return the parsed event
     */
    public static Event parseTiledEvent(PlayState state, MapObject object) {
		
    	RectangleMapObject current = (RectangleMapObject) object;
		Rectangle rect = current.getRectangle();
		Vector2 position = new Vector2();
		Vector2 size = new Vector2();
		rect.getCenter(position);
		rect.getSize(size);
		
		Event e = null;

		//Go through every event type to create events
		switch (object.getName()) {
			case "Start" -> {
				e = new StartPoint(state, position, size,
					object.getProperties().get("startId", "", String.class),
					object.getProperties().get("teamIndex", 0, Integer.class));
				state.addSavePoint((StartPoint) e);
			}
			case "Switch" -> e = new Switch(state, position, size);
			case "Sensor" -> e = new Sensor(state, position, size,
				object.getProperties().get("player", true, boolean.class),
				object.getProperties().get("hbox", false, boolean.class),
				object.getProperties().get("event", false, boolean.class),
				object.getProperties().get("enemy", false, boolean.class),
				object.getProperties().get("gravity", 0.0f, float.class),
				object.getProperties().get("collision", false, boolean.class));
			case "Timer" -> e = new Timer(state,
				object.getProperties().get("interval", 0.0f, float.class),
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
			case "Dummy" -> e = new PositionDummy(state, position, size,
				object.getProperties().get("dummyId", "", String.class));
			case "UI" -> e = new UIChanger(state,
				object.getProperties().get("tags", "", String.class),
				object.getProperties().get("clear", true, boolean.class));
			case "Game" -> e = new GameChanger(state,
				object.getProperties().get("lives", 0, int.class),
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
			case "Sound" -> e = new SoundEmitter(state, position, size,
				object.getProperties().get("sound", String.class),
				object.getProperties().get("float", 1.0f, float.class),
				object.getProperties().get("global", true, boolean.class),
				object.getProperties().get("universal", true, boolean.class));
			case "Objective" -> e = new ObjectiveChanger(state,
				object.getProperties().get("displayOffScreen", false, boolean.class),
				object.getProperties().get("displayOnScreen", false, boolean.class),
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
			case "ParticleField" -> e = new ParticleField(state, position, size,
				Particle.valueOf(object.getProperties().get("particle", "NOTHING", String.class)),
				object.getProperties().get("speed", 1.0f, float.class),
				object.getProperties().get("duration", 1.0f, float.class),
				object.getProperties().get("scale", 1.0f, float.class));
			case "SchmuckSpawn" -> e = new SpawnerSchmuck(state, position, size,
				object.getProperties().get("enemyId", String.class),
				object.getProperties().get("amount", 1, int.class),
				object.getProperties().get("limit", 0, int.class),
				object.getProperties().get("extra", 0, int.class),
				object.getProperties().get("delay", 1.0f, float.class),
				object.getProperties().get("boss", false, boolean.class),
				object.getProperties().get("bossname", "", String.class));
			case "WaveSpawn" -> e = new SpawnerWave(state, position, size,
				object.getProperties().get("point", 1, int.class),
				object.getProperties().get("extra", 0, int.class),
				object.getProperties().get("tag", "", String.class));
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
				object.getProperties().get("exclude", false, boolean.class));
			case "PlayerAlign" -> e = new PlayerAlignmentChanger(state,
				object.getProperties().get("pvp", true, boolean.class),
				object.getProperties().get("filter", (float) Constants.PLAYER_HITBOX, float.class));
			case "TouchPortal" -> e = new PortalTouch(state, position, size);
			case "WrapPortal" -> e = new PortalWrap(state, position, size,
				object.getProperties().get("axis", true, boolean.class),
				object.getProperties().get("direction", false, boolean.class));
			case "Text" -> e = new Text(state, position, size,
				object.getProperties().get("text", String.class),
				object.getProperties().get("scale", 0.5f, float.class));
			case "CurrentTemp" -> {
				Vector2 power = new Vector2(
					object.getProperties().get("currentX", 0.0f, float.class),
					object.getProperties().get("currentY", 0.0f, float.class));
				e = new Currents(state, position, size, power,
					object.getProperties().get("duration", 0.0f, float.class));
			}
			case "Current" -> {
				Vector2 power = new Vector2(
					object.getProperties().get("currentX", 0.0f, float.class),
					object.getProperties().get("currentY", 0.0f, float.class));
				e = new Currents(state, position, size, power);
			}
			case "Displacer" -> {
				Vector2 power = new Vector2(
					object.getProperties().get("displaceX", 0.0f, float.class),
					object.getProperties().get("displaceY", 0.0f, float.class));
				e = new Displacer(state, position, size, power);
			}
			case "SpringTemp" -> {
				Vector2 power = new Vector2(
					object.getProperties().get("springX", 0.0f, float.class),
					object.getProperties().get("springY", 0.0f, float.class));
				e = new Spring(state, position, size, power,
					object.getProperties().get("duration", 0.0f, float.class));
			}
			case "Spring" -> {
				Vector2 power = new Vector2(
					object.getProperties().get("springX", 0.0f, float.class),
					object.getProperties().get("springY", 0.0f, float.class));
				e = new Spring(state, position, size, power);
			}
			case "Equip" -> e = new PickupEquip(state, position,
				object.getProperties().get("pool", "", String.class));
			case "Dropthrough" -> e = new DropThroughPlatform(state, position, size);
			case "Dialog" -> e = new Dialog(state,
				object.getProperties().get("textId", String.class),
				object.getProperties().get("dialogType", "DIALOG", String.class));
			case "End" -> e = new End(state,
				object.getProperties().get("text", "", String.class),
				object.getProperties().get("victory", true, boolean.class));
			case "Destr_Obj" -> e = new DestructableBlock(state, position, size,
				object.getProperties().get("Hp", 100, int.class),
				object.getProperties().get("static", true, boolean.class));
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
				object.getProperties().get("filter", (short) 0, short.class));
			case "Poison" -> e = new Poison(state, position, size,
				object.getProperties().get("particle", "POISON", String.class),
				object.getProperties().get("damage", 0.0f, float.class),
				object.getProperties().get("draw", true, boolean.class),
				object.getProperties().get("filter", (short) 0, short.class));
			case "HealTemp" -> e = new HealingArea(state, position, size,
				object.getProperties().get("heal", 0.0f, float.class),
				object.getProperties().get("duration", 0.0f, float.class),
				state.getWorldDummy(),
				object.getProperties().get("filter", (short) 0, short.class));
			case "Heal" -> e = new HealingArea(state, position, size,
				object.getProperties().get("heal", 0.0f, float.class),
				object.getProperties().get("filter", (short) 0, short.class));
			case "Buzzsaw" -> e = new Buzzsaw(state, position, size,
				object.getProperties().get("damage", 0.0f, float.class),
				object.getProperties().get("filter", (short) 0, short.class));
			case "MovePoint" -> {
				e = new MovingPoint(state, position, size,
					object.getProperties().get("speed", 1.0f, float.class),
					object.getProperties().get("pause", false, boolean.class),
					object.getProperties().get("syncConnected", true, boolean.class));
				movePointConnections.put((MovingPoint) e, object.getProperties().get("connections", "", String.class));
			}
			case "Rotator" -> e = new Rotator(state,
				object.getProperties().get("continuous", true, boolean.class),
				object.getProperties().get("angle", 0.0f, float.class));
			case "SeeSaw" -> e = new SeeSawPlatform(state, position, size);
			case "Scale" -> e = new ScalePlatform(state, position, size,
				object.getProperties().get("minHeight", -1.0f, float.class),
				object.getProperties().get("density", 0.5f, float.class));
			case "Pusher" -> e = new Pusher(state,
				object.getProperties().get("xPush", 0.0f, float.class),
				object.getProperties().get("yPush", 0.0f, float.class));
			case "Platform" -> e = new Platform(state, position, size,
				object.getProperties().get("restitution", 0.0f, float.class),
				object.getProperties().get("wall", true, boolean.class),
				object.getProperties().get("player", true, boolean.class),
				object.getProperties().get("hbox", true, boolean.class),
				object.getProperties().get("event", true, boolean.class),
				object.getProperties().get("enemy", true, boolean.class));
			case "FootballGoal" -> e = new FootballGoal(state, position, size,
				object.getProperties().get("teamIndex", 0, Integer.class));
			case "FootballSpawn" -> e = new FootballSpawner(state, position, size);
			case "FlagSpawn" -> e = new SpawnerFlag(state, position, size,
				object.getProperties().get("teamIndex", 0, Integer.class));
			case "Armory" -> e = new Armory(state, position, size,
				object.getProperties().get("title", "Armory", String.class),
				object.getProperties().get("tag", "ARMORY", String.class),
				object.getProperties().get("unlock", true, Boolean.class),
				object.getProperties().get("closeOnLeave", true, Boolean.class));
			case "Reliquary" -> e = new Reliquary(state, position, size,
				object.getProperties().get("title", "Reliquary", String.class),
				object.getProperties().get("tag", "RELIQUARY", String.class),
				object.getProperties().get("unlock", true, Boolean.class),
				object.getProperties().get("closeOnLeave", true, Boolean.class));
			case "Dispensary" -> e = new Dispensary(state, position, size,
				object.getProperties().get("title", "Dispensary", String.class),
				object.getProperties().get("tag", "DISPENSARY", String.class),
				object.getProperties().get("unlock", true, Boolean.class),
				object.getProperties().get("closeOnLeave", true, Boolean.class));
			case "Dormitory" -> e = new Dormitory(state, position, size,
				object.getProperties().get("title", "Dormitory", String.class),
				object.getProperties().get("tag", "DORMITORY", String.class),
				object.getProperties().get("unlock", true, Boolean.class),
				object.getProperties().get("closeOnLeave", true, Boolean.class));
			case "Navigation" -> e = new Navigations(state, position, size,
				object.getProperties().get("title", "Navigations", String.class),
				object.getProperties().get("tag", "NAVIGATIONS", String.class),
				object.getProperties().get("level", "", String.class),
				object.getProperties().get("unlock", true, Boolean.class),
				object.getProperties().get("closeOnLeave", true, Boolean.class));
			case "NavigationMultiplayer" -> e = new NavigationsMultiplayer(state, position, size,
				object.getProperties().get("title", "Navigations", String.class),
				object.getProperties().get("tag", "NAVIGATIONS", String.class),
				object.getProperties().get("closeOnLeave", true, Boolean.class));
			case "Quartermaster" -> e = new Quartermaster(state, position, size,
				object.getProperties().get("title", "Quartermaster", String.class),
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
				object.getProperties().get("title", "Team Color", String.class),
				object.getProperties().get("tag", "PAINTER", String.class),
				object.getProperties().get("unlock", false, boolean.class),
				object.getProperties().get("closeOnLeave", true, Boolean.class));
			case "Wallpaper" -> e = new Wallpaper(state, position, size,
				object.getProperties().get("title", "Wallpaper", String.class),
				object.getProperties().get("tag", "WALLPAPER", String.class),
				object.getProperties().get("unlock", false, boolean.class),
				object.getProperties().get("closeOnLeave", true, Boolean.class));
			case "Prefab" -> genPrefab(state, object, rect);
		}
		
		//Extra, universal functions to change event sprite properties		
		if (e != null) {
			if (object.getProperties().get("triggeringId", String.class) != null) {
				triggeringEvents.put(e, object.getProperties().get("triggeringId", String.class));
			}
			if (object.getProperties().get("triggeredId", String.class) != null) {
				triggeredEvents.put(object.getProperties().get("triggeredId", String.class), e);
			}
			if (object.getProperties().get("default", true, Boolean.class)) {
				e.loadDefaultProperties();
			}
			if (object.getProperties().get("sprite", String.class) != null) {
				if (object.getProperties().get("frame", int.class) != null) {
					e.setEventSprite(
							Sprite.valueOf(object.getProperties().get("sprite", String.class)), 
							true, 
							object.getProperties().get("frame", 0, int.class), 
							object.getProperties().get("speed", PlayState.spriteAnimationSpeed, float.class), 
							PlayMode.valueOf(object.getProperties().get("mode", "NORMAL", String.class)));
				} else {
					e.setEventSprite(Sprite.valueOf(object.getProperties().get("sprite", String.class)));
				}
			}
			if (object.getProperties().get("scale", float.class) != null) {
				e.setScale(object.getProperties().get("scale", float.class));
			}
			if (object.getProperties().get("align", String.class) != null) {
				e.setScaleAlign(object.getProperties().get("align", String.class));
			}
			if (object.getProperties().get("sync", String.class) != null) {
				e.setSyncType(eventSyncTypes.valueOf(object.getProperties().get("sync", String.class)));
			}
			if (object.getProperties().get("synced", boolean.class) != null) {
				e.setSynced(object.getProperties().get("synced", boolean.class));
			}
			if (object.getProperties().get("cullable", boolean.class) != null) {
				e.setCullable(object.getProperties().get("cullable", boolean.class));
			}
			if (object.getProperties().get("independent", boolean.class) != null) {
				e.setIndependent(object.getProperties().get("independent", boolean.class));
			}
			if (object.getProperties().get("gravity", float.class) != null) {
				e.setGravity(object.getProperties().get("gravity", float.class));
			}
			if (object.getProperties().get("particle_amb", String.class) != null) {
				float offsetX = object.getProperties().get("particle_offsetX", 0.0f, float.class);
				float offsetY = object.getProperties().get("particle_offsetY", 0.0f, float.class);
				e.addAmbientParticle(Particle.valueOf(object.getProperties().get("particle_amb", String.class)),
					offsetX, offsetY);
			}
			if (object.getProperties().get("particle_std", String.class) != null) {
				e.setStandardParticle(Particle.valueOf(object.getProperties().get("particle_std", String.class)));
			}

			//set the event's blueprint and data representation. This is used for sending client event info
			e.setBlueprint((RectangleMapObject) object);
			e.setDto(new EventDto((RectangleMapObject) object));
		}
		return e;
    }
    
    /**
     * Generate a prefab combination of events
     * @param state: Play State the events will be created in
     * @param object: MapObject of the prefab
     * @param rect: dimensions of the prefab
     */
    public static void genPrefab(PlayState state, MapObject object, Rectangle rect) {
    	
    	Prefabrication p = switch (object.getProperties().get("prefabId", "", String.class)) {
			case "Door" -> new Door(state, (int) rect.width, (int) rect.height, (int) rect.x, (int) rect.y,
				object.getProperties().get("triggeredId", "", String.class),
				object.getProperties().get("speed", 1.0f, float.class),
				object.getProperties().get("xDisplace", 0, int.class),
				object.getProperties().get("yDisplace", 0, int.class));
			case "Spawner" -> new SpawnerPickupTimed(state, (int) rect.width, (int) rect.height, (int) rect.x, (int) rect.y,
				object.getProperties().get("interval", 1.0f, float.class),
				object.getProperties().get("type", 0, int.class),
				object.getProperties().get("power", 0.0f, float.class));
			case "SpawnerTriggered" -> new SpawnerPickupTriggered(state, (int) rect.width, (int) rect.height, (int) rect.x,	(int) rect.y,
				object.getProperties().get("triggeredId", "", String.class),
				object.getProperties().get("type", 0, int.class),
				object.getProperties().get("power", 0.0f, float.class));
			case "SpawnerUnlock" -> new SpawnerUnlockable(state, (int) rect.width, (int) rect.height, (int) rect.x, (int) rect.y,
				object.getProperties().get("triggeredId", "", String.class),
				object.getProperties().get("triggeringId", "", String.class),
				object.getProperties().get("type", "", String.class),
				object.getProperties().get("name", "", String.class));
			case "ScrapCache" -> new ScrapCache(state, (int) rect.width, (int) rect.height, (int) rect.x, (int) rect.y,
				object.getProperties().get("triggeredId", "", String.class),
				object.getProperties().get("triggeringId", "", String.class),
				object.getProperties().get("cacheId", "", String.class),
				object.getProperties().get("amount", 0, int.class));
			case "Camera" -> new CameraPanZone(state, (int) rect.width, (int) rect.height, (int) rect.x, (int) rect.y,
				object.getProperties().get("zoom1", 1.0f, float.class),
				object.getProperties().get("zoom2", 1.0f, float.class),
				object.getProperties().get("align", 0, int.class),
				object.getProperties().get("point1", "", String.class),
				object.getProperties().get("point2", "", String.class));
			case "Alternator" -> new EventAlternatorZone(state,	(int) rect.width, (int) rect.height, (int) rect.x, (int) rect.y,
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
			case "Weapon" -> new SpawnerWeapon(state, (int) rect.width, (int) rect.height, (int) rect.x, (int) rect.y,
				object.getProperties().get("triggeredId", "", String.class),
				object.getProperties().get("triggeringId", "", String.class),
				object.getProperties().get("pool", "", String.class));
			case "LeverActivate" -> new LeverActivate(state, (int) rect.width, (int) rect.height, (int) rect.x,	(int) rect.y,
				object.getProperties().get("triggeringId", "", String.class));
			case "LeverActivateOnce" -> new LeverActivateOnce(state, (int) rect.width, (int) rect.height, (int) rect.x, (int) rect.y,
				object.getProperties().get("triggeringId", "", String.class));
			case "PVPSettingSetter" -> new PVPSettingSetter(state);
			case "ArenaSettingSetter" -> new ArenaSettingSetter(state);
			case "SpecialSettingSetter" -> new SpecialSettingSetter(state);
			default -> null;
		};

		if (p != null) {
        	p.generateParts();
    	}
    	prefabrications.put(object.getProperties().get("triggeredId", "", String.class), p);
    }
    
    /*
     * When a prefab is created, its triggerIds are generated dynamically using this to ensure that there are no repeats.
     */
    private static int nextId = 0;
    public static String getPrefabTriggerId() {
    	String id = "prefabTriggerId" + nextId;
    	nextId++;
    	return id;
    }
    
    private static final String globalTimer = "runOnGlobalTimerConclude";
    /**
     * This method parses special events from the Tiled map.
     * Certain events have a special id that makes them tagged for special use in the play state.
     * Be sure not to use the above static strings as and for a normal event
     * @param state: this is the state we are adding the newly parsed event to
     */
    public static void parseDesignatedEvents(PlayState state) {
    	for (String key : triggeredEvents.keySet()) {
    		if (key.equals(globalTimer)) {
    			state.setGlobalTimer(triggeredEvents.get(key));
    		}
    	}
    }
    
    /**
     * This parses the triggers of all events that have been added to any of the trigger lists
     */
    public static void parseTiledTriggerLayer() {
    	
    	//for all triggering effects, connect them to the event they trigger
    	for (Event key : triggeringEvents.keySet()) {
    		if (!triggeringEvents.get(key).equals("")) {
        		key.setConnectedEvent(triggeredEvents.getOrDefault(triggeringEvents.get(key), null));
    		}
    	}
    	
    	//for all multi-triggers, connect them to each event that they trigger
    	for (TriggerMulti key : multiTriggeringEvents.keySet()) {
    		for (String id : multiTriggeringEvents.get(key).split(",")) {
    			if (!id.equals("")) {
    				key.addTrigger(triggeredEvents.getOrDefault(id, null));
    			}
    		}
    	}
    	
    	//for all conditional triggers, connect them to each event that they can possibly trigger
    	for (TriggerCond key : condTriggeringEvents.keySet()) {
    		for (String id : condTriggeringEvents.get(key).split(",")) {
    			if (!id.equals("")) {
    				key.addTrigger(id, triggeredEvents.getOrDefault(id, null));
    			}
    		}
    	}
    	
    	//for all redirect triggers, connect them to the event that it blames when it triggers another event
    	for (TriggerRedirect key : redirectTriggeringEvents.keySet()) {
    		if (!redirectTriggeringEvents.get(key).equals("")) {
        		key.setBlame(triggeredEvents.getOrDefault(redirectTriggeringEvents.get(key), null));
    		}
    	}
    	
    	//for all move points, connect them to all events that move along with it
    	for (MovingPoint key : movePointConnections.keySet()) {
    		for (String id : movePointConnections.get(key).split(",")) {
    			if (!id.equals("")) {
        			key.addConnection(triggeredEvents.getOrDefault(id, null));

        			//for prefabs, connect to the event parts that are specified to be moveable
        			Prefabrication prefab = prefabrications.getOrDefault(id, null);
        			if (prefab != null) {
        				for (String e: prefab.getConnectedEvents()) {
        					key.addConnection(triggeredEvents.getOrDefault(e, null));
        				}
        			}
    			}
    		}
    	}
    	
    	//for all choice branches, connect them to each event that corresponds to a choosable option
    	for (ChoiceBranch branch : choiceBranchOptions.keySet()) {
    		String[] options = choiceBranchOptions.get(branch).split(",");
    		for (int i = 0; i < options.length; i++) {
    			if (!options[i].equals("")) {
        			branch.addOption(branch.getOptionNames()[i], triggeredEvents.getOrDefault(options[i], null));
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
    	for (Event key : triggeringEvents.keySet()) {
    		if (!triggeringEvents.get(key).equals("") && triggeringEvents.get(key).equals(triggeredId)) {
        		key.setConnectedEvent(e);
    		}
    	}
    	
    	//connect e to the event that it triggers
    	if (!triggeringId.equals("")) {
    		e.setConnectedEvent(triggeredEvents.getOrDefault(triggeringId, null));
    	}

		String myId;

		//connect e to any redirect events that blame it for triggers
		for (TriggerRedirect key : redirectTriggeringEvents.keySet()) {
			if (!redirectTriggeringEvents.get(key).equals("") && redirectTriggeringEvents.get(key).equals(triggeredId)) {
				key.setBlame(e);
			}
		}

		//if e is a redirect trigger, connect it to the event that it blames when it triggers another event
		if (e instanceof TriggerRedirect) {
			myId = redirectTriggeringEvents.get(e);
			if (myId != null) {
				if (!myId.equals("")) {
					((TriggerRedirect) e).setBlame(triggeredEvents.getOrDefault(myId, null));
				}
			}
		}

		//connect e to any multi-triggers that trigger it
		for (TriggerMulti key : multiTriggeringEvents.keySet()) {
			for (String id : multiTriggeringEvents.get(key).split(",")) {
				if (!id.equals("") && id.equals(triggeredId)) {
					key.addTrigger(e);
				}
			}
		}

		//if e is a multi-trigger, connect it to all events that it triggers
		if (e instanceof TriggerMulti) {
			myId = multiTriggeringEvents.get(e);
			if (myId != null) {
				for (String id : myId.split(",")) {
					if (!id.equals("")) {
						((TriggerMulti) e).addTrigger(triggeredEvents.getOrDefault(id, null));
					}
				}
			}
		}

    	//connect e to any conditional triggers that can trigger it
    	for (TriggerCond key : condTriggeringEvents.keySet()) {
    		for (String id : condTriggeringEvents.get(key).split(",")) {
    			if (!id.equals("") && id.equals(triggeredId)) {
    				key.addTrigger(id, triggeredEvents.getOrDefault(id, null));
    			}
    		}
    	}
    	
    	//if e is a conditional trigger, connect it to each event that it can trigger
		if (e instanceof TriggerCond) {
			myId = condTriggeringEvents.get(e);
			if (myId != null) {
				for (String id : myId.split(",")) {
					if (!id.equals("")) {
						((TriggerCond) e).addTrigger(id, triggeredEvents.getOrDefault(id, null));
					}
				}
			}
		}

    	//connect e to any move points that connect to it.
		// We don't need a case for when e is a move point b/c the client doesn't process that and we haven't needed to clone move points yet.
    	for (MovingPoint key : movePointConnections.keySet()) {
    		for (String id : movePointConnections.get(key).split(",")) {
    			if (!id.equals("") && id.equals(triggeredId)) {
        			key.addConnection(e);
    			}
    		}
    	} 
    	
    	//connect e to any choice branches that can trigger it
    	for (ChoiceBranch branch : choiceBranchOptions.keySet()) {
    		String[] options = choiceBranchOptions.get(branch).split(",");
    		for (int i = 0; i < options.length; i++) {
    			if (!options[i].equals("") && options[i].equals(triggeredId)) {
        			branch.addOption(branch.getOptionNames()[i], e);
    			}
    		}
    	} 
    	
    	//if e is a choice branch, connect it to each event that it can trigger
		if (e instanceof ChoiceBranch) {
			myId = choiceBranchOptions.get(e);
			if (myId != null) {
				String[] options = myId.split(",");
				for (int i = 0; i < options.length; i++) {
					if (!options[i].equals("")) {
						((ChoiceBranch) e).addOption(((ChoiceBranch) e).getOptionNames()[i], triggeredEvents.getOrDefault(options[i], null));
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
    	Event e = parseTiledEvent(state, object);
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
    	prefabrications.clear();
    }
    
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
