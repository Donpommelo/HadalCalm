package com.mygdx.hadal.equip.actives;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.enemies.Scissorfish;
import com.mygdx.hadal.schmucks.bodies.enemies.Spittlefish;
import com.mygdx.hadal.schmucks.bodies.enemies.Torpedofish;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class FishGang extends ActiveItem {

	private final static String name = "Fish Gang";
	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 20.0f;
	
	private final static int numFish = 5;
	private final static boolean spread = true;
	
	public FishGang(Schmuck user) {
		super(user, name, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		for (int i = 0; i < numFish; i++) {
			int randX = (int) (user.getPlayer().getBody().getPosition().x * PPM + (spread ? (int)( (Math.random() - 0.5) * 100) : 0));
			int randY = (int) (user.getPlayer().getBody().getPosition().y * PPM + (spread ? (int)( (Math.random() - 0.5) * 100) : 0));
			if (Math.random() > 0.4f) {
				new Scissorfish(state, randX, randY, user.getPlayer().getHitboxfilter());
			} else if (Math.random() > 0.7f){
				new Spittlefish(state, randX, randY, user.getPlayer().getHitboxfilter());
			} else {
				new Torpedofish(state, randX, randY, user.getPlayer().getHitboxfilter());
			}
		}
	}
}
