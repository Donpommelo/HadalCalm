package com.mygdx.hadal.schmucks.bodies.enemies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * This is a boss in the game
 * @author Zachary Tu
 *
 */
public class Boss2 extends EnemyFloating {
				
    private static final float aiAttackCd = 3.0f;
    
    private final static int scrapDrop = 15;
    
	private static final int width = 250;
	private static final int height = 250;
	
	private static final int hbWidth = 250;
	private static final int hbHeight = 250;
	
	private static final float scale = 1.0f;
	
	private static final int hp = 4500;
	
	private static final Sprite sprite = Sprite.NOTHING;
	
	private Body[] links = new Body[5];
	private TextureRegion headSprite, bodySprite, faceSprite;
	
	public Boss2(PlayState state, Vector2 startPos, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hbWidth, hbHeight).scl(scale), sprite, EnemyType.BOSS2, filter, hp, aiAttackCd, scrapDrop, spawner);
		this.headSprite = Sprite.KAMABOKO_BODY.getFrames().get(0);
		this.bodySprite = Sprite.KAMABOKO_BODY.getFrames().get(1);
		setFaceSprite();
	}

	@Override
	public void create() {
		super.create();

		getBodyData().addStatus(new StatChangeStatus(state, Stats.KNOCKBACK_RES, 1.0f, getBodyData()));
		
		for (int i = 0; i < links.length; i ++) {
			links[i] = BodyBuilder.createBox(world, new Vector2(startPos).sub(0, width * i / 2 * scale), getHboxSize(), 0, 1, 0, false, false, Constants.BIT_ENEMY, 
					(short) (Constants.BIT_SENSOR | Constants.BIT_PROJECTILE),
					hitboxfilter, false, getBodyData());
			
			RevoluteJointDef joint1 = new RevoluteJointDef();
			
			if (i == 0) { 
				joint1.bodyA = body;
			} else {
				joint1.bodyA = links[i - 1];
			}
			
			joint1.bodyB = links[i];
			joint1.collideConnected = false;
			joint1.localAnchorA.set(0, -width / 2 / 32 * scale);
			joint1.localAnchorB.set(0, width / 2 / 32 * scale);
			joint1.enableLimit = true;
			joint1.lowerAngle = -1.5f;
			joint1.upperAngle = 1.5f;
			
			world.createJoint(joint1);
		}
		
		links[4].setType(BodyType.KinematicBody);
	}
	
	@Override
	public void render(SpriteBatch batch) {	
		boolean flip = false;
		double realAngle = getOrientation() % (Math.PI * 2);
		if ((realAngle > Math.PI && realAngle < 2 * Math.PI) || (realAngle < 0 && realAngle > -Math.PI)) {
			flip = true;
		}
		
		for (int i = links.length - 1; i >= 0; i--) {
			batch.draw(bodySprite, 
					links[i].getPosition().x * PPM - getHboxSize().y / 2, 
					(flip ? size.y : 0) + links[i].getPosition().y * PPM - getHboxSize().x / 2, 
					getHboxSize().y / 2, 
					(flip ? -1 : 1) * getHboxSize().x / 2,
					size.x, (flip ? -1 : 1) * size.y, 1, 1, 
					(float) Math.toDegrees(links[i].getAngle()) - 90);
		}
		
		batch.draw(headSprite, 
				getPixelPosition().x - getHboxSize().y / 2, 
				(flip ? size.y : 0) + getPixelPosition().y - getHboxSize().x / 2, 
				getHboxSize().y / 2, 
				(flip ? -1 : 1) * getHboxSize().x / 2,
				size.x, (flip ? -1 : 1) * size.y, 1, 1, 
				(float) Math.toDegrees(getOrientation()) - 90);
		
		batch.draw(faceSprite, 
				getPixelPosition().x - getHboxSize().y / 2, 
				(flip ? size.y : 0) + getPixelPosition().y - getHboxSize().x / 2, 
				getHboxSize().y / 2, 
				(flip ? -1 : 1) * getHboxSize().x / 2,
				size.x, (flip ? -1 : 1) * size.y, 1, 1, 
				(float) Math.toDegrees(getOrientation()) - 90);
	}
	
	public void setFaceSprite() {
		faceSprite = Sprite.KAMABOKO_FACE.getFrames().get(GameStateManager.generator.nextInt(5));
	}
	
	private final static float driftDurationMax = 5.0f;
	@Override
	public void attackInitiate() {
		setFaceSprite();
		int randomIndex = GameStateManager.generator.nextInt(4);
		switch(randomIndex) {
		case 0: 
			meleeAttack();
			break;
		case 1: 
			horizontalLaser();
			break;
		case 2: 
			fireBreath();
			break;
		case 3: 
			sweepingLaser();
			break;
		}
	}
	
	private final static int driftSpeed = 6;
	private static final int charge1Speed = 60;
	private static final float charge1Damage = 20.0f;
	private static final int defaultMeleeKB = 50;
	private final static int returnSpeed = 15;
	public void meleeAttack() {
		EnemyUtils.moveToDummy(state, this, "back", driftSpeed, driftDurationMax);
		EnemyUtils.meleeAttackContact(state, this, charge1Damage, defaultMeleeKB, 1.0f);
		EnemyUtils.moveToDummy(state, this, "platformCenter", charge1Speed, driftDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, getAngle(), 0.5f);
		EnemyUtils.moveToDummy(state, this, "back", returnSpeed, driftDurationMax);
		EnemyUtils.moveToDummy(state, this, "neutral", returnSpeed, driftDurationMax);
	}
	
	private static final float laser1Interval = 0.03f;
	private static final int laser1Amount = 40;
	private static final float laser1Damage = 6.0f;
	private static final float laserKnockback = 5.0f;
	private static final float laser1Speed = 55.0f;
	private static final int laserSize = 30;
	private static final float laserLifespan = 1.2f;
	
	public void horizontalLaser() {
		EnemyUtils.moveToDummy(state, this, "platformLip", driftSpeed, driftDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -180.0f, 0.5f);
		EnemyUtils.stopStill(this, 0.2f);
		for (int i = 0; i < laser1Amount; i++) {
			EnemyUtils.fireLaser(state, this, laser1Damage, laser1Speed, laserKnockback, laserSize, laserLifespan, laser1Interval, Particle.LASER);
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, getAngle(), 0.5f);
		EnemyUtils.moveToDummy(state, this, "back", returnSpeed, driftDurationMax);
		EnemyUtils.moveToDummy(state, this, "neutral", returnSpeed, driftDurationMax);
	}
	
	private static final int fireballDamage = 4;
	private static final int burnDamage = 3;
	private static final int fireSpeed = 12;
	private static final int fireKB = 10;
	private static final int fireSize = 50;
	private static final float fireLifespan = 1.5f;
	private static final float burnDuration = 4.0f;

	private static final int fireballNumber = 40;
	private static final float fireballInterval = 0.02f;
	
	private void fireBreath() {
		EnemyUtils.moveToDummy(state, this, "high", returnSpeed, driftDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -90.0f, 1.5f);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -180.0f, 0.0f);
		for (int i = 0; i < fireballNumber; i++) {
			EnemyUtils.fireball(state, this, fireballDamage, burnDamage, fireSpeed, fireKB, fireSize, fireLifespan, burnDuration, fireballInterval);
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
		EnemyUtils.moveToDummy(state, this, "back", returnSpeed, driftDurationMax);
		EnemyUtils.moveToDummy(state, this, "neutral", returnSpeed, driftDurationMax);
	}
	
	private static final float laser2Interval = 0.04f;
	private static final int laser3Amount = 25;
	private static final float laser3Damage = 7.5f;
	private static final float laser3Knockback = 1.0f;
	private static final float laser3Speed = 55.0f;
	private static final int explosionNumber = 5;
	private static final float explosionDamage = 35.0f;
	private static final float explosionKnockback = 35.0f;
	private static final float explosionSize = 300;
	private static final float explosionInterval = 0.25f;
	
	private void sweepingLaser() {
		EnemyUtils.moveToDummy(state, this, "high", returnSpeed, driftDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -90.0f, 1.0f);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -165.0f, 0.0f);
		for (int i = 0; i < laser3Amount; i++) {
			EnemyUtils.fireLaser(state, this, laser3Damage, laser3Speed, laser3Knockback, laserSize, laserLifespan, laser2Interval, Particle.LASER_PULSE);
		}
		for (int i = 1; i <= explosionNumber; i++) {
			EnemyUtils.createExplosion(state, this, new Vector2(EnemyUtils.getRightSide(state) - i * explosionSize / 2,
					EnemyUtils.floorHeight(state)), explosionSize, explosionDamage, explosionKnockback, explosionInterval);
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
		EnemyUtils.moveToDummy(state, this, "back", returnSpeed, driftDurationMax);
		EnemyUtils.moveToDummy(state, this, "neutral", returnSpeed, driftDurationMax);
	}
	
	/**
	 * This method is called by the playstate next engine tick after deleting this entity.
	 * This is where the body is actually deleted
	 */
	@Override
	public void dispose() {
		
		//check of destroyed to aavoid double-destruction
		if (destroyed == false) {
			destroyed = true;
			alive = false;
			if (body != null) {
				world.destroyBody(body);
				
				for (int i = 0; i < links.length; i++) {
					world.destroyBody(links[i]);
				}
			}
		}
	}	

	@Override
	public boolean isVisible() {
		return true;
	}
}
