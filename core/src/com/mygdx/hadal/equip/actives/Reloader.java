package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

/**
 * @author Gerrbort Gnolfredo
 */
public class Reloader extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 13.0f;

	private static final float duration = 1.5f;
	private static final float bonusAtkSpd1 = 0.45f;
	private static final float bonusAtkSpd2 = 0.3f;

	public Reloader(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.RELOAD.playUniversal(state, user.getPlayer().getPixelPosition(), 0.4f, false);

		new ParticleEntity(state, user.getSchmuck(), Particle.BRIGHT, 1.0f, duration, true, SyncType.CREATESYNC).setColor(
			HadalColor.RED);

		user.addStatus(new StatusComposite(state, duration, false, user, user,
			new Status(state, user)) {

			@Override
			public void onShoot(Equippable tool) {

				float modifiedAttackSpeed = bonusAtkSpd1;

				if (tool instanceof RangedWeapon weapon) {
					if (weapon.getClipSize() <= 1) {
						modifiedAttackSpeed = 0;
					} else if (weapon.getClipSize() <= 4) {
						modifiedAttackSpeed = bonusAtkSpd2;
					}
				}

				float cooldown = inflicter.getSchmuck().getShootCdCount();
				inflicter.getSchmuck().setShootCdCount(cooldown * (1 - modifiedAttackSpeed));
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
