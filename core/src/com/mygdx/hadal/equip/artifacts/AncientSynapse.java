package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.constants.Constants.PRIORITY_LAST_LAST;

public class AncientSynapse extends Artifact {

	private static final int slotCost = 2;

	private static final float baseDegen = 1.0f;
	private static final float degen = 0.15f;
	private static final float procCd = 0.02f;
	
	public AncientSynapse() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private float procCdCount;
			private float damageLeft;
			@Override
			public void timePassing(float delta) {
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					if (damageLeft > 0.0f) {
						float damage = procCd * (baseDegen + degen * damageLeft);
						p.receiveDamage(damage, new Vector2(), p, false, null, DamageSource.ANCIENT_SYNAPSE);
						damageLeft -= damage;
					}
				}
				procCdCount += delta;
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				damageLeft += damage;
				return 0;
			}
		}.setPriority(PRIORITY_LAST_LAST);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) baseDegen),
				String.valueOf((int) (degen * 100))};
	}
}
