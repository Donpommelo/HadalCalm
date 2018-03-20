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
    		
    		//atm, all events are just rectangles.
    		RectangleMapObject current = (RectangleMapObject)object;
			Rectangle rect = current.getRectangle();
			
			if (object.getName().equals("Start")) {
    			state.setStart((int)rect.x, (int)rect.y);
    		}
			
			//Go through every event type to create events
    		if (object.getName().equals("Switch")) {
    			Event swich = new Switch(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
    					object.getProperties().get("oneTime", true, boolean.class));

    			triggeringEvents.put(swich, object.getProperties().get("triggeringId", "", String.class));
    			triggeredEvents.put(object.getProperties().get("triggeredId", "", String.class), swich);
    		}
    		if (object.getName().equals("Sensor")) {
    			Event sensor = new Sensor(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2)
    					, object.getProperties().get("oneTime", true, boolean.class));
    			triggeringEvents.put(sensor, object.getProperties().get("triggeringId", "", String.class));
    			triggeredEvents.put(object.getProperties().get("triggeredId", "", String.class), sensor);
    		}
    		if (object.getName().equals("Timer")) {
    			Event timer = new Timer(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
    					object.getProperties().get("interval", float.class), object.getProperties().get("limit", int.class),
    					object.getProperties().get("startOn", true, boolean.class));
    			triggeringEvents.put(timer, object.getProperties().get("triggeringId", "", String.class));
    			triggeredEvents.put(object.getProperties().get("triggeredId", "", String.class), timer);
    		}
    		if (object.getName().equals("Target")) {
    			Event target = new Target(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
    					object.getProperties().get("oneTime", boolean.class));
    			
    			triggeringEvents.put(target, object.getProperties().get("triggeringId", "", String.class));
    			triggeredEvents.put(object.getProperties().get("triggeredId", "", String.class), target);
    		}
    		if (object.getName().equals("Counter")) {
    			Event counter = new Counter(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2),
    					object.getProperties().get("count", int.class), object.getProperties().get("countStart", 0, int.class));
    			triggeringEvents.put(counter, object.getProperties().get("triggeringId", "", String.class));
    			triggeredEvents.put(object.getProperties().get("triggeredId", "", String.class), counter);
    		}
    		if (object.getName().equals("Multitrigger")) {
    			TriggerMulti trigger = new TriggerMulti(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
    			multiTriggeringEvents.put(trigger, object.getProperties().get("triggeringId", "", String.class));
    			triggeredEvents.put(object.getProperties().get("triggeredId", "", String.class), trigger);
    		}
    		if (object.getName().equals("Condtrigger")) {
    			TriggerCond trigger = new TriggerCond(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
    					object.getProperties().get("start", "", String.class));
    			condTriggeringEvents.put(trigger, object.getProperties().get("triggeringId", "", String.class));
    			triggeredEvents.put(object.getProperties().get("triggeredId", "", String.class), trigger);
    		}
    		if (object.getName().equals("Alttrigger")) {
    			Event trigger = new TriggerAlt(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), object.getProperties().get("message","", String.class));
    			triggeringEvents.put(trigger, object.getProperties().get("triggeringId", "", String.class));
    			triggeredEvents.put(object.getProperties().get("triggeredId", "", String.class), trigger);
    		}
    		if (object.getName().equals("Redirecttrigger")) {
    			TriggerRedirect trigger = new TriggerRedirect(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
    			triggeringEvents.put(trigger, object.getProperties().get("triggeringId", "", String.class));
    			triggeredEvents.put(object.getProperties().get("triggeredId", "", String.class), trigger);
    			redirectTriggeringEvents.put(trigger, object.getProperties().get("blameId", "", String.class));
    		}
    		if (object.getName().equals("Dummy")) {
    			Event dummy = new PositionDummy(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
    			triggeringEvents.put(dummy, object.getProperties().get("triggeringId", "", String.class));
    			triggeredEvents.put(object.getProperties().get("triggeredId", "", String.class), dummy);
    		}
    		
    		if (object.getName().equals("SchmuckSpawn")) {
    			
    			Event spawn = new SpawnerSchmuck(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), object.getProperties().get("id", int.class), 
    					object.getProperties().get("limit", int.class), object.getProperties().get("spread", true, boolean.class));	
    			
    			triggeringEvents.put(spawn, object.getProperties().get("triggeringId", "", String.class));
    			triggeredEvents.put(object.getProperties().get("triggeredId", "", String.class), spawn);
    		}
    		if (object.getName().equals("EventSpawn")) {
    			
    			triggeredEvents.put(object.getProperties().get("triggeredId", "", String.class), 
    					new SpawnerEvent(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), object.getProperties().get("id", int.class), 
    					object.getProperties().get("reset", true, boolean.class),
    					object.getProperties().get("args", "", String.class)));	
    		}
    		if (object.getName().equals("UsePortal")) {
    			
    			Event portal = new PortalUse(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
    					object.getProperties().get("oneTime", boolean.class));
    			
    			triggeringEvents.put(portal, object.getProperties().get("triggeringId", "", String.class));
    			triggeredEvents.put(object.getProperties().get("triggeredId", "", String.class), portal);
    		}
    		
    		if (object.getName().equals("TouchPortal")) {
    			
    			Event portal = new PortalTouch(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
    			
    			triggeringEvents.put(portal, object.getProperties().get("triggeringId", "", String.class));
    			triggeredEvents.put(object.getProperties().get("triggeredId", "", String.class), portal);
    		}
    		
    		if (object.getName().equals("Current")) {
    			Vector2 power = new Vector2(object.getProperties().get("currentX", float.class), object.getProperties().get("currentY", float.class));
    			new Currents(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), power);
    		}
    		if (object.getName().equals("Spring")) {
    			Vector2 power = new Vector2(object.getProperties().get("springX", float.class), object.getProperties().get("springY", float.class));
    			new Spring(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), power);
    		}
    		if (object.getName().equals("Equip")) {
    			Event equip = new PickupEquip(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
    					object.getProperties().get("pool", "", String.class),
    					object.getProperties().get("startOn", true, boolean.class));
    			triggeringEvents.put(equip, object.getProperties().get("triggeringId", "", String.class));
    			triggeredEvents.put(object.getProperties().get("triggeredId", "", String.class), equip);
    		}
    		if (object.getName().equals("Artifact")) {
    			Event artifact = new PickupArtifact(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
    					object.getProperties().get("pool", "", String.class),
    					object.getProperties().get("startOn", true, boolean.class));
    			triggeringEvents.put(artifact, object.getProperties().get("triggeringId", "", String.class));
    			triggeredEvents.put(object.getProperties().get("triggeredId", "", String.class), artifact);
    		}
    		if (object.getName().equals("Dropthrough")) {
    			new DropThroughPlatform(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
    		}
    		if (object.getName().equals("Text")) {
    			new InfoFlag(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
    					object.getProperties().get("text", String.class));
    		}
    		if (object.getName().equals("Radio")) {
    			Event radio = new Radio(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
    					object.getProperties().get("id", String.class));
    			triggeringEvents.put(radio, object.getProperties().get("triggeringId", "", String.class));
    			triggeredEvents.put(object.getProperties().get("triggeredId", "", String.class), radio);
    		}
    		if (object.getName().equals("Door")) {
    			triggeredEvents.put(object.getProperties().get("triggeredId", "", String.class), new Door(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2)));
    		}
    		if (object.getName().equals("Rock")) {
    			new AirblastableRock(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
    		}
    		if (object.getName().equals("Victory")) {
    			new Victory(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
    		}
    		if (object.getName().equals("Destr_Obj")) {
    			new DestructableBlock(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
    					object.getProperties().get("Hp", 100, Integer.class));
    		}
    		if (object.getName().equals("Warp")) {
    			new LevelWarp(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
    					object.getProperties().get("Level", String.class));
    		}
    		if (object.getName().equals("Poison")) {
    			Event poison = new Poison(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
    					object.getProperties().get("Damage", float.class), object.getProperties().get("startOn", true, boolean.class));
    			triggeredEvents.put(object.getProperties().get("triggeredId", "", String.class), poison);
    		}
    		if (object.getName().equals("Save")) {
    			new SavePoint(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
    		}
    		if (object.getName().equals("Pit")) {
    			new Pit(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
    		}
    		if (object.getName().equals("Platform")) {
    			MovingPlatform platform = new MovingPlatform(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
    					object.getProperties().get("speed", 1.0f, float.class));
    			triggeringEvents.put(platform, object.getProperties().get("triggeringId", "", String.class));
    			triggeredEvents.put(object.getProperties().get("triggeredId", "", String.class), platform);
    			platformConnections.put(platform, object.getProperties().get("connections", "", String.class));
    		}
    		
    		if (object.getName().equals("Armory")) {
    			new Armory(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
    		}
    		if (object.getName().equals("Reliquary")) {
    			new Reliquary(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
    		}
    		if (object.getName().equals("Dormitory")) {
    			new Dormitory(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
    		}
    		if (object.getName().equals("Navigation")) {
    			new Navigations(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
    		}
    		if (object.getName().equals("Quartermaster")) {
    			new Quartermaster(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
    		}
    	}
    }
    
    public static void parseTiledTriggerLayer(PlayState state, World world, OrthographicCamera camera, RayHandler rays) {
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