package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Blinded;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class Flashbang extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 9.0f;

	private static final float baseDamage = 30.0f;
	private static final float knockback = 0.0f;
	private static final Vector2 projectileSize = new Vector2(34, 64);
	private static final float lifespan = 2.0f;

	private static final float projectileSpeed = 30.0f;

	private static final Sprite projSprite = Sprite.FLASH_GRENADE;

	private static final int currentRadius = 200;
	private static final float blindDuration = 4.5f;
	private static final float flashbangRotationSpeed = 8.0f;

	public Flashbang(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.FLASHBANG.initiateSyncedAttackSingle(state, user.getPlayer(), user.getPlayer().getProjectileOrigin(weaponVelo, projectileSize.x),
				new Vector2(weaponVelo).nor().scl(projectileSpeed));
	}

	public static Hitbox createFlashbang(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.LAUNCHER.playSourced(state, user.getPixelPosition(), 0.35f);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
				false, false, user, projSprite);
		hbox.setGravity(1.0f);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new RotationConstant(state, hbox, user.getBodyData(), flashbangRotationSpeed));

		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback,
				DamageSource.FLASH_BANG, DamageTag.MAGIC));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			@Override
			public void die() {
				SoundEffect.FLASHBANG.playSourced(state, this.hbox.getPixelPosition(), 1.5f, 1.8f);

				Hitbox hbox = new Hitbox(state, this.hbox.getPixelPosition(), new Vector2(currentRadius, currentRadius),
						0.4f, new Vector2(0, 0), (short) 0, true, false, user, Sprite.NOTHING);
				hbox.setSyncDefault(false);

				hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
				hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
				hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.EXPLOSION, 0.0f, 0.2f)
						.setParticleSize(25).setSyncType(SyncType.NOSYNC));

				if (state.isServer()) {
					hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

						@Override
						public void onHit(HadalData fixB) {
							if (fixB instanceof BodyData bodyData) {
								bodyData.addStatus(new Blinded(state, blindDuration, creator, bodyData, true));
							}
						}
					});
				}

				if (!state.isServer()) {
					((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ClientState.ObjectLayer.EFFECT);
				}
			}
		});

		return hbox;
	}
}
