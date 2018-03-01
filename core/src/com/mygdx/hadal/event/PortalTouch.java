package com.mygdx.hadal.event;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * A Use Portal is a portal that transports the player elsewhere when they interact with it.
 * The event they are transported to does not have to be a portal.
 * @author Zachary Tu
 *
 */
public class PortalTouch extends Event {

	private static final String name = "Portal";

	private Set<HadalEntity> justTeleported;
	
	public PortalTouch(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y) {
		super(state, world, camera, rays, name, width, height, x, y);
		justTeleported = new HashSet<HadalEntity>();
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(world, this) {
			
			@Override
			public void onRelease(HadalData fixB) {
				if (fixB != null) {
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
			
			if (getConnectedEvent() instanceof PortalTouch) {
				for (HadalEntity s : eventData.getSchmucks()) {
					if (!justTeleported.contains(s)) {
						((PortalTouch)getConnectedEvent()).getJustTeleported().add(s);
						s.getBody().setTransform(getConnectedEvent().getBody().getPosition(), 0);
					}
				}
			} else {
				for (HadalEntity s : eventData.getSchmucks()) {
					if (!justTeleported.contains(s)) {
						s.getBody().setTransform(getConnectedEvent().getBody().getPosition(), 0);
					}
				}
			}
		}	
	}

	public Set<HadalEntity> getJustTeleported() {
		return justTeleported;
	}
}
