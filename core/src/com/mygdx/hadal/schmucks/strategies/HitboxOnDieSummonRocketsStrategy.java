package com.mygdx.hadal.schmucks.strategies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * This strategy makes a hitbox create homing torpedos upon death.
 * @author Zachary Tu
 *
 */
public class HitboxOnDieSummonRocketsStrategy extends HitboxStrategy{
	
	//the number of torpedos created.
	private int numTorp;
	
	//the tool used to create the hitbox that has this strategy.
	private Equipable tool;
	
	//the hitbox filter of units that can be targeted by the torpedos.
	private short filter;
	
	public HitboxOnDieSummonRocketsStrategy(PlayState state, Hitbox proj, BodyData user, Equipable tool, int numTorp, short filter) {
		super(state, proj, user);
		this.tool = tool;
		this.numTorp = numTorp;
		this.filter = filter;
	}
	
	@Override
	public void die() {
		WeaponUtils.createHomingTorpedo(state, this.hbox.getPixelPosition(), creator.getSchmuck(), tool, numTorp, 60, new Vector2(0, 1), false, filter);	
	}
}
