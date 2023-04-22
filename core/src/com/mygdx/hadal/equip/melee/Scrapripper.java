package com.mygdx.hadal.equip.melee;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.Scraprip;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

public class Scrapripper extends MeleeWeapon {

	private static final float SHOOT_CD = 0.45f;
	private static final float BASE_DAMAGE = Scraprip.BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_SCRAPRIPPER;
	private static final Sprite EVENT_SPRITE = Sprite.P_SCRAPRIPPER;

	public Scrapripper(Player user) {
		super(user, SHOOT_CD, WEAPON_SPRITE, EVENT_SPRITE);
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.SCRAPRIP.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	@Override
	public float getBotRangeMax() { return 6.25f; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(SHOOT_CD)};
	}
}
