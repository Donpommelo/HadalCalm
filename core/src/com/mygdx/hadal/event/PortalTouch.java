package com.mygdx.hadal.event;

import java.util.HashSet;
import java.util.Set;

import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

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
 * 
 * @author Zachary Tu
 *
 */
public class PortalTouch extends Event {

	private static final String name = "Portal";

	//This is a set of schmucks that have just been teleported and cannot teleport instantly until they exit this event.
	private Set<HadalEntity> justTeleported;
	
	public PortalTouch(PlayState state, int width, int height, int x, int y) {
		super(state, name, width, height, x, y);
		justTeleported = new HashSet<HadalEntity>();
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
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE),
				(short) 0, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		if (getConnectedEvent() != null) {
			
			//If teleporting someone to another portal, add them to the justTeleported list so htey cannot teleport again right away.
			if (getConnectedEvent() instanceof PortalTouch) {
				for (HadalEntity s : eventData.getSchmucks()) {
					if (!justTeleported.contains(s)) {
						((PortalTouch)getConnectedEvent()).getJustTeleported().add(s);
						s.getBody().setTransform(getConnectedEvent().getBody().getPosition(), 0);
						
						if (getConnectedEvent().getStandardParticle() != null) {
							getConnectedEvent().getStandardParticle().onForBurst(1.0f);
						}
					}
				}
			} else {
				for (HadalEntity s : eventData.getSchmucks()) {
					if (!justTeleported.contains(s)) {
						s.getBody().setTransform(getConnectedEvent().getBody().getPosition(), 0);
						
						if (getConnectedEvent().getStandardParticle() != null) {
							getConnectedEvent().getStandardParticle().onForBurst(1.0f);
						}
					}
				}
			}
		}	
	}

	public Set<HadalEntity> getJustTeleported() {
		
		if (getStandardParticle() != null) {
			getStandardParticle().onForBurst(1.0f);
		}
		
		return justTeleported;
	}
	
	@Override
	public void loadDefaultProperties() {
		setEventSprite("portal");
		setStandardParticle("MOMENTUM");
		addAmbientParticle("PORTAL");
	}
}
