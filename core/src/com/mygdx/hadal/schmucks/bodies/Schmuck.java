package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public abstract class Schmuck {

	public PlayState state;
	public World world;
	public OrthographicCamera camera;
	public RayHandler rays;
	
	public Body body;
	
	
	public Schmuck(PlayState state, World world, OrthographicCamera camera, RayHandler rays) {
		this.state = state;
		this.world = world;
		this.camera = camera;
		this.rays = rays;
	}
		
	public abstract void create();

	public abstract void controller(float delta);
	
	public abstract void render(SpriteBatch batch);
	
	public void dispose() {
		world.destroyBody(body);
	}
	
	public void queueDeletion() {
		state.destroy(this);
	}
	
	public Vector2 getPosition() {
        return body.getPosition();
    }
	
	public void recoil(int x, int y, float power) {
		
		Vector3 bodyScreenPosition = new Vector3(body.getPosition().x, body.getPosition().y, 0);
		camera.project(bodyScreenPosition);
		
		float powerDiv = bodyScreenPosition.dst(x, y, 0) / power;
		
		float xImpulse = (bodyScreenPosition.x - x) / powerDiv;
		float yImpulse = (bodyScreenPosition.y - y) / powerDiv;
		
		body.applyLinearImpulse(new Vector2(xImpulse, yImpulse), body.getWorldCenter(), true);
	}
	
}
