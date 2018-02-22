package com.mygdx.hadal.equip.misc;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.MeleeHitbox;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;

public class MomentumStopper extends MeleeWeapon {
	
	private final static String name = "Momentum Stopper";
	private final static float swingCd = 1.0f;
	private final static float windup = 0.0f;
	private final static float backSwing = 0.5f;
	private final static int hitboxSize = 800;
	private final static int swingArc = 800;
	private final static float momentum = 0.0f;
	
	private final static HitboxFactory onSwing = new HitboxFactory() {

		@Override
		public void makeHitbox(Schmuck user, PlayState state, Vector2 startAngle, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {

			MeleeHitbox hbox = new MeleeHitbox(state, x, y, hitboxSize, swingArc, swingCd, backSwing, startAngle, 
					new Vector2(0, 0), (short) 0, world, camera, rays, user);
			
			final Schmuck user2 = user;
			
			hbox.setUserData(new HitboxData(state, world, hbox) {
						
				@Override
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY) || fixB.getType().equals(UserDataTypes.HITBOX)) {
							
							Vector2 velo = new Vector2(
									fixB.getEntity().getBody().getLinearVelocity().x, 
									fixB.getEntity().getBody().getLinearVelocity().y);
							((Player) user2).getMomentums().addLast(velo);
							fixB.getEntity().getBody().setLinearVelocity(new Vector2(0, 0));
						}
					}
				}
			});		
		}
		
	};
	
	public MomentumStopper(Schmuck user) {
		super(user, name, swingCd, windup, momentum, onSwing);
	}
}
