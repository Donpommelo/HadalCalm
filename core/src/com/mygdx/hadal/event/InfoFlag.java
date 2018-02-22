package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class InfoFlag extends Event {

	private static final String name = "Current";

	private String text;
	
	public InfoFlag(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height, int x, int y, String text) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.text = text;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(world, this);
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),
				(short) 0, true, eventData);
	}
	
	@Override
	public String getText() {
		if (eventData.getSchmucks().isEmpty()) {
			return "";
		} else {
			return text;
		}
	}

}
