package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Shocked;
import com.mygdx.hadal.statuses.Status;

/**
 * @author Borpwood Bluwood
 */
public class PlusMinus extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 12.0f;

	private static final float duration = 3.0f;
	private static final float procCd = 1.0f;

	private static final float chainDamage = 15.0f;
	private static final int chainRadius = 15;
	private static final int chainAmount = 4;
	
	public PlusMinus(Schmuck user) {
		super(user, usecd, usedelay, maxCharge);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		new ParticleEntity(state, user.getPlayer(), Particle.LIGHTNING_CHARGE, 1.0f, duration,
				true, SyncType.CREATESYNC).setColor(HadalColor.SUNGLOW);

		user.addStatus(new Status(state, duration, false, user, user) {

			private float procCdCount = procCd;
			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);
				if (procCdCount >= procCd) {
					procCdCount -= procCd;

					SoundEffect.THUNDER.playUniversal(state, user.getPlayer().getPixelPosition(), 0.5f, false);
					user.addStatus(new Shocked(state, user, user, chainDamage, chainRadius, chainAmount,
							user.getSchmuck().getHitboxfilter(), DamageSource.PLUS_MINUS));
				}
				procCdCount += delta;
			}
		});
	}

	@Override
	public float getBotRangeMin() { return 7.0f; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) maxCharge),
				String.valueOf((int) (duration / procCd)),
				String.valueOf((int) chainDamage),
				String.valueOf(chainAmount)};
	}
}
