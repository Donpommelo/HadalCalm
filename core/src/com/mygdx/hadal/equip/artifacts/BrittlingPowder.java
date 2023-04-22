package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.attacks.artifact.BrittlingPowderActivate;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.DieFrag;
import com.mygdx.hadal.strategies.hitbox.DieSound;

public class BrittlingPowder extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final int NUM_FRAG = 8;
	private static final float PROC_CD = 0.5f;
	
	public BrittlingPowder() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p, new Status(state, p) {

			private float procCdCount = PROC_CD;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < PROC_CD) {
					procCdCount += delta;
				}
			}
			
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (!hbox.isEffectsHit()) { return; }
				
				if (procCdCount >= PROC_CD) {
					procCdCount -= PROC_CD;
					hbox.addStrategy(new DieFrag(state, hbox, p, NUM_FRAG));
					hbox.addStrategy(new DieSound(state, hbox, p, SoundEffect.WALL_HIT1, 0.75f).setSynced(false));
				}
			}
		}).setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf(PROC_CD),
				String.valueOf(NUM_FRAG),
				String.valueOf((int) BrittlingPowderActivate.BASE_DAMAGE)};
	}
}
