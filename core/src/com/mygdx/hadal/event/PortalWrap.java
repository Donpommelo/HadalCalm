package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * A Wrapping portal transports players that touch it to a destination but keep the player's x or y coordinate constant.
 * Triggered Behavior: N/A
 * Triggering Behavior: This is the event that the player will be teleported to.
 * 
 * Fields:
 * axis: boolean do we keep the player's x constant or y?. Default: true (x)
 * @author Zachary Tu
 *
 */
public class PortalWrap extends Event {

	private boolean axis;
	
	public PortalWrap(PlayState state, Vector2 startPos, Vector2 size, boolean axis) {
		super(state, startPos, size);
		this.axis = axis;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this);
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, Constants.BIT_SENSOR, Constants.BIT_PLAYER, (short) 0, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		if (getConnectedEvent() != null) {
			
			for (HadalEntity s : eventData.getSchmucks()) {

				Vector3 newCamera= new Vector3(state.camera.position).sub(new Vector3(state.getPlayer().getPixelPosition().x, state.getPlayer().getPixelPosition().y, 0));

				if (axis) {
					s.setTransform(getConnectedEvent().getPosition().x, s.getPosition().y, 0);
				} else {
					s.setTransform(s.getPosition().x, getConnectedEvent().getPosition().y, 0);
				}
				
				//If the player is being teleported, instantly adjust the camera to make for a seamless movement.
				if(s.equals(state.getPlayer())) {
					state.camera.position.set(new Vector3(state.getPlayer().getPixelPosition().x, state.getPlayer().getPixelPosition().y, 0).add(newCamera));
				}
			}
		}	
	}
}
