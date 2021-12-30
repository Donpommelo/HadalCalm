package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.SyncedAttack;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.*;

public class GrenadeLauncher extends RangedWeapon {

	private static final int clipSize = 6;
	private static final int ammoSize = 28;
	private static final float shootCd = 0.25f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 0.5f;
	private static final int reloadAmount = 1;
	private static final float baseDamage = 15.0f;
	private static final float recoil = 2.5f;
	private static final float knockback = 0.0f;
	private static final float projectileSpeed = 28.0f;
	private static final Vector2 projectileSize = new Vector2(25, 25);
	private static final float lifespan = 2.5f;

	private static final int explosionRadius = 150;
	private static final float explosionDamage = 45.0f;
	private static final float explosionKnockback = 25.0f;

	private static final Sprite projSprite = Sprite.GRENADE;
	private static final Sprite weaponSprite = Sprite.MT_GRENADE;
	private static final Sprite eventSprite = Sprite.P_GRENADE;

	public GrenadeLauncher(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}

	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.GRENADE.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	public static Hitbox createGrenade(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.LAUNCHER.playSourced(state, startPosition, 0.25f);

		Hitbox hboxBouncy = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
				false, false, user, Sprite.NOTHING);
		hboxBouncy.setGravity(2.5f);
		hboxBouncy.setRestitution(0.5f);

		hboxBouncy.addStrategy(new ControllerDefault(state, hboxBouncy, user.getBodyData()));
		hboxBouncy.addStrategy(new DropThroughPassability(state, hboxBouncy, user.getBodyData()));

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
				false, true, user, projSprite);
		hbox.setSyncDefault(false);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), hboxBouncy, new Vector2(), new Vector2()).setKillOnDeath(true));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.EXPLOSIVE, DamageTypes.RANGED));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback,
				(short) 0, false));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.BOMB, 0.4f).setSynced(false));
		hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.WALL_HIT1, 0.2f).setSynced(false));
		hbox.addStrategy(new FlashNearDeath(state, hbox, user.getBodyData(), 1.0f, false));

		if (!state.isServer()) {
			((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ClientState.ObjectLayer.HBOX);
		}
		return hboxBouncy;
	}
}