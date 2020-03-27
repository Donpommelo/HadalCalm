package com.mygdx.hadal.strategies.hitbox;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy is used by hitboxes that do set knockback on hit regardless of its own position or direction.
 * This is used by airblast
 * @author Zachary Tu
 *
 */
public class DamageConstant extends HitboxStrategy {
	
	//the amount of damage and knockback this hbox will inflict
	private float baseDamage;
	private Vector2 knockback;
	
	//damage tags determine the type of damage inflicted and is used for certain effects
	private DamageTypes[] tags;
	
	private ArrayList<HadalData> damaged;
	
	public DamageConstant(PlayState state, Hitbox proj, BodyData user, float damage, Vector2 knockback, DamageTypes... tags) {
		super(state, proj, user);
		this.baseDamage = damage;
		this.knockback = knockback;
		this.tags = tags;
		
		damaged = new ArrayList<HadalData>();
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			if (!damaged.contains(fixB)) {
				damaged.add(fixB);
				fixB.receiveDamage(baseDamage * hbox.getDamageMultiplier(), knockback, creator, true, tags);
			}
		}
	}
}
