package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class OriginCoil extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float slow = 0.02f;
	private final static float boost = 75.0f;
	private final static float delay = 0.25f;
	
	public OriginCoil() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.RANGED_PROJ_LIFESPAN, 1.0f, b),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, 0.6f, b),
				new Status(state, b) {

			@Override
			public void onHitboxCreation(Hitbox hbox) {
				
				hbox.getStartVelo().scl(slow);
				hbox.addStrategy(new HitboxStrategy(state, hbox, inflicted) {
					
					float controllerCount = 0;
					private float count = delay;
					
					@Override
					public void create() {
						hbox.getBody().setGravityScale(0.0f);
					}
					
					@Override
					public void controller(float delta) {
						if (count > 0) {
							count-=delta;
						} else {
							controllerCount+=delta;

							if (controllerCount >= 1/60f) {
								hbox.applyForceToCenter(hbox.getStartVelo().nor().scl(hbox.getMass() * boost));
								controllerCount = 0;
							}
						}
					}
				});
			}
		});
		return enchantment;
	}
}
