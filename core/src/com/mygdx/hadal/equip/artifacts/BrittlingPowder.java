package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.DieFrag;
import com.mygdx.hadal.strategies.hitbox.DieSound;

public class BrittlingPowder extends Artifact {

	private static final int slotCost = 2;
	
	private static final int numFrag = 8;
	private static final float procCd = 0.5f;
	
	public BrittlingPowder() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p, new Status(state, p) {

			private float procCdCount = procCd;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}
			
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (!hbox.isEffectsHit()) { return; }
				
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					hbox.addStrategy(new DieFrag(state, hbox, p, numFrag));
					hbox.addStrategy(new DieSound(state, hbox, p, SoundEffect.WALL_HIT1, 0.75f));
				}
			}
		});
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf(procCd),
				String.valueOf(numFrag),
				String.valueOf((int) DieFrag.baseDamage)};
	}
}
