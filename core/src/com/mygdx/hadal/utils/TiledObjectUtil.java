package com.mygdx.hadal.utils;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.hadal.event.*;
import com.mygdx.hadal.event.hub.*;
import com.mygdx.hadal.event.utility.*;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

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
    public static void parseTiledEventLayer(PlayState state, World world, OrthographicCamera camera, RayHandler rays, MapObjects objects) {
    	for(MapObject object : objects) {
    		parseTiledEvent(state, world, camera, rays, object);
    	}
    }
    
    public static Event parseTiledEvent(PlayState state, World world, OrthographicCamera camera, RayHandler rays, MapObject object) {
    	//atm, all events are just rectangles.
		RectangleMapObject current = (RectangleMapObject)object;
		Rectangle rect = current.getRectangle();
		
		if (object.getName().equals("Start")) {
			state.setStart((int)rect.x, (int)rect.y);
		}
		
		Event e = null;
		
		//Go through every event type to create events
		if (object.getName().equals("Switch")) {
			e = new Switch(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
		}
		if (object.getName().equals("Sensor")) {
			e = new Sensor(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
					object.getProperties().get("player", true, boolean.class), object.getProperties().get("hbox", false, boolean.class), 
					object.getProperties().get("event", false, boolean.class), object.getProperties().get("enemy", false, boolean.class));
		}
		if (object.getName().equals("Timer")) {
			e = new Timer(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
					object.getProperties().get("interval", float.class));
		}
		if (object.getName().equals("Counter")) {
			e = new Counter(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2),
					object.getProperties().get("count", int.class), object.getProperties().get("countStart", 0, int.class));
		}
		if (object.getName().equals("Limiter")) {
			e = new Limiter(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2),
					object.getProperties().get("count", int.class));
		}
		if (object.getName().equals("Multitrigger")) {
			e = new TriggerMulti(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
			multiTriggeringEvents.put((TriggerMulti)e, object.getProperties().get("triggeringId", "", String.class));
		}
		if (object.getName().equals("Condtrigger")) {
			e = new TriggerCond(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
					object.getProperties().get("start", "", String.class));
			condTriggeringEvents.put((TriggerCond)e, object.getProperties().get("triggeringId", "", String.class));
		}
		if (object.getName().equals("Alttrigger")) {
			e = new TriggerAlt(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
					object.getProperties().get("message","", String.class));
		}
		if (object.getName().equals("Redirecttrigger")) {
			e = new TriggerRedirect(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
			redirectTriggeringEvents.put((TriggerRedirect)e, object.getProperties().get("blameId", "", String.class));
		}
		if (object.getName().equals("Dummy")) {
			e = new PositionDummy(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
		}
		if (object.getName().equals("UI")) {
			
			e = new UIChanger(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2),
					object.getProperties().get("tags", String.class),
					object.getProperties().get("change", 0, Integer.class),
					object.getProperties().get("lives", 0, Integer.class), 
					object.getProperties().get("score", 0, Integer.class),
					object.getProperties().get("var1", 0, Integer.class),
					object.getProperties().get("var2", 0, Integer.class),
					object.getProperties().get("timer", 0.0f, float.class),
					object.getProperties().get("misc", "", String.class));
		}
		if (object.getName().equals("Camera")) {
			e = new CameraChanger(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2),
					object.getProperties().get("zoom", 1.0f, float.class));
		}
		if (object.getName().equals("Objective")) {
			e = new ObjectiveChanger(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
		}
		if (object.getName().equals("Player")) {
			e = new PlayerChanger(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2),
					object.getProperties().get("hp", 0.0f, float.class), object.getProperties().get("fuel", 0.0f, float.class), 
					object.getProperties().get("scrap", 0, Integer.class));
		}
		
		if (object.getName().equals("SchmuckSpawn")) {
			e = new SpawnerSchmuck(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
					object.getProperties().get("enemyId", int.class), object.getProperties().get("amount", 1, int.class), 
					object.getProperties().get("spread", true, boolean.class));	
		}
		
		if (object.getName().equals("EventClone")) {
			e = new EventCloner(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));	
		}
		if (object.getName().equals("EventDelete")) {
			e = new EventDeleter(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));	
		}
		if (object.getName().equals("EventMove")) {
			e = new EventMover(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
					object.getProperties().get("gravityChange", -1.0f, float.class));	
		}
		
		if (object.getName().equals("UsePortal")) {		
			e = new PortalUse(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
		}
		if (object.getName().equals("TouchPortal")) {		
			e = new PortalTouch(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
		}
		if (object.getName().equals("Current")) {
			Vector2 power = new Vector2(object.getProperties().get("currentX", float.class), object.getProperties().get("currentY", float.class));
			e = new Currents(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), power);
		}
		if (object.getName().equals("Spring")) {
			Vector2 power = new Vector2(object.getProperties().get("springX", float.class), object.getProperties().get("springY", float.class));
			e = new Spring(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), power);
		}
		if (object.getName().equals("Equip")) {
			e = new PickupEquip(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
					object.getProperties().get("pool", "", String.class));
		}
		if (object.getName().equals("Artifact")) {
			e = new PickupArtifact(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
					object.getProperties().get("pool", "", String.class));
		}
		if (object.getName().equals("Dropthrough")) {
			e = new DropThroughPlatform(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
		}
		if (object.getName().equals("Text")) {
			e = new InfoFlag(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
					object.getProperties().get("text", String.class));
		}
		if (object.getName().equals("Dialog")) {
			e = new Dialog(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
					object.getProperties().get("textId", String.class));
		}
		if (object.getName().equals("Door")) {
			e = new Door(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
		}
		if (object.getName().equals("Rock")) {
			e = new AirblastableRock(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
		}
		if (object.getName().equals("End")) {
			e = new End(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
					object.getProperties().get("won", true, boolean.class));
		}
		if (object.getName().equals("Destr_Obj")) {
			e = new DestructableBlock(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
					object.getProperties().get("Hp", 100, Integer.class));
		}
		if (object.getName().equals("Warp")) {
			e = new LevelWarp(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
					object.getProperties().get("Level", String.class), object.getProperties().get("reset", true, Boolean.class));
		}
		if (object.getName().equals("Poison")) {
			e = new Poison(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
					object.getProperties().get("Damage", float.class), 
					object.getProperties().get("filter", (short)0, short.class));
		}
		if (object.getName().equals("Save")) {
			e = new SavePoint(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
		}
		if (object.getName().equals("Pit")) {
			e = new Pit(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
		}
		if (object.getName().equals("Platform")) {
			e = new MovingPlatform(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
					object.getProperties().get("speed", 1.0f, float.class));
			platformConnections.put((MovingPlatform)e, object.getProperties().get("connections", "", String.class));
		}
		
		if (object.getName().equals("Armory")) {
			e = new Armory(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
		}
		if (object.getName().equals("Reliquary")) {
			e = new Reliquary(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
		}
		if (object.getName().equals("Dormitory")) {
			e = new Dormitory(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
		}
		if (object.getName().equals("Navigation")) {
			e = new Navigations(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
		}
		if (object.getName().equals("Quartermaster")) {
			e = new Quartermaster(state, world, camera, rays, (int)rect.width, (int)rect.height, 
					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
		}
		
		//Extra, universal functions to change event sprite properties		
		if (e != null) {
			triggeringEvents.put(e, object.getProperties().get("triggeringId", "", String.class));
			triggeredEvents.put(object.getProperties().get("triggeredId", "", String.class), e);
			
			if (object.getProperties().get("sprite", String.class) != null) {
				e.setEventSprite(object.getProperties().get("sprite", String.class));
			}
			if (object.getProperties().get("scale", float.class) != null) {
				e.setScale(object.getProperties().get("scale", float.class));
			}
			if (object.getProperties().get("align", Integer.class) != null) {
				e.setScaleAlign(object.getProperties().get("align", Integer.class));
			}
			if (object.getProperties().get("particle_amb", String.class) != null) {
				e.addAmbientParticle(object.getProperties().get("particle_amb", String.class));
			}
			if (object.getProperties().get("particle_std", String.class) != null) {
				e.setStandardParticle(object.getProperties().get("particle_std", String.class));
			}
			
			e.setGravity(object.getProperties().get("gravity", 0.0f, float.class));
			
			e.setBlueprint(object);
		}
		
		return e;
    }
    
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