package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.SyncedAttack;
import com.mygdx.hadal.states.PlayState;

public class SpeargunNerfed extends RangedWeapon {

	private static final int clipSize = 8;
	private static final int ammoSize = 88;
	private static final float shootCd = 0.2f;
	private static final float shootDelay = 0;
	private static final float reloadTime = 1.5f;
	private static final int reloadAmount = 0;
	private static final float projectileSpeed = 35.0f;
	private static final Vector2 projectileSize = new Vector2(50, 12);
	private static final float lifespan = 1.0f;

	private static final Sprite weaponSprite = Sprite.MT_SPEARGUN;
	private static final Sprite eventSprite = Sprite.P_SPEARGUN;

	public SpeargunNerfed(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.SPEAR_NERFED.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}
}
