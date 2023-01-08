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

	private static final float SWING_CD = 0.07f;
	private static final float BASE_DAMAGE = 36.0f;
	private static final Vector2 PROJECTILE_SIZE = new Vector2(60, 40);
	private static final float PROJECTILE_SPEED = 24.0f;
	private static final float LIFESPAN = 0.15f;
	private static final float KNOCKBACK = 25.0f;
	private static final int SPREAD = 30;

	private static final Sprite PROJ_SPRITE = Sprite.PUNCH;
	private static final Sprite WEAPON_SPRITE = Sprite.MT_DEFAULT;
	private static final Sprite EVENT_SPRITE = Sprite.P_DEFAULT;

	public Fisticuffs(Schmuck user) {
		super(user, SWING_CD, WEAPON_SPRITE, EVENT_SPRITE);
	}
	
	private final Vector2 startVelo = new Vector2();
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.FIST.initiateSyncedAttackSingle(state, user, user.getProjectileOrigin(weaponVelo, PROJECTILE_SIZE.x),
				startVelo.set(startVelocity).nor().scl(PROJECTILE_SPEED));
	}

	public static Hitbox createFist(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.WOOSH.playSourced(state, startPosition, 0.75f);

		Hitbox hbox = new Hitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxfilter(),
				true, true, user, PROJ_SPRITE);
		hbox.makeUnreflectable();

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
				DamageSource.FISTICUFFS, DamageTag.MELEE));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.SLAP, 0.8f, true).setSynced(false));
		hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), SPREAD));

		return hbox;
	}

	@Override
	public float getBotRangeMax() { return PROJECTILE_SPEED * LIFESPAN; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(SWING_CD)};
	}
}
