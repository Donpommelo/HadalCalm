package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class GomezsAmygdala extends Artifact {

	private final static String name = "Gomez's Amygdala";
	private final static String descr = "Temporarily boosts speed and damage when taking damage.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final float dura = 2.0f;
	
	public GomezsAmygdala() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			private float procCdCount;
			private float procCd = 1.0f;
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;

				}
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					inflicted.addStatus(new StatusComposite(state, "Self-Preservatory", descr, perp,
							new StatChangeStatus(state, dura, 4, 0.50f, perp, inflicted),
							new StatChangeStatus(state, dura, 5, 0.50f, perp, inflicted),
							new StatChangeStatus(state, dura, 21, 0.25f, perp, inflicted),
							new StatChangeStatus(state, dura, 23, 0.25f, perp, inflicted)							
							));

				}
				return damage;
			}
		};
		return enchantment;
	}
}
