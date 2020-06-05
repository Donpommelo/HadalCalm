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
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactUnitShock;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class Vajra extends RangedWeapon {

	private final static int clipSize = 4;
	private final static int ammoSize = 24;
	private final static float shootCd = 0.4f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.1f;
	private final static int reloadAmount = 0;

	private final static float recoil = 0.0f;
	private final static float baseDamage = 20.0f;
	private final static float knockback = 5.0f;
	private final static float projectileSpeedStart = 30.0f;
	private final static Vector2 projectileSize = new Vector2(84, 30);
	private final static float lifespan = 1.0f;
	
	private final static float chainDamage = 25.0f;
	private final static int chainRadius = 25;
	private final static int chainAmount = 6;
	
	private final static Sprite weaponSprite = Sprite.MT_CHAINLIGHTNING;
	private final static Sprite eventSprite = Sprite.P_CHAINLIGHTNING;
	
	public Vajra(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeedStart, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.THUNDER.playUniversal(state, startPosition, 0.5f, false);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, Sprite.LIGHTNING);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitShock(state, hbox, user.getBodyData(), chainDamage, chainRadius, chainAmount, user.getHitboxfilter()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.ENERGY, DamageTypes.RANGED));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.LIGHTNING_CHARGE, 0.0f, 3.0f));
	}
}
