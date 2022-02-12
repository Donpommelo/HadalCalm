package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallSound;
import com.mygdx.hadal.strategies.hitbox.RemoveStrategy;
import com.mygdx.hadal.utils.Stats;

public class CuriousSauce extends Artifact {

	private static final int slotCost = 2;
	
	private static final float procCd = 0.25f;
	
	public CuriousSauce() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.RANGED_PROJ_RESTITUTION, 1.0f, p),
				new Status(state, p) {
			
			private float procCdCount = procCd;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}
			
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (hbox.isEffectsMovement()) {
					hbox.addStrategy(new RemoveStrategy(state, hbox, p, ContactWallDie.class));
					hbox.setSensor(false);

					if (procCdCount >= procCd) {
						procCdCount = 0;
						hbox.addStrategy(new ContactWallSound(state, hbox, p, SoundEffect.SPRING, 0.1f));
					}
				}
			}
		});
	}
}
