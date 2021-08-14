package com.mygdx.hadal.equip.modeMods;

import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

/**
 * This "Artifact" is automatically applied to all characters for modes that have no ammo restriction
 * (gun game and football)
 * @author Humquat Hanek
 */
public class PlayerInvisible extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 0;

	public PlayerInvisible() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {

			private static final float invisInterval = 4.0f;
			private static final float semiInvisInterval = 1.0f;
			private float counter;
			private boolean invis;
			@Override
			public void timePassing(float delta) {
				if (counter <= 0.0f) {
					if (invis) {
						counter = semiInvisInterval;
						((PlayerBodyData) inflicted).getPlayer().setInvisible(1);
					} else {
						counter = invisInterval;
						((PlayerBodyData) inflicted).getPlayer().setInvisible(2);
					}
					invis = !invis;
				}
				counter -= delta;
			}
		};
		return enchantment;
	}
}
