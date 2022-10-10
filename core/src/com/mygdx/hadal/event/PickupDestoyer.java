package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.hadal.event.modes.CrownHoldable;
import com.mygdx.hadal.event.modes.FlagCapturable;
import com.mygdx.hadal.event.modes.ReviveGravestone;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

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
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true,
				Constants.BIT_SENSOR, (short) (Constants.BIT_SENSOR | Constants.BIT_PROJECTILE),
				(short) 0, true, eventData);
		this.body.setType(BodyType.KinematicBody);
	}
	
	@Override
	public void controller(float delta) {
		for (HadalEntity entity : eventData.getSchmucks()) {
			if (entity instanceof Scrap scrap) {
				scrap.queueDeletion();
			}
			else if (entity instanceof PickupEquip pickup) {
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
