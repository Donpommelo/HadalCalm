package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Hitbox;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;
import static com.mygdx.hadal.utils.Constants.PPM;

public class Boomerang extends RangedWeapon {

	private final static String name = "Boomerang";
	private final static int clipSize = 5;
	private final static float shootCd = 0.23f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.0f;
	private final static int reloadAmount = 5;
	private final static float baseDamage = 40.0f;
	private final static float recoil = 1.2f;
	private final static float knockback = 3.0f;
	private final static float projectileSpeed = 20.0f;
	private final static int projectileWidth = 30;
	private final static int projectileHeight = 30;
	private final static float lifespanx = 5.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public Hitbox makeHitbox(PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			
			Hitbox proj = new Hitbox(state, x, y, projectileWidth, projectileHeight, gravity, lifespanx, projDura, 0, startVelocity,
					filter, true, world, camera, rays) {
				
				public void controller(float delta) {
					super.controller(delta);
					Vector2 diff = new Vector2(state.getPlayer().getPosition().x * PPM - body.getPosition().x * PPM, 
							state.getPlayer().getPosition().y * PPM - body.getPosition().y * PPM);
					body.applyForceToCenter(diff.nor().scl(projectileSpeed * body.getMass()), true);
				}
			};
			
			proj.setUserData(new HitboxData(state, world, proj) {
				
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY)) {
							((BodyData) fixB).receiveDamage(baseDamage, this.hbox.body.getLinearVelocity().nor().scl(knockback));
						}
					}
					super.onHit(fixB);
				}
			});		
			
			return null;
		}
		
	};
	
	public Boomerang(HadalEntity user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot);
	}

}
