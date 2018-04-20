package com.mygdx.hadal.event;

import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.TiledObjectUtil;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * An event cloner TBA
 * @author Zachary Tu
 *
 */
public class EventCloner extends Event {
	
	private static final String name = "Event Spawner";

	public EventCloner(PlayState state, int width, int height, int x, int y) {
		super(state, name, width, height, x, y, "event_base", 0.25f, 2);
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator) {
				if (event.getConnectedEvent() != null) {
										
					if (event.getConnectedEvent().getBlueprint() != null) {
						Event clone = TiledObjectUtil.parseTiledEvent(state, world, camera, rays, event.getConnectedEvent().getBlueprint());
						
						if (standardParticle != null) {
							standardParticle.onForBurst(1.0f);
						}
						
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
