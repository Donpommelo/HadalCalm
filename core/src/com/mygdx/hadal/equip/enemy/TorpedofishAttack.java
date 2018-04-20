package com.mygdx.hadal.equip.enemy;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

public class TorpedofishAttack extends RangedWeapon {

	private final static String name = "Torpedofish Torpedo";
	private final static int clipSize = 1;
	private final static float shootCd = 2.5f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 0.2f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 5.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 0.5f;
	private final static float projectileSpeed = 12.0f;
	private final static int projectileWidth = 45;
	private final static int projectileHeight = 45;
	private final static float lifespan = 5.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
	
	private final static int explosionRadius = 300;
	private final static float explosionDamage = 20.0f;
	private final static float explosionKnockback = 25.0f;
	
	private final static String spriteId = "orb_red";
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter) {
			
			HitboxImage proj = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
					filter, true, user, spriteId);
			
			
			proj.setUserData(new HitboxData(state, proj) {
				
				@Override
				public void onHit(HadalData fixB) {
					boolean explode = false;
					if (fixB != null) {
						fixB.receiveDamage(baseDamage, hbox.getBody().getLinearVelocity().nor().scl(knockback), 
								user.getBodyData(), true, DamageTypes.RANGED);
						if (fixB.getType().equals(UserDataTypes.BODY) || fixB.getType().equals(UserDataTypes.WALL)) {
							explode = true;
						}
						
					} else {
						explode = true;
					}
					if (explode && hbox.isAlive()) {
						WeaponUtils.explode(state, hbox.getBody().getPosition().x * PPM , hbox.getBody().getPosition().y * PPM, 
								user, explosionRadius, explosionDamage, explosionKnockback, (short)0);
						hbox.queueDeletion();
					}
					
				}
			});		
		}		
	};
	
	

	public TorpedofishAttack(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot);
	}

}
