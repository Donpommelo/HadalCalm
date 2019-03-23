package com.mygdx.hadal.equip.artifacts;

import java.util.concurrent.ThreadLocalRandom;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class Piffler extends Artifact {

	private final static String name = "Piffler";
	private final static String descr = "Piffling Projectiles";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final static int spread = 30;

	public Piffler() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, Stats.RANGED_PROJ_SPD, -0.5f, b),
				new StatChangeStatus(state, Stats.RANGED_ATK_SPD, 0.4f, b),
				new StatChangeStatus(state, Stats.RANGED_CLIP, 1.0f, b),
				new StatChangeStatus(state, Stats.RANGED_PROJ_DURA, -0.6f, b),
				new StatChangeStatus(state, Stats.RANGED_RECOIL, -0.8f, b),
				new Status(state, name, descr, b) {
			
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				float newDegrees = (float) (hbox.getStartVelo().angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
				hbox.setStartVelo(hbox.getStartVelo().setAngle(newDegrees));
			}
			
			@Override
			public void onReload(Equipable tool) {
				if (this.inflicted instanceof PlayerBodyData) {
					if (((PlayerBodyData)this.inflicted).getCurrentTool() instanceof RangedWeapon) {
						RangedWeapon weapon = (RangedWeapon)((PlayerBodyData)this.inflicted).getCurrentTool();
						weapon.gainClip((int)(weapon.getClipSize()));
					}
				}
			}
		});
		return enchantment;
	}
}
