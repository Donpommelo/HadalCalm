package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.utils.Stats;

public class YightKite extends Artifact {

	private static final int slotCost = 2;
	
	private static final float projSpdReduction = -0.5f;
	private static final float bonusProjLifespan = 0.5f;
	
	public YightKite() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.RANGED_PROJ_SPD, projSpdReduction, p),
				new StatChangeStatus(state, Stats.RANGED_PROJ_LIFESPAN, bonusProjLifespan, p),
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
