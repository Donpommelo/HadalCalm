package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.TiledObjectUtil;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * An event cloner TBA
 * @author Zachary Tu
 *
 */
public class EventCloner extends Event {
	
	private static final String name = "Event Spawner";

	private ParticleEntity particle;

	public EventCloner(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y) {
		super(state, world, camera, rays, name, width, height, x, y, "event_base", 0.25f, 2);
		
		particle = new ParticleEntity(state, world, camera, rays, this, AssetList.EVENT_HOLO.toString(), 1.0f, 0.0f, false);
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(world, this) {
			
			@Override
			public void onActivate(EventData activator) {
				if (event.getConnectedEvent() != null) {
										
					if (event.getConnectedEvent().getBlueprint() != null) {
						Event clone = TiledObjectUtil.parseTiledEvent(state, world, camera, rays, event.getConnectedEvent().getBlueprint());
						
						particle.onForBurst(1.0f);
						
						clone.setStartX(event.getStartX());
						clone.setStartY(event.getStartY());
												
						TiledObjectUtil.parseTiledSingleTrigger(clone);
					}
				}
				
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (0), (short) 0, true, eventData);
	}
	
}
