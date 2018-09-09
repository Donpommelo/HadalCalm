package com.mygdx.hadal.equip.artifacts;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class ForagersHive extends Artifact {

	private final static String name = "Forager's Hive";
	private final static String descr = "Release bees when damaged.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final static int numBees = 6;
	
	public ForagersHive() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			private float procCdCount;
			private float procCd = .5f;
			
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
					
					WeaponUtils.createBees(state, 
							inflicted.getSchmuck().getBody().getPosition().x * PPM, 
							inflicted.getSchmuck().getBody().getPosition().y * PPM, 
							inflicted.getSchmuck(), inflicted.getCurrentTool(), numBees, 180, 
							new Vector2(1, 1), false, inflicted.getSchmuck().getHitboxfilter());
				}
				return damage;
			}
		};
		return enchantment;
	}
}
