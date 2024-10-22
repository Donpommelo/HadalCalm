package com.mygdx.hadal.event.utility;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * The ParticleCreator creates a particle entity
 * <p>
 * Triggered Behavior: When triggered, this event either toggles a particle emitter or fires it for a burst.
 * Triggering Behavior: N/A. However, if existent, the connected event will be the event that the particles are attached
 * too (otherwise, they will be attached to the player)
 * <p>
 * Fields:
 * <p>
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

	private ParticleEntity particles;
	private Particle particle;

	public ParticleCreator(PlayState state, Particle particle, float duration, boolean startOn) {
		super(state);
		this.duration = duration;
		this.on = startOn;

		if (duration == 0) {
			particles = EffectEntityManager.getParticle(state, new ParticleCreate(particle, (HadalEntity) null)
					.setStartOn(on));
		} else {
			this.particle = particle;
		}
	}
	
	@Override
	public void create() {
		
		if (getConnectedEvent() != null && particles != null) {
			particles.setAttachedEntity(getConnectedEvent());
		}
		
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				HadalEntity attachedEntity;
				if (getConnectedEvent() != null) {
					attachedEntity = getConnectedEvent();
				} else {
					attachedEntity = p;
				}

				if (particle == null) {
					if (particles != null && particles.isAlive()) {
						particles.setAttachedEntity(attachedEntity);
						if (on) {
							particles.turnOff();
						} else {
							particles.turnOn();
						}
						on = !on;
					} else {
						particles = EffectEntityManager.getParticle(state, new ParticleCreate(particle, attachedEntity)
								.setLifespan(duration));
					}
				} else {
					EffectEntityManager.getParticle(state, new ParticleCreate(particle, attachedEntity)
							.setLifespan(duration));
				}
			}
		};
	}
}
