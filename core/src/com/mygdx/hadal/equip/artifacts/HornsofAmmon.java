package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.statuses.Status;

public class HornsofAmmon extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 3;
	
	private static final float threshold = 5.0f;
	private static final float invulnDura = 1.0f;
	
	public HornsofAmmon() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {

			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				
				if (damage > threshold) {
					if (inflicted.getStatus(Invulnerability.class) == null) {
						SoundEffect.MAGIC18_BUFF.playUniversal(state, inflicted.getSchmuck().getPixelPosition(), 0.5f, false);

						inflicted.receiveDamage(damage, new Vector2(), perp, false, tags);
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
