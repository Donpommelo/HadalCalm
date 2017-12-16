package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class Schmuck {

	public PlayState state;
	public World world;
	public OrthographicCamera camera;
	public RayHandler rays;
	
	public Body body;
	
	public BodyData bodyData;
	
	private PointLight light;
	
	public Schmuck(PlayState state, World world, OrthographicCamera camera, RayHandler rays) {
		this.state = state;
		this.world = world;
		this.camera = camera;
		this.rays = rays;
	}
	
	public void create() {
		
	}

	public void controller(float delta) {

	}
	
	public void render(SpriteBatch batch) {
		
	}
	
	public void dispose() {
		world.destroyBody(body);
	}
	
	public void queueDeletion() {
		state.destroy(this);
	}
	
	public Vector2 getPosition() {
        return body.getPosition();
    }
	
	public BodyData getPlayerData() {
		return bodyData;
	}
}
