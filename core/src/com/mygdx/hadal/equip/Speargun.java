package com.mygdx.hadal.equip;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Projectile;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.ProjectileData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.ProjectileFactory;

import box2dLight.RayHandler;

public class Speargun extends RangedWeapon {

	private final static String name = "";
	private final static int clipSize = 6;
	private final static int shootCd = 25;
	private final static float reloadTime = 8.0f;
	private final static float baseDamage = 25.0f;
	private final static float weaponSwitchTimeMod = 1.0f;
	private final static float projectileSpeed = 25.0f;
	private final static int projectileWidth = 10;
	private final static int projectileHeight = 10;
	
	private final static ProjectileFactory onShoot = new ProjectileFactory() {

		@Override
		public void makeProjectile(PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			Projectile proj = new Projectile(state, x, y, projectileWidth, projectileHeight, startVelocity, filter, world, camera, rays);
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
		
	};
	
	public Speargun() {
		super(name, clipSize, reloadTime, baseDamage, weaponSwitchTimeMod, projectileSpeed, shootCd, onShoot);
	}

}
