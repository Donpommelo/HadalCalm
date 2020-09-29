package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.ParticleColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactStick;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactUnitParticles;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ContactWallSound;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStatic;
import com.mygdx.hadal.strategies.hitbox.Spread;

public class Nematocydearm extends RangedWeapon {

	private final static int clipSize = 7;
	private final static int ammoSize = 49;
	private final static float shootCd = 0.1f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 0.75f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 35.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 20.0f;
	private final static float projectileSpeed = 45.0f;
	private final static Vector2 projectileSize = new Vector2(91, 35);
	private final static Vector2 stickySize = new Vector2(70, 25);
	private final static float lifespan = 12.0f;
	
	private final static int spread = 5;

	private final static Sprite projSprite = Sprite.NEMATOCYTE;
	private final static Sprite weaponSprite = Sprite.MT_NEMATOCYTEARM;
	private final static Sprite eventSprite = Sprite.P_NEMATOCYTEARM;
	
	public Nematocydearm(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.ATTACK1.playUniversal(state, startPosition, 0.4f, false);

		Hitbox hbox = new RangedHitbox(state, startPosition, stickySize, lifespan, startVelocity, filter, true, true, user, projSprite);
		hbox.setSpriteSize(projectileSize);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStatic(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.POKING, DamageTypes.RANGED));
		hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), spread));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.STAB, 0.6f, true));
		hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(ParticleColor.SKY_BLUE));
		hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.SQUISH, 0.75f));
		hbox.addStrategy(new ContactStick(state, hbox, user.getBodyData(), true, false));
	}
}
