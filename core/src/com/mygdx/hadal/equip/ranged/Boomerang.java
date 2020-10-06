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
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ContactWallParticles;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateSound;
import com.mygdx.hadal.strategies.hitbox.DamageStandardRepeatable;
import com.mygdx.hadal.strategies.hitbox.ReturnToUser;

public class Boomerang extends RangedWeapon {

	private static final int clipSize = 3;
	private static final int ammoSize = 24;
	private static final float shootCd = 0.75f;
	private static final float shootDelay = 0;
	private static final float reloadTime = .75f;
	private static final int reloadAmount = 1;
	private static final float baseDamage = 35.0f;
	private static final float recoil = 0.0f;
	private static final float knockback = 30.0f;
	private static final float projectileSpeed = 60.0f;
	private static final Vector2 projectileSize = new Vector2(60, 60);
	private static final float lifespan = 2.5f;
	private static final float returnAmp = 5.0f;
	
	private static final Sprite projSprite = Sprite.BOOMERANG;
	private static final Sprite weaponSprite = Sprite.MT_BOOMERANG;
	private static final Sprite eventSprite = Sprite.P_BOOMERANG;
	
	public Boomerang(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.BOOMERANG_WHIZ.playUniversal(state, startPosition, 1.0f, false);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, false, true, user, projSprite);		
		hbox.setRestitution(0.0f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandardRepeatable(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.WHACKING, DamageTypes.RANGED));	
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS));
		hbox.addStrategy(new ReturnToUser(state, hbox, user.getBodyData(), hbox.getStartVelo().len() * returnAmp));
		hbox.addStrategy(new CreateSound(state, hbox, user.getBodyData(), SoundEffect.WOOSH, 0.5f, true));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE1, 0.75f, true));

		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			@Override
			public void create() {
				
				//Set boomerang to have constant angular velocity for visual effect.
				hbox.setAngularVelocity(10);
			}
		});	
	}
}
