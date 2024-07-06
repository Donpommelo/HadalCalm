package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class AnchorTalisman extends Artifact {

	private static final int SLOT_COST = 2;

	private static final int ARMOR_AMOUNT = 2;
	
	public AnchorTalisman() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public int onCalcArmorReceive(int armor, float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (p.getPlayer().getGroundedHelper().isGrounded() && damage > 0) {
					p.getPlayer().getArtifactIconHelper().addArtifactFlash(UnlockArtifact.ANCHOR_AMULET);

					return armor + ARMOR_AMOUNT;
				}
				return armor;
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf(ARMOR_AMOUNT)};
	}
}
