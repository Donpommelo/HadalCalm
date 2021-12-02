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

	private static final int slotCost = 0;

	public PlayerInvisible() { super(slotCost); }

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private static final float invisInterval = 4.0f;
			private static final float semiInvisInterval = 1.0f;
			private float counter;
			private boolean invis;
			@Override
			public void timePassing(float delta) {
				if (counter <= 0.0f) {
					if (invis) {
						counter = semiInvisInterval;
						p.getPlayer().setInvisible(1);
					} else {
						counter = invisInterval;
						p.getPlayer().setInvisible(2);
					}
					invis = !invis;
				}
				counter -= delta;
			}
		};
	}
}
