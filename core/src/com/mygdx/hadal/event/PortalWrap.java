package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * A Wrapping portal transports players that touch it to a destination but keep the player's x or y coordinate constant.
 * Triggered Behavior: N/A
 * Triggering Behavior: This is the event that the player will be teleported to.
 * <p>
 * Fields:
 * axis: boolean do we keep the player's x constant or y?. Default: true (x)
 * direction: boolean. what direction do we warp the player? Default: false (upwards/rightwards)
 * @author Thimeister Talitta
 */
public class PortalWrap extends Event {

	private final boolean axis, direction;
	
	public PortalWrap(PlayState state, Vector2 startPos, Vector2 size, boolean axis, boolean direction) {
		super(state, startPos, size);
		this.axis = axis;
		this.direction = direction;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this);

		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, BodyConstants.BIT_PLAYER, (short) 0)
				.setBodyType(BodyDef.BodyType.StaticBody)
				.addToWorld(world);
	}

	private final Vector3 newCamera = new Vector3();
	private final Vector3 playerTempLocation = new Vector3();
	private final Vector2 playerLocation = new Vector2();
	private final Vector2 entityLocation = new Vector2();
	private final Vector2 connectedLocation = new Vector2();
	@Override
	public void controller(float delta) {
		if (getConnectedEvent() != null) {
			Player ownPlayer = HadalGame.usm.getOwnPlayer();

			if (ownPlayer == null) { return; }
			if (ownPlayer.getPlayerData() == null) { return; }

			playerLocation.set(ownPlayer.getPixelPosition());
			connectedLocation.set(getConnectedEvent().getPosition());
			for (HadalEntity s : eventData.getSchmucks()) {
				
				newCamera.set(state.getCamera().position).sub(playerTempLocation.set(playerLocation.x, playerLocation.y, 0));
				entityLocation.set(s.getPosition());
				if (axis) {
					if (direction) {
						s.setTransform(connectedLocation.x + s.getSize().x / 64, entityLocation.y, 0);
					} else {
						s.setTransform(connectedLocation.x - s.getSize().x / 64, entityLocation.y, 0);
					}
				} else {
					if (direction) {
						s.setTransform(entityLocation.x, connectedLocation.y + s.getSize().y / 64, 0);
					} else {
						s.setTransform(entityLocation.x, connectedLocation.y - s.getSize().y / 64, 0);
					}
				}
				
				//If the player is being teleported, instantly adjust the camera to make for a seamless movement.
				if (s.equals(ownPlayer)) {
					playerLocation.set(ownPlayer.getPixelPosition());
					state.getCamera().position.set(newCamera.add(playerLocation.x, playerLocation.y, 0));
					state.getCamera().update();
				}
			}
		}	
	}
}
