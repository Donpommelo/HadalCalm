package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.statuses.Status;

public class HornsofAmmon extends Artifact {

	private final static String name = "Horns of Ammon";
	private final static String descr = "Brief Invulnerability on damage.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final static float threshold = 5.0f;
	private final static float invulnDura = 1.0f;
	
	public HornsofAmmon() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {

			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				
				if (damage > threshold) {
					if (inflicted.getStatus(Invulnerability.class) == null) {
						inflicted.receiveDamage(damage, new Vector2(0, 0), perp, inflicted.getCurrentTool(), false, tags);
						inflicted.addStatus(new Invulnerability(state, invulnDura, inflicted, inflicted));
						return 0;
					}					
				}
				
				return damage;
			}
		};
		return enchantment;
	}
}
