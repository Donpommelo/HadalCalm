package com.mygdx.hadal.utils;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.hadal.event.*;
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
    		if (object.getName().equals("Spawn")) {
    			new EntitySpawner(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), object.getProperties().get("id", int.class), 
    					object.getProperties().get("interval", float.class), object.getProperties().get("limit", int.class));
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