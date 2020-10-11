package com.mygdx.hadal.equip.artifacts;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class RingofTesting extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 0;
	
	private static final int numFrag = 500;
	private static final Vector2 projectileSize = new Vector2(50, 50);

	private static final float lifespan = 10.0f;
	private static final float fragSpeed = 2.0f;
	
	private static final float baseDamage = 1.0f;
	private static final float knockback = 5.0f;
	
	public RingofTesting() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {
			
			@Override
			public void onReloadFinish(Equippable tool) {
				Vector2 fragVelo = new Vector2();
				for (int i = 0; i < numFrag; i++) {
					float newDegrees = (ThreadLocalRandom.current().nextInt(0, 360));
					fragVelo.set(0, fragSpeed).setAngle(newDegrees);

					Hitbox frag = new Hitbox(state, inflicted.getSchmuck().getPixelPosition(), projectileSize, lifespan, fragVelo, inflicted.getSchmuck().getHitboxfilter(), 
							true, false, inflicted.getSchmuck(), Sprite.FLOUNDER_A);
					frag.setSyncDefault(false);
					frag.setSyncInstant(true);
					frag.addStrategy(new ControllerDefault(state, frag, inflicted));
					frag.addStrategy(new DamageStandard(state, frag, inflicted, baseDamage, knockback, DamageTypes.SHRAPNEL));
				}
			}
		});
		
		return enchantment;
	}
}
