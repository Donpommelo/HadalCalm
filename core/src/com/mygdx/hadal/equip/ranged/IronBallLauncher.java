package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.hitbox.ContactWallSound;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DropThroughPassability;

public class IronBallLauncher extends RangedWeapon {

	private static final int clipSize = 1;
	private static final int ammoSize = 23;
	private static final float shootCd = 0.15f;
	private static final float shootDelay = 0.2f;
	private static final float reloadTime = 0.75f;
	private static final int reloadAmount = 1;
	private static final float baseDamage = 85.0f;
	private static final float recoil = 18.0f;
	private static final float knockback = 55.0f;
	private static final float projectileSpeed = 60.0f;
	private static final Vector2 projectileSize = new Vector2(50, 50);
	private static final float lifespan = 2.5f;

	private static final Sprite projSprite = Sprite.CANNONBALL;
	private static final Sprite weaponSprite = Sprite.MT_IRONBALL;
	private static final Sprite eventSprite = Sprite.P_IRONBALL;
	
	public IronBallLauncher(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.IRON_BALL.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	public static Hitbox createIronBall(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.CANNON.playSourced(state, startPosition, 0.8f);
		user.recoil(startVelocity, recoil);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
				false, true, user, projSprite);
		hbox.setGravity(10);
		hbox.setFriction(1.0f);
		hbox.setRestitution(0.5f);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DropThroughPassability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageSource.IRON_BALL_LAUNCHER,
				DamageTag.WHACKING, DamageTag.RANGED));
		hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.WALL_HIT1, 0.4f).setSynced(false));

		return hbox;
	}
}
