package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class Reloader extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 14.0f;

	private static final float duration = 1.5f;
	private static final float bonusAtkSpd = 0.3f;

	public Reloader(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.RELOAD.playUniversal(state, user.getPlayer().getPixelPosition(), 0.4f, false);

		new ParticleEntity(state, user.getSchmuck(), Particle.BRIGHT, 1.0f, duration, true, ParticleEntity.particleSyncType.CREATESYNC).setColor(
			HadalColor.RED);

		user.addStatus(new StatusComposite(state, duration, false, user, user,
			new StatChangeStatus(state, Stats.TOOL_SPD, bonusAtkSpd, user),
			new Status(state, user)) {

			@Override
			public void onShoot(Equippable tool) {
				tool.gainClip(1);
			}
		});

		for (Equippable e : user.getMultitools()) {
			e.gainClip(100);
			e.gainAmmo(0.5f);
		}
	}

	@Override
	public float getUseDuration() { return duration; }
}
