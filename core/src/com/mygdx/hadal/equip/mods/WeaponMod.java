package com.mygdx.hadal.equip.mods;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.save.UnlockManager.ModTag;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.WeaponModifier;

public enum WeaponMod {

	PLUS_DAMAGE(ModTag.RANDOM_POOL) {
		@Override
		public Status retrieveMod(BodyData b, PlayState state) {
			return new StatChangeStatus(state, 26, 0.25f, b);
		}
	},
	
	PLUS_ATK_SPD(ModTag.RANDOM_POOL) {
		@Override
		public Status retrieveMod(BodyData b, PlayState state) {
			return new StatChangeStatus(state, 27, 0.25f, b);
		}
	},
	
	PLUS_RLD_SPD(ModTag.RANDOM_POOL) {
		@Override
		public Status retrieveMod(BodyData b, PlayState state) {
			return new StatChangeStatus(state, 28, 0.25f, b);
		}
	},
	
	PLUS_ClIP(ModTag.RANDOM_POOL) {
		@Override
		public Status retrieveMod(BodyData b, PlayState state) {
			return new StatChangeStatus(state, 29, 0.25f, b);
		}
	},
	
	PLUS_KB(ModTag.RANDOM_POOL) {
		@Override
		public Status retrieveMod(BodyData b, PlayState state) {
			return new StatChangeStatus(state, 23, 0.2f, b);
		}
	}
	
	;
	
	
	;
	
	private ModTag[] tags;
	
	WeaponMod(ModTag... tags) {
		this.tags = tags;
	}
	
	public static Array<WeaponMod> getUnlocks(ModTag... tags) {
		Array<WeaponMod> items = new Array<WeaponMod>();
		
		for (WeaponMod u : WeaponMod.values()) {
			boolean get = false;
			
			for (int i = 0; i < tags.length; i++) {
				for (int j = 0; j < u.tags.length; j++) {
					if (tags[i].equals(u.tags[j])) {
						get = true;
					}
				}
			}
			
			if (get) {
				items.add(u);
			}
		}
		
		return items;
	}
	
	public Status retrieveMod(BodyData b, PlayState state) {
		return null;
	}
	
	public void acquireMod(BodyData b, PlayState state, Equipable tool) {
		b.addStatus(new WeaponModifier(state, b, tool, retrieveMod(b, state)));
	}
}
