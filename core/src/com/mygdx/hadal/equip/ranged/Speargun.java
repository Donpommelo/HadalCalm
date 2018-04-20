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

public class Speargun extends RangedWeapon {

	private final static String name = "Harpoon Gun";
	private final static int clipSize = 6;
	private final static float shootCd = 0.25f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.2f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 50.0f;
	private final static float recoil = 7.5f;
	private final static float knockback = 12.5f;
	private final static float projectileSpeed = 22.5f;
	private final static int projectileWidth = 75;
	private final static int projectileHeight = 8;
	private final static float lifespan = 1.2f;
	private final static float gravity = 1;
	
	private final static int projDura = 1;
	
	private final static String weapSpriteId = "speargun";
	private final static String projSpriteId = "harpoon";
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter) {
			
			HitboxImage proj = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
					filter, true, user, projSpriteId);
			
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
	
	public Speargun(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}

}
