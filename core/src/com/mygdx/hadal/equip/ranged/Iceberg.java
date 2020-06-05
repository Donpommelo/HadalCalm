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
import com.mygdx.hadal.strategies.hitbox.ContactUnitParticles;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandardRepeatable;
import com.mygdx.hadal.strategies.hitbox.DieParticles;
import com.mygdx.hadal.strategies.hitbox.DropThroughPassability;

public class Iceberg extends RangedWeapon {

	private final static int clipSize = 4;
	private final static int ammoSize = 25;
	private final static float shootCd = 0.75f;
	private final static float shootDelay = 0.15f;
	private final static float reloadTime = 1.0f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 45.0f;
	private final static float recoil = 15.0f;
	private final static float knockback = 30.0f;
	private final static float projectileSpeed = 45.0f;
	private final static Vector2 projectileSize = new Vector2(48, 48);
	private final static float lifespan = 3.0f;

	private final static Sprite projSprite = Sprite.ICEBERG;
	private final static Sprite weaponSprite = Sprite.MT_ICEBERG;
	private final static Sprite eventSprite = Sprite.P_ICEBERG;
	
	public Iceberg(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.ICE_IMPACT.playUniversal(state, startPosition, 1.0f, false);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, false, true, user, projSprite);
		hbox.setGravity(5);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandardRepeatable(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.WHACKING, DamageTypes.RANGED));	
		hbox.addStrategy(new DropThroughPassability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.ICE_CLOUD, 0.0f, 3.0f));
		hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.ICE_IMPACT));
		hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.ICE_IMPACT));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			float lastX = 0;
			
			@Override
			public void controller(float delta) {
				
				//when we hit a wall, we reverse momentum instead of staying still.
				//This is necessary b/c we cannot turn restitution up without having the projectile bounce instead of slide,
				if (hbox.getLinearVelocity().x == 0) {
					hbox.setLinearVelocity(-lastX, hbox.getLinearVelocity().y);
				}
				
				lastX = hbox.getLinearVelocity().x;
			}
		});
	}
}
