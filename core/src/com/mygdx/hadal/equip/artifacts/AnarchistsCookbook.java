package com.mygdx.hadal.equip.artifacts;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class AnarchistsCookbook extends Artifact {

	private final static String name = "Anarchist's Cookbook";
	private final static String descr = "Explosions!";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final static float baseDamage = 8.0f;
	private final static float knockback = 0.0f;
	private final static int projectileWidth = 40;
	private final static float lifespan = 3.0f;
	private final static float gravity = 1;
	private final static float restitution = 0.3f;
	
	private final static int projDura = 1;
		
	private final static int explosionRadius = 300;
	private final static float explosionDamage = 60.0f;
	private final static float explosionKnockback = 25.0f;
	
	public AnarchistsCookbook() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		
		enchantment[0] = new Status(state, name, descr, b) {
			
			private float procCdCount;
			private float procCd = .5f;
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					WeaponUtils.createGrenade(state,
							inflicted.getSchmuck().getBody().getPosition().x * PPM, 
							inflicted.getSchmuck().getBody().getPosition().y * PPM,
							inflicted.getSchmuck(), inflicted.getCurrentTool(), 
							baseDamage, knockback, projectileWidth, gravity, lifespan, restitution, projDura, 
							new Vector2(0, 0), false, explosionRadius, explosionDamage, explosionKnockback, inflicted.getSchmuck().getHitboxfilter());
				}
				procCdCount += delta;
			}
		};
		
		return enchantment;
	}
}
