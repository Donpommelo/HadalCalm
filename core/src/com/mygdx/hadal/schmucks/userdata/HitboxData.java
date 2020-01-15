package com.mygdx.hadal.schmucks.userdata;

import java.util.Arrays;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This stat contains the information relevant to a particular Hitbox.
 * This class is usually extended as an anonymous inner class in each weapon's hitboxFactory where most of the projectile's
 * states + effects are managed. This mostly contains the stats regarding the body and box2d physics
 * @author Zachary Tu
 *
 */
public class HitboxData extends HadalData {

	//reference to game state.
	protected PlayState state;
	
	//The hitbox containing this data
	protected Hitbox hbox;

	/**
	 * This data is usually initialized after making a hitbox. It is given to the newly created hitbox using the setUserData() method
	 */
	public HitboxData(PlayState state, Hitbox proj) {
		super(UserDataTypes.HITBOX, proj);
		this.state = state;
		this.hbox = proj;
	}
	
	@Override
	public void receiveDamage(float basedamage, Vector2 knockback, BodyData perp, Boolean procEffects, DamageTypes... tags) {
		if (!hbox.isAlive())
			return;
		
		if (Arrays.asList(tags).contains(DamageTypes.DEFLECT) && hbox.isAlive() && hbox.isReflectable()) {
			super.receiveDamage(basedamage, knockback, perp, procEffects, tags);
		}
		
		if (Arrays.asList(tags).contains(DamageTypes.REFLECT) && hbox.isAlive()  && hbox.isReflectable()) {
			Filter filter = hbox.getBody().getFixtureList().get(0).getFilterData();
			filter.groupIndex = (short)0;
			hbox.getBody().getFixtureList().get(0).setFilterData(filter);
		}
	}
	
	/**
	 * This method is run when the hitbox collides with something.
	 * Default behavious: despawn when touching a wall. Otherwise -1 durability and despawn at 0 durability.
	 * @param fixB: The fixture the hitbox collides with.
	 */
	public void onHit(HadalData fixB) {
		if (!hbox.isAlive())
			return;
		
		for (HitboxStrategy s : hbox.getStrategies()) {
			s.onHit(fixB);
		}
	}

	public Hitbox getHbox() { return hbox; }	
}