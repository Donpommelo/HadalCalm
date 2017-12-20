package com.mygdx.hadal.equip;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Hitbox;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;

public class IronBall extends RangedWeapon {

	private final static String name = "Iron Ball Launcher";
	private final static int clipSize = 1;
	private final static int shootCd = 15;
	private final static int shootDelay = 10;
	private final static float reloadTime = 45.0f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 100.0f;
	private final static float recoil = 3.0f;
	private final static float knockback = 15.0f;
	private final static float projectileSpeed = 30.0f;
	private final static int projectileWidth = 25;
	private final static int projectileHeight = 25;
	private final static int lifespan = 70;
	private final static float gravity = 10;
	
	private final static int projDura = 5;
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public Hitbox makeHitbox(PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			
			Hitbox proj = new Hitbox(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, startVelocity,
					filter, false, world, camera, rays);
			
			proj.setUserData(new HitboxData(state, world, proj) {
				
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY)) {
							((BodyData) fixB).receiveDamage(baseDamage, this.hbox.body.getLinearVelocity().nor().scl(knockback));
						}
					}
				}
			});		
			
			return null;
		}
		
	};
	
	public IronBall(HadalEntity user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot);
	}

}
