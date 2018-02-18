package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class Poison extends Event {
	
	private float controllerCount = 0;
	private float dps;
	private BodyData perp;
	
	private static final String name = "Poison";

	public Poison(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height, 
			int x, int y, float dps) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.dps = dps;
		this.perp = state.worldDummy.getBodyData();
	}
	
	public Poison(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height, 
			int x, int y, float dps, float duration, BodyData perp) {
		super(state, world, camera, rays, name, width, height, x, y, duration);
		this.dps = dps;
		this.perp = perp;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(world, this);
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY),
				(short) 0, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		controllerCount+=delta;
		if (controllerCount >= 1/60f) {
			controllerCount = 0;
			
			for (HadalEntity entity : eventData.schmucks) {
				if (entity instanceof Schmuck) {
					((Schmuck)entity).getBodyData().receiveDamage(dps, new Vector2(0, 0), perp, true);
				}
			}
		}
		super.controller(delta);
	}	
}