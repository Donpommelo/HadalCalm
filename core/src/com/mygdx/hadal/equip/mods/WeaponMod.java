package com.mygdx.hadal.equip.mods;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.save.UnlockManager.ModTag;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.WeaponModifier;
import com.mygdx.hadal.utils.Stats;

public enum WeaponMod {
	
	NOTHING("Nothing", "", 1) {
		@Override
		public Status retrieveMod(BodyData b, PlayState state) {
			return new StatChangeStatus(state, Stats.DAMAGE_AMP, 0.0f, b);
		}
	},
	
	PLUS_DAMAGE("+Damage", "", 1, ModTag.RANDOM_POOL) {
		@Override
		public Status retrieveMod(BodyData b, PlayState state) {
			return new StatChangeStatus(state, Stats.DAMAGE_AMP, 0.25f, b);
		}
	},
	
	PLUS_ATK_SPD("+Attack Speed", "", 1, ModTag.RANDOM_POOL) {
		@Override
		public Status retrieveMod(BodyData b, PlayState state) {
			return new StatChangeStatus(state, Stats.RANGED_ATK_SPD, 0.25f, b);
		}
	},
	
	PLUS_RLD_SPD("+Reload Speed", "", 1, ModTag.RANDOM_POOL) {
		@Override
		public Status retrieveMod(BodyData b, PlayState state) {
			return new StatChangeStatus(state, Stats.RANGED_RELOAD, 0.25f, b);
		}
	},
	
	PLUS_ClIP("+Clip Size", "", 1, ModTag.RANDOM_POOL) {
		@Override
		public Status retrieveMod(BodyData b, PlayState state) {
			return new StatChangeStatus(state, Stats.RANGED_CLIP, 0.25f, b);
		}
	},
	
	PLUS_AMMO("+Ammo Capacity", "", 1, ModTag.RANDOM_POOL) {
		@Override
		public Status retrieveMod(BodyData b, PlayState state) {
			return new StatChangeStatus(state, Stats.AMMO_CAPACITY, 0.25f, b);
		}
	},
	
	PLUS_KB("+Knockback", "", 1, ModTag.RANDOM_POOL) {
		@Override
		public Status retrieveMod(BodyData b, PlayState state) {
			return new StatChangeStatus(state, Stats.KNOCKBACK_AMP, 0.25f, b);
		}
	},
	
	PLUS_RUN_SPD("+Run Speed", "", 1, ModTag.RANDOM_POOL) {
		@Override
		public Status retrieveMod(BodyData b, PlayState state) {
			return new StatChangeStatus(state, Stats.GROUND_SPD, 0.2f, b);
		}
	},
	
	PLUS_DEF("+Defense", "", 1, ModTag.RANDOM_POOL) {
		@Override
		public Status retrieveMod(BodyData b, PlayState state) {
			return new StatChangeStatus(state, Stats.DAMAGE_RES, 0.2f, b);
		}
	},
	
	PLUS_PROJ_SIZE("+Projectile Size", "", 1, ModTag.RANDOM_POOL) {
		@Override
		public Status retrieveMod(BodyData b, PlayState state) {
			return new StatChangeStatus(state, Stats.RANGED_PROJ_SIZE, 0.5f, b);
		}
	},
	
	PLUS_PROJ_SPD("+Projectile Speed", "", 1, ModTag.RANDOM_POOL) {
		@Override
		public Status retrieveMod(BodyData b, PlayState state) {
			return new StatChangeStatus(state, Stats.RANGED_PROJ_SPD, 0.25f, b);
		}
	},
	
	PLUS_PROJ_RNG("+Range", "", 1, ModTag.RANDOM_POOL) {
		@Override
		public Status retrieveMod(BodyData b, PlayState state) {
			return new StatChangeStatus(state, Stats.RANGED_PROJ_LIFESPAN, 0.25f, b);
		}
	},
	
	PLUS_PROJ_PIERCE("+Pierce", "", 1, ModTag.RANDOM_POOL) {
		@Override
		public Status retrieveMod(BodyData b, PlayState state) {
			return new StatChangeStatus(state, Stats.RANGED_PROJ_DURABILITY, 1.0f, b);
		}
	},	
	
	;
	
	private ModTag[] tags;
	private String name, descr;
	private int weight;
	
	WeaponMod(String name, String descr, int weight, ModTag... tags) {
		this.name = name;
		this.descr = descr;
		this.weight = weight;
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
	
	public String getName() { return name; }

	public String getDescr() { return descr; }

	public int getWeight() { return weight; }

	public Status retrieveMod(BodyData b, PlayState state) { return null; }
	
	public void acquireMod(BodyData b, PlayState state, Equipable tool) {
		WeaponModifier newMod = new WeaponModifier(state, b, tool, this, retrieveMod(b, state));
		tool.getWeaponMods().add(newMod);
		b.addStatus(newMod);
	}
}
