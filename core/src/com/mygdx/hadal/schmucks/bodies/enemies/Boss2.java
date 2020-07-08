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
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * This is a boss in the game
 * @author Zachary Tu
 */
public class Boss2 extends EnemyFloating {
				
	private final static String name = "KING KAMABOKO";

    private static final float aiAttackCd = 2.0f;
    private static final float aiAttackCd2 = 1.5f;
    private static final float aiAttackCd3 = 1.0f;
    
    private final static int scrapDrop = 15;
    
	private static final int width = 250;
	private static final int height = 250;
	
	private static final int hbWidth = 200;
	private static final int hbHeight = 150;
	
	private static final float scale = 1.0f;
	
	private static final int hp = 6000;
	private static final float linkResist = 0.2f;
	
	private static final Sprite sprite = Sprite.NOTHING;
	
	private Body[] links = new Body[5];
	private TextureRegion headSprite, bodySprite, faceSprite;
	
	private int phase = 1;
	private static final float phaseThreshold2 = 0.8f;
	private static final float phaseThreshold3 = 0.4f;
	
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
		
		final BodyData me = getBodyData();
		
		BodyData link = new BodyData(this, baseHp) {
			
			@Override
			public float receiveDamage(float basedamage, Vector2 knockback, BodyData perp, Boolean procEffects, DamageTypes... tags) {
				me.receiveDamage(basedamage * linkResist, knockback, perp, procEffects, tags);
				return 0;
			}
		};
		
		for (int i = 0; i < links.length; i ++) {
			links[i] = BodyBuilder.createBox(world, startPos, getHboxSize(), 0, 1, 0, false, false, Constants.BIT_ENEMY, 
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
		links[links.length - 1].setType(BodyType.KinematicBody);
		
		//shitty hard coded way of making the anchor link sync with client
		if (!state.isServer()) {
			links[links.length - 1].setTransform(new Vector2(serverPos).scl(32), 0);
		}
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
		
		if (phase == 1) {
			if (getBodyData().getCurrentHp() <= phaseThreshold2 * getBodyData().getStat(Stats.MAX_HP)) {
				phase = 2;
				setAttackCd(aiAttackCd2);
				summonCrawler();
			} else {
				if (attackNum % 2 == 1) {
					
					int randomIndex = GameStateManager.generator.nextInt(5);
					switch(randomIndex) {
					case 0: 
						meleeAttack1();
						break;
					case 1: 
						meleeAttack2();
						break;
					case 2: 
						fireBreath();
						break;
					case 3: 
						slodgeBreath();
						break;
					case 4: 
						fuguShots();
						break;
					}
				} else {
					kamabokoShot1();
				}
				
				if (attackNum % 4 == 0) {
					summonSwimmer();
				}
			}
		}
		
		if (phase == 2) {
			if (getBodyData().getCurrentHp() <= phaseThreshold3 * getBodyData().getStat(Stats.MAX_HP)) {
				phase = 3;
				setAttackCd(aiAttackCd3);
				summonCrawler();
			} else {
				if (attackNum % 2 == 1) {
					
					int randomIndex = GameStateManager.generator.nextInt(5);
					switch(randomIndex) {
					case 0: 
						meleeAttack1();
						break;
					case 1: 
						meleeAttack2();
						break;
					case 2: 
						fireBreath();
						break;
					case 3: 
						slodgeBreath();
						break;
					case 4: 
						fuguShots();
						break;
					}
				} else {
					kamabokoShot2();
				}
				
				if (attackNum % 3 == 0) {
					summonSwimmer();
				}
			}
		}
		
		if (phase == 3) {
			if (attackNum % 2 == 1) {
				int randomIndex = GameStateManager.generator.nextInt(5);
				switch(randomIndex) {
				case 0: 
					meleeAttack1();
					break;
				case 1: 
					meleeAttack2();
					break;
				case 2: 
					fireBreath();
					break;
				case 3: 
					slodgeBreath();
					break;
				case 4: 
					fuguShots();
					break;
				}
			} else {
				kamabokoShot3();
			}
			
			if (attackNum % 2 == 0) {
				summonSwimmer();
			}
		}
	}
	
	private static final int bulletDamage = 10;
	private static final int bulletSpeed1 = 18;
	private static final int bulletSpeed2 = 5;
	private static final int bulletKB = 25;
	private static final int bulletSize = 60;
	private static final float bulletLifespan = 3.0f;
	private static final float bulletInterval1 = 0.4f;
	private static final float bulletInterval2 = 0.6f;
	private static final float bulletInterval3 = 0.8f;
	private static final int bulletNumber = 3;
	private void kamabokoShot1() {
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.25f);
		for (int i = 0; i < bulletNumber; i++) {
			EnemyUtils.shootKamaboko(state, this, bulletDamage, bulletSpeed1, bulletKB, bulletSize, bulletLifespan, bulletInterval1, 1);
		}
	}
	
	private void kamabokoShot2() {
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.25f);
		for (int i = 0; i < bulletNumber; i++) {
			EnemyUtils.shootKamaboko(state, this, bulletDamage, bulletSpeed2, bulletKB, bulletSize, bulletLifespan, bulletInterval2, 2);
		}
	}
	
	private void kamabokoShot3() {
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.25f);
		for (int i = 0; i < bulletNumber; i++) {
			EnemyUtils.shootKamaboko(state, this, bulletDamage, bulletSpeed2, bulletKB, bulletSize, bulletLifespan, bulletInterval3, 3);
		}
	}
	
	private final static int driftSpeed = 6;
	private static final int charge1Speed = 50;
	private static final float charge1Damage = 5.0f;
	private static final float chargeAttackInterval = 1 / 60.0f;

	private static final int defaultMeleeKB = 50;
	private final static int returnSpeed = 15;
	private void meleeAttack1() {
		EnemyUtils.moveToDummy(state, this, "back", driftSpeed, driftDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -180.0f, 1.0f);
		EnemyUtils.meleeAttackContinuous(state, this, charge1Damage, chargeAttackInterval, defaultMeleeKB, 0.8f);
		EnemyUtils.moveToDummy(state, this, "platformLeft", charge1Speed, driftDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, getAngle(), 0.0f);
		EnemyUtils.moveToDummy(state, this, "neutral", returnSpeed, driftDurationMax);
	}
	
	private static final int thrashSpeed = 30;
	private static final int thrashDownSpeed = 70;
	private static final float thrash1Damage = 6.0f;
	
	private void meleeAttack2() {
		EnemyUtils.moveToDummy(state, this, "back", driftSpeed, driftDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -180.0f, 1.0f);
		
		int rand = GameStateManager.generator.nextInt(2);
		switch(rand) {
			case 0:
				EnemyUtils.moveToDummy(state, this, "highLeft", thrashSpeed, driftDurationMax);
				EnemyUtils.meleeAttackContinuous(state, this, thrash1Damage, chargeAttackInterval, defaultMeleeKB, 0.25f);
				EnemyUtils.moveToDummy(state, this, "platformLeft", thrashDownSpeed, driftDurationMax);
				
				EnemyUtils.moveToDummy(state, this, "highCenter", thrashSpeed, driftDurationMax);
				EnemyUtils.meleeAttackContinuous(state, this, thrash1Damage, chargeAttackInterval, defaultMeleeKB, 0.25f);
				EnemyUtils.moveToDummy(state, this, "platformCenter", thrashDownSpeed, driftDurationMax);
				
				EnemyUtils.moveToDummy(state, this, "highRight", thrashSpeed, driftDurationMax);
				EnemyUtils.meleeAttackContinuous(state, this, thrash1Damage, chargeAttackInterval, defaultMeleeKB, 0.25f);
				EnemyUtils.moveToDummy(state, this, "platformRight", thrashDownSpeed, driftDurationMax);
				break;
			case 1:
				EnemyUtils.moveToDummy(state, this, "highRight", thrashSpeed, driftDurationMax);
				EnemyUtils.meleeAttackContinuous(state, this, thrash1Damage, chargeAttackInterval, defaultMeleeKB, 0.25f);
				EnemyUtils.moveToDummy(state, this, "platformRight", thrashDownSpeed, driftDurationMax);
				
				EnemyUtils.moveToDummy(state, this, "highCenter", thrashSpeed, driftDurationMax);
				EnemyUtils.meleeAttackContinuous(state, this, thrash1Damage, chargeAttackInterval, defaultMeleeKB, 0.25f);
				EnemyUtils.moveToDummy(state, this, "platformCenter", thrashDownSpeed, driftDurationMax);
				
				EnemyUtils.moveToDummy(state, this, "highLeft", thrashSpeed, driftDurationMax);
				EnemyUtils.meleeAttackContinuous(state, this, thrash1Damage, chargeAttackInterval, defaultMeleeKB, 0.25f);
				EnemyUtils.moveToDummy(state, this, "platformLeft", thrashDownSpeed, driftDurationMax);
				break;
		}
		
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
	
	private static final int fuguDamage = 5;
	private static final int fuguSpeed = 18;
	private static final int fuguKB = 5;
	private static final int fuguSize = 70;
	private static final float fuguLifespan = 2.5f;
	private static final int fuguNumber = 3;
	private static final float fuguInterval = 0.25f;
	
	private final static int poisonRadius = 150;
	private final static float poisonDamage = 0.4f;
	private final static float poisonDuration = 4.0f;
	
	private void fuguShots() {
		EnemyUtils.moveToDummy(state, this, "back", returnSpeed, driftDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -210.0f, 1.5f);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -260.0f, 0.0f);
		for (int i = 0; i < fuguNumber; i++) {
			EnemyUtils.fugu(state, this, fuguDamage, fuguSpeed, fuguKB, fuguSize, fuguLifespan, poisonRadius, poisonDamage, poisonDuration, fuguInterval);
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
		EnemyUtils.moveToDummy(state, this, "back", returnSpeed, driftDurationMax);
		EnemyUtils.moveToDummy(state, this, "neutral", returnSpeed, driftDurationMax);
	}
	
	private static final int numCrawler = 3;
	private static final float crawlerInterval = 1.0f;
	private void summonCrawler() {
		EnemyUtils.moveToDummy(state, this, "hide", returnSpeed, driftDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 2.0f);
		for (int i = 0; i < numCrawler; i++) {
			EnemyUtils.callMinion(state, this, crawlerInterval, EnemyType.CRAWLER1, 0.0f);
		}
		EnemyUtils.moveToDummy(state, this, "back", returnSpeed, driftDurationMax);
		EnemyUtils.moveToDummy(state, this, "neutral", returnSpeed, driftDurationMax);
	}
	
	private void summonSwimmer() {
		EnemyUtils.callMinion(state, this, 0.0f, EnemyType.SWIMMER1, 0.0f);
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
