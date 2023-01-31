package com.mygdx.hadal.equip.modeMods;

import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

/**
 * This "Artifact" is automatically applied to all characters for modes that have no ammo restriction
 * (gun game and football)
 * @author Humquat Hanek
 */
public class PlayerInvisible extends Artifact {

	private static final int SLOT_COST = 0;
	private static final float INVIS_INTERVAL = 4.0f;
	private static final float SEMI_INVIS_INTERVAL = 1.0f;

	public PlayerInvisible() { super(SLOT_COST); }

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private float counter;
			private boolean invis;
			@Override
			public void timePassing(float delta) {
				if (counter <= 0.0f) {
					if (invis) {
						counter = SEMI_INVIS_INTERVAL;
						p.getPlayer().getEffectHelper().setTranslucent(true);
						p.getPlayer().getEffectHelper().setInvisible(false);
					} else {
						counter = INVIS_INTERVAL;
						p.getPlayer().getEffectHelper().setTranslucent(false);
						p.getPlayer().getEffectHelper().setInvisible(true);
					}
					invis = !invis;
				}
				counter -= delta;
			}
		};
	}
}
