package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.HitboxImage;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;

public class BouncingBlade extends RangedWeapon {

	private final static String name = "Bouncing Blades";
	private final static int clipSize = 3;
	private final static float shootCd = 0.15f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.0f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 20.0f;
	private final static float recoil = 1.2f;
	private final static float knockback = 1.0f;
	private final static float projectileSpeed = 15.0f;
	private final static int projectileWidth = 40;
	private final static int projectileHeight = 40;
	private final static float lifespan = 3.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 3;
	
	private final static float restitution = 1.0f;
	
	private final static String weapSpriteId = "bladegun";
	private final static String projSpriteId = "orb_blue";
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			
			HitboxImage proj = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, restitution, startVelocity,
					filter, false, world, camera, rays, user, projSpriteId);
			
			proj.setUserData(new HitboxData(state, world, proj) {
				
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						fixB.receiveDamage(baseDamage, this.hbox.getBody().getLinearVelocity().nor().scl(knockback), 
								user.getBodyData(), true, DamageTypes.RANGED);
					}
				}
			});		
		}
		
	};
	
	public BouncingBlade(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}

}
