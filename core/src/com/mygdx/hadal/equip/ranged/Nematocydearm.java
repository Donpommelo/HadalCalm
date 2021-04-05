package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.*;

public class Nematocydearm extends RangedWeapon {

	private static final int clipSize = 4;
	private static final int ammoSize = 40;
	private static final float shootCd = 0.1f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 0.9f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 33.0f;
	private static final float recoil = 0.0f;
	private static final float knockback = 20.0f;
	private static final float projectileSpeed = 45.0f;
	private static final Vector2 projectileSize = new Vector2(91, 35);
	private static final Vector2 stickySize = new Vector2(70, 25);
	private static final float lifespan = 7.0f;
	
	private static final int spread = 5;

	private static final Sprite projSprite = Sprite.NEMATOCYTE;
	private static final Sprite weaponSprite = Sprite.MT_NEMATOCYTEARM;
	private static final Sprite eventSprite = Sprite.P_NEMATOCYTEARM;
	
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
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.POKING, DamageTypes.RANGED).setStaticKnockback(true));
		hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), spread));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.STAB, 0.6f, true));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.DANGER_BLUE, 0.0f, 1.0f));
		hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(
			HadalColor.SKY_BLUE));
		hbox.addStrategy(new ContactStick(state, hbox, user.getBodyData(), true, false));
	}
}
