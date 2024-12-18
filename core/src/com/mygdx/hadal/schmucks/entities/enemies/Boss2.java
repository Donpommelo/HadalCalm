package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.SpriteManager;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.enemy.CreateMultiplayerHpScaling;
import com.mygdx.hadal.strategies.enemy.FollowRallyPoints;
import com.mygdx.hadal.strategies.enemy.MovementFloat.FloatingState;
import com.mygdx.hadal.strategies.enemy.TargetNoPathfinding;
import com.mygdx.hadal.utils.b2d.HadalBody;

import static com.mygdx.hadal.constants.Constants.PPM;

/**
 * This is a boss in the game
 * @author Maman Momegranite
 */
public class Boss2 extends EnemyFloating {
				
    private static final float aiAttackCd = 2.0f;
    private static final float aiAttackCd2 = 1.5f;
    private static final float aiAttackCd3 = 1.0f;
    
    private static final int scrapDrop = 15;
    
	private static final float width = 250;
	private static final float height = 250;
	
	private static final int hbWidth = 200;
	private static final int hbHeight = 150;
	
	private static final float scale = 1.0f;
	
	private static final int hp = 8000;

	//the boss' links receive reduced damage
	private static final float linkResist = 0.2f;
	
	private static final Sprite sprite = Sprite.NOTHING;
	
	private final Body[] links = new Body[5];
	private final TextureRegion headSprite, bodySprite;
	private TextureRegion faceSprite;
	
	private int phase = 1;
	private static final float phaseThreshold2 = 0.8f;
	private static final float phaseThreshold3 = 0.4f;
	
	public Boss2(PlayState state, Vector2 startPos, short filter) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hbWidth, hbHeight).scl(scale), sprite, EnemyType.BOSS2, filter, hp, aiAttackCd, scrapDrop);
		this.headSprite = SpriteManager.getFrame(Sprite.KAMABOKO_BODY, 0);
		this.bodySprite = SpriteManager.getFrame(Sprite.KAMABOKO_BODY, 1);
		setFaceSprite();

		addStrategy(new CreateMultiplayerHpScaling(state, this, 1800));
	}

	@Override
	public void create() {
		super.create();

		if (getMainFixture() != null) {
			Filter filter = getMainFixture().getFilterData();
			filter.maskBits = (short) (BodyConstants.BIT_SENSOR | BodyConstants.BIT_PROJECTILE);
			getMainFixture().setFilterData(filter);
		}

		final BodyData me = getBodyData();
		
		//each boss link has the same body data. damaging a link damages the boss with reduced damage
		BodyData link = new BodyData(this, baseHp) {
			
			@Override
			public float receiveDamage(float baseDamage, Vector2 knockback, BodyData perp, Boolean procEffects, Hitbox hbox,
									   DamageSource source, DamageTag... tags) {
				me.receiveDamage(baseDamage * linkResist, knockback, perp, procEffects, hbox, source, tags);
				return 0;
			}
		};
		
		//create each link of the boss' body
		for (int i = 0; i < links.length; i ++) {

			links[i] = new HadalBody(link, startPos, getHboxSize(), BodyConstants.BIT_ENEMY,
					(short) (BodyConstants.BIT_SENSOR | BodyConstants.BIT_PROJECTILE), hitboxFilter)
					.setFixedRotate(false)
					.setSensor(false)
					.addToWorld(world);

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
			links[links.length - 1].setTransform(new Vector2(serverPos), 0);
		}
	}
	
	private final Vector2 linkPosition = new Vector2();
	@Override
	public void render(SpriteBatch batch, Vector2 entityLocation) {
		boolean flip = true;
		float realAngle = getAngle() % (MathUtils.PI * 2);
		if ((realAngle > MathUtils.PI / 2 && realAngle < 3 * MathUtils.PI / 2) || (realAngle < -MathUtils.PI / 2 && realAngle > -3 * MathUtils.PI / 2)) {
			flip = false;
		}

		for (int i = links.length - 1; i >= 0; i--) {
			linkPosition.set(links[i].getPosition());

			batch.draw(bodySprite,
					(flip ? size.x : 0) + linkPosition.x * PPM - size.x / 2, linkPosition.y * PPM - size.y / 2,
					(flip ? -1 : 1) * size.x / 2, size.y / 2,
					(flip ? -1 : 1) * size.x, size.y, 1, 1, 
					(flip ? 0 : 180) + MathUtils.radDeg * links[i].getAngle());
		}
		
		batch.draw(headSprite,
				(flip ? size.x : 0) + entityLocation.x - size.x / 2, 
				entityLocation.y - size.y / 2, 
				(flip ? -1 : 1) * size.x / 2, size.y / 2,
				(flip ? -1 : 1) * size.x, size.y, 1, 1, 
				(flip ? 0 : 180) + MathUtils.radDeg * getAngle());
		
		batch.draw(faceSprite, 
				(flip ? size.x : 0) + entityLocation.x - size.x / 2, 
				entityLocation.y - size.y / 2, 
				(flip ? -1 : 1) * size.x / 2, size.y / 2,
				(flip ? -1 : 1) * size.x, size.y, 1, 1, 
				(flip ? 0 : 180) + MathUtils.radDeg * getAngle());
	}
	
	//this changes the boss' face to a random one
	public void setFaceSprite() {
		faceSprite = SpriteManager.getFrame(Sprite.KAMABOKO_FACE, MathUtils.random(4));
	}
	
	private static final float driftDurationMax = 5.0f;
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
					
					int randomIndex = MathUtils.random(4);
					switch (randomIndex) {
						case 0 -> meleeAttack1();
						case 1 -> meleeAttack2();
						case 2 -> fireBreath();
						case 3 -> slodgeBreath();
						case 4 -> fuguShots();
					}
				} else {
					kamabokoShot(1);
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
					
					int randomIndex = MathUtils.random(4);
					switch (randomIndex) {
						case 0 -> meleeAttack1();
						case 1 -> meleeAttack2();
						case 2 -> fireBreath();
						case 3 -> slodgeBreath();
						case 4 -> fuguShots();
					}
				} else {
					kamabokoShot(2);
				}
				
				if (attackNum % 3 == 0) {
					summonSwimmer();
				}
			}
		}
		
		if (phase == 3) {
			if (attackNum % 2 == 1) {
				int randomIndex = MathUtils.random(4);
				switch (randomIndex) {
					case 0 -> meleeAttack1();
					case 1 -> meleeAttack2();
					case 2 -> fireBreath();
					case 3 -> slodgeBreath();
					case 4 -> fuguShots();
				}
			} else {
				kamabokoShot(3);
			}
			
			if (attackNum % 2 == 0) {
				summonSwimmer();
			}
		}
	}
	
	private static final int bulletSpeed = 18;
	private static final float bulletWindup1 = 0.6f;
	private static final float bulletWindup2 = 0.2f;
	private static final float bulletInterval1 = 0.4f;
	private static final float bulletInterval2 = 0.6f;
	private static final float bulletInterval3 = 0.8f;
	private static final int bulletNumber = 3;

	private void kamabokoShot(int phase) {
		
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
		EnemyUtils.windupParticles(state, this, bulletWindup1, Particle.CHARGING, HadalColor.MAGENTA, 80.0f);
		EnemyUtils.windupParticles(state, this, bulletWindup2, Particle.OVERCHARGE, HadalColor.MAGENTA, 80.0f);
		for (int i = 0; i < bulletNumber; i++) {
			if (phase == 1) {
				shootKamaboko(state, this, bulletSpeed, bulletInterval1, 1);
			}
			if (phase == 2) {
				shootKamaboko(state, this, bulletSpeed, bulletInterval2, 2);
			}
			if (phase == 3) {
				shootKamaboko(state, this, bulletSpeed, bulletInterval3, 3);
			}
		}
		
	}
	
	public void shootKamaboko(final PlayState state, Enemy boss, final float projSpeed, final float duration, final int type) {
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				Vector2 startVelo = new Vector2(0, projSpeed).setAngleDeg(getAttackAngle());
				SyncedAttack.KING_KAMABOKO_SHOT.initiateSyncedAttackSingle(state, enemy, enemy.getPixelPosition(), startVelo, type);
			}
		});
	}
	
	private static final int driftSpeed = 6;
	private static final int charge1Speed = 50;
	private static final float charge1Damage = 5.0f;
	private static final float chargeAttackInterval = 1 / 60.0f;

	private static final int defaultMeleeKB = 50;
	private static final int returnSpeed = 15;
	private void meleeAttack1() {
		EnemyUtils.moveToDummy(state, this, "back", driftSpeed, driftDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -180.0f, 1.0f);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, chargeAttackInterval, 1.0f, 1.5f, SoundEffect.WOOSH, false);
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
		
		if (MathUtils.randomBoolean()) {
			EnemyUtils.moveToDummy(state, this, "highLeft", thrashSpeed, driftDurationMax);
			EnemyUtils.createSoundEntity(state,this,0.0f, chargeAttackInterval,
				1.0f,1.5f, SoundEffect.WOOSH,false);
			EnemyUtils.meleeAttackContinuous(state,this, thrash1Damage, chargeAttackInterval, defaultMeleeKB,0.25f);
			EnemyUtils.moveToDummy(state, this, "platformLeft", thrashDownSpeed, driftDurationMax);
			EnemyUtils.moveToDummy(state, this, "highCenter", thrashSpeed, driftDurationMax);
			EnemyUtils.createSoundEntity(state,this,0.0f, chargeAttackInterval,
				1.0f,1.5f, SoundEffect.WOOSH,false);
			EnemyUtils.meleeAttackContinuous(state,this, thrash1Damage, chargeAttackInterval, defaultMeleeKB,0.25f);
			EnemyUtils.moveToDummy(state, this, "platformCenter", thrashDownSpeed, driftDurationMax);
			EnemyUtils.moveToDummy(state, this, "highRight", thrashSpeed, driftDurationMax);
			EnemyUtils.createSoundEntity(state,this,0.0f, chargeAttackInterval,
				1.0f,1.5f, SoundEffect.WOOSH,false);
			EnemyUtils.meleeAttackContinuous(state,this, thrash1Damage, chargeAttackInterval, defaultMeleeKB,0.25f);
			EnemyUtils.moveToDummy(state, this, "platformRight", thrashDownSpeed, driftDurationMax);
		} else {
			EnemyUtils.moveToDummy(state, this, "highRight", thrashSpeed, driftDurationMax);
			EnemyUtils.createSoundEntity(state,this,0.0f, chargeAttackInterval,
				1.0f,1.5f, SoundEffect.WOOSH,false);
			EnemyUtils.meleeAttackContinuous(state,this, thrash1Damage, chargeAttackInterval, defaultMeleeKB,0.25f);
			EnemyUtils.moveToDummy(state, this, "platformRight", thrashDownSpeed, driftDurationMax);
			EnemyUtils.moveToDummy(state, this, "highCenter", thrashSpeed, driftDurationMax);
			EnemyUtils.createSoundEntity(state,this,0.0f, chargeAttackInterval,
				1.0f,1.5f, SoundEffect.WOOSH,false);
			EnemyUtils.meleeAttackContinuous(state,this, thrash1Damage, chargeAttackInterval, defaultMeleeKB,0.25f);
			EnemyUtils.moveToDummy(state, this, "platformCenter", thrashDownSpeed, driftDurationMax);
			EnemyUtils.moveToDummy(state, this, "highLeft", thrashSpeed, driftDurationMax);
			EnemyUtils.createSoundEntity(state,this,0.0f, chargeAttackInterval,
				1.0f,1.5f, SoundEffect.WOOSH,false);
			EnemyUtils.meleeAttackContinuous(state,this, thrash1Damage, chargeAttackInterval, defaultMeleeKB,0.25f);
			EnemyUtils.moveToDummy(state, this, "platformLeft", thrashDownSpeed, driftDurationMax);
		}

		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, getAngle(), 0.0f);
		EnemyUtils.moveToDummy(state, this, "neutral", returnSpeed, driftDurationMax);
	}
	
	private static final int fireSpeed = 12;
	private static final float breathWindup = 1.5f;
	private static final int fireballNumber = 40;
	private static final float fireballInterval = 0.02f;
	private void fireBreath() {
		EnemyUtils.moveToDummy(state, this, "high", returnSpeed, driftDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -90.0f, 0.0f);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, breathWindup, 0.6f, 0.5f, SoundEffect.FLAMETHROWER, true);
		EnemyUtils.windupParticles(state, this, breathWindup, Particle.FIRE, 40.0f);

		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -180.0f, 0.0f);
		EnemyUtils.createSoundEntity(state, this, 0.0f, fireballNumber * fireballInterval, 0.6f, 1.5f, SoundEffect.FLAMETHROWER, true);
		for (int i = 0; i < fireballNumber; i++) {
			EnemyUtils.fireball(state, this, fireSpeed,  fireballInterval, 2);
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
		EnemyUtils.moveToDummy(state, this, "back", returnSpeed, driftDurationMax);
		EnemyUtils.moveToDummy(state, this, "neutral", returnSpeed, driftDurationMax);
	}
	
	private static final int slodgeSpeed = 10;
	private static final int slodgeNumber = 40;
	private static final float slodgeInterval = 0.02f;
	
	private void slodgeBreath() {
		EnemyUtils.moveToDummy(state, this, "back", returnSpeed, driftDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -180.0f, 0.0f);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, breathWindup, 0.8f, 0.5f, SoundEffect.OOZE, true);
		EnemyUtils.windupParticles(state, this, breathWindup, Particle.SLODGE, 60.0f);

		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -240.0f, 0.0f);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, slodgeNumber * slodgeInterval, 0.8f, 1.5f, SoundEffect.OOZE, true);
		for (int i = 0; i < slodgeNumber; i++) {
			getActions().add(new EnemyAction(this, slodgeInterval) {
				
				private final Vector2 startVelo = new Vector2();
				@Override
				public void execute() {
					startVelo.set(slodgeSpeed, slodgeSpeed).setAngleDeg(getAttackAngle());
					SyncedAttack.KING_KAMABOKO_SLODGE.initiateSyncedAttackSingle(state, enemy, enemy.getPixelPosition(), startVelo);
				}
			});
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
		EnemyUtils.moveToDummy(state, this, "back", returnSpeed, driftDurationMax);
		EnemyUtils.moveToDummy(state, this, "neutral", returnSpeed, driftDurationMax);
	}
	
	private static final int fuguSpeed = 18;
	private static final int fuguNumber = 3;
	private static final float fuguInterval = 0.25f;
	private void fuguShots() {
		EnemyUtils.moveToDummy(state, this, "back", returnSpeed, driftDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -210.0f, 0.0f);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, breathWindup, 0.8f, 1.8f, SoundEffect.OOZE, true);
		EnemyUtils.windupParticles(state, this, breathWindup, Particle.POISON, 40.0f);
		
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -260.0f, 0.0f);
		for (int i = 0; i < fuguNumber; i++) {
			
			getActions().add(new EnemyAction(this, fuguInterval) {
				
				private final Vector2 startVelo = new Vector2();
				@Override
				public void execute() {
					startVelo.set(fuguSpeed, fuguSpeed).setAngleDeg(getAttackAngle());
					SyncedAttack.KING_KAMABOKO_POISON.initiateSyncedAttackSingle(state, enemy, enemy.getPixelPosition(), startVelo);
				}
			});
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
		
		//check destroyed to avoid double-destruction
		if (!destroyed) {
			destroyed = true;
			alive = false;
			if (body != null) {
				world.destroyBody(body);
				body = null;

				//make sure we delete the links as well
				for (final Body link : links) {
					world.destroyBody(link);
				}
			}
		}
	}	

	@Override
	public void setupPathingStrategies() {
		addStrategy(new TargetNoPathfinding(state, this, true));
		addStrategy(new FollowRallyPoints(state, this));
	}

	//boss is never culled to avoid culling links when head is out of view
	@Override
	public boolean isVisible(Vector2 objectiveLocation) {
		return true;
	}
}
