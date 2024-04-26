package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.schmucks.entities.enemies.Scissorfish;
import com.mygdx.hadal.schmucks.entities.enemies.Spittlefish;
import com.mygdx.hadal.schmucks.entities.enemies.Torpedofish;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.Summoned;

public class SummoningTwofish extends Artifact {

	private static final int SLOT_COST = 1;

	private static final int NUM_FISH = 2;

	public SummoningTwofish() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private boolean created;
			@Override
			public void timePassing(float delta) {
				if (!created) {
					created = true;

					for (int i = 0; i < NUM_FISH; i++) {
						float randFloat = MathUtils.random();
						if (randFloat < 0.34f) {
							new Scissorfish(state, p.getPlayer().getPixelPosition(), 0.0f, p.getPlayer().getHitboxFilter()) {

								@Override
								public void create() {
									super.create();
									getBodyData().addStatus(new Summoned(state, getBodyData(), p.getPlayer()));
								}
							};

						} else if (randFloat < 0.68f) {
							new Spittlefish(state, p.getPlayer().getPixelPosition(), 0.0f, p.getPlayer().getHitboxFilter()) {

								@Override
								public void create() {
									super.create();
									getBodyData().addStatus(new Summoned(state, getBodyData(), p.getPlayer()));
								}
							};
						} else {
							new Torpedofish(state, p.getPlayer().getPixelPosition(), 0.0f, p.getPlayer().getHitboxFilter()) {

								@Override
								public void create() {
									super.create();
									getBodyData().addStatus(new Summoned(state, getBodyData(), p.getPlayer()));
								}
							};
						}
					}
				}
			}
		}.setServerOnly(true);
	}
}
