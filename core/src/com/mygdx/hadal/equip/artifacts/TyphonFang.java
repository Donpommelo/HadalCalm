package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class TyphonFang extends Artifact {

	private static final int SLOT_COST = 2;
	private static final float PLAYER_CLIP_PERCENT = 1.0f;
	private static final int MONSTER_CLIP_AMOUNT = 1;

	public TyphonFang() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p, new Status(state, p) {
			
			@Override
			public void onKill(BodyData vic, DamageSource source) {
				if (p.getPlayer().getEquipHelper().getCurrentTool() instanceof RangedWeapon weapon) {
					SyncedAttack.ARTIFACT_AMMO_ACTIVATE.initiateSyncedAttackNoHbox(state, p.getPlayer(), new Vector2(), true);

					if (vic instanceof PlayerBodyData) {
						weapon.gainClip((int) (weapon.getClipSize() * PLAYER_CLIP_PERCENT));
					} else {
						weapon.gainClip(MONSTER_CLIP_AMOUNT);
					}
				}
			}
		}).setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (PLAYER_CLIP_PERCENT * 100)),
				String.valueOf(MONSTER_CLIP_AMOUNT)};
	}
}
