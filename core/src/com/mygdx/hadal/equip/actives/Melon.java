package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class Melon extends ActiveItem {

	private final static String name = "Melon";
	private final static float usecd = 0.0f;
	private final static float usedelay = 0.2f;
	private final static float maxCharge = 600.0f;
	
	private final static float duration = 5.0f;
	private final static float power = 8.0f;
	
	private ParticleEntity regen;
	
	public Melon(Schmuck user) {
		super(user, name, usecd, usedelay, maxCharge, chargeStyle.byDamage);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		user.addStatus(new StatChangeStatus(state, duration, Stats.HP_REGEN, power, user, user) {
			@Override
			public void onInflict(Status s) {
				if (s.equals(this)) {
					regen = new ParticleEntity(state, inflicted.getSchmuck(), Particle.REGEN, 3.0f, 0.0f, true, particleSyncType.TICKSYNC);
				}
			}
			
			@Override
			public void onRemove(Status s) {
				if (s.equals(this) && regen != null) {
					regen.setDespawn(true);
					regen.turnOff();
				}
			}
		});
	}
}
