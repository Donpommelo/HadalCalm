package com.mygdx.hadal.equip.enemy;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;

public class TurretAttack extends RangedWeapon {

	private final static String name = "Turret Gun";
	private final static int clipSize = 1;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 0.75f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 25.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = .6f;
	private final static float projectileSpeed = 15.0f;
	private final static int projectileWidth = 192;
	private final static int projectileHeight = 24;
	private final static float lifespan = 1.50f;
	private final static float gravity = 1;
	
	private final static int projDura = 1;
	
	private final static int numProj = 3;
	private final static int spread = 30;

	private final static String weapSpriteId = "machinegun";
	private final static String projSpriteId = "bullet";
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			
			Vector2 center = new Vector2(startVelocity);
			
			for (int i = -numProj / 2; i <= numProj / 2; i++) {
				HitboxImage proj = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, 
						startVelocity.setAngle(center.angle() + i * spread),
						filter, true, world, camera, rays, user, projSpriteId);
				
				proj.setUserData(new HitboxData(state, world, proj) {
					
					@Override
					public void onHit(HadalData fixB) {
						if (fixB != null) {
							fixB.receiveDamage(baseDamage, this.hbox.getBody().getLinearVelocity().nor().scl(knockback), 
									user.getBodyData(), true, DamageTypes.RANGED);
						}
						super.onHit(fixB);
					}
				});		
			}
		}
		
	};
	
	public TurretAttack(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}

}
