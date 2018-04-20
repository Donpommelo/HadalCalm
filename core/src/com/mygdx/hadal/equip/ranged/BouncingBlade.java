package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

public class BouncingBlade extends RangedWeapon {

	private final static String name = "Bouncing Blades";
	private final static int clipSize = 6;
	private final static float shootCd = 0.45f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.75f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 20.0f;
	private final static float recoil = 6.0f;
	private final static float knockback = 15.0f;
	private final static float projectileSpeed = 25.0f;
	private final static int projectileWidth = 75;
	private final static int projectileHeight = 75;
	private final static float lifespan = 3.5f;
	private final static float gravity = 0;
	
	private final static int projDura = 5;
	
	private final static float restitution = 1.0f;
	
	private final static String weapSpriteId = "bladegun";
	private final static String projSpriteId = "bouncing_blade";
	
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
						
						if (fixB.getType().equals(UserDataTypes.WALL)){
							hbox.setDura(hbox.getDura() - 1);
						}
					} else {
						hbox.setDura(hbox.getDura() - 1);
					}
					if (hbox.getDura() <= 0) {
						hbox.queueDeletion();
					}
					
					hbox.particle.onForBurst(0.25f);
				}
			});		
		}
		
	};
	
	public BouncingBlade(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId);
	}

}
