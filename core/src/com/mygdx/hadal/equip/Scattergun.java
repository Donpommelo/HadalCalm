package com.mygdx.hadal.equip;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Projectile;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.ProjectileData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.ProjectileFactory;

import box2dLight.RayHandler;

public class Scattergun extends RangedWeapon {

	private final static String name = "CR4P Cannon";
	private final static int clipSize = 2;
	private final static int shootCd = 15;
	private final static float reloadTime = 50.0f;
	private final static int reloadAmount = 2;
	private final static float baseDamage = 9.0f;
	private final static float recoil = 3.0f;
	private final static float weaponSwitchTimeMod = 1.0f;
	private final static float projectileSpeed = 20.0f;
	private final static int projectileWidth = 10;
	private final static int projectileHeight = 10;
	private final static int lifespan = 25;
	private final static float gravity = 0.5f;
	
	private final static int numProj = 10;
	private final static int spread = 10;
	
	private final static ProjectileFactory onShoot = new ProjectileFactory() {

		@Override
		public void makeProjectile(PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			
			for (int i = 0; i < numProj; i++) {
				
				float newDegrees = (float) (startVelocity.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
				
				Projectile proj = new Projectile(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, startVelocity.setAngle(newDegrees),
						filter, world, camera, rays);
				proj.setUserData(new ProjectileData(state, world, proj) {
					
					public void onHit(HadalData fixB) {
						if (fixB != null) {
							if (fixB.getType().equals(UserDataTypes.BODY)) {
								((BodyData) fixB).receiveDamage(baseDamage);
							}
						}
						super.onHit(fixB);
					}
				});		
			}
			
			
		}
		
	};
	
	public Scattergun(Schmuck user) {
		super(user, name, clipSize, reloadTime, baseDamage, recoil, weaponSwitchTimeMod, projectileSpeed, shootCd, reloadAmount, onShoot);
	}

}
