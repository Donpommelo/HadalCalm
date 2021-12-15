package com.mygdx.hadal.event.utility;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * The ParticleCreator creates a particle entity
 * 
 * Triggered Behavior: When triggered, this event either toggles a particle emitter or fires it for a burst.
 * Triggering Behavior: N/A. However, if existent, the connected event will be the event that the particles are attached
 * too (otherwise, they will be attached to the player)
 * 
 * Fields:
 * 
 * particle: name of the particle
 * duration: if nonzero, this event will fire the particle for that duration. Otherwise, this event just toggles.
 * 	Optional. Default 0.0f
 * startOn: Does the particle effect start on? Optional. Default: False
 * 
 * @author Futtham Fruvich
 */
public class ParticleCreator extends Event {

	private final float duration;
	private boolean on;
	
	private final ParticleEntity particles;
	
	public ParticleCreator(PlayState state, Particle particle, float duration, boolean startOn) {
		super(state);
		this.duration = duration;
		this.on = startOn;
		
		particles = new ParticleEntity(state, null, particle, 0.0f, 0.0f, on, SyncType.TICKSYNC);
	}
	
	@Override
	public void create() {
		
		if (getConnectedEvent() != null) {
			particles.setAttachedEntity(getConnectedEvent());
		}
		
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				
				if (getConnectedEvent() != null) {
					particles.setAttachedEntity(getConnectedEvent());
				} else {
					particles.setAttachedEntity(p);
				}
								
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
