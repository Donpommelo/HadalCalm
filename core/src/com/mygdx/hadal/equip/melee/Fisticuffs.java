package com.mygdx.hadal.equip.melee;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.Spread;

public class Fisticuffs extends MeleeWeapon {

	private static final float swingCd = 0.07f;
	private static final float windup = 0.0f;
	private static final float baseDamage = 36.0f;

	private static final Vector2 projectileSize = new Vector2(60, 40);
	private static final float projectileSpeed = 24.0f;
	private static final float lifespan = 0.15f;
	private static final float knockback = 25.0f;
	private static final Sprite projSprite = Sprite.PUNCH;
	private static final Sprite weaponSprite = Sprite.MT_DEFAULT;
	private static final Sprite eventSprite = Sprite.P_DEFAULT;
	private static final int spread = 30;
	
	public Fisticuffs(Schmuck user) {
		super(user, swingCd, windup, weaponSprite, eventSprite);
	}
	
	private final Vector2 startVelo = new Vector2();
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.FIST.initiateSyncedAttackSingle(state, user, user.getProjectileOrigin(weaponVelo, projectileSize.x),
				startVelo.set(startVelocity).nor().scl(projectileSpeed));
	}

	public static Hitbox createFist(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.WOOSH.playSourced(state, startPosition, 0.75f);

		Hitbox hbox = new Hitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
				true, true, user, projSprite);
		hbox.makeUnreflectable();

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback,
				DamageSource.FISTICUFFS, DamageTag.MELEE));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.SLAP, 0.8f, true).setSynced(false));
		hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), spread));

		return hbox;
	}

	@Override
	public float getBotRangeMax() { return projectileSpeed * lifespan; }
}
