package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.strategies.ContactUnitDie;
import com.mygdx.hadal.schmucks.strategies.ContactWallDie;
import com.mygdx.hadal.schmucks.strategies.DieFrag;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class BrittlingPowder extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private final static int numFrag = 8;
	
	private final static float procCd = 0.5f;
	
	public BrittlingPowder() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {

			private float procCdCount;
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}
			
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					hbox.addStrategy(new ContactWallDie(state, hbox, inflicted));
					hbox.addStrategy(new ContactUnitDie(state, hbox, inflicted));
					hbox.addStrategy(new DieFrag(state, hbox, inflicted, numFrag));
				}
			}
		});
		
		return enchantment;
	}
}
