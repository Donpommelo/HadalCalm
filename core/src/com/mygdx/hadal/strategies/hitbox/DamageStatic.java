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
 * This strategy is used by explosives or other non-moving hboxes.
 * Knockback is applied outwards from the center of the hbox.
 * Explosions deal damage to all units but less damage to the user.
 * @author Blibrooke Bebalante
 */
public class DamageStatic extends HitboxStrategy {
	
	//the amount of damage and knockback this hbox will inflict
	private final float baseDamage, knockback;
	
	//damage tags determine the type of damage inflicted and is used for certain effects
	private final DamageTypes[] tags;
	
	//this contains all the units this hbox has damaged. Used to avoid damaging the same unit multiple times.
	private final ArrayList<HadalData> damaged;

	public DamageStatic(PlayState state, Hitbox proj, BodyData user, float damage, float knockback, DamageTypes... tags) {
		super(state, proj, user);
		this.baseDamage = damage;
		this.knockback = knockback;
		this.tags = tags;
		
		damaged = new ArrayList<>();
	}
	
	private final Vector2 kb = new Vector2();
	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			if (!damaged.contains(fixB)) {
				damaged.add(fixB);
				kb.set(fixB.getEntity().getPixelPosition().x - this.hbox.getPixelPosition().x, fixB.getEntity().getPixelPosition().y - this.hbox.getPixelPosition().y);
				fixB.receiveDamage(baseDamage * hbox.getDamageMultiplier(), kb.nor().scl(knockback), creator, true, tags);
			}
		}
	}
}
