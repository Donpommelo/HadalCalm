package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.constants.Stats;

public class OriginCoil extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float SLOW = 0.02f;
	private static final float TRACK_BOOST = 10.0f;
	private static final float BOOST_MULTIPLIER = 1.2f;
	private static final float DELAY = 1.0f;

	private static final float BONUS_CLIP_SIZE = 0.25f;
	
	private static final float BOOST_INTERVAL = 1 / 60f;
	
	public OriginCoil() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatChangeStatus(state, Stats.RANGED_CLIP, BONUS_CLIP_SIZE, p) {

			private final Vector2 startVelo = new Vector2();
			@Override
			public void onHitboxCreation(Hitbox hbox) {
				if (!hbox.isEffectsMovement()) { return; }

				hbox.setSynced(true);
				hbox.setSyncedDelete(true);

				startVelo.set(hbox.getStartVelo()).scl(BOOST_MULTIPLIER);
				hbox.getStartVelo().scl(SLOW);
				hbox.setLifeSpan(hbox.getLifeSpan() + DELAY);
				hbox.addStrategy(new HitboxStrategy(state, hbox, p) {
					
					float controllerCount;
					private float count = DELAY;
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
							while (controllerCount >= BOOST_INTERVAL) {
								if (count > 0) {
									lastPush.set(p.getPlayer().getMouseHelper().getPosition()).sub(hbox.getPosition()).nor().scl(hbox.getMass() * TRACK_BOOST);
									lastPush.add(hbox.getStartVelo().nor().scl(hbox.getMass() * TRACK_BOOST));
									hbox.applyForceToCenter(lastPush);
								} else {
									hbox.getBody().setLinearVelocity(lastPush.nor().scl(startVelo.len()));
									activated = true;
								}
								controllerCount -= BOOST_INTERVAL;
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
				String.valueOf((int) (BONUS_CLIP_SIZE * 100))};
	}
}
