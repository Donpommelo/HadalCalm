package com.mygdx.hadal.utils;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.*;
import com.mygdx.hadal.event.Event.eventSyncTypes;
import com.mygdx.hadal.event.hub.*;
import com.mygdx.hadal.event.prefab.*;
import com.mygdx.hadal.event.utility.*;
import com.mygdx.hadal.states.PlayState;

/**
 * This util parses a Tiled file into an in-game map.
 * @author Zachary Tu
 *
 */
public class TiledObjectUtil {
	
	/**
	 * Parses objects to create walls and stuff.
	 * @param world: The Box2d world to add the created walls to.
	 * @param objects: The list of Tiled objects to parse through
	 */
    public static void parseTiledObjectLayer(World world, MapObjects objects) {
        for(MapObject object : objects) {
            Shape shape;

            //Atm, we only parse PolyLines into solid walls
            if(object instanceof PolylineMapObject) {
                shape = createPolyline((PolylineMapObject) object);
            } else {
            	continue;
        	}

            Body body;
            BodyDef bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.StaticBody;
            body = world.createBody(bdef);
            body.createFixture(shape, 1.0f);
            Filter filter = new Filter();
			filter.categoryBits = (short) (Constants.BIT_WALL);
			filter.maskBits = (short) (Constants.BIT_SENSOR | Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE);
            body.getFixtureList().get(0).setFilterData(filter);
                        
            shape.dispose();
        }
    }
    
    static Map<String, Event> triggeredEvents = new HashMap<String, Event>();
    static Map<Event, String> triggeringEvents = new HashMap<Event, String>();
    static Map<TriggerMulti, String> multiTriggeringEvents = new HashMap<TriggerMulti, String>();
    static Map<TriggerCond, String> condTriggeringEvents = new HashMap<TriggerCond, String>();
    static Map<TriggerRedirect, String> redirectTriggeringEvents = new HashMap<TriggerRedirect, String>();
    static Map<MovingPlatform, String> platformConnections = new HashMap<MovingPlatform, String>();

    /**
     * Parses Tiled objects into in game events
     * @param state: Current GameState
	 * @param world: The Box2d world to add the created events to.
     * @param camera: The camera to pass to the created events.
     * @param rays: The rayhandler to pass to the created events.
     * @param objects: The list of Tiled objects to parse into events.
     */
    public static void parseTiledEventLayer(PlayState state, MapObjects objects) {
    	for(MapObject object : objects) {
    		parseTiledEvent(state, object);
    	}
    }
    
    /**
     * This parses a single tiled map object into an event
     * @param state: The playstate that the event will be placed into
     * @param object: The map object to parse
     * @return
     */
    public static Event parseTiledEvent(PlayState state, MapObject object) {
		
    	RectangleMapObject current = (RectangleMapObject)object;
		Rectangle rect = current.getRectangle();
		Vector2 position = new Vector2();
		Vector2 size = new Vector2();
		rect.getCenter(position);
		rect.getSize(size);
		
		//this sets the starting point of the map to the Start object with the corresponding startId
		if (object.getName().equals("Start")) {
			if (state.getStartId().equals(object.getProperties().get("startId", "", String.class))) {
				rect.getCenter(state.getStartPosition());
			}
		}
		
		Event e = null;
		
		//Go through every event type to create events
		if (object.getName().equals("Switch")) {
			e = new Switch(state, position, size);
		}
		if (object.getName().equals("Sensor")) {
			e = new Sensor(state, position, size, 
					object.getProperties().get("player", true, boolean.class), object.getProperties().get("hbox", false, boolean.class), 
					object.getProperties().get("event", false, boolean.class), object.getProperties().get("enemy", false, boolean.class),
					object.getProperties().get("gravity", 0.0f, float.class), object.getProperties().get("collision", false, boolean.class));
		}
		if (object.getName().equals("Timer")) {
			e = new Timer(state, object.getProperties().get("interval", float.class),
					object.getProperties().get("startOn", true, boolean.class));
		}
		if (object.getName().equals("Counter")) {
			e = new Counter(state, object.getProperties().get("count", int.class), object.getProperties().get("countStart", 0, int.class));
		}
		if (object.getName().equals("Multitrigger")) {
			e = new TriggerMulti(state);
			multiTriggeringEvents.put((TriggerMulti)e, object.getProperties().get("triggeringId", "", String.class));
		}
		if (object.getName().equals("Condtrigger")) {
			e = new TriggerCond(state, object.getProperties().get("start", "", String.class));
			condTriggeringEvents.put((TriggerCond)e, object.getProperties().get("triggeringId", "", String.class));
		}
		if (object.getName().equals("Alttrigger")) {
			e = new TriggerAlt(state, object.getProperties().get("message", "", String.class));
		}
		if (object.getName().equals("Redirecttrigger")) {
			e = new TriggerRedirect(state);
			redirectTriggeringEvents.put((TriggerRedirect)e, object.getProperties().get("blameId", "", String.class));
		}
		if (object.getName().equals("Dummy")) {
			e = new PositionDummy(state, position, size, 
					object.getProperties().get("dummyId", "", String.class));
		}
		if (object.getName().equals("UI")) {
			e = new UIChanger(state,
					object.getProperties().get("tags", "", String.class),
					object.getProperties().get("clear", false, boolean.class));
		}
		if (object.getName().equals("Game")) {
			e = new GameChanger(state,
					object.getProperties().get("lives", 0, int.class), 
					object.getProperties().get("score", 0, int.class),
					object.getProperties().get("timer", 0.0f, float.class),
					object.getProperties().get("timerIncr", 0.0f, float.class));
		}
		if (object.getName().equals("Camera")) {
			e = new CameraChanger(state, object.getProperties().get("zoom", 1.0f, float.class));
		}
		if (object.getName().equals("Bounds")) {
			e = new CameraBounder(state, position, size, 
					object.getProperties().get("right", false, boolean.class),
					object.getProperties().get("left", false, boolean.class),
					object.getProperties().get("up", false, boolean.class),
					object.getProperties().get("down", false, boolean.class));
		}
		if (object.getName().equals("Objective")) {
			e = new ObjectiveChanger(state);
		}
		if (object.getName().equals("Player")) {
			e = new PlayerChanger(state,
					object.getProperties().get("hp", 0.0f, float.class), 
					object.getProperties().get("fuel", 0.0f, float.class), 
					object.getProperties().get("ammo", 0.0f, float.class), 
					object.getProperties().get("scrap", 0, int.class));
		}
		if (object.getName().equals("Particle")) {
			e = new ParticleCreator(state, 
					Particle.valueOf(object.getProperties().get("particle", String.class)), 
					object.getProperties().get("duration", 0.0f, float.class),
					object.getProperties().get("startOn", false, Boolean.class));	
		}
		if (object.getName().equals("SchmuckSpawn")) {
			e = new SpawnerSchmuck(state, position, size, 
					object.getProperties().get("enemyId", int.class), 
					object.getProperties().get("amount", 1, int.class), 
					object.getProperties().get("extra", 0, int.class),
					object.getProperties().get("boss", false, boolean.class),
					object.getProperties().get("bossname", "", String.class));	
		}
		if (object.getName().equals("EventClone")) {
			e = new EventCloner(state, position, size);	
		}
		if (object.getName().equals("EventDelete")) {
			e = new EventDeleter(state);	
		}
		if (object.getName().equals("EventMove")) {
			e = new EventMover(state, position, size, 
					object.getProperties().get("gravity", -1.0f, float.class));	
		}
		if (object.getName().equals("SpriteChange")) {
			e = new SpriteChanger(state, 
					object.getProperties().get("newSprite", String.class),
					object.getProperties().get("mode", "NORMAL", String.class),
					object.getProperties().get("still", false, boolean.class),
					object.getProperties().get("frame", 0, int.class),
					object.getProperties().get("speed", 0.8f, float.class), 
					object.getProperties().get("align", "NONE", String.class),
					object.getProperties().get("scale", -1.0f, float.class));	
		}
		if (object.getName().equals("QuestChange")) {
			e = new QuestChanger(state, 
					object.getProperties().get("quest", String.class), 
					object.getProperties().get("change", 0, int.class));	
		}
		if (object.getName().equals("QuestCheck")) {
			e = new QuestChecker(state, 
					object.getProperties().get("quest", String.class), 
					object.getProperties().get("check", 0, int.class));	
		}
		
		if (object.getName().equals("PlayerMove")) {		
			e = new PlayerMover(state, position, size);
		}
		if (object.getName().equals("TouchPortal")) {		
			e = new PortalTouch(state, position, size);
		}
		if (object.getName().equals("Current")) {
			Vector2 power = new Vector2(object.getProperties().get("currentX", float.class), object.getProperties().get("currentY", float.class));
			e = new Currents(state, position, size, power);
		}
		if (object.getName().equals("Spring")) {
			Vector2 power = new Vector2(object.getProperties().get("springX", float.class), object.getProperties().get("springY", float.class));
			e = new Spring(state, position, size, power);
		}
		if (object.getName().equals("Equip")) {
			e = new PickupEquip(state, position, 
					object.getProperties().get("pool", "", String.class));
		}
		if (object.getName().equals("Active")) {
			e = new PickupActive(state, position, 
					object.getProperties().get("pool", "", String.class));
		}
		if (object.getName().equals("Dropthrough")) {
			e = new DropThroughPlatform(state, position, size);
		}
		if (object.getName().equals("Dialog")) {
			e = new Dialog(state, object.getProperties().get("textId", String.class));
		}
		if (object.getName().equals("End")) {
			e = new End(state, object.getProperties().get("text", "", String.class));
		}
		if (object.getName().equals("Destr_Obj")) {
			e = new DestructableBlock(state, position, size, object.getProperties().get("Hp", 100, int.class));
		}
		if (object.getName().equals("Warp")) {
			e = new LevelWarp(state, position, size,
					object.getProperties().get("level", String.class), 
					object.getProperties().get("reset", false, Boolean.class), 
					object.getProperties().get("startId", "", String.class));
		}
		if (object.getName().equals("Poison")) {
			e = new Poison(state, position, size, 
					object.getProperties().get("damage", 0.0f, float.class),
					object.getProperties().get("draw", true, boolean.class), 
					object.getProperties().get("filter", (short)0, short.class));
		}
		if (object.getName().equals("Save")) {
			e = new SaveSetter(state, position, size, 
					object.getProperties().get("zoom", 1.0f, float.class),
					object.getProperties().get("clear", true, boolean.class));
		}
		if (object.getName().equals("Platform")) {
			e = new MovingPlatform(state, position, size, 
					object.getProperties().get("speed", 1.0f, float.class),
					object.getProperties().get("pause", false, boolean.class));
			platformConnections.put((MovingPlatform)e, object.getProperties().get("connections", "", String.class));
		}
		if (object.getName().equals("Semiperm")) {
			e = new SemipermWall(state, position, size, 
					object.getProperties().get("player", false, boolean.class), object.getProperties().get("hbox", false, boolean.class), 
					object.getProperties().get("event", false, boolean.class), object.getProperties().get("enemy", false, boolean.class));
		}
		if (object.getName().equals("Armory")) {
			e = new Armory(state, position, size);
		}
		if (object.getName().equals("Reliquary")) {
			e = new Reliquary(state, position, size);
		}
		if (object.getName().equals("Dispensary")) {
			e = new Dispensary(state, position, size);
		}
		if (object.getName().equals("Dormitory")) {
			e = new Dormitory(state, position, size);
		}
		if (object.getName().equals("Navigation")) {
			e = new Navigations(state, position, size, 
					object.getProperties().get("title", "Navigations", String.class),
					object.getProperties().get("tag", "NAVIGATIONS", String.class));
		}
		if (object.getName().equals("Quartermaster")) {
			e = new Quartermaster(state, position, size,
					object.getProperties().get("shopId", String.class));
		}
		if (object.getName().equals("Prefab")) {
			genPrefab(state, object, rect);
		}
		
		//Extra, universal functions to change event sprite properties		
		if (e != null) {
			if (object.getProperties().get("triggeringId", String.class) != null) {
				triggeringEvents.put(e, object.getProperties().get("triggeringId", String.class));
			}
			if (object.getProperties().get("triggeredId",String.class) != null) {
				triggeredEvents.put(object.getProperties().get("triggeredId", String.class), e);
			}
			
			if (object.getProperties().get("sprite", String.class) != null) {
				if (object.getProperties().get("frame", int.class) != null) {
					e.setEventSprite(
							Sprite.valueOf(object.getProperties().get("sprite", String.class)), 
							true, 
							object.getProperties().get("frame", 0, int.class), 
							object.getProperties().get("speed", 0.8f, float.class), 
							PlayMode.valueOf(object.getProperties().get("mode", "NORMAL", String.class)));
				} else {
					e.setEventSprite(Sprite.valueOf(object.getProperties().get("sprite", String.class)));
				}
			}
			if (object.getProperties().get("scale", float.class) != null) {
				e.setScale(object.getProperties().get("scale", float.class));
			}
			if (object.getProperties().get("align", int.class) != null) {
				e.setScaleAlign(object.getProperties().get("align", String.class));
			}
			
			if (object.getProperties().get("sync", String.class) != null) {
				e.setSyncType(eventSyncTypes.valueOf(object.getProperties().get("sync", "ILLUSION", String.class)));
			}
			
			if (object.getProperties().get("synced", boolean.class) != null) {
				e.setSynced(object.getProperties().get("synced", boolean.class));
			}
			if (object.getProperties().get("particle_amb", String.class) != null) {
				e.addAmbientParticle(Particle.valueOf(object.getProperties().get("particle_amb", String.class)));
			}
			if (object.getProperties().get("particle_std", String.class) != null) {
				e.setStandardParticle(Particle.valueOf(object.getProperties().get("particle_std", String.class)));
			}
			
			if (object.getProperties().get("default", true, Boolean.class)) {
				e.loadDefaultProperties();
			}
			
			e.setGravity(object.getProperties().get("gravity", 0.0f, float.class));
			
			e.setBlueprint(object);
		}

		return e;
    }
    
    /**
     * Generate a prefrab combination of events
     * @param state: Playstate the events will be created in
     * @param object: MapObject of the prefab
     * @param rect: dimensions of the prefab
     */
    public static void genPrefab(PlayState state, MapObject object, Rectangle rect) {
    	
    	Prefabrication p = null;
    	
    	if (object.getProperties().get("prefabId", "", String.class).equals("Door")) {
    		p = new Door(state, (int)rect.width, (int)rect.height, 
					(int)(rect.x), (int)(rect.y), 
					object.getProperties().get("triggeredId", "", String.class), 
					object.getProperties().get("speed", 1.0f, float.class),
					object.getProperties().get("xDisplace", 0, int.class),
					object.getProperties().get("yDisplace", 0, int.class));
    	}
    	
    	if (object.getProperties().get("prefabId", "", String.class).equals("Spawner")) {
    		p = new SpawnerPickupTimed(state, (int)rect.width, (int)rect.height, 
					(int)(rect.x), (int)(rect.y), 
					object.getProperties().get("interval", 1.0f, float.class),
					object.getProperties().get("type", 0, int.class),
					object.getProperties().get("power", 0.0f, float.class));
    	}
    	
    	if (object.getProperties().get("prefabId", "", String.class).equals("SpawnerTriggered")) {
    		p = new SpawnerPickupTriggered(state, (int)rect.width, (int)rect.height, 
					(int)(rect.x), (int)(rect.y), 
					object.getProperties().get("triggeredId", "", String.class),
					object.getProperties().get("type", 0, int.class),
					object.getProperties().get("power", 0.0f, float.class));
    	}
    	
    	if (object.getProperties().get("prefabId", "", String.class).equals("Camera")) {
    		p = new CameraPanZone(state, (int)rect.width, (int)rect.height, 
					(int)(rect.x), (int)(rect.y), 
					object.getProperties().get("zoom1", 1.0f, float.class),
					object.getProperties().get("zoom2", 1.0f, float.class),
					object.getProperties().get("align", 0, int.class),
					object.getProperties().get("point1", "", String.class),
					object.getProperties().get("point2", "", String.class));
    	}
    	
    	if (object.getProperties().get("prefabId", "", String.class).equals("Limit")) {
    		p = new Limiter(state, (int)rect.width, (int)rect.height, 
					(int)(rect.x), (int)(rect.y), 
					object.getProperties().get("triggeredId", "", String.class),
					object.getProperties().get("triggeringId", "", String.class),
					object.getProperties().get("limit", 0, int.class));
    	}
    	
    	if (object.getProperties().get("prefabId", "", String.class).equals("Weapon")) {
    		p = new SpawnerWeapon(state, (int)rect.width, (int)rect.height, 
					(int)(rect.x), (int)(rect.y), 
					object.getProperties().get("triggeredId", "", String.class),
					object.getProperties().get("triggeringId", "", String.class),
					object.getProperties().get("mods", 0, int.class),
					object.getProperties().get("pool", "", String.class));
    	}
    	
    	if (object.getProperties().get("prefabId", "", String.class).equals("Active")) {
    		p = new SpawnerActiveItem(state, (int)rect.width, (int)rect.height, 
					(int)(rect.x), (int)(rect.y), 
					object.getProperties().get("triggeredId", "", String.class),
					object.getProperties().get("triggeringId", "", String.class),
					object.getProperties().get("pool", "", String.class));
    	}
    	
    	if (object.getProperties().get("prefabId", "", String.class).equals("LeverActivate")) {
    		p = new LeverActivate(state, (int)rect.width, (int)rect.height, 
					(int)(rect.x), (int)(rect.y), 
					object.getProperties().get("triggeringId", "", String.class));
    	}
    	
    	if (object.getProperties().get("prefabId", "", String.class).equals("LeverActivateOnce")) {
    		p = new LeverActivateOnce(state, (int)rect.width, (int)rect.height, 
					(int)(rect.x), (int)(rect.y), 
					object.getProperties().get("triggeringId", "", String.class));
    	}
    	
    	if (p != null) {
        	p.generateParts();
    	}
    }
    
    /*
     * When a prefab is created, its triggerIds are generated dynamically using this to ensure that there are no repeats.
     */
    public static int nextId = 0;
    public static String getPrefabTriggerId() {
    	String id = "prefabTriggerId" + nextId;
    	nextId++;
    	return id;
    }
    
    private final static String globalTimer = "runOnGlobalTimerConclude";
    /**
     * This method parses special events from the Tiled map.
     * Certain events have a special id that makes them tagged for special use in the play state.
     * Be sure not to use the above static strings as anid for a normal event
     * @param state
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
    	for (Event key : triggeringEvents.keySet()) {
    		if (!triggeringEvents.get(key).equals("")) {
        		key.setConnectedEvent(triggeredEvents.getOrDefault(triggeringEvents.get(key), null));
    		}
    	}
    	for (TriggerMulti key : multiTriggeringEvents.keySet()) {
    		for (String id : multiTriggeringEvents.get(key).split(",")) {
    			if (!id.equals("")) {
    				key.addTrigger(triggeredEvents.getOrDefault(id, null));
    			}
    		}
    	}
    	for (TriggerCond key : condTriggeringEvents.keySet()) {
    		for (String id : condTriggeringEvents.get(key).split(",")) {
    			if (!id.equals("")) {
    				key.addTrigger(id, triggeredEvents.getOrDefault(id, null));
    			}
    		}
    	}
    	for (TriggerRedirect key : redirectTriggeringEvents.keySet()) {
    		if (!redirectTriggeringEvents.get(key).equals("")) {
        		key.setBlame(triggeredEvents.getOrDefault(redirectTriggeringEvents.get(key), null));
    		}
    	}
    	for (MovingPlatform key : platformConnections.keySet()) {
    		for (String id : platformConnections.get(key).split(",")) {
    			if (!id.equals("")) {
        			key.addConnection(triggeredEvents.getOrDefault(id, null));
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

    	for (Event key : triggeringEvents.keySet()) {
    		if (!triggeringEvents.get(key).equals("") && triggeringEvents.get(key).equals(triggeredId)) {
        		key.setConnectedEvent(e);
    		}
    	}
    	for (TriggerRedirect key : redirectTriggeringEvents.keySet()) {
    		if (!redirectTriggeringEvents.get(key).equals("") && redirectTriggeringEvents.get(key).equals(triggeredId)) {
        		key.setBlame(e);
    		}
    	}
    	for (TriggerMulti key : multiTriggeringEvents.keySet()) {
			if (!multiTriggeringEvents.get(key).equals("") && multiTriggeringEvents.get(key).equals(triggeredId)) {
				key.addTrigger(e);
			}
    	}
    	for (TriggerCond key : condTriggeringEvents.keySet()) {
    		if (!condTriggeringEvents.get(key).equals("") && condTriggeringEvents.get(key).equals(triggeredId)) {
				key.addTrigger(triggeredId, e);
			}
    	}
    	e.setConnectedEvent(triggeredEvents.getOrDefault(triggeringId, null));
    }
    
    /**
     * Add a single event to the world, alongside triggers
     * @param state: Playstate to add event to
     * @param object: Map object of the event t oadd.
     * @return: The newly created event
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
    	platformConnections.clear();
    }
    
    /**
     * Helper function for parseTiledObjectLayer that creates line bodies
     * @param polyline: Tiled map object
     * @return: Box2d body
     */
    private static ChainShape createPolyline(PolylineMapObject polyline) {
        float[] vertices = polyline.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];
        
        
        for(int i = 0; i < worldVertices.length; i++) {
            worldVertices[i] = new Vector2(vertices[i * 2] / Constants.PPM, vertices[i * 2 + 1] / Constants.PPM);
        }
        ChainShape cs = new ChainShape();
        cs.createChain(worldVertices);
        return cs;
    }
}
