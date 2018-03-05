package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.TrackingHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;

public class TelekineticBlast extends RangedWeapon {

	private final static String name = "Telekinetic Blast";
	private final static int clipSize = 1;
	private final static float shootCd = 0.15f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.4f;
	private final static int reloadAmount = 0;

	private final static float recoil = 0.0f;

	private final static float projectileSpeedStart = 30.0f;
	private final static int projectileWidth = 120;
	private final static int projectileHeight = 120;
	private final static float lifespan = 4.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
		
	private final static String weapSpriteId = "default";
	private final static String projSpriteId = "orb_pink";

	private static final float maxLinearSpeed = 600;
	private static final float maxLinearAcceleration = 1200;
	private static final float maxAngularSpeed = 720;
	private static final float maxAngularAcceleration = 360;
	
	private static final int boundingRadius = 500;
	private static final int decelerationRadius = 0;

	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			
			new TrackingHitbox(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 1, startVelocity,
					filter, false, world, camera, rays, user, projSpriteId,
					maxLinearSpeed, maxLinearAcceleration, maxAngularSpeed, maxAngularAcceleration, boundingRadius, decelerationRadius);
					
		}
		
	};
	
	public TelekineticBlast(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeedStart, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}

}
