package com.mygdx.hadal.equip.artifacts;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class AbyssalInsignia extends Artifact {

	private final static String name = "Abyssal Insignia";
	private final static String descr = "Release Vengeful Spirit Upon Killing or Dying.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final static float spiritLifespan = 6.0f;
	private final static float spiritDamage = 25.0f;
	private final static float spiritKnockback = 8.0f;
	
	public AbyssalInsignia() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			@Override
			public void onKill(BodyData vic) {
				WeaponUtils.releaseVengefulSpirits(state, spiritLifespan, spiritDamage, spiritKnockback, 
						new Vector2((vic.getSchmuck().getPosition().x * PPM), (vic.getSchmuck().getPosition().y * PPM)), 
						inflicted, inflicted.getSchmuck().getHitboxfilter());
			}
			
			@Override
			public void onDeath(BodyData perp) {
				WeaponUtils.releaseVengefulSpirits(state, spiritLifespan, spiritDamage, spiritKnockback, new Vector2(
						(inflicted.getSchmuck().getPosition().x * PPM), 
						(inflicted.getSchmuck().getPosition().y * PPM)), inflicted, inflicted.getSchmuck().getHitboxfilter());
			}
		};
		
		return enchantment;
	}
}
