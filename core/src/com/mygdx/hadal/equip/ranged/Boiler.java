package com.mygdx.hadal.equip.ranged;

import java.util.concurrent.ThreadLocalRandom;

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
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.HitboxFactory;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

import box2dLight.RayHandler;

public class Boiler extends RangedWeapon {

	private final static String name = "Boiler";
	private final static int clipSize = 40;
	private final static float shootCd = 0.1f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 2.0f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 18.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = .4f;
	private final static float projectileSpeed = 10.0f;
	private final static int projectileWidth = 70;
	private final static int projectileHeight = 70;
	private final static float lifespan = 0.8f;
	private final static float gravity = 0;
	private final static float restitution = 1.0f;
	
	private final static int projDura = 3;
	
	private final static int spread = 25;

	private final static String weapSpriteId = "default";
	private final static String projSpriteId = "orb_yellow";
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			
			float newDegrees = (float) (startVelocity.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));

			HitboxImage proj = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity.setAngle(newDegrees),
					filter, true, world, camera, rays, user, projSpriteId) {
				
				@Override
				public void create() {
					super.create();
					getBody().createFixture(FixtureBuilder.createFixtureDef(projectileWidth / 2, projectileHeight / 2, 
							new Vector2(0,  0), false, 0, 0, restitution,
						Constants.BIT_SENSOR, (short)(Constants.BIT_WALL), Constants.PLAYER_HITBOX));
				}
			};
			
			proj.setUserData(new HitboxData(state, world, proj) {
				
				@Override
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						fixB.receiveDamage(baseDamage, this.hbox.getBody().getLinearVelocity().nor().scl(knockback), 
								user.getBodyData(), true, DamageTypes.RANGED);
					}
				}
			});
		}
		
	};
	
	public Boiler(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}

}
