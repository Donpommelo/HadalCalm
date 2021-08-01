package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

import java.util.concurrent.ThreadLocalRandom;

public class Puffballer extends RangedWeapon {

	private static final int clipSize = 1;
	private static final int ammoSize = 40;
	private static final float shootCd = 0.5f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 1.5f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 42.0f;
	private static final float recoil = 2.5f;
	private static final float knockback = 5.0f;
	private static final float projectileSpeed = 8.0f;
	private static final float projectileSpeed2 = 24.0f;
	private static final Vector2 projectileSize = new Vector2(40, 40);
	private static final Vector2 projectileSize2 = new Vector2(60, 60);
	private static final float lifespan = 5.0f;

	private static final Sprite projSprite = Sprite.SPORE_CLUSTER_YELLOW;
	private static final Sprite weaponSprite = Sprite.MT_TORPEDO;
	private static final Sprite eventSprite = Sprite.P_TORPEDO;

	private static final float sporeFragLifespan = 2.0f;
	private static final float sporeFragDamage = 10.0f;
	private static final float sporeFragDamage2 = 15.0f;
	private static final float sporeFragKB = 8.0f;
	private static final Vector2 sporeFragSize = new Vector2(16, 16);
	private static final Vector2 sporeFragSize2 = new Vector2(24, 24);

	private static final int sporeFragNumber = 10;
	private static final int sporeSpread = 16;
	private static final float fragSpeed = 8.0f;
	private static final float fragVeloSpread = 0.4f;
	private static final float fragDampen = 2.0f;

	private static final float deathDelay = 0.8f;
	private static final float homePower = 50.0f;

	//list of hitboxes created
	private final Queue<Hitbox> puffballs = new Queue<>();

	public Puffballer(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}

	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, shooter, faction, mouseLocation);

		if (reloading || getClipLeft() == 0) { return; }

		super.execute(state, shooter);
	}

	@Override
	public void execute(PlayState state, BodyData shooter) {}

	@Override
	public void release(PlayState state, BodyData bodyData) {
		//upon releasing mouse, detonate all laid bombs
		for (Hitbox puffball : puffballs) {
			if (puffball.isAlive()) {
				puffball.die();
			}
		}
		puffballs.clear();
	}

	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.SPIT.playUniversal(state, startPosition, 1.2f, 0.5f, false);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, false, true, user, projSprite) {

			private boolean markedForDeath;
			private float count;
			@Override
			public void controller(float delta) {
				super.controller(delta);

				count += delta;
				if (count >= deathDelay) {
					super.die();
					if (markedForDeath) {
						createFrags(state, this, sporeFragSize, sporeFragDamage);
					} else {
						Hitbox hbox2 = new RangedHitbox(state, getPixelPosition(), projectileSize2, lifespan,
								getLinearVelocity().nor().scl(projectileSpeed2), filter,false, true, user, projSprite);

						hbox2.setRestitution(1.0f);

						hbox2.addStrategy(new ControllerDefault(state, hbox2, user.getBodyData()));
						hbox2.addStrategy(new AdjustAngle(state, hbox2, user.getBodyData()));
						hbox2.addStrategy(new DamageStandard(state, hbox2, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
						hbox2.addStrategy(new CreateParticles(state, hbox2, user.getBodyData(), Particle.DIATOM_TRAIL_DENSE, 0.0f, 1.0f));
						hbox2.addStrategy(new HitboxStrategy(state, hbox2, user.getBodyData()) {

							@Override
							public void die() {
								createFrags(state, hbox2, sporeFragSize2, sporeFragDamage2);
							}
						});

						puffballs.addLast(hbox2);
					}
				}
			}

			@Override
			public void die() {
				if (count >= deathDelay) {
					createFrags(state, this, sporeFragSize, sporeFragDamage);
					super.die();
				} else {
					markedForDeath = true;
				}
			}
		};
		hbox.setRestitution(1.0f);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.DIATOM_TRAIL_DENSE, 0.0f, 1.0f));
		hbox.addStrategy(new HomingMouse(state, hbox, user.getBodyData(), homePower));

		puffballs.addLast(hbox);
	}

	private final Vector2 newVelocity = new Vector2();
	private void createFrags(PlayState state, Hitbox hbox, Vector2 sporeFragSize, float sporeFragDamage) {
		SoundEffect.EXPLOSION_FUN.playUniversal(state, hbox.getPixelPosition(), 1.0f, 0.6f, false);

		for (int i = 0; i < sporeFragNumber; i++) {
			newVelocity.setToRandomDirection().scl(fragSpeed)
					.scl((ThreadLocalRandom.current().nextFloat() * fragVeloSpread + 1 - fragVeloSpread / 2));

			RangedHitbox frag = new RangedHitbox(state, hbox.getPixelPosition(), new Vector2(sporeFragSize), sporeFragLifespan,
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
}
