package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.statuses.Status;

public class NoctilucentPromise extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;

	private static final float invulnDura = 4.0f;

	public NoctilucentPromise() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {

			private boolean activated;
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageTypes... tags) {
				if (!activated) {
					if (damage >= inflicted.getCurrentHp()) {
						activated = true;
						inflicted.setCurrentHp(1);

						SoundEffect.MAGIC18_BUFF.playUniversal(state, inflicted.getSchmuck().getPixelPosition(), 0.5f, false);
						inflicted.addStatus(new Invulnerability(state, invulnDura, inflicted, inflicted));

						return 0;
					}
				}
				return damage;
			}
		};
		return enchantment;
	}
}
