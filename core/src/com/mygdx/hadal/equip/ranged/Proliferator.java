package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.ProliferatorProjectile;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;

public class Proliferator extends RangedWeapon {

	private static final int CLIP_SIZE = 9;
	private static final int AMMO_SIZE = 72;
	private static final float SHOOT_CD = 0.6f;
	private static final float RELOAD_TIME = 1.4f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 90.0f;

	private static final Vector2 PROJECTILE_SIZE = ProliferatorProjectile.PROJECTILE_SIZE;
	private static final float LIFESPAN = ProliferatorProjectile.LIFESPAN;
	private static final float BASE_DAMAGE = ProliferatorProjectile.BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_ICEBERG;
	private static final Sprite EVENT_SPRITE = Sprite.P_ICEBERG;

	public Proliferator(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, WEAPON_SPRITE, EVENT_SPRITE,
				PROJECTILE_SIZE.x, LIFESPAN);
	}

	private final Vector2 closestPosition = new Vector2();
	private final Vector2 mousePosition = new Vector2();
	private final Vector2 spawnPosition = new Vector2();

	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		mousePosition.set(user.getMouseHelper().getPixelPosition());
		closestPosition.set(user.getPixelPosition());
		spawnPosition.set(closestPosition);

		float closestDistance = closestPosition.dst2(mousePosition);

		for (Hitbox hitbox : user.getSpecialWeaponHelper().getLeapFrogs()) {
			spawnPosition.set(hitbox.getPixelPosition());
			float prospectiveDistance = spawnPosition.dst2(mousePosition);
			if (prospectiveDistance < closestDistance) {
				closestPosition.set(spawnPosition);
				closestDistance = prospectiveDistance;
			}
		}

		startVelocity.setAngleDeg(mousePosition.sub(closestPosition).angleDeg());
		SyncedAttack.PROLIFERATOR.initiateSyncedAttackSingle(state, user, closestPosition, startVelocity);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD)};
	}
}
