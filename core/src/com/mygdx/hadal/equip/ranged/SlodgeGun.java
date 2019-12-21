package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitLoseDuraStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.FiringWeapon;
import com.mygdx.hadal.statuses.Slodged;

public class SlodgeGun extends RangedWeapon {

	private final static String name = "Slodge Gun";
	private final static int clipSize = 1;
	private final static int ammoSize = 21;
	private final static float shootCd = 0.1f;
	private final static float shootDelay = 0.25f;
	private final static float reloadTime = 1.2f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 3.0f;
	private final static float recoil = 24.0f;
	private final static float knockback = 5.0f;
	private final static float projectileSpeed = 25.0f;
	private final static int projectileWidth = 50;
	private final static int projectileHeight = 50;
	private final static float lifespan = 4.0f;
	
	private final static float procCd = .05f;

	private final static float slowDura = 3.0f;
	private final static float slow = 0.75f;
	private final static float fireDuration = 0.75f;

	private final static Sprite weaponSprite = Sprite.MT_SLODGEGUN;
	private final static Sprite eventSprite = Sprite.P_SLODGEGUN;
	
	public SlodgeGun(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileWidth);
	}
	
	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startVelocity, float x, float y, final short filter) {
		
		Hitbox hbox = new RangedHitbox(state, x, y, projectileWidth, projectileHeight, lifespan, startVelocity,	filter, true, true, user);
		hbox.setGravity(3.0f);
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactUnitLoseDuraStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback, DamageTypes.RANGED));
		new ParticleEntity(state, hbox, Particle.SHADOW_PATH, 3.0f, 0.0f, true, particleSyncType.TICKSYNC);
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			@Override
			public void onHit(HadalData fixB) {
				if (fixB != null) {
					if (fixB instanceof BodyData) {
						((BodyData)fixB).addStatus(new Slodged(state, slowDura, slow, user.getBodyData(), ((BodyData)fixB)));
					}
				}
			}
			
		});
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {
		if (processClip(state, shooter)) {
			shooter.addStatus(new FiringWeapon(state, fireDuration, shooter, shooter, projectileSpeed, 0, 0, projectileWidth, procCd, this));
		}
	}
}
