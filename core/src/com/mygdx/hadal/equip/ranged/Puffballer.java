package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.Puffball;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class Puffballer extends RangedWeapon {

	private static final int CLIP_SIZE = 1;
	private static final int AMMO_SIZE = 25;
	private static final float SHOOT_CD = 0.8f;
	private static final float RELOAD_TIME = 1.5f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 30.0f;
	private static final float FRAG_SPEED = 6.0f;
	private static final float FRAG_VELO_SPREAD = 1.2f;

	private static final Vector2 PROJECTILE_SIZE = Puffball.PROJECTILE_SIZE;
	private static final float LIFESPAN = Puffball.LIFESPAN;
	private static final float BASE_DAMAGE = Puffball.BASE_DAMAGE;

	private static final float SPORE_FRAG_LIFESPAN = Puffball.SPORE_FRAG_LIFESPAN;
	private static final float SPORE_FRAG_DAMAGE = Puffball.SPORE_FRAG_DAMAGE;
	private static final int SPORE_FRAG_NUMBER = Puffball.SPORE_FRAG_NUMBER;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_TORPEDO;
	private static final Sprite EVENT_SPRITE = Sprite.P_TORPEDO;

	//list of hitboxes created
	private final Queue<Hitbox> puffballs = new Queue<>();

	private boolean held = false;

	public Puffballer(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN);
	}

	@Override
	public void mouseClicked(float delta, PlayState state, PlayerBodyData playerData, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, playerData, faction, mouseLocation);

		if (reloading || getClipLeft() == 0) { return; }
		if (!held) {
			held = true;
			super.execute(state, playerData);
		}
	}

	@Override
	public void execute(PlayState state, PlayerBodyData playerData) {}

	@Override
	public void release(PlayState state, PlayerBodyData playerData) {
		held = false;

		//upon releasing mouse, detonate all laid bombs
		for (Hitbox puffball : puffballs) {
			if (puffball.isAlive()) {
				puffball.die();
			}
		}
		puffballs.clear();
	}

	private final Vector2 newVelocity = new Vector2();
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		float[] fragAngles = new float[SPORE_FRAG_NUMBER * 2];
		for (int i = 0; i < SPORE_FRAG_NUMBER; i++) {
			newVelocity.setToRandomDirection().scl(FRAG_SPEED).scl(MathUtils.random() * FRAG_VELO_SPREAD + 1 - FRAG_VELO_SPREAD / 2);
			fragAngles[2 * i] = newVelocity.x;
			fragAngles[2 * i + 1] = newVelocity.y;
		}

		Hitbox hbox = SyncedAttack.PUFFBALL.initiateSyncedAttackSingle(state, user, startPosition, startVelocity, fragAngles);
		puffballs.addLast(hbox);
	}

	@Override
	public void unequip(PlayState state) {
		held = false;
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf((int) SPORE_FRAG_DAMAGE),
				String.valueOf(SPORE_FRAG_NUMBER),
				String.valueOf((int) SPORE_FRAG_LIFESPAN),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD)};
	}
}
