package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Hitbox;
import com.mygdx.hadal.schmucks.bodies.HitboxImage;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;

public class ChargeBeam extends RangedWeapon {

	private final static String name = "Charge Beam";
	private final static int clipSize = 4;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.0f;
	private final static int reloadAmount = 4;
	private final static float baseDamage = 8.0f;
	private final static float recoil = 1.5f;
	private final static float knockback = 1.0f;
	private final static float projectileSpeed = 6.0f;
	private final static int projectileWidth = 15;
	private final static int projectileHeight = 15;
	private final static float lifespan = 3.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 5;
	
	private static float chargeDura = 0.0f;
	private static int chargeStage = 0;
	private static final float maxCharge = 1.0f;
	
	private final static String spriteId = "orb_yellow";

	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public Hitbox makeHitbox(Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {			
			
			if (chargeDura >= maxCharge) {
				chargeStage = 3;
			} else if (chargeDura >= 0.8f) {
				chargeStage = 2;
			} else if (chargeDura >= 0.4f) {
				chargeStage = 1;
			} else {
				chargeStage = 0;
			}
			
			float sizeMultiplier = 1;
			float speedMultiplier = 1;
			float damageMultiplier = 1;
			float kbMultiplier = 1;

			switch(chargeStage) {
			case 3:
				sizeMultiplier = 8.0f;
				speedMultiplier = 5.0f;
				damageMultiplier = 10.0f;
				kbMultiplier = 12.0f;
				break;
			case 2:
				sizeMultiplier = 4.0f;
				speedMultiplier = 4.0f;
				damageMultiplier = 6.0f;
				kbMultiplier = 7.5f;
				break;
			case 1:
				sizeMultiplier = 2.0f;
				speedMultiplier = 3.0f;
				damageMultiplier = 2.0f;
				kbMultiplier = 2.5f;
				break;
			}
			
			final float damageMultiplier2 = damageMultiplier;
			final float kbMultiplier2 = kbMultiplier;
			
			HitboxImage proj = new HitboxImage(state, x, y, (int)(projectileWidth * sizeMultiplier), (int)(projectileHeight * sizeMultiplier), gravity, lifespan, projDura, 0, startVelocity.scl(speedMultiplier),
					filter, true, world, camera, rays, user, spriteId);
			
			proj.setUserData(new HitboxData(state, world, proj) {
				
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY)) {
							((BodyData) fixB).receiveDamage(baseDamage * damageMultiplier2, this.hbox.getBody().getLinearVelocity().nor().scl(knockback * kbMultiplier2));
						}
					}
					if (chargeStage != 3) {
						super.onHit(fixB);
					}
				}
			});		
			
			return null;
		}
		
	};
	
	public ChargeBeam(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot);
	}
	
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, int x, int y, World world, OrthographicCamera camera, RayHandler rays) {
		if (chargeDura < maxCharge) {
			chargeDura+=delta;
		}
		super.mouseClicked(delta, state, shooter, faction, x, y, world, camera, rays);
	}
	
	public void execute(PlayState state, BodyData shooter, World world, OrthographicCamera camera, RayHandler rays) {

	}
	
	public void release(PlayState state, BodyData bodyData, World world, OrthographicCamera camera, RayHandler rays) {
		super.execute(state, bodyData, world, camera, rays);
		chargeDura = 0;
	}
	
	@Override
	public String getText() {
		if (reloading) {
			return name + ": " + clipLeft + "/" + clipSize + " CHARGE: " + Math.round(chargeDura * 100 / maxCharge) + "% RELOADING";
		} else {
			return name + ": " + clipLeft + "/" + clipSize + " CHARGE: " + Math.round(chargeDura * 100 / maxCharge) + "%";

		}
	}
}
