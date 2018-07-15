package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Slodged;
import com.mygdx.hadal.utils.HitboxFactory;
import static com.mygdx.hadal.utils.Constants.PPM;

public class SlodgeGun extends RangedWeapon {

	private final static String name = "Slodge Gun";
	private final static int clipSize = 1;
	private final static float shootCd = 0.1f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 0.8f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 20.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 0.0f;
	private final static float projectileSpeed = 25.0f;
	private final static int projectileWidth = 40;
	private final static int projectileHeight = 40;
	private final static float lifespan = 3.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
	
	private final static float slowDura = 3.0f;

	private final static int explosionRadius = 250;

	private final static String weapSpriteId = "slodgegun";
	private final static String weapEventSpriteId = "event_slodgegun";
	private final static String projSpriteId = "debris_c";
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Equipable tool, Vector2 startVelocity, float x, float y, final short filter) {
			
			Hitbox hbox = new HitboxImage(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
					filter, true, true, user, projSpriteId);
			
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxOnContactStandardStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));
			hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
				
				@Override
				public void die() {
					
					Hitbox explosion = new Hitbox(state, 
							this.hbox.getBody().getPosition().x * PPM , 
							this.hbox.getBody().getPosition().y * PPM,	
							explosionRadius, explosionRadius, 0, .02f, 1, 0, new Vector2(0, 0),
							filter, true, false, user);
					
					explosion.addStrategy(new HitboxDefaultStrategy(state, explosion, user.getBodyData()));
					explosion.addStrategy(new HitboxStrategy(state, explosion, user.getBodyData()) {
						
						@Override
						public void onHit(HadalData fixB) {
							if (fixB != null) {
								if (fixB instanceof BodyData) {
									((BodyData)fixB).addStatus(new Slodged(state, slowDura, user.getBodyData(), ((BodyData)fixB)));
								}
							}
						}
						
					});
				}
			});
		}
	};
	
	public SlodgeGun(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weapSpriteId, weapEventSpriteId);
	}
}
