package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * A touch Portal is a portal that transports schmucks and hboxes that touch it
 * The event they are transported to does not have to be a portal.
 * 
 * Triggered Behavior: N/A
 * Triggering Behavior: This event's connected event serves as the point that schmucks will be teleported to
 * 
 * Fields:
 * N/A
 * 
 * @author Locraft Lulzertier
 */
public class PortalTouch extends Event {

	//This is a set of schmucks that have just been teleported and cannot teleport instantly until they exit this event.
	private final Set<HadalEntity> justTeleported;
	
	public PortalTouch(PlayState state, Vector2 startPos, Vector2 size) {
		super(state, startPos, size);
		justTeleported = new HashSet<>();
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
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true,
				Constants.BIT_SENSOR, (short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE),
				(short) 0, true, eventData);
		this.body.setType(BodyType.KinematicBody);
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

	/**
	 * @return a list of entities that just teleported and cannot teleport using the same portal right away.
	 */
	public Set<HadalEntity> getJustTeleported() {
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
