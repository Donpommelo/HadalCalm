package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Shocked;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.utils.Constants.PRIORITY_PROC;

public class VolatileDermis extends Artifact {

	private static final int slotCost = 2;
	
	private static final float procCd = 4.0f;
	private static final float chainDamage = 15.0f;
	private static final int chainRadius = 10;
	private static final int chainAmount = 3;

	public VolatileDermis() { super(slotCost); }

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private float procCdCount = procCd;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageTypes... tags) {
				if (procCdCount >= procCd && damage > 0) {
					procCdCount = 0;
					SoundEffect.THUNDER.playUniversal(state, p.getSchmuck().getPixelPosition(), 0.5f, false);
					p.addStatus(new Shocked(state, p, p, chainDamage, chainRadius, chainAmount, p.getSchmuck().getHitboxfilter()));
				}
				return damage;
			}
		}.setPriority(PRIORITY_PROC);
	}
}
