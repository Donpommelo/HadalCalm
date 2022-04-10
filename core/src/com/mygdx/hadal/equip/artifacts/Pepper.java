package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class Pepper extends Artifact {

	private static final int slotCost = 1;
	
	private static final float radius = 10.0f;
	private static final float damage = 9.0f;
	private static final float effectDuration = 1.0f;
	
	private static final float procCd = 1.5f;

	public Pepper() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private float procCdCount = procCd;
			private final Vector2 entityLocation = new Vector2();
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					entityLocation.set(p.getSchmuck().getPosition());
					state.getWorld().QueryAABB(fixture -> {
						if (fixture.getUserData() instanceof BodyData bodyData) {
							if (bodyData.getSchmuck().getHitboxfilter() != p.getSchmuck().getHitboxfilter()) {
								bodyData.receiveDamage(damage, new Vector2(), p, true, null, DamageSource.PEPPER);
								bodyData.getSchmuck().setShader(Shader.STATIC, effectDuration, true);
							}
						}
						return true;
					},
						entityLocation.x - radius, entityLocation.y - radius, 
						entityLocation.x + radius, entityLocation.y + radius);		
				}
			}
		};
	}
}
