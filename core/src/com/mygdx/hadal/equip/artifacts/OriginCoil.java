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

	private final static String name = "Origin Coil";
	private final static String descr = "Propulsive Projectiles, +Reload Speed";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final static float slow = 0.02f;
	private final static float boost = 75.0f;
	private final static float delay = 0.25f;
	
	public OriginCoil() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, Stats.RANGED_PROJ_LIFESPAN, 1.0f, b),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, 0.50f, b),
				new Status(state, name, descr, b) {

			@Override
			public void onHitboxCreation(Hitbox hbox) {
				hbox.setGravity(0.0f);
				hbox.setStartVelo(hbox.getStartVelo().scl(slow));
				hbox.addStrategy(new HitboxStrategy(state, hbox, inflicted) {
					
					float controllerCount = 0;
					private float count = delay;
					
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
