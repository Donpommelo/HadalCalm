package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.UserDataType;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
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
	protected final PlayState state;
	
	//The hitbox containing this data
	protected final Hitbox hbox;

	//this is a damage multiplier for reflected hitboxes
	private static final float reflectMultiplier = 1.4f;
	
	/**
	 * This data is usually initialized after making a hitbox. It is given to the newly created hitbox using the setUserData() method
	 */
	public HitboxData(PlayState state, Hitbox proj) {
		super(UserDataType.HITBOX, proj);
		this.state = state;
		this.hbox = proj;
	}
	
	@Override
	public float receiveDamage(float baseDamage, Vector2 knockback, BodyData perp, Boolean procEffects, Hitbox hbox, DamageTypes... tags) {
		if (!this.hbox.isAlive()) { return 0.0f; }
		
		//process hbox reflections/deflections
		if (Arrays.asList(tags).contains(DamageTypes.DEFLECT) && this.hbox.isReflectable()) {
			super.receiveDamage(baseDamage, knockback, perp, procEffects, hbox, tags);
		}

		//reflected hboxes can damage any unit and have their damage amplified
		if (Arrays.asList(tags).contains(DamageTypes.REFLECT) && this.hbox.isReflectable()) {
			this.hbox.setDamageMultiplier(reflectMultiplier * (1 + perp.getStat(Stats.REFLECT_DAMAGE)));
			this.hbox.setFilter((short) 0);

			//reflecting a projectile should take ownership of it
			for (HitboxStrategy strat: this.hbox.getStrategies()) {
				strat.setCreator(perp);
			}

			//reset hbox's lifespan
			this.hbox.setLifeSpan(this.hbox.getMaxLifespan());
		}
		
		//this is used for hitboxes hat are capable of receiving damage and knockback
		for (HitboxStrategy s : this.hbox.getStrategies()) {
			s.receiveDamage(perp, baseDamage, knockback, tags);
		}
		
		return baseDamage;
	}
	
	/**
	 * This method is run when the hitbox collides with something.
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