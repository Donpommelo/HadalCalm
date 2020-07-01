package com.mygdx.hadal.schmucks.bodies.enemies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
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
				
	private final static String name = "KING KAMABOKO";

    private static final float aiAttackCd = 2.0f;
    
    private final static int scrapDrop = 15;
    
	private static final int width = 250;
	private static final int height = 250;
	
	private static final int hbWidth = 200;
	private static final int hbHeight = 150;
	
	private static final float scale = 1.0f;
	
	private static final int hp = 4500;
	private static final float linkResist = 0.2f;
	
	private static final Sprite sprite = Sprite.NOTHING;
	
	private Body[] links = new Body[7];
	private TextureRegion headSprite, bodySprite, faceSprite;
	
	public Boss2(PlayState state, Vector2 startPos, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hbWidth, hbHeight).scl(scale), name, sprite, EnemyType.BOSS2, filter, hp, aiAttackCd, scrapDrop, spawner);
		this.headSprite = Sprite.KAMABOKO_BODY.getFrames().get(0);
		this.bodySprite = Sprite.KAMABOKO_BODY.getFrames().get(1);
		setFaceSprite();
	}

	@Override
	public void create() {
		super.create();
		Filter filter = getMainFixture().getFilterData();
		filter.maskBits = (short) (Constants.BIT_SENSOR | Constants.BIT_PROJECTILE);
		getMainFixture().setFilterData(filter);
		
		getBodyData().addStatus(new StatChangeStatus(state, Stats.KNOCKBACK_RES, 1.0f, getBodyData()));
		
		final BodyData me = getBodyData();
		
		BodyData link = new BodyData(this, baseHp) {
			
			@Override
			public float receiveDamage(float basedamage, Vector2 knockback, BodyData perp, Boolean procEffects, DamageTypes... tags) {
				me.receiveDamage(basedamage * linkResist, knockback, perp, procEffects, tags);
				return 0;
			}
		};
		
		for (int i = 0; i < links.length; i ++) {
			links[i] = BodyBuilder.createBox(world, new Vector2(startPos).sub(0, width * i / 2 * scale), getHboxSize(), 0, 1, 0, false, false, Constants.BIT_ENEMY, 
					(short) (Constants.BIT_SENSOR | Constants.BIT_PROJECTILE),
					hitboxfilter, false, link);
			
			RevoluteJointDef joint1 = new RevoluteJointDef();
			
			if (i == 0) { 
				joint1.bodyA = body;
			} else {
				joint1.bodyA = links[i - 1];
			}
			
			joint1.bodyB = links[i];
			joint1.collideConnected = false;
			joint1.localAnchorA.set(-width / 2 / 32 * scale, 0);
			joint1.localAnchorB.set(width / 2 / 32 * scale, 0);
			
			world.createJoint(joint1);
		}
		
		body.setType(BodyType.KinematicBody);
		links[6].setType(BodyType.KinematicBody);
	}
	
	@Override
	public void render(SpriteBatch batch) {	
		
		boolean flip = true;
		double realAngle = getAngle() % (Math.PI * 2);
		if ((realAngle > Math.PI / 2 && realAngle < 3 * Math.PI / 2) || (realAngle < -Math.PI / 2 && realAngle > -3 * Math.PI / 2)) {
			flip = false;
		}
		
		for (int i = links.length - 1; i >= 0; i--) {
			batch.draw(bodySprite, 
					(flip ? size.x : 0) + links[i].getPosition().x * PPM - size.x / 2,
					links[i].getPosition().y * PPM - size.y / 2, 
					(flip ? -1 : 1) * size.x / 2, size.y / 2,
					(flip ? -1 : 1) * size.x, size.y, 1, 1, 
					(flip ? 0 : 180) + (float) Math.toDegrees(links[i].getAngle()));
		}
		
		batch.draw(headSprite, 
				(flip ? size.x : 0) + getPixelPosition().x - size.x / 2, 
				getPixelPosition().y - size.y / 2, 
				(flip ? -1 : 1) * size.x / 2, size.y / 2,
				(flip ? -1 : 1) * size.x, size.y, 1, 1, 
				(flip ? 0 : 180) + (float) Math.toDegrees(getAngle()));
		
		batch.draw(faceSprite, 
				(flip ? size.x : 0) + getPixelPosition().x - size.x / 2, 
				getPixelPosition().y - size.y / 2, 
				(flip ? -1 : 1) * size.x / 2, size.y / 2,
				(flip ? -1 : 1) * size.x, size.y, 1, 1, 
				(flip ? 0 : 180) + (float) Math.toDegrees(getAngle()));
	}
	
	public void setFaceSprite() {
		faceSprite = Sprite.KAMABOKO_FACE.getFrames().get(GameStateManager.generator.nextInt(5));
	}
	
	private final static float driftDurationMax = 5.0f;
	private int attackNum = 0;
	@Override
	public void attackInitiate() {
		attackNum++;
		setFaceSprite();
		
		if (attackNum % 2 == 0) {
			int randomIndex = GameStateManager.generator.nextInt(4);
			switch(randomIndex) {
			case 0: 
				meleeAttack();
				break;
			case 1: 
				slodgeBreath();
				break;
			case 2: 
				fireBreath();
				break;
			case 3: 
				slodgeBreath();
				break;
			}
		} else {
			kamabokoShot();
		}
	}
	
	private static final int bulletDamage = 10;
	private static final int bulletSpeed = 20;
	private static final int bulletKB = 35;
	private static final int bulletSize = 60;
	private static final float bulletLifespan = 2.0f;
	private static final float bulletInterval = 0.5f;
	private static final int bulletNumber = 3;
	public void kamabokoShot() {
		for (int i = 0; i < bulletNumber; i++) {
			EnemyUtils.shootKamaboko(state, this, bulletDamage, bulletSpeed, bulletKB, bulletSize, bulletLifespan, bulletInterval);
		}
	}
	
	private final static int driftSpeed = 6;
	private static final int charge1Speed = 50;
	private static final float charge1Damage = 5.0f;
	private static final float chargeAttackInterval = 1 / 60.0f;

	private static final int defaultMeleeKB = 50;
	private final static int returnSpeed = 15;
	public void meleeAttack() {
		EnemyUtils.moveToDummy(state, this, "back", driftSpeed, driftDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -180.0f, 1.0f);
		EnemyUtils.meleeAttackContinuous(state, this, charge1Damage, chargeAttackInterval, defaultMeleeKB, 0.8f);
		EnemyUtils.moveToDummy(state, this, "platformCenter", charge1Speed, driftDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, getAngle(), 0.0f);
		EnemyUtils.moveToDummy(state, this, "neutral", returnSpeed, driftDurationMax);
	}
	
	private static final int fireballDamage = 4;
	private static final int burnDamage = 3;
	private static final int fireSpeed = 12;
	private static final int fireKB = 10;
	private static final int fireSize = 50;
	private static final float fireLifespan = 1.7f;
	private static final float burnDuration = 4.0f;

	private static final int fireballNumber = 40;
	private static final float fireballInterval = 0.02f;
	
	private void fireBreath() {
		EnemyUtils.moveToDummy(state, this, "high", returnSpeed, driftDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -90.0f, 1.5f);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -180.0f, 0.0f);
		for (int i = 0; i < fireballNumber; i++) {
			EnemyUtils.fireball(state, this, fireballDamage, burnDamage, fireSpeed, fireKB, fireSize, fireLifespan, burnDuration, fireballInterval, Particle.FIRE);
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
		EnemyUtils.moveToDummy(state, this, "back", returnSpeed, driftDurationMax);
		EnemyUtils.moveToDummy(state, this, "neutral", returnSpeed, driftDurationMax);
	}
	
	private static final int slodgeDamage = 6;
	private static final int slodgeSpeed = 10;
	private static final int slodgeKB = 10;
	private static final int slodgeSize = 50;
	private static final float slodgeLifespan = 2.5f;
	private static final float slodgeSlow = 0.8f;
	private static final float slodgeDuration = 3.0f;
	
	private static final int slodgeNumber = 40;
	private static final float slodgeInterval = 0.02f;
	
	private void slodgeBreath() {
		EnemyUtils.moveToDummy(state, this, "back", returnSpeed, driftDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -180.0f, 1.5f);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -240.0f, 0.0f);
		for (int i = 0; i < slodgeNumber; i++) {
			EnemyUtils.slodge(state, this, slodgeDamage, slodgeSpeed, slodgeKB, slodgeSize, slodgeLifespan, slodgeSlow, slodgeDuration, slodgeInterval);
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
		
		//check of destroyed to avoid double-destruction
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
