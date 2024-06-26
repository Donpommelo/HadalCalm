package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.schmucks.entities.enemies.KBKBuddy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.Summoned;

public class PeerPressure extends Artifact {

	private static final int slotCost = 2;
	
	private static final float procCd = 11.0f;
	
	public PeerPressure() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private float procCdCount = procCd;
			private boolean summonActive;
			private KBKBuddy buddy;
			@Override
			public void timePassing(float delta) {
				
				if (!summonActive) {
					if (procCdCount < procCd) {
						procCdCount += delta;
					}
					
					if (procCdCount >= procCd) {
						procCdCount = 0;
						buddy = new KBKBuddy(state, p.getSchmuck().getPixelPosition(), 0.0f, p.getSchmuck().getHitboxFilter()) {
							
							@Override
							public void create() {
								super.create();
								getBodyData().addStatus(new Summoned(state, p, p.getPlayer()));
							}
						};
						buddy.setMoveTarget(p.getSchmuck());
						summonActive = true;
					} 
				}
				
				if (buddy == null) {
					summonActive = false;
				} else if (!buddy.isAlive()) {
					summonActive = false;
				}
			}
			
			@Override
			public void onRemove() {
				if (buddy != null) {
					if (buddy.isAlive()) {
						buddy.getBodyData().die(buddy.getBodyData(), DamageSource.MISC);
					}
				}
			}
			
			@Override
			public void onDeath(BodyData perp, DamageSource source, DamageTag... tags) {
				if (buddy != null) {
					if (buddy.isAlive()) {
						buddy.getBodyData().die(buddy.getBodyData(), DamageSource.MISC);
					}
				}
			}
		}.setServerOnly(true);
	}
}
