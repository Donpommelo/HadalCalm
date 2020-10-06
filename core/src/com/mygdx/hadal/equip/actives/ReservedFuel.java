package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.ParticleColor;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.utils.Stats;

public class ReservedFuel extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 15.0f;
	
	private static final float duration = 5.0f;
	private static final float power = 18.0f;
	
	public ReservedFuel(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.MAGIC2_FUEL.playUniversal(state, user.getPlayer().getPixelPosition(), 0.5f, false);
		new ParticleEntity(state, user.getSchmuck(), Particle.BRIGHT, 1.0f, duration, true, particleSyncType.CREATESYNC).setColor(ParticleColor.BLUE);
		user.addStatus(new StatChangeStatus(state, duration, Stats.FUEL_REGEN, power, user, user));
	}
	
	@Override
	public float getUseDuration() { return duration; }
}
