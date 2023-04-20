package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.general.ExplodingReticle;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.HitboxStrategy;

public class VestigialChamber extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final float PROC_CD = 1.5f;
	
	private static final float RETICLE_LIFESPAN = ExplodingReticle.RETICLE_LIFESPAN;
	private static final float EXPLOSION_DAMAGE = ExplodingReticle.EXPLOSION_DAMAGE;

	public VestigialChamber() {
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
					hbox.addStrategy(new HitboxStrategy(state, hbox, p) {
						
						@Override
						public void die() {
							SyncedAttack.EXPLODING_RETICLE.initiateSyncedAttackSingle(state, inflicted.getSchmuck(),
									hbox.getPixelPosition(), new Vector2());
						}
					});
				}
			}
		}.setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf(RETICLE_LIFESPAN),
				String.valueOf((int) EXPLOSION_DAMAGE)};
	}
}
