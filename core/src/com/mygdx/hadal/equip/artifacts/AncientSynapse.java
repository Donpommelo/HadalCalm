package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.schmucks.entities.helpers.SpecialHpHelper;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.constants.StatusPriority.PRIORITY_LAST;

public class AncientSynapse extends Artifact {

	private static final int SLOT_COST = 2;

	public AncientSynapse() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				p.getPlayer().getSpecialHpHelper().addConditionalHp(damage, DamageSource.ANCIENT_SYNAPSE);
				return 0;
			}
		}.setPriority(PRIORITY_LAST);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) SpecialHpHelper.BASE_DEGEN),
				String.valueOf((int) (SpecialHpHelper.DEGEN * 100))};
	}
}
