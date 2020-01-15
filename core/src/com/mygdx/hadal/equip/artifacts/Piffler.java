package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.Spread;
import com.mygdx.hadal.utils.Stats;

public class Piffler extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static int spread = 30;
	private final static float projSpeedReduction = -0.6f;
	private final static float projLifeReduction = -0.6f;
	private final static float projRecoilReduction = -0.75f;
	private final static float damageReduction = -0.25f;
	private final static float bonusAtkSpd = 0.6f;
	private final static float bonusClip = 1.0f;

	public Piffler() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.RANGED_PROJ_SPD, projSpeedReduction, b),
				new StatChangeStatus(state, Stats.RANGED_ATK_SPD, bonusAtkSpd, b),
				new StatChangeStatus(state, Stats.RANGED_CLIP, bonusClip, b),
				new StatChangeStatus(state, Stats.RANGED_PROJ_LIFESPAN, projLifeReduction, b),
				new StatChangeStatus(state, Stats.RANGED_RECOIL, projRecoilReduction, b),
				new StatChangeStatus(state, Stats.DAMAGE_AMP, damageReduction, b),
				new Status(state, b) {
			
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				hbox.addStrategy(new Spread(state, hbox, inflicted, spread));
			}
			
			@Override
			public void onReload(Equipable tool) {
				if (this.inflicted instanceof PlayerBodyData) {
					if (((PlayerBodyData)this.inflicted).getCurrentTool() instanceof RangedWeapon) {
						RangedWeapon weapon = (RangedWeapon)((PlayerBodyData)this.inflicted).getCurrentTool();
						weapon.gainAmmo(1.0f);
					}
				}
			}
		});
		return enchantment;
	}
}
