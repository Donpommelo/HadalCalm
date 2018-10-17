package com.mygdx.hadal.equip.artifacts;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class AbyssalInsignia extends Artifact {

	private final static String name = "Abyssal Insignia";
	private final static String descr = "Enemies Explode on Death.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final static int explosionRadius = 500;
	private final static float explosionDamage = 40.0f;
	private final static float explosionKnockback = 40.0f;
	
	public AbyssalInsignia() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			@Override
			public void onKill(BodyData vic) {
				WeaponUtils.createExplosion(state, 
						(int)(vic.getSchmuck().getBody().getPosition().x * PPM), 
						(int)(vic.getSchmuck().getBody().getPosition().y * PPM),  
						inflicter.getSchmuck(), inflicted.getCurrentTool(),
						explosionRadius, explosionDamage, explosionKnockback, inflicter.getSchmuck().getHitboxfilter());
			}
		};
		
		return enchantment;
	}
}
