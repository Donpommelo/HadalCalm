package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public class Event extends HadalEntity {
	
	public EventData eventData;
	
	public String name;
	
	public Event(PlayState state, World world, OrthographicCamera camera, RayHandler rays, String name,
			int width, int height, int x, int y) {
		super(state, world, camera, rays, width, height, x, y);
		this.name = name;
	}
	
	@Override
	public void create() {

	}

	@Override
	public void controller(float delta) {
		
	}

	@Override
	public void render(SpriteBatch batch) {
		batch.setProjectionMatrix(state.hud.combined);
		Vector3 bodyScreenPosition = new Vector3(body.getPosition().x, body.getPosition().y, 0);
		camera.project(bodyScreenPosition);
		state.font.draw(batch, name, bodyScreenPosition.x - width, bodyScreenPosition.y - height);
	}
	
	public String getText() {
		return "";
	}
}
