package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;
import static com.mygdx.hadal.utils.Constants.PPM;

public class Boomerang extends RangedWeapon {

	private final static String name = "Boomerang";
	private final static int clipSize = 3;
	private final static float shootCd = 1.25f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.75f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 50.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 25.0f;
	private final static float projectileSpeed = 25.0f;
	private final static int projectileWidth = 90;
	private final static int projectileHeight = 85;
	private final static float lifespanx = 4.0f;
	private final static float gravity = 0;
	private final static float returnAmp = 1.5f;

	private final static int projDura = 1;
	
	private final static String weapSpriteId = "boomeranglauncher";
	private final static String projSpriteId = "boomerang";
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter,
				World world, OrthographicCamera camera,
				RayHandler rays) {
			
			HitboxImage proj = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespanx, projDura, 0, startVelocity,
					(short) 0, false, world, camera, rays, user, projSpriteId) {
				
				float controllerCount = 0;
				
				@Override
				public void create() {
					super.create();
					body.setAngularVelocity(5);
				}
				
				@Override
				public void controller(float delta) {
					controllerCount+=delta;
					if (controllerCount >= 1/60f) {
						Vector2 diff = new Vector2(user.getBody().getPosition().x * PPM - body.getPosition().x * PPM, 
								user.getBody().getPosition().y * PPM - body.getPosition().y * PPM);
						body.applyForceToCenter(diff.nor().scl(projectileSpeed * body.getMass() * returnAmp), true);
						setLifeSpan(getLifeSpan() - delta);
						if (getLifeSpan() <= 0) {
							queueDeletion();
						}
						controllerCount = 0;
					}
				}
			};
			
			proj.setUserData(new HitboxData(state, world, proj) {
				
				@Override
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						if (fixB instanceof PlayerBodyData) {
							if (hbox.getLifeSpan() < lifespanx - 0.25f) {
								if (((PlayerBodyData)fixB).getCurrentTool() instanceof Boomerang) {
									((Boomerang)((PlayerBodyData)fixB).getCurrentTool()).gainAmmo(1);
								}
								this.hbox.queueDeletion();
							}
						} else {
							fixB.receiveDamage(baseDamage, this.hbox.getBody().getLinearVelocity().nor().scl(knockback), 
									user.getBodyData(), true, DamageTypes.RANGED);
						}
					}
				}
			});		
		}
		
	};
	
	public Boomerang(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}

}
