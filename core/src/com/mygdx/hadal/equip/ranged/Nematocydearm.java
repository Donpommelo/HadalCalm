package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;
import static com.mygdx.hadal.utils.Constants.PPM;

public class Nematocydearm extends RangedWeapon {

	private final static String name = "Nematocydearm";
	private final static int clipSize = 3;
	private final static float shootCd = 0.45f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.0f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 30.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 12.5f;
	private final static float projectileSpeed = 25.0f;
	private final static int projectileWidth = 40;
	private final static int projectileHeight = 40;
	private final static float lifespan = 3.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
		
	private final static int poisonRadius = 200;
	private final static float poisonDamage = 40/60f;
	private final static float poisonDuration = 4.0f;

	private final static String weapSpriteId = "default";
	private final static String projSpriteId = "debris_c";
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter,
				final World world, final OrthographicCamera camera, final RayHandler rays) {
			
			HitboxImage proj = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
					filter, true, world, camera, rays, user, projSpriteId) {
				
				@Override
				public void controller(float delta) {
					if (lifeSpan <= 0) {
						new Poison(state, world, camera, rays, poisonRadius, poisonRadius,
								(int)(body.getPosition().x * PPM), (int)(body.getPosition().y * PPM), poisonDamage, poisonDuration, user, true);
					}
					super.controller(delta);
				}
			};
			
			proj.setUserData(new HitboxData(state, world, proj) {
				
				@Override
				public void onHit(HadalData fixB) {
					boolean explode = false;
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY) || fixB.getType().equals(UserDataTypes.WALL)) {
							explode = true;
						}
						fixB.receiveDamage(baseDamage, this.hbox.getBody().getLinearVelocity().nor().scl(knockback), 
								user.getBodyData(), true, DamageTypes.RANGED);
					} else {
						explode = true;
					}
					if (explode) {
						new Poison(state, world, camera, rays, poisonRadius, poisonRadius,
								(int)(this.hbox.getBody().getPosition().x * PPM), 
								(int)(this.hbox.getPosition().y * PPM), poisonDamage, poisonDuration, user, true);
						hbox.queueDeletion();
					}
					
				}
			});		
		}
	};
	
	public Nematocydearm(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}
}
