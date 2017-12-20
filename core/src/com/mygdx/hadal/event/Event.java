package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public class Event extends HadalEntity {
	
	public EventData eventData;
		
	public Event(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int x, int y, int width, int height) {
		super(state, world, camera, rays, width, height, x, y);

	}
	
	@Override
	public void create() {

	}

	@Override
	public void controller(float delta) {
		
	}

	@Override
	public void render(SpriteBatch batch) {
		
	}	
}
