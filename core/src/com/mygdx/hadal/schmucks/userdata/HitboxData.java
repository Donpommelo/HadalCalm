package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.utils.Stats;

import java.util.Arrays;

/**
 * This stat contains the information relevant to a particular Hitbox.
 * This class is usually extended as an anonymous inner class in each weapon's hitboxFactory where most of the projectile's
 * states + effects are managed. This mostly contains the stats regarding the body and box2d physics
 * @author Pavinsky Praggelvich
 */
public class HitboxData extends HadalData {

	//reference to game state.
	protected PlayState state;
	
	//The hitbox containing this data
	protected Hitbox hbox;

	//this is a damage multiplier for reflected hitboxes
	private static final float reflectMultiplier = 2.0f;
	
	/**
	 * This data is usually initialized after making a hitbox. It is given to the newly created hitbox using the setUserData() method
	 */
	public HitboxData(PlayState state, Hitbox proj) {
		super(UserDataTypes.HITBOX, proj);
		this.state = state;
		this.hbox = proj;
	}
	
	@Override
	public float receiveDamage(float basedamage, Vector2 knockback, BodyData perp, Boolean procEffects, DamageTypes... tags) {
		if (!hbox.isAlive()) { return 0.0f; }
		
		//process hbox reflections/deflections
		if (Arrays.asList(tags).contains(DamageTypes.DEFLECT) && hbox.isReflectable()) {
			super.receiveDamage(basedamage, knockback, perp, procEffects, tags);
		}
		
		if (Arrays.asList(tags).contains(DamageTypes.REFLECT) && hbox.isReflectable()) {
			hbox.setDamageMultiplier(reflectMultiplier * (1 + perp.getStat(Stats.REFLECT_DAMAGE)));
			hbox.setFilter((short) 0);

			//reflecting a projectile should take ownership of it
			for (HitboxStrategy strat: hbox.getStrategies()) {
				strat.setCreator(perp);
			}

			//reset hbox's lifespan
			hbox.setLifeSpan(hbox.getMaxLifespan());
		}
		
		//this is used for hitboxes hat are capable of receiving damage and knockback
		for (HitboxStrategy s : hbox.getStrategies()) {
			s.receiveDamage(perp, basedamage, knockback, tags);
		}
		
		return basedamage;
	}
	
	/**
	 * This method is run when the hitbox collides with something.
	 * Default behaviour: despawn when touching a wall. Otherwise -1 durability and despawn at 0 durability.
	 * @param fixB: The fixture the hitbox collides with.
	 */
	public void onHit(HadalData fixB) {
		if (!hbox.isAlive()) { return; }
		
		for (HitboxStrategy s : hbox.getStrategies()) {
			s.onHit(fixB);
		}
	}

	public Hitbox getHbox() { return hbox; }	
}