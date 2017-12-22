package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Hitbox;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;

public class ChargeBeam extends RangedWeapon {

	private final static String name = "Charge Beam";
	private final static int clipSize = 4;
	private final static float shootCd = 0.15f;
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
	private static final float maxCharge = 2.0f;
	private static final float chargeMag = 1.0f;
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public Hitbox makeHitbox(HadalEntity user, PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {			
			
			final float chargePow = 1 + chargeDura * chargeMag;
			
			Hitbox proj = new Hitbox(state, x, y, (int)(projectileWidth * chargePow), (int)(projectileHeight * chargePow), gravity, lifespan, projDura, 0, startVelocity.scl(chargePow),
					filter, true, world, camera, rays, user);
			
			proj.setUserData(new HitboxData(state, world, proj) {
				
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY)) {
							((BodyData) fixB).receiveDamage(baseDamage * chargePow, this.hbox.body.getLinearVelocity().nor().scl(knockback * chargePow));
						}
					}
					super.onHit(fixB);
				}
			});		
			
			return null;
		}
		
	};
	
	public ChargeBeam(HadalEntity user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot);
	}
	
	public void charge(float delta, PlayState state, BodyData shooter, short faction, int x, int y, World world, OrthographicCamera camera, RayHandler rays) {
		if (chargeDura < maxCharge) {
			chargeDura+=delta;
		}
	}
	
	public void release(PlayState state, BodyData bodyData, World world, OrthographicCamera camera, RayHandler rays) {
		super.execute(state, bodyData, world, camera, rays);
		chargeDura = 1;
	}
	
	public boolean charging() {
		return true;
	}

}
