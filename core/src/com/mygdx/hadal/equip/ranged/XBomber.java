package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactUnitParticles;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageConstant;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DieParticles;
import com.mygdx.hadal.strategies.hitbox.DieSound;
import com.mygdx.hadal.strategies.hitbox.Static;

public class XBomber extends RangedWeapon {

	private final static int clipSize = 2;
	private final static int ammoSize = 26;
	private final static float shootCd = 0.3f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.2f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 20.0f;
	private final static float recoil = 12.0f;
	private final static float knockback = 30.0f;
	private final static float projectileSpeed = 40.0f;
	private final static Vector2 projectileSize = new Vector2(40, 40);
	private final static float lifespan = 0.5f;
	
	private final static Sprite projSprite = Sprite.ORB_PINK;
	private final static Sprite crossSprite = Sprite.LASER_BEAM;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private final static Vector2 crossSize = new Vector2(700, 40);
	private final static float crossLifespan = 0.25f;
	private final static float crossDamage = 35.0f;

	public XBomber(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startPosition, Vector2 startVelocity, final short filter) {
		SoundEffect.FIRE9.playUniversal(state, startPosition, 0.25f, false);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		hbox.setGravity(2.0f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION_FUN, 0.6f));
		hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT));

		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			@Override
			public void die() {
				
				Hitbox cross1 = new RangedHitbox(state, hbox.getPixelPosition(), crossSize, crossLifespan, new Vector2(), filter, true, true, user, crossSprite) {
					
					@Override
					public void create() {
						super.create();
						setTransform(body.getPosition().x, body.getPosition().y, (float) (Math.PI / 4));
					}
				};
				
				cross1.makeUnreflectable();
				
				cross1.addStrategy(new ControllerDefault(state, cross1, user.getBodyData()));
				cross1.addStrategy(new DamageConstant(state, cross1, user.getBodyData(), baseDamage, new Vector2(startVelocity).nor().scl(knockback), DamageTypes.ENERGY, DamageTypes.RANGED));
				cross1.addStrategy(new ContactUnitParticles(state, cross1, user.getBodyData(), Particle.LASER_IMPACT).setDrawOnSelf(false));
				cross1.addStrategy(new Static(state, cross1, user.getBodyData()));
				
				Hitbox cross2 = new RangedHitbox(state, hbox.getPixelPosition(), crossSize, crossLifespan, new Vector2(), filter, true, true, user, crossSprite) {
					
					@Override
					public void create() {
						super.create();
						setTransform(body.getPosition().x, body.getPosition().y, (float) (- Math.PI / 4));
					}
				};
				
				cross2.makeUnreflectable();
				
				cross2.addStrategy(new ControllerDefault(state, cross2, user.getBodyData()));
				cross2.addStrategy(new DamageConstant(state, cross2, user.getBodyData(), crossDamage, new Vector2(startVelocity).nor().scl(knockback), DamageTypes.ENERGY, DamageTypes.RANGED));
				cross2.addStrategy(new ContactUnitParticles(state, cross2, user.getBodyData(), Particle.LASER_IMPACT).setDrawOnSelf(false));
				cross2.addStrategy(new Static(state, cross2, user.getBodyData()));
			}
		});
	}
}
