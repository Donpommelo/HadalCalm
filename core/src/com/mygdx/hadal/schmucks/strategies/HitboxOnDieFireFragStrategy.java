package com.mygdx.hadal.schmucks.strategies;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.concurrent.ThreadLocalRandom;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class HitboxOnDieFireFragStrategy extends HitboxStrategy{
	
	private int numFrag;
	private Equipable tool;
	private short filter;

	private final static int projectileWidth = 60;
	private final static int projectileHeight = 60;
	private final static float lifespan = 0.5f;
	private final static float gravity = 2.0f;

	private final static int projDura = 5;
	
	private final static String projSpriteId = "orb_orange";
	
	private final static float baseDamage = 8.0f;
	private final static float knockback = 5.0f;
	
	public HitboxOnDieFireFragStrategy(PlayState state, Hitbox proj, BodyData user, Equipable tool, int numFrag, short filter) {
		super(state, proj, user);
		this.tool = tool;
		this.filter = filter;
		this.numFrag = numFrag;
	}
	
	@Override
	public void die() {
		for (int i = 0; i < numFrag; i++) {
			
			float newDegrees = (ThreadLocalRandom.current().nextInt(0, 360));

			HitboxImage hbox = new HitboxImage(state, 
					this.hbox.getBody().getPosition().x * PPM, 
					this.hbox.getBody().getPosition().y * PPM,
					projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, 
					this.hbox.getLinearVelocity().setAngle(newDegrees),
					filter, true, true, creator.getSchmuck(), projSpriteId);
			
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, creator));
			hbox.addStrategy(new HitboxOnContactUnitLoseDuraStrategy(state, hbox, creator));
			hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, creator));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, creator, tool, baseDamage, knockback, DamageTypes.RANGED));
		}
	}
}
