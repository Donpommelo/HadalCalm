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
import com.mygdx.hadal.event.utility.Counter;
import com.mygdx.hadal.event.utility.Sensor;
import com.mygdx.hadal.event.utility.Switch;
import com.mygdx.hadal.event.utility.Target;
import com.mygdx.hadal.event.utility.Timer;
import com.mygdx.hadal.schmucks.bodies.enemies.Turret;
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
			
			//Go through every event type to create events
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
    			new EquipPickup(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
    					object.getProperties().get("equipId", int.class));
    		}
    		if (object.getName().equals("EquipRand")) {
    			new EquipPickupRandom(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
    		}
    		if (object.getName().equals("AirBubble")) {
    			new AirBubbleSpawner(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2),
    					object.getProperties().get("interval", float.class));
    		}
    		if (object.getName().equals("Medpak")) {
    			new MedpakSpawner(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2),
    					object.getProperties().get("interval", float.class));
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
    			new Radio(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
    					object.getProperties().get("id", String.class));
    		}
    		
    		if (object.getName().equals("Door")) {
    			triggeredEvents.put(object.getProperties().get("triggeredId", String.class), new Door(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2)));
    		}
    		if (object.getName().equals("Switch")) {
    			triggeringEvents.put(new Switch(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2)), object.getProperties().get("triggeringId", String.class));
    		}
    		if (object.getName().equals("Sensor")) {
    			triggeringEvents.put(new Sensor(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
    					object.getProperties().get("oneTime", boolean.class)), object.getProperties().get("triggeringId", String.class));
    		}
    		if (object.getName().equals("Timer")) {
    			triggeringEvents.put(new Timer(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
    					object.getProperties().get("interval", float.class), object.getProperties().get("limit", int.class)), 
    					object.getProperties().get("triggeringId", String.class));
    		}
    		if (object.getName().equals("Target")) {
    			triggeringEvents.put(new Target(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
    					object.getProperties().get("oneTime", boolean.class)), object.getProperties().get("triggeringId", String.class));
    		}
    		if (object.getName().equals("Rock")) {
    			new AirblastableRock(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
    		}
    		if (object.getName().equals("Counter")) {
    			Event counter = new Counter(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), object.getProperties().get("count", int.class));
    			triggeringEvents.put(counter, object.getProperties().get("triggeringId", String.class));
    			triggeredEvents.put(object.getProperties().get("triggeredId", String.class), counter);
    		}
    		if (object.getName().equals("Spawn")) {
    			
    			Event spawn = new Spawn(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), object.getProperties().get("id", int.class), 
    					object.getProperties().get("limit", int.class));	
    			
    			triggeringEvents.put(spawn, object.getProperties().get("triggeringId", String.class));
    			triggeredEvents.put(object.getProperties().get("triggeredId", String.class), spawn);
    		}
    		if (object.getName().equals("UsePortal")) {
    			
    			Event portal = new UsePortal(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
    					object.getProperties().get("oneTime", boolean.class));
    			
    			triggeringEvents.put(portal, object.getProperties().get("triggeringId", String.class));
    			triggeredEvents.put(object.getProperties().get("triggeredId", String.class), portal);
    		}
    		if (object.getName().equals("Victory")) {
    			new Victory(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
    		}
    		if (object.getName().equals("Destr_Obj")) {
    			new DestructableBlock(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
    					object.getProperties().get("Hp", Integer.class));
    		}
    		if (object.getName().equals("Turret")) {
    			new Turret(state, world, camera, rays, (int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2));
    		}
    		if (object.getName().equals("Warp")) {
    			new LevelWarp(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 
    					object.getProperties().get("Level", String.class));
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
    	}
    }
    
    public static void parseTiledTriggerLayer(PlayState state, World world, OrthographicCamera camera, RayHandler rays) {
    	for (Event key : triggeringEvents.keySet()) {
    		key.setConnectedEvent(triggeredEvents.getOrDefault(triggeringEvents.get(key), null));
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