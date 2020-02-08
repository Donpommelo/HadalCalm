package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy is used by explosives.
 * Explosions deal damage to all units but less damage to the user.
 * Because explosions are static, their knock back is based on positioning rather than their own momentum
 * @author Zachary Tu
 *
 */
public class DamageConstant extends HitboxStrategy{
	
	//the amount of damage and knockback this hbox will inflict
	private float baseDamage;
	private Vector2 knockback;
	
	//damage tags determine the type of damage inflicted and is used for certain effects
	private DamageTypes[] tags;
	
	public DamageConstant(PlayState state, Hitbox proj, BodyData user, float damage, Vector2 knockback, DamageTypes... tags) {
		super(state, proj, user);
		this.baseDamage = damage;
		this.knockback = knockback;
		this.tags = tags;
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			fixB.receiveDamage(baseDamage, knockback, creator, true, tags);
		}
	}
}
