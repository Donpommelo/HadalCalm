package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.enemies.Scissorfish;
import com.mygdx.hadal.schmucks.entities.enemies.Spittlefish;
import com.mygdx.hadal.schmucks.entities.enemies.Torpedofish;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Summoned;
import com.mygdx.hadal.statuses.Temporary;

/**
 * @author Locliff Lifinder
 */
public class FishGang extends ActiveItem {

	private static final float MAX_CHARGE = 32.0f;
	
	private static final int NUM_FISH = 5;
	private static final float FISH_LIFESPAN = 20.0f;
	
	public FishGang(Schmuck user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		for (int i = 0; i < NUM_FISH; i++) {
			float randFloat = MathUtils.random();
			if (randFloat > 0.4f) {
				new Scissorfish(state, user.getPlayer().getPixelPosition(), 0.0f, user.getPlayer().getHitboxFilter()) {
					
					@Override
					public void create() {
						super.create();
						getBodyData().addStatus(new Temporary(state, FISH_LIFESPAN, getBodyData(), getBodyData(), FISH_LIFESPAN));
						getBodyData().addStatus(new Summoned(state, getBodyData(), user.getPlayer()));
					}
				};
				
			} else if (randFloat > 0.7f) {
				new Spittlefish(state, user.getPlayer().getPixelPosition(), 0.0f, user.getPlayer().getHitboxFilter()) {
					
					@Override
					public void create() {
						super.create();
						getBodyData().addStatus(new Temporary(state, FISH_LIFESPAN, getBodyData(), getBodyData(), FISH_LIFESPAN));
						getBodyData().addStatus(new Summoned(state, getBodyData(), user.getPlayer()));
					}
				};
			} else {
				new Torpedofish(state, user.getPlayer().getPixelPosition(), 0.0f, user.getPlayer().getHitboxFilter()) {
					
					@Override
					public void create() {
						super.create();
						getBodyData().addStatus(new Temporary(state, FISH_LIFESPAN, getBodyData(), getBodyData(), FISH_LIFESPAN));
						getBodyData().addStatus(new Summoned(state, getBodyData(), user.getPlayer()));
					}
				};
			}
		}
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf(NUM_FISH)};
	}
}
