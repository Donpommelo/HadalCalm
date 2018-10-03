package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.MeleeHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallParticles;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class CrownofThorns extends Artifact {

	private final static String name = "Crown of Thorns";
	private final static String descr = "Damages nearby enemies when reloading.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final static float swingCd = 1.5f;
	private final static float backSwing = 1.0f;
	private final static float baseDamage = 45.0f;
	private final static int hitboxSize = 400;
	private final static int swingArc = 400;
	private final static float knockback = 40.0f;
	
	public CrownofThorns() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			@Override
			public void onReload(Equipable tool) {
				Hitbox hbox = new MeleeHitbox(state, 0, 0, hitboxSize, swingArc, swingCd, backSwing, new Vector2(0, 0), 
						new Vector2(0, 0), true, inflicted.getSchmuck().getHitboxfilter(), inflicted.getSchmuck());
				
				hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, inflicted));
				hbox.addStrategy(new HitboxOnContactWallParticles(state, hbox, inflicted, "SPARK_TRAIL"));
				hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, inflicted, inflicted.getCurrentTool(),
						baseDamage, knockback, DamageTypes.MELEE));
			}
		};
		return enchantment;
	}
}
