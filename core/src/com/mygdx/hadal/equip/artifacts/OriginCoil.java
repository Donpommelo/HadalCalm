package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.constants.Stats;

public class OriginCoil extends Artifact {

	private static final int slotCost = 1;
	
	private static final float slow = 0.02f;
	private static final float trackBoost = 10.0f;
	private static final float boostMultiplier = 1.2f;
	private static final float delay = 1.0f;

	private static final float bonusClipSize = 0.25f;
	
	private static final float boostInterval = 1 / 60f;
	
	public OriginCoil() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatChangeStatus(state, Stats.RANGED_CLIP, bonusClipSize, p) {

			private final Vector2 startVelo = new Vector2();
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (!hbox.isEffectsMovement()) { return; }

				startVelo.set(hbox.getStartVelo()).scl(boostMultiplier);
				hbox.getStartVelo().scl(slow);
				hbox.setLifeSpan(hbox.getLifeSpan() + delay);
				hbox.addStrategy(new HitboxStrategy(state, hbox, p) {
					
					float controllerCount;
					private float count = delay;
					@Override
					public void create() {
						hbox.getBody().setGravityScale(0.0f);
					}

					private final Vector2 lastPush = new Vector2();
					private boolean activated;
					@Override
					public void controller(float delta) {
						count -= delta;
						controllerCount += delta;
						if (!activated) {
							while (controllerCount >= boostInterval) {
								if (count > 0) {
									if (p.getPlayer().getMouse() != null) {
										lastPush.set(p.getPlayer().getMouse().getPosition()).sub(hbox.getPosition()).nor().scl(hbox.getMass() * trackBoost);
										lastPush.add(hbox.getStartVelo().nor().scl(hbox.getMass() * trackBoost));
										hbox.applyForceToCenter(lastPush);
									}
								} else {
									hbox.getBody().setLinearVelocity(lastPush.nor().scl(startVelo.len()));
									activated = true;
								}
								controllerCount -= boostInterval;
							}
						}
					}
				});
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (bonusClipSize * 100))};
	}
}
