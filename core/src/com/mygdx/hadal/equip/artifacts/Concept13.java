package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayStateClient;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.DamagePulse;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.OrbitUser;

public class Concept13 extends Artifact {

	private static final int SLOT_COST = 2;

	private static final Vector2 PROJ_SIZE = new Vector2(60, 60);
	private static final float BASE_DAMAGE = 13.0f;
	private static final float KNOCKBACK = 3.0f;

	private static final float PROJ_SPEED = 80.0f;
	private static final float PROJ_RANGE = 4.0f;

	public Concept13() {
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

					hbox = new Hitbox(state, p.getSchmuck().getPixelPosition(), PROJ_SIZE,0, new Vector2(),
							p.getSchmuck().getHitboxFilter(), true, false, p.getSchmuck(), Sprite.EMOTE_DICE);
					hbox.makeUnreflectable();

					hbox.addStrategy(new DamageStandard(state, hbox, p, BASE_DAMAGE, KNOCKBACK, DamageSource.CONCEPT13,
							DamageTag.WHACKING, DamageTag.MELEE)
							.setStaticKnockback(true).setRepeatable(true));
					hbox.addStrategy(new OrbitUser(state, hbox, p, 0, PROJ_RANGE, PROJ_SPEED));

					hbox.addStrategy(new HitboxStrategy(state, hbox, p) {

						@Override
						public void die() {
							if (hbox.getState().isServer()) {
								hbox.queueDeletion();
							} else {
								((PlayStateClient) state).removeEntity(hbox.getEntityID());
							}
						}
					});
					hbox.addStrategy(new DamagePulse(state, hbox, p, PROJ_SIZE, BASE_DAMAGE, KNOCKBACK,
							DamageSource.DIAMOND_CUTTER, DamageTag.MELEE));

					if (!state.isServer()) {
						((PlayStateClient) state).addEntity(hbox.getEntityID(), hbox, false, PlayStateClient.ObjectLayer.HBOX);
					}
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf(BASE_DAMAGE),
				String.valueOf(DamagePulse.PULSE_INTERVAL)};
	}
}
