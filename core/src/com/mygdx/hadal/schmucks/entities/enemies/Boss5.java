package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.enemy.CreateMultiplayerHpScaling;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;

import static com.mygdx.hadal.constants.Constants.PPM;

/**
 * This is a boss in the game
 */
public class Boss5 extends EnemyFloating {

    private static final float aiAttackCd = 2.1f;
    private static final float aiAttackCd2 = 1.6f;

    private static final int scrapDrop = 15;

	private static final int width = 200;
	private static final int height = 200;

	private static final int hbWidth = 200;
	private static final int hbHeight = 200;

	private static final float scale = 1.0f;

	private static final int hp = 8000;

	private final int phase = 1;
	private static final float phaseThreshold2 = 0.0f;

	private final Animation<TextureRegion> coreSprite, bodySprite;
	private final TextureRegion crownSprite;
	private static final float crownWidth = 89;
	private static final float crownHeight = 84;
	private static final float crownOffsetX = 0.0f;
	private static final float crownOffsetY= 110.0f;

	public Boss5(PlayState state, Vector2 startPos, short filter) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hbWidth, hbHeight).scl(scale), Sprite.NOTHING, EnemyType.BOSS5, filter, hp, aiAttackCd, scrapDrop);
		this.coreSprite = new Animation<>(PlayState.SPRITE_ANIMATION_SPEED_FAST, Sprite.NEPTUNE_KING_CORE.getFrames());
		this.bodySprite = new Animation<>(PlayState.SPRITE_ANIMATION_SPEED_FAST, Sprite.NEPTUNE_KING_BODY.getFrames());
		this.crownSprite = Sprite.NEPTUNE_KING_CROWN.getFrame();
		addStrategy(new CreateMultiplayerHpScaling(state, this, 1400));

		if (state.isServer()) {
			new ParticleEntity(state, this, Particle.TYRRAZZA_TRAIL, 1.0f, 0.0f, true, SyncType.CREATESYNC).setScale(2.0f);
		}
	}

	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.KNOCKBACK_RES, 1.0f, getBodyData()));
	}

	@Override
	public void render(SpriteBatch batch, Vector2 entityLocation) {
		batch.draw(coreSprite.getKeyFrame(animationTime, true),
			entityLocation.x - size.x / 2,entityLocation.y - size.y / 2,
			size.x / 2, size.y / 2,
			size.x, size.y, 1, 1, 0);

		batch.draw(bodySprite.getKeyFrame(animationTime, true),
			entityLocation.x - size.x / 2,entityLocation.y - size.y / 2,
			size.x / 2, size.y / 2,
			size.x, size.y, 1, 1, 0);

		batch.draw(crownSprite,
			entityLocation.x - crownWidth / 2 + crownOffsetX,entityLocation.y - crownHeight / 2 + crownOffsetY,
			crownWidth / 2, crownHeight / 2,
			crownWidth, crownHeight, 1, 1, 0);
	}

	private int attackNum;
	@Override
	public void attackInitiate() {
		attackNum++;
		if (phase == 1) {
			phase1Attack();
		}
	}
	
	private static final int phase1NumAttacks = 4;

	//these lists are used to make the boss perform all attacks in its pool before repeating any
	private final IntArray attacks1 = new IntArray();
	private final IntArray attacks2 = new IntArray();
	private void phase1Attack() {
		if (attacks1.isEmpty()) {
			for (int i = 0; i < phase1NumAttacks; i++) {
				attacks1.add(i);
			}
		}
		if (attacks2.isEmpty()) {
			for (int i = 0; i < phase1NumAttacks; i++) {
				attacks2.add(i);
			}
		}

		if (attackNum % 2 == 0) {
			int nextAttack = attacks1.removeIndex(MathUtils.random(attacks1.size - 1));
			switch (nextAttack) {
				case 0 -> orbitalCharge();
				case 1 -> vineLash();
				case 2 -> scytheAttack();
				case 3 -> spreadingShadow();
			}

		} else {
			int nextAttack = attacks2.removeIndex(MathUtils.random(attacks2.size - 1));
			switch (nextAttack) {
				case 0 -> tripleRadialBurst();
				case 1 -> sporeBurst();
				case 2 -> seedBomber();
				case 3 -> poisonSpew();
			}
		}
	}
	
	private static final int move1Speed = 30;
	private static final int charge1Speed = 40;
	private static final float moveDurationMax = 5.0f;
	private void tripleRadialBurst() {

		if (MathUtils.randomBoolean()) {
			EnemyUtils.moveToDummy(state, this, "0", move1Speed, moveDurationMax);
			radialBurst("0");
			radialBurst("2");
			radialBurst("1");
			radialBurst("3");
		} else {
			EnemyUtils.moveToDummy(state, this, "4", move1Speed, moveDurationMax);
			radialBurst("4");
			radialBurst("2");
			radialBurst("3");
			radialBurst("1");
		}
	}

	private static final float radialWindup = 0.75f;

	private void radialBurst(String dummyId) {
		EnemyUtils.moveToDummy(state, this, dummyId, charge1Speed, moveDurationMax);
		windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.VIOLET, 40.0f, radialWindup, radialWindup);

		getActions().add(new EnemyAction(this, 0.0f) {

			@Override
			public void execute() {
				SyncedAttack.NEPTUNE_RADIAL.initiateSyncedAttackMulti(state, enemy, new Vector2(), new Vector2[] {}, new Vector2[] {});
			}
		});
	}

	private static final float vineWindup = 1.5f;
	private static final float vineSpeed = 20.0f;
	private static final int vineNum = 8;
	private void vineLash() {
		if (MathUtils.randomBoolean()) {
			EnemyUtils.moveToDummy(state, this, "0", charge1Speed, moveDurationMax);
		} else {
			EnemyUtils.moveToDummy(state, this, "4", charge1Speed, moveDurationMax);

		}
		windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.GREEN, 40.0f, vineWindup, vineWindup);

		getActions().add(new EnemyAction(this, 0.0f) {

			@Override
			public void execute() {
				Vector2 vineVelo = new Vector2(0, vineSpeed).setAngleDeg(attackAngle);
				WeaponUtils.createVine(state, enemy, enemy.getPixelPosition(), vineVelo, vineNum, 2, SyncedAttack.NEPTUNE_VINE);
			}
	 	});
	}

	private static final float seedWindup = 1.5f;
	private static final int seedMoveSpeed = 15;
	private static final int seedNumber = 9;
	private static final float seedInterval = 0.5f;
	private void seedBomber() {

		if (MathUtils.randomBoolean()) {
			EnemyUtils.moveToDummy(state, this, "0", charge1Speed, moveDurationMax);
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.GREEN, 40.0f, seedWindup, seedWindup);
			sowSeed();
			EnemyUtils.moveToDummy(state, this, "4", seedMoveSpeed, moveDurationMax);
		} else {
			EnemyUtils.moveToDummy(state, this, "4", charge1Speed, moveDurationMax);
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.GREEN, 40.0f, seedWindup, seedWindup);
			sowSeed();
			EnemyUtils.moveToDummy(state, this, "0", seedMoveSpeed, moveDurationMax);
		}
	}

	private void sowSeed() {
		getActions().add(new EnemyAction(this, 0.0f) {

			@Override
			public void execute() {
				for (int i = 0; i < seedNumber; i++) {
					getSecondaryActions().add(new EnemyAction(enemy, seedInterval) {

						@Override
						public void execute() {
							SyncedAttack.NEPTUNE_SEED.initiateSyncedAttackSingle(state, enemy, enemy.getPixelPosition(), new Vector2());
						}
					});
				}
			}
		});
	}

	private void poisonSpew() {
		if (MathUtils.randomBoolean()) {
			poisonSpewSingle("1");
			poisonSpewSingle("3");
		} else {
			poisonSpewSingle("3");
			poisonSpewSingle("1");
		}
	}

	private static final float poisonWindup = 0.5f;
	private static final float poisonCooldown = 2.0f;
	private void poisonSpewSingle(String dummyId) {
		EnemyUtils.moveToDummy(state, this, dummyId, move1Speed, moveDurationMax);
		windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.GREEN, 40.0f, poisonWindup, poisonWindup);

		getActions().add(new EnemyAction(this, 0.0f) {

			@Override
			public void execute() {
				SyncedAttack.NEPTUNE_POISON.initiateSyncedAttackSingle(state, enemy, enemy.getPixelPosition(), new Vector2());
			}
		});
		windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.GREEN, 40.0f, poisonCooldown, poisonCooldown);
	}

	private static final float sporeWindup = 1.5f;
	private static final int burstNumber = 4;
	private static final float sporeSpeed = 15.0f;
	private static final float sporeInterval = 1.0f;
	private static final int SPORE_FRAG_NUMBER = 16;
	private static final float FRAG_SPEED = 40.0f;
	private static final float FRAG_VELO_SPREAD = 0.4f;

	private void sporeBurst() {
		EnemyUtils.moveToDummy(state, this, "2", move1Speed, moveDurationMax);
		windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.BLUE, 40.0f, sporeWindup, sporeWindup);

		for (int i = 0; i < burstNumber; i++) {
			getActions().add(new EnemyAction(this, sporeInterval) {

				private final Vector2 newVelocity = new Vector2();
				@Override
				public void execute() {
					float[] fragAngles = new float[SPORE_FRAG_NUMBER * 2];
					for (int i = 0; i < SPORE_FRAG_NUMBER; i++) {
						newVelocity.setToRandomDirection().scl(FRAG_SPEED).scl(
								MathUtils.random() * FRAG_VELO_SPREAD + 1 - FRAG_VELO_SPREAD / 2);
						fragAngles[2 * i] = newVelocity.x;
						fragAngles[2 * i + 1] = newVelocity.y;
					}
					Vector2 startVelo = new Vector2(0, sporeSpeed).setAngleDeg(getAttackAngle());
					SyncedAttack.NEPTUNE_SPOREBURST.initiateSyncedAttackSingle(state, enemy, enemy.getPixelPosition(), startVelo, fragAngles);
				}
			});
		}
	}

	private static final float scytheWindup = 0.5f;
	private static final float scytheCooldown = 2.5f;
	private void scytheAttack() {
		int rand = MathUtils.random(2);
		if (rand == 0) {
			EnemyUtils.moveToDummy(state, this, "1", move1Speed, moveDurationMax);
		} else if (rand == 1) {
			EnemyUtils.moveToDummy(state, this, "2", move1Speed, moveDurationMax);
		} else {
			EnemyUtils.moveToDummy(state, this, "3", move1Speed, moveDurationMax);
		}
		windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.BLUE, 40.0f, scytheWindup, scytheWindup);
		getActions().add(new EnemyAction(this, 0.0f) {

			@Override
			public void execute() {
				SyncedAttack.NEPTUNE_SCYTHE.initiateSyncedAttackMulti(state, enemy, new Vector2(), new Vector2[] {}, new Vector2[] {});
			}
		});
		windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.BLUE, 40.0f, scytheCooldown, scytheCooldown);
	}

	private static final int orbitalChargeSpeed = 30;
	private static final float orbitalWindup = 1.5f;
	private static final float orbitalPause = 0.4f;

	private void orbitalCharge() {
		if (MathUtils.randomBoolean()) {
			EnemyUtils.moveToDummy(state, this, "0", move1Speed, moveDurationMax);
			orbitalChargeSingle();
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.RED, 40.0f, orbitalWindup, orbitalWindup);
			EnemyUtils.moveToDummy(state, this, "1", orbitalChargeSpeed, moveDurationMax);
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.RED, 40.0f, orbitalPause, orbitalPause);
			EnemyUtils.moveToDummy(state, this, "2", orbitalChargeSpeed, moveDurationMax);
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.RED, 40.0f, orbitalPause, orbitalPause);
			EnemyUtils.moveToDummy(state, this, "3", orbitalChargeSpeed, moveDurationMax);
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.RED, 40.0f, orbitalPause, orbitalPause);
			EnemyUtils.moveToDummy(state, this, "4", orbitalChargeSpeed, moveDurationMax);
		} else {
			EnemyUtils.moveToDummy(state, this, "4", move1Speed, moveDurationMax);
			orbitalChargeSingle();
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.RED, 40.0f, orbitalWindup, orbitalWindup);
			EnemyUtils.moveToDummy(state, this, "3", orbitalChargeSpeed, moveDurationMax);
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.RED, 40.0f, orbitalPause, orbitalPause);
			EnemyUtils.moveToDummy(state, this, "2", orbitalChargeSpeed, moveDurationMax);
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.RED, 40.0f, orbitalPause, orbitalPause);
			EnemyUtils.moveToDummy(state, this, "1", orbitalChargeSpeed, moveDurationMax);
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.RED, 40.0f, orbitalPause, orbitalPause);
			EnemyUtils.moveToDummy(state, this, "0", orbitalChargeSpeed, moveDurationMax);
		}
		EnemyUtils.moveToDummy(state, this, "2", orbitalChargeSpeed, moveDurationMax);

	}

	private void orbitalChargeSingle() {
		getActions().add(new EnemyAction(this, 0.0f) {

			@Override
			public void execute() {
				SyncedAttack.NEPTUNE_ORBITAL.initiateSyncedAttackMulti(state, enemy, new Vector2(), new Vector2[] {}, new Vector2[] {});
			}
		});
	}

	private static final int shadowChargeSpeed = 45;
	private static final int shadowNum = 12;
	private static final float shadowInterval = 0.05f;
	private void spreadingShadow() {
		if (MathUtils.randomBoolean()) {
			EnemyUtils.moveToDummy(state, this, "0", move1Speed, moveDurationMax);
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.NOTHING, 40.0f, orbitalWindup, orbitalWindup);
			spreadingShadowSingle("2");
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.NOTHING, 40.0f, orbitalWindup, orbitalWindup);
			spreadingShadowSingle("4");
		} else {
			EnemyUtils.moveToDummy(state, this, "4", move1Speed, moveDurationMax);
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.NOTHING, 40.0f, orbitalWindup, orbitalWindup);
			spreadingShadowSingle("2");
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.NOTHING, 40.0f, orbitalWindup, orbitalWindup);
			spreadingShadowSingle("0");
		}
	}

	private void spreadingShadowSingle(String dummyId) {
		getActions().add(new EnemyAction(this, 0.0f) {

			@Override
			public void execute() {
				for (int i = 0; i < shadowNum; i++) {
					final int me = i;
					getSecondaryActions().add(new EnemyAction(enemy, shadowInterval) {

						@Override
						public void execute() {
							//projectile saves boss velocity
							final Vector2 savedVelo = new Vector2(getLinearVelocity());
							SyncedAttack.NEPTUNE_SHADOW.initiateSyncedAttackSingle(state, enemy, enemy.getPixelPosition(),
									new Vector2(), me, savedVelo.x, savedVelo.y);
						}
					});
				}
			}
		});
		EnemyUtils.moveToDummy(state, this, dummyId, shadowChargeSpeed, moveDurationMax);
	}

	private static final Vector2 windupSize = new Vector2(90, 90);
	private static final float particleLinger = 1.0f;

	private void windupParticle(Particle particle, HadalColor color, float particleScale, float lifespan, float duration) {

		getActions().add(new EnemyAction(this, duration) {

			private final Vector2 addVector = new Vector2();
			@Override
			public void execute() {
				Hitbox hbox1 = new Hitbox(state, getPixelPosition(), windupSize, lifespan, new Vector2(), getHitboxFilter(), true, false, enemy, Sprite.NOTHING);
				hbox1.setSynced(true);
				hbox1.setSyncedDelete(true);

				hbox1.addStrategy(new ControllerDefault(state, hbox1, getBodyData()));
				hbox1.addStrategy(new CreateParticles(state, hbox1, getBodyData(), particle, 0.0f, particleLinger).setParticleColor(color).setParticleSize(particleScale));
				hbox1.addStrategy(new HitboxStrategy(state, hbox1, getBodyData()) {

					@Override
					public void controller(float delta) {
						if (!enemy.isAlive()) {
							hbox.die();
						} else {
							hbox.setTransform(addVector.set(getPixelPosition()).scl(1.0f / PPM), 0);
						}
					}
				});
			}
		});
	}
}
