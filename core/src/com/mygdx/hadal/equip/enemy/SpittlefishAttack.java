package com.mygdx.hadal.equip.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxAnimated;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

public class SpittlefishAttack extends RangedWeapon {

	private final static String name = "Spittlefish Spittle";
	private final static int clipSize = 6;
	private final static float shootCd = 1.0f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 0.5f;
	private final static int reloadAmount = 6;
	private final static float baseDamage = 10.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 4.5f;
	private final static float projectileSpeed = 8.0f;
	private final static int projectileWidth = 60;
	private final static int projectileHeight = 20;
	private final static float lifespan = 5.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
	
	private final static String spriteId = "spit";
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startVelocity, float x, float y, short filter) {
			
			HitboxAnimated proj = new HitboxAnimated(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
					filter, true, user, spriteId);
			
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
	
	public SpittlefishAttack(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot);
	}

}
