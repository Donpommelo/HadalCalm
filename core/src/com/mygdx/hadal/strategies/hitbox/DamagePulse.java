package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

import static com.mygdx.hadal.constants.Constants.PPM;

/**
 */
public class DamagePulse extends HitboxStrategy {

	public static final float PULSE_INTERVAL = 0.1f;

	private final Vector2 pulseSize;
	private final float damage, knockback;
	private float interval;

	//damage tags determine the type of damage inflicted and is used for certain effects
	private final DamageTag[] tags;

	//this is the effect/item/weapon source of the damage
	private final DamageSource source;

	public DamagePulse(PlayState state, Hitbox proj, BodyData user, Vector2 pulseSize, float damage, float knockback,
					   DamageSource source, DamageTag... tags) {
		super(state, proj, user);
		this.pulseSize = pulseSize;
		this.damage = damage;
		this.knockback = knockback;
		this.interval = PULSE_INTERVAL;

		this.source = source;
		this.tags = tags;
	}

	private float controllerCount;
	private final Vector2 hboxPosition = new Vector2();
	private final Vector2 kb = new Vector2();
	@Override
	public void controller(float delta) {

		controllerCount += delta;
		while (controllerCount >= interval) {
			controllerCount -= interval;
			hboxPosition.set(hbox.getPosition());

			state.getWorld().QueryAABB(fixture -> {
					if (fixture.getUserData() instanceof BodyData bodyData) {
						if (bodyData.getSchmuck().getHitboxFilter() != creator.getSchmuck().getHitboxFilter()) {
							kb.set(bodyData.getEntity().getPosition()).sub(hboxPosition).nor().scl(knockback);
							bodyData.receiveDamage(damage, kb, creator, true, hbox, source, tags);
						}
					}
					return true;
				},
				hboxPosition.x - pulseSize.x / 2 / PPM, hboxPosition.y - pulseSize.y / 2 / PPM,
				hboxPosition.x + pulseSize.x / 2 / PPM, hboxPosition.y + pulseSize.y / 2 / PPM);
		}
	}

	public DamagePulse setInterval(float interval) {
		this.interval = interval;
		return this;
	}
}
