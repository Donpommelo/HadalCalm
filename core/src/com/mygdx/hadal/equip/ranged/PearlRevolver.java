package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallSound;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DieParticles;

public class PearlRevolver extends RangedWeapon {

	private static final int clipSize = 6;
	private static final int ammoSize = 48;
	private static final float shootCd = 0.3f;
	private static final float shootDelay = 0;
	private static final float reloadTime = 0.6f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 35.0f;
	private static final float recoil = 6.0f;
	private static final float knockback = 9.0f;
	private static final float projectileSpeed = 55.0f;
	private static final Vector2 projectileSize = new Vector2(20, 20);
	private static final float lifespan = 1.0f;
	
	private static final Sprite projSprite = Sprite.PEARL;
	private static final Sprite weaponSprite = Sprite.MT_GRENADE;
	private static final Sprite eventSprite = Sprite.P_GRENADE;
	
	public PearlRevolver(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		super.release(state, bodyData);
		
		//Rapidly clicking this weapon incurs no cooldown between shots
		bodyData.getSchmuck().setShootCdCount(0);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.PISTOL.playUniversal(state, startPosition, 0.6f, false);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.EXPLOSION));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.BULLET, DamageTypes.RANGED));
		hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.WALL_HIT1, 0.5f));
	}
}
