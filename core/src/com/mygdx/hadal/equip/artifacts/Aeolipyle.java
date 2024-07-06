package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.RemoveStrategy;
import com.mygdx.hadal.strategies.hitbox.RotationConstant;

public class Aeolipyle extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float ROTATION_SPEED = 20.0f;

	public Aeolipyle() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (hbox.isEffectsMovement()) {
					hbox.addStrategy(new RemoveStrategy(state, hbox, p, AdjustAngle.class));
					hbox.addStrategy(new RotationConstant(state, hbox, p, ROTATION_SPEED));
				}
			}
		};
	}
}
