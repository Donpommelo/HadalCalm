package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.constants.Stats;

public class YightKite extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final float PROJ_SPD_REDUCTION = -0.5f;
	private static final float BONUS_PROJ_LIFESPAN = 0.5f;
	
	public YightKite() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.RANGED_PROJ_SPD, PROJ_SPD_REDUCTION, p),
				new StatChangeStatus(state, Stats.RANGED_PROJ_LIFESPAN, BONUS_PROJ_LIFESPAN, p),
				new Status(state, p) {
			
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (!hbox.isEffectsMovement()) { return; }

				hbox.addStrategy(new HitboxStrategy(state, hbox, p) {
					
					private final Vector2 playerPos = new Vector2();
					private final Vector2 entityLocation = new Vector2();
					@Override
					public void create() {
						playerPos.set(p.getSchmuck().getPosition());
					}
					
					@Override
					public void controller(float delta) {
						if (p.getSchmuck().getBody() != null) {
							entityLocation.set(p.getSchmuck().getPosition());
							hbox.setTransform(new Vector2(hbox.getPosition()).add(entityLocation).sub(playerPos), hbox.getAngle());
							playerPos.set(entityLocation);
						}
					}
					
				});
			}
		});
	}
}
