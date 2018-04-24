package com.mygdx.hadal.equip.artifacts;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxOnDieExplodeStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxPoisonTrailStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class RingofTesting extends Artifact {

	private final static String name = "Ring of Testing";
	private final static String descr = "Tests Things";
	private final static String descrLong = "";
	private final static int statusNum = 3;
	
	
	private static final float maxLinSpd = 100;
	private static final float maxLinAcc = 1000;
	private static final float maxAngSpd = 180;
	private static final float maxAngAcc = 90;
	
	private static final int boundingRad = 500;
	private static final int decelerationRadius = 0;
	private final static float homeRadius = 10;
	
	public RingofTesting() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new Status(state, 0, name, true, true, false, false , b, b, 50) {
			
			@Override
			public void onShoot(Equipable tool) {
				if (tool instanceof RangedWeapon) {
					
					Vector2 newVelo = new Vector2(tool.getWeaponVelo());
					
					((RangedWeapon)tool).getOnShoot().makeHitbox(vic.getSchmuck(), state, tool, 
							newVelo.setAngle(newVelo.angle() + 20),
							vic.getSchmuck().getBody().getPosition().x * PPM, 
							vic.getSchmuck().getBody().getPosition().y * PPM, 
							vic.getSchmuck().getHitboxfilter());
					
					((RangedWeapon)tool).getOnShoot().makeHitbox(vic.getSchmuck(), state, tool, 
							newVelo.setAngle(newVelo.angle() - 40),
							vic.getSchmuck().getBody().getPosition().x * PPM, 
							vic.getSchmuck().getBody().getPosition().y * PPM, 
							vic.getSchmuck().getHitboxfilter());
				}
			}
			
		};
		
		enchantment[1] = new Status(state, 0, name, true, true, false, false , b, b, 50) {
			
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				hbox.addStrategy(new HitboxPoisonTrailStrategy(state, hbox, b, 50, 20 / 60f, 1.0f, b.getSchmuck().getHitboxfilter()));
				hbox.addStrategy(new HitboxOnDieExplodeStrategy(state, hbox, b, null, 200, 25, 16.0f, vic.getSchmuck().getHitboxfilter()));
			}
			
		};
		
		enchantment[2] = new Status(state, 0, name, true, true, false, false , b, b, 50) {
			
			@Override
			public void onReload(Equipable tool) {
				
				WeaponUtils.createBees(state, 
						vic.getSchmuck().getBody().getPosition().x * PPM, 
						vic.getSchmuck().getBody().getPosition().y * PPM, 
						vic.getSchmuck(), tool, 14, 5.0f, 23, 21, 4.0f,
						5, 180, new Vector2(1, 1), false,
						maxLinSpd, maxLinAcc, maxAngSpd, maxAngAcc, boundingRad, decelerationRadius, homeRadius, vic.getSchmuck().getHitboxfilter());
			}
		};
		
		return enchantment;
	}
}
