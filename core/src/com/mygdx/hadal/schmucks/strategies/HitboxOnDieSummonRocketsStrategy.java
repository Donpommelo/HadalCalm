package com.mygdx.hadal.schmucks.strategies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class HitboxOnDieSummonRocketsStrategy extends HitboxStrategy{
	
	private int numTorp;
	private Equipable tool;
	private short filter;

	
	public HitboxOnDieSummonRocketsStrategy(PlayState state, Hitbox proj, BodyData user, Equipable tool, int numTorp, short filter) {
		super(state, proj, user);
		this.tool = tool;
		this.numTorp = numTorp;
		this.filter = filter;
	}
	
	@Override
	public void die() {
		WeaponUtils.createHomingTorpedo(state, this.hbox.getBody().getPosition().x * PPM , this.hbox.getBody().getPosition().y * PPM, 
				creator.getSchmuck(), tool, numTorp, 60, new Vector2(0, 1), false, filter);	
	}
}
