package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.*;

public class Puffballer extends RangedWeapon {

	private static final int clipSize = 1;
	private static final int ammoSize = 25;
	private static final float shootCd = 0.8f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 1.5f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 36.0f;
	private static final float recoil = 2.5f;
	private static final float knockback = 5.0f;
	private static final float projectileSpeed = 30.0f;
	private static final Vector2 projectileSize = new Vector2(60, 60);
	private static final float lifespan = 5.0f;

	private static final Sprite projSprite = Sprite.SPORE_CLUSTER_YELLOW;
	private static final Sprite weaponSprite = Sprite.MT_TORPEDO;
	private static final Sprite eventSprite = Sprite.P_TORPEDO;

	private static final float sporeFragLifespan = 5.0f;
	private static final float sporeFragDamage = 12.0f;
	private static final float sporeFragKB = 8.0f;
	private static final Vector2 sporeFragSize = new Vector2(30, 30);

	private static final int sporeFragNumber = 12;
	private static final float fragSpeed = 6.0f;
	private static final float fragVeloSpread = 1.2f;
	private static final float fragDampen = 1.2f;

	private static final float deathDelay = 0.5f;

	//list of hitboxes created
	private final Queue<Hitbox> puffballs = new Queue<>();

	private boolean held = false;

	public Puffballer(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}

	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, shooter, faction, mouseLocation);

		if (reloading || getClipLeft() == 0) { return; }
		if (!held) {
			held = true;
			super.execute(state, shooter);
		}
	}

	@Override
	public void execute(PlayState state, BodyData shooter) {}

	@Override
	public void release(PlayState state, BodyData bodyData) {
		held = false;

		//upon releasing mouse, detonate all laid bombs
		for (Hitbox puffball : puffballs) {
			if (puffball.isAlive()) {
				puffball.die();
			}
		}
		puffballs.clear();
	}

	private final Vector2 newVelocity = new Vector2();
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		float[] fragAngles = new float[sporeFragNumber * 2];
		for (int i = 0; i < sporeFragNumber; i++) {
			newVelocity.setToRandomDirection().scl(fragSpeed).scl(MathUtils.random() * fragVeloSpread + 1 - fragVeloSpread / 2);
			fragAngles[2 * i] = newVelocity.x;
			fragAngles[2 * i + 1] = newVelocity.y;
		}

		Hitbox hbox = SyncedAttack.PUFFBALL.initiateSyncedAttackSingle(state, user, startPosition, startVelocity, fragAngles);
		puffballs.addLast(hbox);
	}

	public static Hitbox createPuffball(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
		SoundEffect.SPIT.playSourced(state, startPosition, 1.2f, 0.5f);
		user.recoil(startVelocity, recoil);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
				false,true, user, projSprite) {

			private boolean markedForDeath, died;
			private float count;
			@Override
			public void controller(float delta) {
				super.controller(delta);

				count += delta;
				if (count >= deathDelay) {
					if (markedForDeath && !died) {
						died = true;
						createFrags();
						super.die();
					}
				}
			}

			@Override
			public void die() {
				if (count >= deathDelay && !died) {
					died = true;
					createFrags();
					super.die();
				} else {
					markedForDeath = true;
				}
			}

			private final Vector2 newVelocity = new Vector2();
			private void createFrags() {
				SoundEffect.EXPLOSION_FUN.playSourced(state, getPixelPosition(), 1.0f, 0.6f);

				for (int i = 0; i < sporeFragNumber; i++) {
					if (extraFields.length > i * 2 + 1) {
						newVelocity.set(extraFields[i * 2], extraFields[i * 2 + 1]);

						RangedHitbox frag = new RangedHitbox(state, getPixelPosition(), new Vector2(sporeFragSize), sporeFragLifespan,
								new Vector2(newVelocity), user.getHitboxfilter(), false, false, user, Sprite.SPORE_YELLOW) {

							@Override
							public void create() {
								super.create();
								getBody().setLinearDamping(fragDampen);
							}
						};
						frag.setRestitution(1.0f);
						frag.setSyncDefault(false);

						frag.addStrategy(new ControllerDefault(state, frag, user.getBodyData()));
						frag.addStrategy(new DamageStandard(state, frag, user.getBodyData(), sporeFragDamage, sporeFragKB, DamageTypes.RANGED).setStaticKnockback(true));
						frag.addStrategy(new ContactUnitLoseDurability(state, frag, user.getBodyData()));

						if (!state.isServer()) {
							((ClientState) state).addEntity(frag.getEntityID(), frag, false, ClientState.ObjectLayer.HBOX);
						}
					}
				}
			}
		};
		hbox.setRestitution(1.0f);
		hbox.setDurability(2);
		hbox.setSyncedDelete(true);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.DIATOM_TRAIL_DENSE, 0.0f, 1.0f).setSyncType(SyncType.NOSYNC));

		return hbox;
	}

	@Override
	public void unequip(PlayState state) {
		held = false;
	}
}
