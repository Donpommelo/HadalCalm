package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;
import com.mygdx.hadal.utils.Constants;

public class CarlocsThesis extends Artifact {

	private static final int slotCost = 3;

	private static final Vector2 size = new Vector2(200, 200);
	private static final float redirectAmount = 25;

	public CarlocsThesis() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private Hitbox hbox;
			private boolean created;
			@Override
			public void onRemove() {
				if (hbox != null) {
					if (hbox.isAlive()) {
						hbox.die();
					}
					created = false;
				}
			}

			@Override
			public void timePassing(float delta) {
				if (!created) {
					created = true;
					
					hbox = new Hitbox(state, p.getSchmuck().getPixelPosition(), size, 0, new Vector2(),
							p.getSchmuck().getHitboxfilter(), true, false, p.getSchmuck(), Sprite.NOTHING);
					hbox.setSyncDefault(false);
					hbox.makeUnreflectable();
					hbox.setPassability(Constants.BIT_PROJECTILE);
					
					hbox.addStrategy(new FixedToEntity(state, hbox, p, new Vector2(), new Vector2()));
					hbox.addStrategy(new HitboxStrategy(state, hbox, p) {

						private final Vector2 projectileVelo = new Vector2();
						private final Vector2 toUserCenter = new Vector2();
						@Override
						public void onHit(HadalData fixB) {
							if (fixB.getEntity() instanceof Hitbox deflected) {
								if (deflected.isReflectable()) {
									projectileVelo.set(deflected.getLinearVelocity());
									toUserCenter.set(inflicted.getSchmuck().getPixelPosition()).sub(deflected.getPixelPosition());
									float dist = (toUserCenter.angleDeg() - projectileVelo.angleDeg()) % 360;
									if (2 * dist % 360 - dist > 0) {
										projectileVelo.rotateDeg(-redirectAmount);
									} else {
										projectileVelo.rotateDeg(redirectAmount);
									}
									deflected.setLinearVelocity(projectileVelo);
								}
							}
						}
						
						@Override
						public void die() {
							hbox.queueDeletion();
						}
					});
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) redirectAmount)};
	}
}
