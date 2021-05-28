package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.UnlocktoItem;

import java.util.Objects;

public class SkippersBoxofFun extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final float procCd = 10.0f;
	private static final float particleDura = 1.0f;
	
	public SkippersBoxofFun() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {
			
			private float procCdCount;
			
			@Override
			public void timePassing(float delta) {
				
				if (procCdCount >= procCd) {
					procCdCount -= procCd;

					SoundEffect.MAGIC27_EVIL.playUniversal(state, inflicted.getSchmuck().getPixelPosition(), 0.5f, false);

					Equippable
                        equip = UnlocktoItem.getUnlock(UnlockEquip.getByName(UnlockEquip.getRandWeapFromPool(state, "")), null);
					((Player) inflicted.getSchmuck()).getPlayerData().pickup(Objects.requireNonNull(equip));
					
					new ParticleEntity(state, inflicted.getSchmuck(), Particle.SMOKE_TOTLC, 1.0f, particleDura, true, particleSyncType.CREATESYNC);
				}
				procCdCount += delta;
			}
		});
		return enchantment;
	}
}
