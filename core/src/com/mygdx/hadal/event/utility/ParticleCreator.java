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

	private float duration;
	private boolean on;
	
	private ParticleEntity particles;
	
	public ParticleCreator(PlayState state, String particle, float duration, boolean startOn) {
		super(state, name);
		this.duration = duration;
		this.on = startOn;
		
		particles = new ParticleEntity(state, state.getPlayer(), "sprites/particle/" + particle + ".particle", 2.0f, 0.0f, on);
	}
	
	@Override
	public void create() {
		
		if (getConnectedEvent() != null) {
			particles.setAttachedEntity(getConnectedEvent());
		} else {
			particles.setAttachedEntity(state.getPlayer());
		}
		
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator) {
				
				if (duration == 0) {
					if (on) {
						particles.turnOff();
					} else {
						particles.turnOn();
					}
					on = !on;
				} else {
					particles.onForBurst(duration);
				}
			}
		};
	}
}
