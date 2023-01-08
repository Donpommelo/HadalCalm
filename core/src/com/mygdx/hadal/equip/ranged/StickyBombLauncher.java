package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class StickyBombLauncher extends RangedWeapon {

	private static final int clipSize = 6;
	private static final int ammoSize = 36;
	private static final float shootCd = 0.35f;
	private static final float reloadTime = 1.25f;
	private static final int reloadAmount = 0;
	private static final float recoil = 2.0f;
	private static final float projectileSpeed = 40.0f;
	private static final Vector2 projectileSize = new Vector2(50, 50);
	private static final Vector2 stickySize = new Vector2(20, 20);
	private static final float lifespan = 5.0f;

	private static final int explosionRadius = 200;
	private static final float explosionDamage = 55.0f;
	private static final float explosionKnockback = 25.0f;

	private static final Sprite projSprite = Sprite.STICKYBOMB;
	private static final Sprite weaponSprite = Sprite.MT_STICKYBOMB;
	private static final Sprite eventSprite = Sprite.P_STICKYBOMB;

	//list of hitboxes created
	private final Queue<Hitbox> bombsLaid = new Queue<>();

	public StickyBombLauncher(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, reloadAmount, false,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}

	@Override
	public boolean reload(float delta) {

		//upon reload, detonate all laid bombs
		for (Hitbox bomb : bombsLaid) {
			if (bomb.isAlive()) {
				bomb.die();
			}
		}
		bombsLaid.clear();

		return super.reload(delta);
	}

	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		Hitbox hbox = SyncedAttack.STICKY_BOMB.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
		bombsLaid.addLast(hbox);
	}

	public static Hitbox createStickyBomb(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.LAUNCHER.playSourced(state, startPosition, 0.25f);
		user.recoil(startVelocity, recoil);

		Hitbox hbox = new RangedHitbox(state, startPosition, stickySize, lifespan, startVelocity, user.getHitboxfilter(),
				true, true, user, projSprite);
		hbox.setSpriteSize(projectileSize);
		hbox.setSyncedDeleteNoDelay(true);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback,
				(short) 0, false, DamageSource.STICKYBOMB_LAUNCHER));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.BOMB, 0.25f).setSynced(false));
		hbox.addStrategy(new ContactStick(state, hbox, user.getBodyData(), true, true));
		hbox.addStrategy(new FlashShaderNearDeath(state, hbox, user.getBodyData(), 1.0f, false));

		return hbox;
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) explosionDamage),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(reloadTime),
				String.valueOf(shootCd)};
	}
}