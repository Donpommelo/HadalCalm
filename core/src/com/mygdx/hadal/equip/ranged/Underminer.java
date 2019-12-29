package com.mygdx.hadal.equip.ranged;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitLoseDuraStrategy;
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
	private final static Vector2 projectileSize = new Vector2(30, 30);
	private final static float lifespan = 3.5f;
	
	private final static Sprite projSprite = Sprite.ORB_BLUE;
	private final static Sprite fragSprite = Sprite.ORB_BLUE;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private final static float activatedSpeed = 10.0f;

	private final static int explosionRadius = 200;
	private final static float explosionDamage = 40.0f;
	private final static float explosionKnockback = 18.0f;
	
	private final static int numProj = 10;
	private final static int spread = 30;
	private final static Vector2 fragSize = new Vector2(20, 20);
	private final static float fragLifespan = 0.25f;
	private final static float fragDamage = 16.0f;
	private final static float fragSpeed = 40.0f;
	
	public Underminer(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startPosition, final Vector2 startVelocity, final short filter) {
		
		Hitbox hbox = new Hitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		hbox.setGravity(4.0f);
		hbox.setDurability(2);
		
		final Equipable tool = this;
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			private boolean activated = false;
			private float invuln = 0.0f;
			
			@Override
			public void controller(float delta) {
				super.controller(delta);
				
				//invuln is incremented to prevent hbox from detonating immediately upon hitting a corner
				if (invuln > 0) {
					invuln -= delta;
				}
			}
			
			@Override
			public void onHit(HadalData fixB) {
				if (fixB == null) {
					
					//upon hitting a wall, hbox activates and begins drilling in a straight line
					if (!activated) {
						activated = true;
						hbox.setLinearVelocity(hbox.getLinearVelocity().nor().scl(activatedSpeed));
						hbox.setGravityScale(0);
						invuln = 0.1f;
					} else {
						
						//if already activated (i.e drilling to other side of wall), hbox explodes
						if (invuln <= 0) {
							hbox.die();
						}
					}
				}
			}
			
			private Vector2 newVelocity = new Vector2();
			
			@Override
			public void die() {
				
				//hbox releases frags when it dies
				WeaponUtils.createExplosion(state, this.hbox.getPixelPosition(), explosionRadius, creator.getSchmuck(), tool, explosionDamage, explosionKnockback, filter);
				
				for (int i = 0; i < numProj; i++) {
					float newDegrees = (float) (startVelocity.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
					
					newVelocity.set(startVelocity);
					
					Hitbox frag = new RangedHitbox(state, hbox.getPixelPosition(), fragSize, fragLifespan, newVelocity.nor().scl(fragSpeed).setAngle(newDegrees), filter, true, true, user, fragSprite);
					frag.addStrategy(new HitboxDefaultStrategy(state, frag, user.getBodyData()));
					frag.addStrategy(new HitboxOnContactUnitLoseDuraStrategy(state, frag, user.getBodyData()));
					frag.addStrategy(new HitboxDamageStandardStrategy(state, frag, user.getBodyData(), tool, fragDamage, knockback, DamageTypes.RANGED));
				}
			}
		});
	}
}
