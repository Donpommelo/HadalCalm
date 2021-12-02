package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Blinded;
import com.mygdx.hadal.statuses.DamageTypes;
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
	private static final float blindDuration = 4.0f;

	public Flashbang(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.LAUNCHER.playUniversal(state, user.getPlayer().getPixelPosition(), 0.35f, false);
		
		Hitbox hbox = new RangedHitbox(state, user.getPlayer().getProjectileOrigin(weaponVelo, projectileSize.x), projectileSize, lifespan,
				new Vector2(weaponVelo).nor().scl(projectileSpeed), user.getPlayer().getHitboxfilter(), false, false, user.getPlayer(), projSprite);
		hbox.setGravity(1.0f);

		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new DamageStandard(state, hbox, user, baseDamage, knockback, DamageTypes.MAGIC));
		hbox.addStrategy(new ContactWallDie(state, hbox, user));
		hbox.addStrategy(new ContactUnitDie(state, hbox, user));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user) {

			@Override
			public void create() {

				//Set grenade to have constant angular velocity for visual effect.
				hbox.setAngularVelocity(8);
			}

			@Override
			public void die() {
				SoundEffect.FLASHBANG.playUniversal(state, this.hbox.getPixelPosition(), 1.5f, 1.8f, false);

				Hitbox hbox = new Hitbox(state, this.hbox.getPixelPosition(), new Vector2(currentRadius, currentRadius),
						0.4f, new Vector2(0, 0), (short) 0, true, false, user.getPlayer(), Sprite.NOTHING);

				hbox.addStrategy(new ControllerDefault(state, hbox, user));
				hbox.addStrategy(new Static(state, hbox, user));
				hbox.addStrategy(new CreateParticles(state, hbox, user, Particle.EXPLOSION, 0.0f, 0.2f).setParticleSize(25));
				hbox.addStrategy(new HitboxStrategy(state, hbox, user) {

					@Override
					public void onHit(HadalData fixB) {
						if (fixB instanceof BodyData bodyData) {
							bodyData.addStatus(new Blinded(state, blindDuration, creator, bodyData));
						}
					}
				});
			}
		}); 
	}
}
