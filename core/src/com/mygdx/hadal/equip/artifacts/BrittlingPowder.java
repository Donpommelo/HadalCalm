package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.DieFrag;
import com.mygdx.hadal.strategies.hitbox.DieSound;

public class BrittlingPowder extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;
	
	private static final int numFrag = 8;
	
	private static final float procCd = 0.5f;
	
	public BrittlingPowder() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {

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
					hbox.addStrategy(new DieFrag(state, hbox, inflicted, numFrag));
					hbox.addStrategy(new DieSound(state, hbox, inflicted, SoundEffect.WALL_HIT1, 0.75f));
				}
			}
		});
		
		return enchantment;
	}
}
