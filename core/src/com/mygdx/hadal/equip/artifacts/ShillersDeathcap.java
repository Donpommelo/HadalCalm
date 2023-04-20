package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.hitbox.PoisonTrail;

public class ShillersDeathcap extends Artifact {

	private static final int SLOT_COST = 2;

	private static final int POISON_RADIUS = 100;
	private static final Vector2 POISON_SIZE = new Vector2(100, 100);
	private static final float POISON_DAMAGE = 15 / 60f;
	private static final float POISON_DURATION = 1.0f;
	private static final float PROC_CD = 1.0f;
	
	public ShillersDeathcap() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
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
					hbox.addStrategy(new PoisonTrail(state, hbox, p, POISON_SIZE, POISON_RADIUS, POISON_DAMAGE, POISON_DURATION,
							p.getSchmuck().getHitboxFilter()));
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) PROC_CD),
				String.valueOf((int) (POISON_DAMAGE * 60))};
	}
}
