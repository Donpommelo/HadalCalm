package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandardRepeatable;

public class AmitaCannon extends RangedWeapon {

	private final static int clipSize = 4;
	private final static int ammoSize = 32;
	private final static float shootCd = 0.4f;
	private final static float shootDelay = 0.1f;
	private final static float reloadTime = 1.6f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 7.5f;
	private final static float recoil = 6.0f;
	private final static float knockback = 15.0f;
	private final static float projectileSpeed = 30.0f;
	private final static Vector2 projectileSize = new Vector2(48, 48);
	private final static float lifespan = 1.0f;
	
	private final static int numOrbitals = 8;
	private final static float orbitalRange = 0.8f;
	private final static float orbitalSpeed = 360.0f;
	private final static Vector2 orbitalSize = new Vector2(24, 24);
	private final static float activatedSpeed = 40.0f;

	private final static Sprite projSprite = Sprite.ORB_ORANGE;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	public AmitaCannon(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startPosition, Vector2 startVelocity, final short filter) {
		SoundEffect.ELECTRIC_CHAIN.playUniversal(state, startPosition, 0.5f, false);

		//we create an ivisible hitbox that moves in a straight line.
		Hitbox center = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, Sprite.NOTHING);
		center.setSyncDefault(false);
		
		center.addStrategy(new ControllerDefault(state, center, user.getBodyData()));
		center.addStrategy(new ContactWallDie(state, center, user.getBodyData()));
		center.addStrategy(new HitboxStrategy(state, center, user.getBodyData()) {
			
			private Vector2 angle = new Vector2(0, orbitalRange);
			
			@Override
			public void create() {
				for (int i = 0; i < numOrbitals; i++) {
					angle.setAngle(angle.angle() + 360 / numOrbitals);
					
					//we create several orbiting projectiles that circle the invisible center
					//when the center hits a wall, the orbitals move outwards
					Hitbox orbital = new RangedHitbox(state, startPosition, orbitalSize, lifespan, startVelocity, filter, true, true, user, projSprite);
					orbital.setSyncDefault(false);
					orbital.setSyncInstant(true);
					
					orbital.addStrategy(new ControllerDefault(state, orbital, user.getBodyData()));
					orbital.addStrategy(new DamageStandardRepeatable(state, orbital, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
					orbital.addStrategy(new ContactWallDie(state, orbital, user.getBodyData()));
					orbital.addStrategy(new HitboxStrategy(state, orbital, user.getBodyData()) {
						
						private Vector2 centerPos = new Vector2();
						private Vector2 offset = new Vector2();
						private float currentAngle = angle.angle();
						private boolean activated = false;
						
						@Override
						public void controller(float delta) {
							
							if (center.getBody() != null && center.isAlive()) {
								currentAngle += orbitalSpeed * delta;
								
								centerPos.set(center.getPosition());
								offset.set(0, orbitalRange).setAngle(currentAngle);
								orbital.setTransform(centerPos.add(offset), orbital.getBody().getAngle());
							} else if (!activated) {
								activated = true;
								hbox.setLinearVelocity(new Vector2(0, activatedSpeed).setAngle(currentAngle));
								hbox.setLifeSpan(lifespan);
							}
						}
					});
				}
			}
		});
	}
}
