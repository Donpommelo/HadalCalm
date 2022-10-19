package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.constants.SyncType;
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

	private static final float USECD = 0.0f;
	private static final float USEDELAY = 0.0f;
	private static final float MAX_CHARGE = 12.0f;

	private static final float DURATION = 3.0f;
	private static final float PROC_CD = 1.0f;
	private static final float CHAIN_DAMAGE = 15.0f;
	private static final int CHAIN_RADIUS = 15;
	private static final int CHAIN_AMOUNT = 4;
	
	public PlusMinus(Schmuck user) {
		super(user, USECD, USEDELAY, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		new ParticleEntity(state, user.getPlayer(), Particle.LIGHTNING_CHARGE, 1.0f, DURATION,
				true, SyncType.CREATESYNC).setColor(HadalColor.SUNGLOW);

		user.addStatus(new Status(state, DURATION, false, user, user) {

			private float procCdCount = PROC_CD;
			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);
				if (procCdCount >= PROC_CD) {
					procCdCount -= PROC_CD;

					SoundEffect.THUNDER.playUniversal(state, user.getPlayer().getPixelPosition(), 0.5f, false);
					user.addStatus(new Shocked(state, user, user, CHAIN_DAMAGE, CHAIN_RADIUS, CHAIN_AMOUNT,
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
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) (DURATION / PROC_CD)),
				String.valueOf((int) CHAIN_DAMAGE),
				String.valueOf(CHAIN_AMOUNT)};
	}
}
