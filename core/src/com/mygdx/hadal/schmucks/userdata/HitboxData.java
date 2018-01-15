package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Hitbox;
import com.mygdx.hadal.states.PlayState;

/**
 * This stat contains the information relevant to a particular Hitbox.
 * This class is usually extended as an anonymous inner class in each weapon's hitboxFactory where most of the projectile's
 * states + effects are managed. This mostly contains the stats regarding the body and box2d physics
 * @author Zachary Tu
 *
 */
public class HitboxData extends HadalData {

	//reference to game state.
	public PlayState state;
	
	//The hitbox containing this data
	public Hitbox hbox;

	/**
	 * This data is usually initialized after making a hitbox. It is given to the newly created hitbox using the setUserData() method
	 */
	public HitboxData(PlayState state, World world, Hitbox proj) {
		super(world, UserDataTypes.HITBOX, proj);
		this.state = state;
		this.hbox = proj;
	}
	
	/**
	 * This method is run when the hitbox collides with something.
	 * Default behavious: despawn when touching a wall. Otherwise -1 durability and despawn at 0 durability.
	 * @param fixB: The fixture the hitbox collides with.
	 */
	public void onHit(HadalData fixB) {
		if (fixB == null) {
			hbox.dura = 0;
		} else if (fixB.getType().equals(UserDataTypes.WALL)){
			hbox.dura = 0;
		} else if (fixB.getType().equals(UserDataTypes.BODY)) {
			hbox.dura--;
		}
		if (hbox.dura <= 0) {
			hbox.queueDeletion();
		}
	}
}
