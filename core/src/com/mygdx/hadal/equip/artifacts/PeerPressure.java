package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.enemies.KBKBuddy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.Summoned;

public class PeerPressure extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;
	
	private static final float procCd = 8.0f;
	
	public PeerPressure() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new Status(state, b) {

			private float procCdCount = procCd;
			
			private boolean summonActive = false;
			private KBKBuddy buddy;
			
			@Override
			public void timePassing(float delta) {
				
				if (!summonActive) {
					if (procCdCount < procCd) {
						procCdCount += delta;
					}
					
					if (procCdCount >= procCd) {
						procCdCount = 0;
						buddy = new KBKBuddy(state, inflicted.getSchmuck().getPixelPosition(), 0.0f, inflicted.getSchmuck().getHitboxfilter(), null) {
							
							@Override
							public void create() {
								super.create();
								getBodyData().addStatus(new Summoned(state, inflicted, (Player) (inflicted.getSchmuck())));
							}
						};
						buddy.setMoveTarget(inflicted.getSchmuck());
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
						buddy.getBodyData().die(buddy.getBodyData());
					}
				}
			}
			
			@Override
			public void onDeath(BodyData perp) {
				if (buddy != null) {
					if (buddy.isAlive()) {
						buddy.getBodyData().die(buddy.getBodyData());
					}
				}
			}
		};
		
		return enchantment;
	}
}
