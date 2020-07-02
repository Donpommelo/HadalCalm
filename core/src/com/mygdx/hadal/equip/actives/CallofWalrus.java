package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
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

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 12.0f;
	
	private final static float duration = 6.0f;
	
	private final static float spdBuff = 0.5f;
	private final static float damageBuff = 0.3f;
	
	private final static float hpCost = 15.0f;
	
	public CallofWalrus(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		
		if (user.getCurrentHp() > hpCost) {
			user.setCurrentHp(user.getCurrentHp() - hpCost);
			SoundEffect.MAGIC18_BUFF.playUniversal(state, user.getPlayer().getPixelPosition(), 0.5f, false);
			
			new ParticleEntity(state, user.getSchmuck(), Particle.PICKUP_ENERGY, 1.0f, duration, true, particleSyncType.CREATESYNC);
			
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
