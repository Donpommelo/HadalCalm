package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.event.modes.CrownHoldable;
import com.mygdx.hadal.event.modes.FlagCapturable;
import com.mygdx.hadal.event.modes.ReviveGravestone;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * The pickup destroyer destroys pickups that make contact with it.
 * Place at the bottom of bottomless pits to despawn things like flags/graves
 * We also want to despawn weapon drops to avoid bots trying to pick them up
 */
public class PickupDestoyer extends Event {

	public PickupDestoyer(PlayState state, Vector2 startPos, Vector2 size) {
		super(state, startPos, size);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this);
		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR,
				(short) (BodyConstants.BIT_SENSOR | BodyConstants.BIT_PROJECTILE), (short) 0)
				.setBodyType(BodyDef.BodyType.KinematicBody)
				.addToWorld(world);
	}
	
	@Override
	public void controller(float delta) {
		for (HadalEntity entity : eventData.getSchmucks()) {
			if (entity instanceof PickupEquip pickup) {
				pickup.queueDeletion();
			}
			else if (entity instanceof Hitbox hbox) {
				hbox.queueDeletion();
			}
			else if (entity instanceof FlagCapturable flag) {
				flag.queueDeletion();
			}
			else if (entity instanceof CrownHoldable crown) {
				crown.queueDeletion();
			}
			else if (entity instanceof ReviveGravestone grave) {
				grave.resetPosition();
			}
			else if (entity.isBotHealthPickup()) {
				entity.queueDeletion();
			}
		}
	}
}
