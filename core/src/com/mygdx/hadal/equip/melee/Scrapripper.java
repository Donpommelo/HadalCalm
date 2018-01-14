package com.mygdx.hadal.equip.melee;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.MeleeHitbox;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;

public class Scrapripper extends MeleeWeapon {

	private final static String name = "Scrap-Ripper";
	private final static float swingCd = 0.5f;
	private final static float windup = 0.2f;
	private final static float backSwing = 0.6f;
	private final static float baseDamage = 50.0f;
	private final static int hitboxSize = 180;
	private final static int swingArc = 100;
	private final static float knockback = 15.0f;
	private final static float momentum = 2.5f;
	
	private final static HitboxFactory onSwing = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startAngle, float x, float y, short filter, World world,
				OrthographicCamera camera, RayHandler rays) {
						
			MeleeHitbox hbox = new MeleeHitbox(state, x, y, hitboxSize, swingArc, swingCd, backSwing, startAngle, 
					startAngle.nor().scl(hitboxSize / 4 / PPM), filter, world, camera, rays, user);
			
			hbox.setUserData(new HitboxData(state, world, hbox) {
				
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY)) {
							((BodyData) fixB).receiveDamage(baseDamage, 
									this.hbox.getBody().getLinearVelocity().nor().scl(knockback), user.getBodyData(), DamageTypes.MELEE);
						}
					}
				}
				
			});
		}

	};
	
	public Scrapripper(Schmuck user) {
		super(user, name, swingCd, windup, momentum, onSwing);
	}

}
