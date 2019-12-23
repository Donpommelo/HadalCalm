package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class MaskofSympathy extends Artifact {

	private final static String name = "Mask of Sympathy";
	private final static String descr = "Reflects Damage.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final static float amount = 0.4f;
	
	public MaskofSympathy() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {

			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {

				if (!perp.equals(inflicted)) {
					perp.receiveDamage(damage * amount, new Vector2(0, 0), inflicted, inflicted.getCurrentTool(), false);
				}
				
				return damage;
			}
		};
		return enchantment;
	}
}
