package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

import java.util.concurrent.ThreadLocalRandom;

public class Puffballer extends RangedWeapon {

	private static final int clipSize = 4;
	private static final int ammoSize = 40;
	private static final float shootCd = 0.5f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 1.4f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 26.0f;
	private static final float recoil = 2.5f;
	private static final float knockback = 5.0f;
	private static final float projectileSpeed = 27.0f;
	private static final Vector2 projectileSize = new Vector2(40, 40);
	private static final float lifespan = 3.0f;

	private static final Sprite projSprite = Sprite.SPORE_CLUSTER_YELLOW;
	private static final Sprite weaponSprite = Sprite.MT_TORPEDO;
	private static final Sprite eventSprite = Sprite.P_TORPEDO;

	private static final float sporeFragLifespan = 3.0f;
	private static final float sporeFragDamage = 10.0f;
	private static final float sporeFragKB = 8.0f;
	private static final Vector2 sporeFragSize = new Vector2(15, 15);

	private static final int sporeFragNumber = 10;
	private static final int sporeSpread = 16;
	private static final float fragSpeed = 35.0f;
	private static final float fragVeloSpread = 0.4f;
	private static final float fragSizeSpread = 0.25f;
	private static final float fragDampen = 2.0f;

	public Puffballer(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.SPIT.playUniversal(state, startPosition, 1.2f, 0.5f, false);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, false, true, user, projSprite);
		hbox.setRestitution(1.0f);
		final Vector2 endLocation = new Vector2(this.mouseLocation);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.DIATOM_TRAIL_DENSE, 0.0f, 1.0f));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			private float distance;
			@Override
			public void create() {
				//keep track of the coil's travel distance
				distance = hbox.getPixelPosition().dst(endLocation) - projectileSize.x;
				lastPosition.set(hbox.getPixelPosition());
			}

			private final Vector2 lastPosition = new Vector2();
			private final Vector2 entityLocation = new Vector2();
			private float displacement;
			@Override
			public void controller(float delta) {
				entityLocation.set(hbox.getPixelPosition());

				//after moving distance equal to a vine, the hbox spawns a vine with random sprite
				if (displacement > distance) {
					hbox.die();
				}

				displacement += lastPosition.dst(entityLocation);
				lastPosition.set(entityLocation);
			}

			private final Vector2 newVelocity = new Vector2();
			private final Vector2 newSize = new Vector2();
			@Override
			public void die() {
				SoundEffect.EXPLOSION_FUN.playUniversal(state, hbox.getPixelPosition(), 1.0f, 0.6f, false);

				for (int i = 0; i < sporeFragNumber; i++) {
					newVelocity.set(hbox.getLinearVelocity()).nor().scl(fragSpeed).scl((ThreadLocalRandom.current().nextFloat() * fragVeloSpread + 1 - fragVeloSpread / 2));
					newSize.set(sporeFragSize).scl((ThreadLocalRandom.current().nextFloat() * fragSizeSpread + 1 - fragSizeSpread / 2));

					RangedHitbox frag = new RangedHitbox(state, hbox.getPixelPosition(), new Vector2(newSize), sporeFragLifespan,
						new Vector2(newVelocity), user.getHitboxfilter(), false, false, user, Sprite.SPORE_MILD) {

						@Override
						public void create() {
							super.create();
							getBody().setLinearDamping(fragDampen);
						}
					};
					frag.setRestitution(1.0f);

					frag.addStrategy(new ControllerDefault(state, frag, user.getBodyData()));
					frag.addStrategy(new DamageStandard(state, frag, user.getBodyData(), sporeFragDamage, sporeFragKB, DamageTypes.RANGED).setStaticKnockback(true));
					frag.addStrategy(new ContactUnitLoseDurability(state, frag, user.getBodyData()));
					frag.addStrategy(new Spread(state, frag, user.getBodyData(), sporeSpread));
				}
			}
		});
	}
}
