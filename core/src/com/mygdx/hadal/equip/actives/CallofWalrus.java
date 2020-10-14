package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class CallofWalrus extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 10.0f;
	
	private static final float duration = 5.0f;
	
	private static final float spdBuff = 0.5f;
	private static final float damageBuff = 0.3f;
	
	private static final float hpCost = 15.0f;
	
	public CallofWalrus(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		
		if (user.getCurrentHp() > hpCost) {
			user.setCurrentHp(user.getCurrentHp() - hpCost);
			SoundEffect.MAGIC18_BUFF.playUniversal(state, user.getPlayer().getPixelPosition(), 0.5f, false);
			
			new ParticleEntity(state, user.getSchmuck(), Particle.PICKUP_ENERGY, 1.0f, duration, true, particleSyncType.CREATESYNC);
			new ParticleEntity(state, user.getSchmuck(), Particle.BRIGHT, 1.0f, duration, true, particleSyncType.CREATESYNC).setColor(
				HadalColor.RED);
			
			user.addStatus(new StatusComposite(state, duration, false, user, user,
					new StatChangeStatus(state, Stats.GROUND_SPD, spdBuff, user),
					new StatChangeStatus(state, Stats.DAMAGE_AMP, damageBuff, user)));
		} else {
			gainChargeByPercent(1.0f);
		}
	}
	
	@Override
	public float getUseDuration() { return duration; }
}
