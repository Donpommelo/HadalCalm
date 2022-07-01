package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class TyphonFang extends Artifact {

	private static final int slotCost = 2;
	private final float playerClipPercent = 1.0f;
	private final int monsterClipAmount = 1;

	private final float particleDura = 1.5f;

	public TyphonFang() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p, new Status(state, p) {
			
			@Override
			public void onKill(BodyData vic, DamageSource source) {
				if (p.getCurrentTool() instanceof RangedWeapon weapon) {
					SoundEffect.RELOAD.playUniversal(state, p.getSchmuck().getPixelPosition(), 0.4f, false);
					new ParticleEntity(state, p.getSchmuck(), Particle.PICKUP_AMMO, 1.0f, particleDura, true, SyncType.CREATESYNC);

					if (vic instanceof PlayerBodyData) {
						weapon.gainClip((int) (weapon.getClipSize() * playerClipPercent));
					} else {
						weapon.gainClip(monsterClipAmount);
					}
				}
			}
		});
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (playerClipPercent * 100)),
				String.valueOf(monsterClipAmount)};
	}
}
