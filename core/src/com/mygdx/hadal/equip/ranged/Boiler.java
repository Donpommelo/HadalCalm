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

public class Boiler extends RangedWeapon {

	private final static String name = "Boiler";
	private final static int clipSize = 40;
	private final static float shootCd = 0.1f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.8f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 18.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 2.0f;
	private final static float projectileSpeed = 10.0f;
	private final static int projectileWidth = 150;
	private final static int projectileHeight = 150;
	private final static float lifespan = 0.75f;
	private final static float gravity = 0;
	private final static float restitution = 0.5f;
	
	private final static int projDura = 3;
	
	private final static String weapSpriteId = "default";
	private final static String projSpriteId = "orb_yellow";
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter) {
			
			HitboxImage proj = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, restitution, startVelocity,
					filter, false, user, projSpriteId);
			
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
	
	public Boiler(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}
}
