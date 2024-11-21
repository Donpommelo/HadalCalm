package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;

public class BrigglesBladedBoot extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float BASE_DAMAGE = 80.0f;
	private static final float KNOCKBACK = 15.0f;

	private static final float RECOIL = 30.0f;

	private static final Vector2 SIZE = new Vector2(28, 10);
	private static final Vector2 POSITION = new Vector2(0, -1.5f);
	
	public BrigglesBladedBoot() {
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
					
					hbox = new Hitbox(state, p.getSchmuck().getPixelPosition(), SIZE, 0, new Vector2(),
							p.getSchmuck().getHitboxFilter(), true, false, p.getSchmuck(), Sprite.NOTHING);
					hbox.makeUnreflectable();
					hbox.setPassability((short) (BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY));
					
					hbox.addStrategy(new FixedToEntity(state, hbox, p, new Vector2(), POSITION));
					hbox.addStrategy(new DamageStandard(state, hbox, p, BASE_DAMAGE, KNOCKBACK, DamageSource.BRIGGLES_BLADED_BOOT,
							DamageTag.WHACKING, DamageTag.MELEE)
						.setStaticKnockback(true).setRepeatable(true));
					hbox.addStrategy(new HitboxStrategy(state, hbox, p) {
						
						@Override
						public void onHit(HadalData fixB, Body body) {
							SoundManager.play(state, new SoundLoad(SoundEffect.KICK1)
									.setVolume(0.3f)
									.setPosition(p.getSchmuck().getPixelPosition()));

							p.getSchmuck().pushMomentumMitigation(0, RECOIL);
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
						((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ObjectLayer.HBOX);
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
