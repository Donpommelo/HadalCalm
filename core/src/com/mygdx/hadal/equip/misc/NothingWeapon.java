package com.mygdx.hadal.equip.misc;

import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.entities.Schmuck;

public class NothingWeapon extends MeleeWeapon {

	private static final float swingCd = 1.0f;
	private static final float windup = 1.0f;
	
	private static final Sprite weaponSprite = Sprite.MT_DEFAULT;
	private static final Sprite eventSprite = Sprite.BASE;
	
	public NothingWeapon(Schmuck user) {
		super(user, swingCd, windup, weaponSprite, eventSprite);
	}
}
