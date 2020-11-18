package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;

import java.util.ArrayList;

/**
 * This strategy makes a hbox damage enemies it comes in contact with
 * @author Blibrooke Bebalante, Clunard Clamhock, Flebideen Febberish, Sloglodyte Slefka
 */
public class DamageStandard extends HitboxStrategy {
	
	//the amount of damage and knockback this hbox will inflict
	private final float baseDamage, knockback;
	
	//damage tags determine the type of damage inflicted and is used for certain effects
	private final DamageTypes[] tags;
	
	//this contains all the units this hbox has damaged. Used to avoid damaging the same unit multiple times.
	private final ArrayList<HadalData> damaged;

	private boolean selfDamageable = true;

	private boolean staticKnockback, constantKnockback, repeatable;
	private final Vector2 knockbackDirection = new Vector2();

	public DamageStandard(PlayState state, Hitbox proj, BodyData user, float damage, float knockback, DamageTypes... tags) {
		super(state, proj, user);
		this.baseDamage = damage;
		this.knockback = knockback;
		this.tags = tags;
		
		damaged = new ArrayList<>();
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			if (selfDamageable) {
				if (fixB == creator) {
					return;
				}
			}

			if (repeatable) {
				inflictDamage(fixB);
			} else {
				if (!damaged.contains(fixB)) {
					damaged.add(fixB);
					inflictDamage(fixB);
				}
			}
		}
	}

	private final Vector2 kb = new Vector2();
	private void inflictDamage(HadalData fixB) {
		if (staticKnockback) {
			kb.set(fixB.getEntity().getPixelPosition().x - this.hbox.getPixelPosition().x, fixB.getEntity().getPixelPosition().y - this.hbox.getPixelPosition().y);
			fixB.receiveDamage(baseDamage * hbox.getDamageMultiplier(), kb.nor().scl(knockback), creator, true, tags);
		} else if (constantKnockback) {
			fixB.receiveDamage(baseDamage * hbox.getDamageMultiplier(), knockbackDirection.nor().scl(knockback), creator, true, tags);
		} else {
			fixB.receiveDamage(baseDamage * hbox.getDamageMultiplier(), hbox.getLinearVelocity().nor().scl(knockback), creator, true, tags);
		}
	}

	public DamageStandard setStaticKnockback(boolean staticKnockback) {
		this.staticKnockback = staticKnockback;
		this.selfDamageable = staticKnockback;
		return this;
	}

	public DamageStandard setRepeatable(boolean repeatable) {
		this.repeatable = repeatable;
		return this;
	}

	public DamageStandard setConstantKnockback(boolean constantKnockback, Vector2 knockbackDirection) {
		this.constantKnockback = constantKnockback;
		this.selfDamageable = constantKnockback;
		this.knockbackDirection.set(knockbackDirection);
		return this;
	}
}
