package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
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
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;

public class KBKSpawner extends EnemySwimming {

	private final static int baseHp = 200;
	private final static String name = "KAMABOKO SPAWNER";
	
	private final static int scrapDrop = 4;

	private static final int width = 1024;
	private static final int height = 1024;
	
	private static final int hboxWidth = 560;
	private static final int hboxHeight = 240;
	
	private static final float attackCd = 3.0f;
	private static final float airSpeed = -0.4f;
	
	private static final float scale = 0.25f;
	private static final float noiseRadius = 2.0f;

	private static final Sprite sprite = Sprite.KAMABOKO_SWIM;
	private final static Sprite projSprite = Sprite.ORB_RED;

	private TextureRegion faceSprite;
	
	public KBKSpawner(PlayState state, Vector2 startPos, float startAngle, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), name, sprite, EnemyType.SWIMMER1, startAngle, filter, baseHp, attackCd, scrapDrop, spawner);
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
	
	private static final float minRange = 5.0f;
	private static final float maxRange = 10.0f;
	
	private final static float projectileSpeed = 25.0f;
	private final static Vector2 projectileSize = new Vector2(50, 50);
	private final static float lifespan = 3.0f;
	private final static float range = 900.0f;
	@Override
	public void attackInitiate() {
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.4f);
		getActions().add(new EnemyAction(this, 0.0f) {
			
			private Vector2 startVelo = new Vector2();
			@Override
			public void execute() {
				
				if (attackTarget == null) {
					return;
				}
				
				startVelo.set(attackTarget.getPixelPosition()).sub(enemy.getPixelPosition());
				
				if (startVelo.len2() < range * range) {
					startVelo.nor().scl(projectileSpeed);
					
					Hitbox hbox = new RangedHitbox(state, enemy.getProjectileOrigin(startVelo, size.x), projectileSize, lifespan, startVelo, enemy.getHitboxfilter(), true, true, enemy, projSprite);
					hbox.setGravity(1.0f);
					
					hbox.addStrategy(new HitboxStrategy(state, hbox, enemy.getBodyData()) {
						
						@Override
						public void die() {
							EnemyType.CRAWLER1.generateEnemy(state, hbox.getPixelPosition(), Constants.ENEMY_HITBOX, 0.0f, null);
						}
					});
					
					hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
					hbox.addStrategy(new ContactUnitDie(state, hbox, enemy.getBodyData()));
					hbox.addStrategy(new ContactWallDie(state, hbox, enemy.getBodyData()));
					hbox.addStrategy(new DieParticles(state, hbox, enemy.getBodyData(), Particle.KAMABOKO_IMPACT));
				}
			}
		});
	}
	
	@Override
	public void render(SpriteBatch batch) {
		super.render(batch);
		
		boolean flip = true;
		double realAngle = getAngle() % (Math.PI * 2);
		if ((realAngle > Math.PI / 2 && realAngle < 3 * Math.PI / 2) || (realAngle < -Math.PI / 2 && realAngle > -3 * Math.PI / 2)) {
			flip = false;
		}

		batch.draw(faceSprite, 
				(flip ? size.x : 0) + getPixelPosition().x - size.x / 2, 
				getPixelPosition().y - size.y / 2, 
				(flip ? -1 : 1) * size.x / 2, 
				size.y / 2,
				(flip ? -1 : 1) * size.x, size.y, 1, 1, 
				(flip ? 0 : 180) + (float) Math.toDegrees(getAngle()));
	}
}
