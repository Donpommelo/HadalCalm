package com.mygdx.hadal.event.utility;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.states.PlayState;

/**
 * TODO
 * 
 * @author Zachary Tu
 *
 */
public class ParticleCreator extends Event {

	private static final String name = "Particle Creator";

	private String particle;
	private float duration;
	
	public ParticleCreator(PlayState state, String particle, float duration) {
		super(state, name);
		this.particle = particle;
		this.duration = duration;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator) {
				
				if (event.getConnectedEvent() != null) {
					new ParticleEntity(state, event.getConnectedEvent(), "sprites/particle/" + particle + ".particle", 1.0f, duration, true);
				} else {
					new ParticleEntity(state, state.getPlayer(), "sprites/particle/" + particle + ".particle", 1.0f, duration, true);
				}
			}
		};
	}
}
