package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.ObjectSet;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * A touch Portal is a portal that transports schmucks and hboxes that touch it
 * The event they are transported to does not have to be a portal.
 * <p>
 * Triggered Behavior: N/A
 * Triggering Behavior: This event's connected event serves as the point that schmucks will be teleported to
 * <p>
 * Fields:
 * N/A
 * 
 * @author Locraft Lulzertier
 */
public class PortalTouch extends Event {

	//This is a set of schmucks that have just been teleported and cannot teleport instantly until they exit this event.
	private final ObjectSet<HadalEntity> justTeleported = new ObjectSet<>();
	
	public PortalTouch(PlayState state, Vector2 startPos, Vector2 size) {
		super(state, startPos, size);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onRelease(HadalData fixB) {
				if (fixB != null) {
					
					//When something leaves the area of this portal, they can teleport again.
					//This solves the issue of something infinitely going between 2 portals
					schmucks.remove(fixB.getEntity());
					justTeleported.remove(fixB.getEntity());
				}
			}
		};

		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR,
				(short) (BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY | BodyConstants.BIT_PROJECTILE), (short) 0)
				.setBodyType(BodyDef.BodyType.KinematicBody)
				.addToWorld(world);
	}
	
	@Override
	public void controller(float delta) {
		if (getConnectedEvent() != null) {
			
			//If teleporting someone to another portal, add them to the justTeleported list so they cannot teleport again right away.
			if (getConnectedEvent() instanceof PortalTouch portal) {
				for (HadalEntity s : eventData.getSchmucks()) {
					if (!justTeleported.contains(s)) {
						portal.getJustTeleported().add(s);
						s.setTransform(getConnectedEvent().getPosition(), 0);
						
						if (getConnectedEvent().getStandardParticle() != null) {
							getConnectedEvent().getStandardParticle().onForBurst(1.0f);
						}
					}
				}
			} else {
				for (HadalEntity s : eventData.getSchmucks()) {
					if (!justTeleported.contains(s)) {
						s.setTransform(getConnectedEvent().getPosition(), 0);
						
						if (getConnectedEvent().getStandardParticle() != null) {
							getConnectedEvent().getStandardParticle().onForBurst(1.0f);
						}
					}
				}
			}
		}	
	}

	@Override
	public void clientController(float delta) {
		controller(delta);
	}

	/**
	 * @return a list of entities that just teleported and cannot teleport using the same portal right away.
	 */
	public ObjectSet<HadalEntity> getJustTeleported() {
		if (getStandardParticle() != null) {
			getStandardParticle().onForBurst(1.0f);
		}
		return justTeleported;
	}
	
	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.PORTAL);
		setStandardParticle(Particle.MOMENTUM);
		addAmbientParticle(Particle.PORTAL, 0, -20);
	}
}
