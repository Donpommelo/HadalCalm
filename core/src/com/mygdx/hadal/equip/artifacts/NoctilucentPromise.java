package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.utils.Constants.PRIORITY_LAST;

public class NoctilucentPromise extends Artifact {

	private static final int slotCost = 1;

	private static final float invulnDura = 3.0f;

	public NoctilucentPromise() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private boolean activated;
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (!activated) {
					if (damage >= p.getCurrentHp()) {
						activated = true;
						p.setCurrentHp(1);

						SoundEffect.MAGIC18_BUFF.playUniversal(state, p.getSchmuck().getPixelPosition(), 0.5f, false);
						p.addStatus(new Invulnerability(state, invulnDura, p, p));

						return 0;
					}
				}
				return damage;
			}
		}.setPriority(PRIORITY_LAST);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) invulnDura)};
	}
}
