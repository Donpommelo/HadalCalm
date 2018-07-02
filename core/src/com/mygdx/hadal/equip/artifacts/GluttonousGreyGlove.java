package com.mygdx.hadal.equip.artifacts;

import java.util.Arrays;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class GluttonousGreyGlove extends Artifact {

	private final static String name = "Gluttonous Grey Glove";
	private final static String descr = "Heal on Kill. No longer heal from medpaks.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private static final float lifesteal = 0.025f;
	
	public GluttonousGreyGlove() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			@Override
			public void onKill(BodyData vic) {
				this.inflicted.regainHp(lifesteal * vic.getMaxHp(), this.inflicted, true, DamageTypes.LIFESTEAL);
			}
			
			@Override
			public float onHeal(float damage, BodyData perp, DamageTypes... tags) {
				if (Arrays.asList(tags).contains(DamageTypes.MEDPAK)) {
					return 0;
				}
				return damage;
			}
			
		};
		return enchantment;
	}
}
