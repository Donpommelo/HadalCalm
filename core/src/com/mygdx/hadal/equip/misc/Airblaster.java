package com.mygdx.hadal.equip.misc;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

public class Airblaster extends MeleeWeapon {

	private static final float SWING_CD = 0.25f;

	public Airblaster(Player user) {
		super(user, SWING_CD, Sprite.MT_DEFAULT, Sprite.P_DEFAULT);
	}

	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.AIRBLAST.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}
}