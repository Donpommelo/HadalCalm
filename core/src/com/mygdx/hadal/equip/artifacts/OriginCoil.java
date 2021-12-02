package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.utils.Stats;

public class OriginCoil extends Artifact {

	private static final int slotCost = 1;
	
	private static final float slow = 0.02f;
	private static final float boost = 75.0f;
	private static final float delay = 0.25f;
	
	private static final float bonusProjLife = 1.0f;
	private static final float bonusReloadSpd = 0.6f;
	
	private static final float boostInterval = 1 / 60f;
	
	public OriginCoil() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.RANGED_PROJ_LIFESPAN, bonusProjLife, p),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, bonusReloadSpd, p),
				new Status(state, p) {

			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (!hbox.isEffectsMovement()) { return; }
				
				hbox.getStartVelo().scl(slow);
				hbox.addStrategy(new HitboxStrategy(state, hbox, p) {
					
					float controllerCount;
					private float count = delay;
					@Override
					public void create() {
						hbox.getBody().setGravityScale(0.0f);
					}
					
					@Override
					public void controller(float delta) {
						if (count > 0) {
							count -= delta;
						} else {
							controllerCount += delta;
							while (controllerCount >= boostInterval) {
								controllerCount -= boostInterval;
								hbox.applyForceToCenter(hbox.getStartVelo().nor().scl(hbox.getMass() * boost));
							}
						}
					}
				});
			}
		});
	}
}
