package com.mygdx.hadal.equip.misc;

import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.entities.Player;

public class NothingWeapon extends MeleeWeapon {

	private static final float SWING_CD = 1.0f;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_DEFAULT;
	private static final Sprite EVENT_SPRITE = Sprite.BASE;
	
	public NothingWeapon(Player user) {
		super(user, SWING_CD, WEAPON_SPRITE, EVENT_SPRITE);
	}
}
