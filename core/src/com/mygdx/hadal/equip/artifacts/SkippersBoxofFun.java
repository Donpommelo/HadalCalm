package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.UnlocktoItem;

public class SkippersBoxofFun extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float procCd = 10.0f;
	private final static float particleDura = 1.0f;
	
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
					Equipable equip = UnlocktoItem.getUnlock(UnlockEquip.valueOf(UnlockEquip.getRandWeapFromPool(state.getGsm().getRecord(), "")), null);
					((Player)inflicted.getSchmuck()).getPlayerData().pickup(equip);
					
					new ParticleEntity(state, inflicted.getSchmuck(), Particle.SMOKE_TOTLC, 0.0f, particleDura, true, particleSyncType.TICKSYNC);
				}
				procCdCount += delta;
			}
		});
		return enchantment;
	}
}
