package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

public class Iceberg extends RangedWeapon {

	private final static String name = "Iceberg";
	private final static int clipSize = 1;
	private final static float shootCd = 0.25f;
	private final static float shootDelay = 0.15f;
	private final static float reloadTime = 1.0f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 60.0f;
	private final static float recoil = 15.0f;
	private final static float knockback = 30.0f;
	private final static float projectileSpeed = 15.0f;
	private final static int projectileWidth = 132;
	private final static int projectileHeight = 130;
	private final static float lifespan = 3.5f;
	private final static float gravity = 10;
	
	private final static int projDura = 5;
	
	private final static float restitution = 0.0f;

	private final static String weapSpriteId = "default";
	private final static String projSpriteId = "orb_blue";
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter) {
			
			HitboxImage proj = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, restitution, 
					startVelocity, filter, false, user, projSpriteId) {
				
				float controllerCount = 0;
				float lastX = 0;
				
				@Override
				public void create() {
					super.create();
					body.setTransform(body.getPosition(), 0);
				}
				
				@Override
				public void controller(float delta) {
					lifeSpan -= delta;
					if (lifeSpan <= 0) {
						queueDeletion();
					}
					controllerCount+=delta;
					if (controllerCount >= 1/60f) {
						
						if (body.getLinearVelocity().x == 0) {
							body.setLinearVelocity(-lastX, body.getLinearVelocity().y);
						}
						
						lastX = body.getLinearVelocity().x;
					}
				}
			};
			
			proj.setFriction(0);
			
			proj.setUserData(new HitboxData(state, proj) {
				
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
	
	public Iceberg(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}

}
