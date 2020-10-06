package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.utils.Stats;

public class YightKite extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;
	
	private static final float projSpdReduction = -0.5f;
	private static final float bonusProjLifespan = 0.5f;
	
	public YightKite() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.RANGED_PROJ_SPD, projSpdReduction, b),
				new StatChangeStatus(state, Stats.RANGED_PROJ_LIFESPAN, bonusProjLifespan, b),
				new Status(state, b) {
			
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				
				if (!hbox.isEffectsMovement()) { return; } 

				hbox.addStrategy(new HitboxStrategy(state, hbox, inflicted) {
					
					private final Vector2 playerPos = new Vector2();
					private final Vector2 entityLocation = new Vector2();
					@Override
					public void create() {
						playerPos.set(inflicted.getSchmuck().getPosition());
					}
					
					@Override
					public void controller(float delta) {
						if (inflicted.getSchmuck().getBody() != null) {
							entityLocation.set(inflicted.getSchmuck().getPosition());
							hbox.setTransform(new Vector2(hbox.getPosition()).add(entityLocation).sub(playerPos), hbox.getBody().getAngle());
							playerPos.set(entityLocation);
						}
					}
					
				});
			}
		});
		
		return enchantment;
	}
}
