package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Schmuck;

/**
 * @author Difield Droothbrush
 */
public class NothingActive extends ActiveItem {

	private static final float MAX_CHARGE = 0.0f;
	
	public NothingActive(Schmuck user) {
		super(user, MAX_CHARGE);
	}
}
