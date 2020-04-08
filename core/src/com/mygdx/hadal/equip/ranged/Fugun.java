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

	private final static int clipSize = 2;
	private final static int ammoSize = 14;
	private final static float shootCd = 0.25f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.2f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 30.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 12.5f;
	private final static float projectileSpeed = 40.0f;
	private final static Vector2 projectileSize = new Vector2(60, 60);
	private final static float lifespan = 1.2f;
		
	private final static int poisonRadius = 250;
	private final static float poisonDamage = 40/60f;
	private final static float poisonDuration = 4.0f;

	private final static Sprite projSprite = Sprite.FUGU;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	public Fugun(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.LAUNCHER4.playUniversal(state, startPosition, 0.5f);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.POISON, DamageTypes.RANGED));
		hbox.addStrategy(new DiePoison(state, hbox, user.getBodyData(), poisonRadius, poisonDamage, poisonDuration, (short) 0));
		hbox.addStrategy(new DieRagdoll(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.DEFLATE, 0.4f));

	}
}
