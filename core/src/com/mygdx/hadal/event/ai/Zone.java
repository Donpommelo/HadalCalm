package com.mygdx.hadal.event.ai;

import java.util.Map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class Zone extends Event {

	public Map<Zone, ConnectionPoint> connections;
	
	public Zone(PlayState state, World world, OrthographicCamera camera, RayHandler rays, String name, int width,
			int height, int x, int y) {
		super(state, world, camera, rays, name, width, height, x, y);
		
		
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(world, this) {
			
			@Override
			public void onTouch(HadalData fixB) {
				super.onTouch(fixB);
				
				if (fixB instanceof BodyData) {
					((BodyData) fixB).currentZone = (Zone) this.event;
				}
			}
			
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE),
				(short) 0, true, eventData);
	}	

}
