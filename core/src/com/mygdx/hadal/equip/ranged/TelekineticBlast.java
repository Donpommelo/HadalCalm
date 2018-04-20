package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.TrackingHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

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

	private static final float maxLinSpd = 600;
	private static final float maxLinAcc = 3000;
	private static final float maxAngSpd = 1080;
	private static final float maxAngAcc = 7200;
	
	private static final int boundingRad = 500;
	private static final int decelerationRadius = 0;
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter) {
			
			new TrackingHitbox(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 1, startVelocity,
					filter, false, user, projSpriteId) {
				
				{
					setTarget(state.getMouse());
					this.maxLinearSpeed = maxLinSpd;
					this.maxLinearAcceleration = maxLinAcc;
					this.maxAngularSpeed = maxAngSpd;
					this.maxAngularAcceleration = maxAngAcc;
					
					this.boundingRadius = boundingRad;
					this.decelerationRad = decelerationRadius;
					
					this.tagged = false;
					this.steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
				}
			};
					
		}
		
	};
	
	public TelekineticBlast(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeedStart, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}

}
