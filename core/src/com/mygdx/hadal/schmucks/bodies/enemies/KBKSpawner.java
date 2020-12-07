package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DeathParticles;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DieParticles;
import com.mygdx.hadal.utils.Stats;

public class KBKSpawner extends EnemySwimming {

	private static final int baseHp = 200;

	private static final int scrapDrop = 4;

	private static final int width = 1024;
	private static final int height = 1024;
	
	private static final int hboxWidth = 560;
	private static final int hboxHeight = 240;
	
	private static final float attackCd = 2.0f;
	private static final float airSpeed = -0.4f;
	
	private static final float scale = 0.25f;
	private static final float noiseRadius = 2.0f;

	private static final Sprite sprite = Sprite.KAMABOKO_SWIM;
	private static final Sprite projSprite = Sprite.ORB_RED;

	private final TextureRegion faceSprite;
	
	public KBKSpawner(PlayState state, Vector2 startPos, float startAngle, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), sprite, EnemyType.SPAWNER, startAngle, filter, baseHp, attackCd, scrapDrop, spawner);
		faceSprite = Sprite.KAMABOKO_FACE.getFrames().get(GameStateManager.generator.nextInt(5));
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		
		setNoiseRadius(noiseRadius);
	}
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.AIR_SPD, airSpeed, getBodyData()));
		getBodyData().addStatus(new DeathParticles(state, getBodyData(), Particle.KAMABOKO_IMPACT, 1.0f));
	}
	
	private static final float attackWindup1 = 0.9f;
	private static final float attackWindup2 = 0.3f;
	
	private static final float minRange = 5.0f;
	private static final float maxRange = 10.0f;
	
	private static final float projectileSpeed = 25.0f;
	private static final Vector2 projectileSize = new Vector2(50, 50);
	private static final float lifespan = 3.0f;
	private static final float range = 900.0f;
	@Override
	public void attackInitiate() {
		
		EnemyUtils.windupParticles(state, this, attackWindup1, Particle.CHARGING, HadalColor.MAGENTA, 100.0f);
		EnemyUtils.changeSwimmingState(this, SwimmingState.STILL, 0.0f, 0.0f);
		EnemyUtils.changeFloatingFreeAngle(this, 0.0f, 0.0f);
		EnemyUtils.windupParticles(state, this, attackWindup2, Particle.OVERCHARGE, HadalColor.MAGENTA, 100.0f);
		
		getActions().add(new EnemyAction(this, 0.0f) {
			
			private final Vector2 startVelo = new Vector2();
			@Override
			public void execute() {
				
				if (attackTarget == null) {
					return;
				}
				
				startVelo.set(projectileSpeed, projectileSpeed).setAngleDeg(getAttackAngle());
				
				if (startVelo.len2() < range * range) {
					startVelo.nor().scl(projectileSpeed);
					SoundEffect.SPIT.playUniversal(state, enemy.getPixelPosition(), 1.0f, 0.60f, false);
					
					Hitbox hbox = new RangedHitbox(state, enemy.getProjectileOrigin(startVelo, size.x), projectileSize, lifespan, startVelo, enemy.getHitboxfilter(), true, true, enemy, projSprite);
					hbox.setGravity(1.0f);
					
					hbox.addStrategy(new HitboxStrategy(state, hbox, enemy.getBodyData()) {
						
						@Override
						public void die() {
							EnemyType.CRAWLER1.generateEnemy(state, hbox.getPixelPosition(), getHitboxfilter(), 0.0f, null);
						}
					});
					
					hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
					hbox.addStrategy(new ContactUnitDie(state, hbox, enemy.getBodyData()));
					hbox.addStrategy(new ContactWallDie(state, hbox, enemy.getBodyData()));
					hbox.addStrategy(new DieParticles(state, hbox, enemy.getBodyData(), Particle.KAMABOKO_IMPACT));
				}
			}
		});
		
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private final Vector2 entityLocation = new Vector2();
	@Override
	public void render(SpriteBatch batch) {
		super.render(batch);
		
		boolean flip = true;
		double realAngle = getAngle() % (Math.PI * 2);
		if ((realAngle > Math.PI / 2 && realAngle < 3 * Math.PI / 2) || (realAngle < -Math.PI / 2 && realAngle > -3 * Math.PI / 2)) {
			flip = false;
		}

		entityLocation.set(getPixelPosition());
		batch.draw(faceSprite, 
				(flip ? size.x : 0) + entityLocation.x - size.x / 2, 
				entityLocation.y - size.y / 2, 
				(flip ? -1 : 1) * size.x / 2, 
				size.y / 2,
				(flip ? -1 : 1) * size.x, size.y, 1, 1, 
				(flip ? 0 : 180) + (float) Math.toDegrees(getAngle()));
	}
}
