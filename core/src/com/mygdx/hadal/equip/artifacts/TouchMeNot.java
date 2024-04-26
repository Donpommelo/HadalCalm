package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;

public class TouchMeNot extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float BASE_DAMAGE = 10.0f;
	private static final float KNOCKBACK = 50.0f;
	private static final float SIZE = 60.0f;

	public TouchMeNot() {
		super(SLOT_COST);
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

					hbox = new Hitbox(state, p.getSchmuck().getPixelPosition(), new Vector2(p.getSchmuck().getSize()).add(SIZE, SIZE),
							0, new Vector2(), p.getSchmuck().getHitboxFilter(), true, false,
							p.getSchmuck(), Sprite.NOTHING);
					hbox.makeUnreflectable();
					hbox.setPassability((short) (BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY));

					hbox.addStrategy(new FixedToEntity(state, hbox, p, new Vector2(), new Vector2()));
					hbox.addStrategy(new DamageStandard(state, hbox, p, BASE_DAMAGE, 0, DamageSource.BRIGGLES_BLADED_BOOT,
							DamageTag.WHACKING, DamageTag.MELEE)
							.setStaticKnockback(true).setRepeatable(true));
					hbox.addStrategy(new HitboxStrategy(state, hbox, p) {

						@Override
						public void onHit(HadalData fixB, Body body) {
							if (fixB != null) {
								if (UserDataType.BODY.equals(fixB.getType())) {
									if (fixB.getEntity().isAlive()) {
										Vector2 newVelo = new Vector2(fixB.getEntity().getPosition()).sub(p.getSchmuck().getPosition());
										fixB.getEntity().setLinearVelocity(newVelo.nor().scl(KNOCKBACK));

										SoundEffect.SPRING.playSourced(state, p.getSchmuck().getPixelPosition(), 0.2f);
									}
								}
							}
						}

						@Override
						public void die() {
							if (hbox.getState().isServer()) {
								hbox.queueDeletion();
							} else {
								((ClientState) state).removeEntity(hbox.getEntityID());
							}
						}
					});

					if (!state.isServer()) {
						((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ClientState.ObjectLayer.HBOX);
					}
				}
            }
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE)};
	}
}
