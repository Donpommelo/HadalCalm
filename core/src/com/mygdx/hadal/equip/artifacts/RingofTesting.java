package com.mygdx.hadal.equip.artifacts;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class RingofTesting extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 0;
	
	private final static int numFrag = 100;
	private final static Vector2 projectileSize = new Vector2(50, 50);

	private final static float lifespan = 10.0f;
	private final static float fragSpeed = 2.0f;
	
	private final static float baseDamage = 1.0f;
	private final static float knockback = 5.0f;
	
	public RingofTesting() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {
			
			@Override
			public void onReload(Equipable tool) {
				Vector2 fragVelo = new Vector2();
				for (int i = 0; i < numFrag; i++) {
					float newDegrees = (ThreadLocalRandom.current().nextInt(0, 360));
					fragVelo.set(0, fragSpeed).setAngle(newDegrees);

					Hitbox frag = new Hitbox(state, inflicted.getSchmuck().getPixelPosition(), projectileSize, lifespan, fragVelo, inflicted.getSchmuck().getHitboxfilter(), 
							true, false, inflicted.getSchmuck(), Sprite.FLOUNDER_A);
					
					frag.addStrategy(new ControllerDefault(state, frag, inflicted));
					frag.addStrategy(new DamageStandard(state, frag, inflicted, baseDamage, knockback, DamageTypes.SHRAPNEL));
				}
			}
		});
		
		return enchantment;
	}
}
