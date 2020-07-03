package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.DeathRagdoll;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactUnitParticles;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallParticles;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.Spread;
import com.mygdx.hadal.utils.Stats;

public class Drone extends EnemySwimming {

	private final static int baseHp = 250;
	private final static String name = "DRONE";
	
	private final static int scrapDrop = 5;

	private static final int width = 450;
	private static final int height = 450;
	
	private static final int hboxWidth = 450;
	private static final int hboxHeight = 450;
	
	private static final float attackCd = 2.5f;
	private static final float airSpeed = -0.1f;
	
	private static final float scale = 0.4f;
	private static final float noiseRadius = 3.0f;

	private static final Sprite sprite = Sprite.DRONE_BODY;
	
	private final static Sprite projSprite = Sprite.LASER;

	private TextureRegion armBackSprite, armFrontSprite;
	private Animation<TextureRegion> eyeSprite, dotSprite;
	
	private int eyeType;
	
	public Drone(PlayState state, Vector2 startPos, float startAngle, short filter, float extraField, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), name, sprite, EnemyType.DRONE, startAngle, filter, baseHp, attackCd, scrapDrop, spawner);
		armBackSprite = Sprite.DRONE_ARM_BACK.getFrame();
		armFrontSprite = Sprite.DRONE_ARM_FRONT.getFrame();
		eyeSprite = new Animation<TextureRegion>(PlayState.spriteAnimationSpeedFast, Sprite.DRONE_EYE.getFrames());
		dotSprite = new Animation<TextureRegion>(PlayState.spriteAnimationSpeedFast, Sprite.DRONE_DOT.getFrames());
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		
		this.eyeType = (int) extraField;
		
		setNoiseRadius(noiseRadius);
	}
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.AIR_SPD, airSpeed, getBodyData()));
		getBodyData().addStatus(new DeathRagdoll(state, getBodyData(), sprite, size));
		getBodyData().addStatus(new DeathRagdoll(state, getBodyData(), Sprite.DRONE_ARM_BACK, size));
		getBodyData().addStatus(new DeathRagdoll(state, getBodyData(), Sprite.DRONE_ARM_FRONT, size));
	}
	
	private static final float minRange = 4.0f;
	private static final float maxRange = 10.0f;

	private static final int laserNumber = 6;
	private static final float laserInterval = 0.05f;
	private final static float baseDamage = 6.0f;
	private final static float knockback = 6.0f;
	private final static float projectileSpeed = 18.0f;
	private final static Vector2 projectileSize = new Vector2(60, 30);
	private final static float lifespan = 3.0f;
	private final static float range = 900.0f;
	private final static int spread = 12;
	@Override
	public void attackInitiate() {
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.4f);
		for (int i = 0; i < laserNumber; i++) {
			getActions().add(new EnemyAction(this, laserInterval) {
				
				private Vector2 startVelo = new Vector2();
				@Override
				public void execute() {
					SoundEffect.LASER2.playUniversal(state, enemy.getPixelPosition(), 0.5f, false);
					
					if (attackTarget == null) {
						return;
					}
					
					startVelo.set(attackTarget.getPixelPosition()).sub(enemy.getPixelPosition());
					
					if (startVelo.len2() < range * range) {
						startVelo.nor().scl(projectileSpeed);
						
						Hitbox hbox = new RangedHitbox(state, enemy.getProjectileOrigin(startVelo, size.x), projectileSize, lifespan, startVelo, enemy.getHitboxfilter(), true, true, enemy, projSprite);
						
						hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
						hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, enemy.getBodyData()));
						hbox.addStrategy(new ContactWallDie(state, hbox, enemy.getBodyData()));
						hbox.addStrategy(new AdjustAngle(state, hbox, enemy.getBodyData()));
						hbox.addStrategy(new ContactWallParticles(state, hbox, enemy.getBodyData(), Particle.LASER_IMPACT).setOffset(true));
						hbox.addStrategy(new ContactUnitParticles(state, hbox, enemy.getBodyData(), Particle.LASER_IMPACT).setOffset(true));
						hbox.addStrategy(new DamageStandard(state, hbox, enemy.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
						hbox.addStrategy(new Spread(state, hbox, enemy.getBodyData(), spread));
						hbox.addStrategy(new ContactUnitSound(state, hbox, enemy.getBodyData(), SoundEffect.DAMAGE3, 0.8f, true));
					}
				}
			});
		}
	};
	
	@Override
	public void render(SpriteBatch batch) {
		
		boolean flip = true;
		double realAngle = getAngle() % (Math.PI * 2);
		if ((realAngle > Math.PI / 2 && realAngle < 3 * Math.PI / 2) || (realAngle < -Math.PI / 2 && realAngle > -3 * Math.PI / 2)) {
			flip = false;
		}
		
		batch.draw(armBackSprite, 
				(flip ? size.x : 0) + getPixelPosition().x - size.x / 2, 
				getPixelPosition().y - size.y / 2, 
				(flip ? -1 : 1) * size.x / 2, 
				size.y / 2,
				(flip ? -1 : 1) * size.x, size.y, 1, 1, 
				(flip ? 0 : 180) + (float) Math.toDegrees(getAngle()));
		
		if (eyeType == 1) {
			batch.draw((TextureRegion) eyeSprite.getKeyFrame(animationTime, false), 
					(flip ? size.x : 0) + getPixelPosition().x - size.x / 2, 
					getPixelPosition().y - size.y / 2, 
					(flip ? -1 : 1) * size.x / 2, 
					size.y / 2,
					(flip ? -1 : 1) * size.x, size.y, 1, 1, 
					(flip ? 0 : 180) + (float) Math.toDegrees(getAngle()));
		}
		if (eyeType == 0) {
			batch.draw((TextureRegion) dotSprite.getKeyFrame(animationTime, false), 
					(flip ? size.x : 0) + getPixelPosition().x - size.x / 2, 
					getPixelPosition().y - size.y / 2, 
					(flip ? -1 : 1) * size.x / 2, 
					size.y / 2,
					(flip ? -1 : 1) * size.x, size.y, 1, 1, 
					(flip ? 0 : 180) + (float) Math.toDegrees(getAngle()));
		}
		
		
		super.render(batch);
		
		batch.draw(armFrontSprite, 
				(flip ? size.x : 0) + getPixelPosition().x - size.x / 2, 
				getPixelPosition().y - size.y / 2, 
				(flip ? -1 : 1) * size.x / 2, 
				size.y / 2,
				(flip ? -1 : 1) * size.x, size.y, 1, 1, 
				(flip ? 0 : 180) + (float) Math.toDegrees(getAngle()));
	}
}
