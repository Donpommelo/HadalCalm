package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.utils.Constants;

public class RingofTesting extends Artifact {

	private static final int slotCost = 0;
	
	private static final int numFrag = 500;
	private static final Vector2 projectileSize = new Vector2(50, 50);

	private static final float lifespan = 10.0f;
	private static final float fragSpeed = 2.0f;
	
	private static final float baseDamage = 1.0f;
	private static final float knockback = 5.0f;
	
	public RingofTesting() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new Status(state, p) {
			
			@Override
			public void onReloadFinish(Equippable tool) {
				Vector2 fragVelo = new Vector2();
				for (int i = 0; i < numFrag; i++) {
					float newDegrees = MathUtils.random(0, 360);
					fragVelo.set(0, fragSpeed).setAngleDeg(newDegrees);

					Hitbox frag = new Hitbox(state, inflicted.getSchmuck().getPixelPosition(), projectileSize, lifespan, fragVelo, inflicted.getSchmuck().getHitboxfilter(), 
							true, false, inflicted.getSchmuck(), Sprite.FLOUNDER_A);
					frag.setPassability((short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY));

					frag.setSyncDefault(false);
					frag.setSyncInstant(true);
					frag.addStrategy(new ControllerDefault(state, frag, inflicted));
					frag.addStrategy(new DamageStandard(state, frag, inflicted, baseDamage, knockback, DamageTypes.SHRAPNEL));
				}
			}
		});
	}
}
