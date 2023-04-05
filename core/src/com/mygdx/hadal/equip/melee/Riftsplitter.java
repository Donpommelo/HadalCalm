package com.mygdx.hadal.equip.melee;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.RiftSplit;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class Riftsplitter extends MeleeWeapon {

	private static final float SHOOT_CD = 0.4f;
	private static final float PROJECTILE_SPEED = 33.0f;

	private static final Vector2 PROJECTILE_SIZE = RiftSplit.PROJECTILE_SIZE;
	private static final float LIFESPAN = RiftSplit.LIFESPAN;
	private static final float BASE_DAMAGE = RiftSplit.BASE_DAMAGE;
	private static final float SHOCKWAVE_DAMAGE = RiftSplit.SHOCKWAVE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_SCRAPRIPPER;
	private static final Sprite EVENT_SPRITE = Sprite.P_SCRAPRIPPER;

	public Riftsplitter(Player user) {
		super(user, SHOOT_CD, WEAPON_SPRITE, EVENT_SPRITE);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, PlayerBodyData shooter, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, shooter, faction, mouseLocation);
		SoundEffect.WOOSH.playUniversal(state, shooter.getSchmuck().getPixelPosition(), 1.0f, false);
	}
	
	private final Vector2 startVelo = new Vector2();
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.RIFT_SPLIT.initiateSyncedAttackSingle(state, user, user.getProjectileOrigin(weaponVelo, PROJECTILE_SIZE.x),
				startVelo.set(startVelocity).nor().scl(PROJECTILE_SPEED));
	}

	@Override
	public float getBotRangeMax() { return PROJECTILE_SPEED * LIFESPAN; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf((int) SHOCKWAVE_DAMAGE),
				String.valueOf(SHOOT_CD)};
	}
}
