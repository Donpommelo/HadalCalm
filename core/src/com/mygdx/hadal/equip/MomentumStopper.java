package com.mygdx.hadal.equip;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.bodies.Hitbox;
import com.mygdx.hadal.schmucks.bodies.MeleeHitbox;
import com.mygdx.hadal.event.MomentumPickup;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;
import static com.mygdx.hadal.utils.Constants.PPM;

public class MomentumStopper extends MeleeWeapon {
	
	private final static String name = "Momentum Stopper";
	private final static float swingCd = 0.0f;
	private final static float windup = 0.0f;
	private final static float backSwing = 0.20f;
	private final static int hitboxSize = 800;
	private final static int swingArc = 800;
	private final static float momentum = 0.0f;
	
	private final static HitboxFactory onSwing = new HitboxFactory() {

		@Override
		public Hitbox makeHitbox(HadalEntity user, PlayState state, Vector2 startAngle, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {

			MeleeHitbox hbox = new MeleeHitbox(state, x, y, hitboxSize, swingArc, swingCd - backSwing, startAngle, 
					new Vector2(0, 0), filter, world, camera, rays, user);
			
			final World world2 = world;
			final OrthographicCamera camera2 = camera;
			final RayHandler rays2 = rays;
			
			hbox.setUserData(new HitboxData(state, world, hbox) {
								
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY) || fixB.getType().equals(UserDataTypes.HITBOX)) {
							
							Vector2 velo = new Vector2(fixB.getEntity().body.getLinearVelocity().x, fixB.getEntity().body.getLinearVelocity().y);
							new MomentumPickup(state, world2, camera2, rays2, 
									(int)(fixB.getEntity().body.getPosition().x * PPM), 
									(int)(fixB.getEntity().body.getPosition().y * PPM) + 32, 
									velo);
							fixB.getEntity().body.setLinearVelocity(new Vector2(0, 0));
						}
					}
				}
			});		
			
			return null;
		}
		
	};
	
	public MomentumStopper(HadalEntity user) {
		super(user, name, swingCd, windup, momentum, onSwing);
	}
}
