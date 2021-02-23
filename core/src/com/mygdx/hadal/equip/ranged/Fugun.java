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
import com.mygdx.hadal.strategies.hitbox.ContactUnitDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DiePoison;
import com.mygdx.hadal.strategies.hitbox.DieRagdoll;
import com.mygdx.hadal.strategies.hitbox.DieSound;

public class Fugun extends RangedWeapon {

	private static final int clipSize = 2;
	private static final int ammoSize = 14;
	private static final float shootCd = 0.2f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 1.1f;
	private static final int reloadAmount = 1;
	private static final float baseDamage = 30.0f;
	private static final float recoil = 0.0f;
	private static final float knockback = 12.5f;
	private static final float projectileSpeed = 45.0f;
	private static final Vector2 projectileSize = new Vector2(36, 36);
	private static final float lifespan = 1.2f;
		
	private static final int poisonRadius = 250;
	private static final float poisonDamage = 0.75f;
	private static final float poisonDuration = 4.0f;

	private static final Sprite projSprite = Sprite.FUGU;
	private static final Sprite weaponSprite = Sprite.MT_IRONBALL;
	private static final Sprite eventSprite = Sprite.P_IRONBALL;
	
	public Fugun(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.LAUNCHER4.playUniversal(state, startPosition, 0.25f, false);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.POISON, DamageTypes.RANGED));
		hbox.addStrategy(new DiePoison(state, hbox, user.getBodyData(), poisonRadius, poisonDamage, poisonDuration, (short) 0));
		hbox.addStrategy(new DieRagdoll(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.DEFLATE, 0.25f));
	}
}
