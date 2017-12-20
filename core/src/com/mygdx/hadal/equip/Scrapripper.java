package com.mygdx.hadal.equip;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Hitbox;
import com.mygdx.hadal.schmucks.bodies.MeleeHitbox;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;

public class Scrapripper extends MeleeWeapon {

	private final static String name = "Scrap-Ripper";
	private final static int swingCd = 35;
	private final static int windup = 12;
	private final static int backSwing = 10;
	private final static float baseDamage = 40.0f;
	private final static int hitboxSize = 240;
	private final static int swingArc = 120;
	private final static float knockback = 50.0f;
	private final static float momentum = 5.0f;
	
	
	private final static HitboxFactory onSwing = new HitboxFactory() {

		@Override
		public Hitbox makeHitbox(PlayState state, Vector2 startAngle, float x, float y, short filter, World world,
				OrthographicCamera camera, RayHandler rays) {
			
			MeleeHitbox hbox = new MeleeHitbox(state, x, y, hitboxSize, swingArc, swingCd - backSwing, startAngle,
					filter, world, camera, rays);
			
			hbox.setUserData(new HitboxData(state, world, hbox) {
				
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY)) {
							((BodyData) fixB).receiveDamage(baseDamage, this.hbox.body.getLinearVelocity().nor().scl(knockback));
						}
					}
				}
				
			});

			return hbox;
			
		}

		
	};
	
	public Scrapripper(HadalEntity user) {
		super(user, name, swingCd, windup, momentum, onSwing);
	}

}
