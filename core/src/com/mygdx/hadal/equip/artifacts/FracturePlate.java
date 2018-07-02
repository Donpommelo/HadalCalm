package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class FracturePlate extends Artifact {

	private final static String name = "Fracture Plate";
	private final static String descr = "Blocks damage every 8 seconds";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private float procCdCount = 0;
	private float cd = 8.0f;
	
	public FracturePlate() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			@Override
			public void timePassing(float delta) {
				procCdCount -= delta;
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				if (damage > 0 && procCdCount <= 0) {
					procCdCount = cd;
					damage = 0;
				}
				return damage;
			}
			
		};
		return enchantment;
	}
}
