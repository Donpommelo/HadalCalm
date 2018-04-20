package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

import static com.mygdx.hadal.utils.Constants.PPM;

public class Stormcaller extends RangedWeapon {

	private final static String name = "Stormcaller";
	private final static int clipSize = 1;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.2f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 0.0f;
	private final static float recoil = 18.5f;
	private final static float knockback = 0.0f;
	private final static float projectileSpeed = 10.0f;
	private final static int projectileWidth = 20;
	private final static int projectileHeight = 20;
	private final static float lifespan = 1.5f;
	private final static float gravity = 0;
	
	private final static int projDura = 10;
	
	private final static String weapSpriteId = "default";
	private final static String projSpriteId = "orb_yellow";
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter) {
			
			HitboxImage proj = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
					filter, false,  user, projSpriteId) {
				
				float damage = 1;
				float controllerCount = 0;
				
				@Override
				public void controller(float delta) {
					controllerCount+=delta;
					if (controllerCount >= 1/60f) {
						width += 2;
						height += 2;
						
						damage += 0.15f;
						
						WeaponUtils.explode(state, this.body.getPosition().x * PPM , this.body.getPosition().y * PPM, 
								user, (int) width, damage, 0.0f, filter);	
					}
					super.controller(delta);
				}
			};
			
			proj.setUserData(new HitboxData(state, proj) {
				
				@Override
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						fixB.receiveDamage(baseDamage, this.hbox.getBody().getLinearVelocity().nor().scl(knockback), 
								user.getBodyData(), true, DamageTypes.RANGED);
					}
					super.onHit(fixB);
				}
			});		
		}
	};
	
	public Stormcaller(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}

}
