package com.mygdx.hadal.utils;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.hadal.event.Currents;
import com.mygdx.hadal.event.EntitySpawner;
import com.mygdx.hadal.event.Spring;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public class TiledObjectUtil {
    public static void parseTiledObjectLayer(World world, MapObjects objects) {
        for(MapObject object : objects) {
            Shape shape;

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
    
    public static void parseTiledEventLayer(PlayState state, World world, OrthographicCamera camera, RayHandler rays, MapObjects objects) {
    	for(MapObject object : objects) {
    		if (object.getName().equals("Current")) {
    			RectangleMapObject current = (RectangleMapObject)object;
    			Rectangle rect = current.getRectangle();
    			Vector2 power = new Vector2(object.getProperties().get("currentX", float.class), object.getProperties().get("currentY", float.class));
    			new Currents(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), power);
    		}
    		if (object.getName().equals("Spring")) {
    			RectangleMapObject current = (RectangleMapObject)object;
    			Rectangle rect = current.getRectangle();
    			Vector2 power = new Vector2(object.getProperties().get("springX", float.class), object.getProperties().get("springY", float.class));
    			new Spring(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), power);
    		}
    		if (object.getName().equals("Spawn")) {
    			RectangleMapObject current = (RectangleMapObject)object;
    			Rectangle rect = current.getRectangle();
    			new EntitySpawner(state, world, camera, rays, (int)rect.width, (int)rect.height, 
    					(int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), object.getProperties().get("id", int.class), 
    					object.getProperties().get("interval", float.class), object.getProperties().get("limit", int.class));
    		}
    	}
    }

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