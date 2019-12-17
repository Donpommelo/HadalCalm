package com.mygdx.hadal.equip.ranged;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitLoseDuraStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallLoseDuraStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class Underminer extends RangedWeapon {

	private final static String name = "Underminer";
	private final static int clipSize = 4;
	private final static int ammoSize = 18;
	private final static float shootCd = 0.2f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.6f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 35.0f;
	private final static float recoil = 8.5f;
	private final static float knockback = 10.0f;
	private final static float projectileSpeed = 30.0f;
	private final static int projectileWidth = 60;
	private final static int projectileHeight = 60;
	private final static float lifespan = 3.5f;
	private final static float gravity = 4;
	
	private final static int projDura = 2;
	
	private final static Sprite projSprite = Sprite.ORB_BLUE;
	private final static Sprite fragSprite = Sprite.ORB_BLUE;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private final static float activatedSpeed = 10.0f;

	private final static int explosionRadius = 400;
	private final static float explosionDamage = 40.0f;
	private final static float explosionKnockback = 18.0f;
	
	private final static int numProj = 10;
	private final static int spread = 30;
	private final static int fragWidth = 40;
	private final static int fragHeight = 40;
	private final static float fragLifespan = 0.25f;
	private final static float fragDamage = 16.0f;
	private final static float fragSpeed = 40.0f;
	
	public Underminer(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileWidth);
	}
	
	@Override
	public void fire(PlayState state, final Schmuck user, final Vector2 startVelocity, float x, float y, final short filter) {
		
		Hitbox hbox = new HitboxSprite(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity, filter, true, true, user, projSprite);
		
		final Equipable tool = this;
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			private boolean activated = false;
			private float invuln = 0.0f;
			
			@Override
			public void controller(float delta) {
				super.controller(delta);
				if (invuln > 0) {
					invuln -= delta;
				}
			}
			
			@Override
			public void onHit(HadalData fixB) {
				if (fixB == null) {
					if (!activated) {
						activated = true;
						hbox.setLinearVelocity(hbox.getLinearVelocity().nor().scl(activatedSpeed));
						hbox.setGravityScale(0);
						invuln = 0.1f;
					} else {
						if (invuln <= 0) {
							hbox.die();
						}
					}
				}
			}
			
			private Vector2 newVelocity = new Vector2();
			@Override
			public void die() {
				WeaponUtils.createExplosion(state, this.hbox.getPosition().x * PPM , this.hbox.getPosition().y * PPM, 
						creator.getSchmuck(), tool, explosionRadius, explosionDamage, explosionKnockback, filter);
				
				for (int i = 0; i < numProj; i++) {
					float newDegrees = (float) (startVelocity.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
					
					newVelocity.set(startVelocity);
					
					Hitbox frag = new HitboxSprite(state, 
							hbox.getPosition().x * PPM, hbox.getPosition().y * PPM,
							fragWidth, fragHeight, gravity, fragLifespan, projDura, 0, newVelocity.nor().scl(fragSpeed).setAngle(newDegrees),
							filter, true, true, user, fragSprite);
					frag.addStrategy(new HitboxDefaultStrategy(state, frag, user.getBodyData()));
					frag.addStrategy(new HitboxOnContactUnitLoseDuraStrategy(state, frag, user.getBodyData()));
					frag.addStrategy(new HitboxOnContactWallLoseDuraStrategy(state, frag, user.getBodyData()));
					frag.addStrategy(new HitboxDamageStandardStrategy(state, frag, user.getBodyData(), tool, fragDamage, knockback, DamageTypes.RANGED));
				}
			}
		});
	}
}
