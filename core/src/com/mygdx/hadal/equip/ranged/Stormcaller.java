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
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateSound;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class Stormcaller extends RangedWeapon {

	private final static int clipSize = 3;
	private final static int ammoSize = 18;
	private final static float shootCd = 0.5f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.5f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 3.0f;
	private final static float recoil = 6.0f;
	private final static float knockback = 25.0f;
	private final static float projectileSpeed = 18.0f;
	private final static Vector2 projectileSize = new Vector2(10, 10);
	private final static float lifespan = 1.8f;
	
	private final static float explosionInterval = 1 / 60f;
	private final static int explosionMaxSize = 175;
	
	private final static Sprite projSprite = Sprite.HURRICANE;
	private final static Sprite weaponSprite = Sprite.MT_STORMCALLER;
	private final static Sprite eventSprite = Sprite.P_STORMCALLER;
	
	public Stormcaller(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}

	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startPosition, Vector2 startVelocity, final short filter) {
		SoundEffect.WIND2.playUniversal(state, startPosition, 1.0f, false);

		Hitbox storm = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, false, true, user, Sprite.NOTHING);
		
		storm.setRestitution(0.5f);
		
		storm.addStrategy(new ControllerDefault(state, storm, user.getBodyData()));
		storm.addStrategy(new CreateSound(state, storm, user.getBodyData(), SoundEffect.WIND3, 0.5f, true));
		storm.addStrategy(new HitboxStrategy(state, storm, user.getBodyData()) {
			
			private float controllerCount = 0;
			private Vector2 explosionSize = new Vector2(projectileSize);
			
			@Override
			public void create() {
				
				//Set hurricane to have constant angular velocity for visual effect.
				hbox.setAngularVelocity(5);
			}
			
			@Override
			public void controller(float delta) {
				
				controllerCount += delta;

				//This hbox periodically spawns hboxes on top of itself.
				while (controllerCount >= explosionInterval) {
					controllerCount -= explosionInterval;
					
					Hitbox pulse = new Hitbox(state, hbox.getPixelPosition(), explosionSize, explosionInterval * 2, new Vector2(), filter, true, true, user, projSprite);
					pulse.addStrategy(new ControllerDefault(state, pulse, user.getBodyData()));
					pulse.addStrategy(new DamageStandard(state, pulse, user.getBodyData(),  baseDamage, knockback, DamageTypes.EXPLOSIVE, DamageTypes.RANGED));
					pulse.addStrategy(new HitboxStrategy(state, pulse, user.getBodyData()) {
						
						@Override
						public void create() {
							hbox.setAngle(storm.getAngle());
						}
					});
					
					
					//spawned hboxes get larger as hbox moves
					if (explosionSize.x <= explosionMaxSize) {
						explosionSize.add(2, 2);
					}
				}
			}
		});
	}
}
