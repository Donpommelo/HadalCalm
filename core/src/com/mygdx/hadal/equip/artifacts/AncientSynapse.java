package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.constants.StatusPriority.PRIORITY_LAST;

public class AncientSynapse extends Artifact {

	private static final int SLOT_COST = 2;

	private static final float BASE_DEGEN = 1.0f;
	private static final float DEGEN = 0.2f;
	private static final float PROC_CD = 0.02f;
	
	public AncientSynapse() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private float procCdCount;
			private float damageLeft;
			@Override
			public void timePassing(float delta) {
				if (procCdCount >= PROC_CD) {
					procCdCount -= PROC_CD;
					if (damageLeft > 0.0f) {
						float damage = PROC_CD * (BASE_DEGEN + DEGEN * damageLeft);
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
		}.setPriority(PRIORITY_LAST);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DEGEN),
				String.valueOf((int) (DEGEN * 100))};
	}
}
