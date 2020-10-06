package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.enemies.Scissorfish;
import com.mygdx.hadal.schmucks.bodies.enemies.Spittlefish;
import com.mygdx.hadal.schmucks.bodies.enemies.Torpedofish;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Summoned;
import com.mygdx.hadal.statuses.Temporary;

public class FishGang extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 25.0f;
	
	private static final int numFish = 5;
	private static final float fishLifespan = 20.0f;
	
	public FishGang(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byDamageInflict);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		for (int i = 0; i < numFish; i++) {
			
			if (Math.random() > 0.4f) {
				new Scissorfish(state, user.getPlayer().getPixelPosition(), 0.0f, user.getPlayer().getHitboxfilter(), null) {
					
					@Override
					public void create() {
						super.create();
						getBodyData().addStatus(new Temporary(state, fishLifespan, getBodyData(), getBodyData(), fishLifespan));
						getBodyData().addStatus(new Summoned(state, getBodyData(), user.getPlayer()));
					}
				};
				
			} else if (Math.random() > 0.7f){
				new Spittlefish(state, user.getPlayer().getPixelPosition(), 0.0f, user.getPlayer().getHitboxfilter(), null) {
					
					@Override
					public void create() {
						super.create();
						getBodyData().addStatus(new Temporary(state, fishLifespan, getBodyData(), getBodyData(), fishLifespan));
						getBodyData().addStatus(new Summoned(state, getBodyData(), user.getPlayer()));
					}
				};
			} else {
				new Torpedofish(state, user.getPlayer().getPixelPosition(), 0.0f, user.getPlayer().getHitboxfilter(), null) {
					
					@Override
					public void create() {
						super.create();
						getBodyData().addStatus(new Temporary(state, fishLifespan, getBodyData(), getBodyData(), fishLifespan));
						getBodyData().addStatus(new Summoned(state, getBodyData(), user.getPlayer()));
					}
				};
			}
		}
	}
}
