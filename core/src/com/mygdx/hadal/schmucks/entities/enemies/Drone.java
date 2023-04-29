package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DeathRagdoll;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.enemy.MovementFloat.FloatingState;
import com.mygdx.hadal.strategies.enemy.MovementSwim.SwimmingState;

public class Drone extends EnemySwimming {

	private static final int baseHp = 225;
	private static final int scrapDrop = 5;

	private static final int width = 450;
	private static final int height = 450;
	
	private static final int hboxWidth = 450;
	private static final int hboxHeight = 450;
	
	private static final float attackCd = 1.0f;
	private static final float airSpeed = -0.1f;
	
	private static final float scale = 0.3f;
	private static final float noiseRadius = 3.0f;

	private static final Sprite sprite = Sprite.DRONE_BODY;
	
	private final TextureRegion armBackSprite, armFrontSprite;
	private final Animation<TextureRegion> eyeSprite, dotSprite;
	
	private final int eyeType;
	
	public Drone(PlayState state, Vector2 startPos, float startAngle, short filter, float extraField) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), sprite, EnemyType.DRONE, startAngle, filter, baseHp, attackCd, scrapDrop);
		armBackSprite = Sprite.DRONE_ARM_BACK.getFrame();
		armFrontSprite = Sprite.DRONE_ARM_FRONT.getFrame();
		eyeSprite = new Animation<>(PlayState.SPRITE_ANIMATION_SPEED_FAST, Sprite.DRONE_EYE.getFrames());
		dotSprite = new Animation<>(PlayState.SPRITE_ANIMATION_SPEED_FAST,	Sprite.DRONE_DOT.getFrames());
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		
		this.eyeType = (int) extraField;
		
		getSwimStrategy().setNoiseRadius(noiseRadius);
	}
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.AIR_SPD, airSpeed, getBodyData()));
		getBodyData().addStatus(new DeathRagdoll(state, getBodyData(), sprite, size));
		getBodyData().addStatus(new DeathRagdoll(state, getBodyData(), Sprite.DRONE_ARM_BACK, size));
		getBodyData().addStatus(new DeathRagdoll(state, getBodyData(), Sprite.DRONE_ARM_FRONT, size));
	}
	
	private static final float attackWindup1 = 0.9f;
	private static final float attackWindup2 = 0.3f;
	
	private static final float minRange = 4.0f;
	private static final float maxRange = 10.0f;

	private static final int laserNumber = 6;
	private static final float laserInterval = 0.05f;
	private static final float projectileSpeed = 28.0f;
	private static final float range = 900.0f;

	@Override
	public void attackInitiate() {
		
		EnemyUtils.windupParticles(state, this, attackWindup1, Particle.CHARGING, HadalColor.RED, 100.0f);
		EnemyUtils.changeSwimmingState(this, SwimmingState.STILL, 0.0f, 0.0f);
		EnemyUtils.changeFloatingFreeAngle(this, 0.0f, 0.0f);
		EnemyUtils.windupParticles(state, this, attackWindup2, Particle.OVERCHARGE, HadalColor.RED, 100.0f);
		
		for (int i = 0; i < laserNumber; i++) {
			getActions().add(new EnemyAction(this, laserInterval) {
				
				private final Vector2 startVelo = new Vector2();
				@Override
				public void execute() {
					if (attackTarget == null) {
						return;
					}
					
					startVelo.set(projectileSpeed, projectileSpeed).setAngleDeg(getAttackAngle());
					
					if (startVelo.len2() < range * range) {
						startVelo.nor().scl(projectileSpeed);
						SyncedAttack.DRONE_LASER.initiateSyncedAttackSingle(state, enemy, enemy.getPixelPosition(), startVelo);
					}
				}
			});
		}
		
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private final Vector2 entityLocation = new Vector2();
	@Override
	public void render(SpriteBatch batch) {
		
		boolean flip = true;
		float realAngle = getAngle() % (MathUtils.PI * 2);
		if ((realAngle > MathUtils.PI / 2 && realAngle < 3 * MathUtils.PI / 2) || (realAngle < -MathUtils.PI / 2 && realAngle > -3 * MathUtils.PI / 2)) {
			flip = false;
		}
		
		entityLocation.set(getPixelPosition());
		batch.draw(armBackSprite, 
				(flip ? size.x : 0) + entityLocation.x - size.x / 2, 
				entityLocation.y - size.y / 2, 
				(flip ? -1 : 1) * size.x / 2, 
				size.y / 2,
				(flip ? -1 : 1) * size.x, size.y, 1, 1, 
				(flip ? 0 : 180) + MathUtils.radDeg * getAngle());
		
		if (eyeType == 1) {
			batch.draw(eyeSprite.getKeyFrame(animationTime, false),
					(flip ? size.x : 0) + entityLocation.x - size.x / 2, 
					entityLocation.y - size.y / 2, 
					(flip ? -1 : 1) * size.x / 2, 
					size.y / 2,
					(flip ? -1 : 1) * size.x, size.y, 1, 1, 
					(flip ? 0 : 180) + MathUtils.radDeg * getAngle());
		}
		if (eyeType == 0) {
			batch.draw(dotSprite.getKeyFrame(animationTime, false),
					(flip ? size.x : 0) + entityLocation.x - size.x / 2, 
					entityLocation.y - size.y / 2, 
					(flip ? -1 : 1) * size.x / 2, 
					size.y / 2,
					(flip ? -1 : 1) * size.x, size.y, 1, 1, 
					(flip ? 0 : 180) + MathUtils.radDeg * getAngle());
		}
		
		
		super.render(batch);
		
		batch.draw(armFrontSprite, 
				(flip ? size.x : 0) + getPixelPosition().x - size.x / 2, 
				getPixelPosition().y - size.y / 2, 
				(flip ? -1 : 1) * size.x / 2, 
				size.y / 2,
				(flip ? -1 : 1) * size.x, size.y, 1, 1, 
				(flip ? 0 : 180) + MathUtils.radDeg * getAngle());
	}
}
