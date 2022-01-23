package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.hadal.event.modes.CrownHoldable;
import com.mygdx.hadal.event.modes.FlagCapturable;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
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
			if (entity instanceof PickupEquip pickup) {
				pickup.queueDeletion();
			}
			if (entity instanceof Hitbox hbox) {
				hbox.queueDeletion();
			}
			if (entity instanceof FlagCapturable flag) {
				flag.queueDeletion();
			}
			if (entity instanceof CrownHoldable crown) {
				crown.queueDeletion();
			}
		}
	}
}
