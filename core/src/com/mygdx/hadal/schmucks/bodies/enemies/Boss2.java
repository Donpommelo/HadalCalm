package com.mygdx.hadal.schmucks.bodies.enemies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.ParticleColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitDie;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSlow;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DieParticles;
import com.mygdx.hadal.strategies.hitbox.DiePoison;
import com.mygdx.hadal.strategies.hitbox.DieRagdoll;
import com.mygdx.hadal.strategies.hitbox.DieSound;
import com.mygdx.hadal.strategies.hitbox.HomingUnit;
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
			links[i] = BodyBuilder.createBox(world, startPos, getHboxSize(), 0, 1, 0, false, false, Constants.BIT_ENEMY, (short) (Constants.BIT_SENSOR | Constants.BIT_PROJECTILE),
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
			links[links.length - 1].setTransform(new Vector2(serverPos), 0);
		}
	}
	
	@Override
	public void multiplayerScaling(int numPlayers) {
		getBodyData().addStatus(new StatChangeStatus(state, Stats.MAX_HP, 3000 * numPlayers, getBodyData()));
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
					kamabokoShot(2);
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
				kamabokoShot(3);
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
	private static final float bulletWindup1 = 0.6f;
	private static final float bulletWindup2 = 0.2f;
	private static final float bulletLifespan = 3.0f;
	private static final float bulletInterval1 = 0.4f;
	private static final float bulletInterval2 = 0.6f;
	private static final float bulletInterval3 = 0.8f;
	private static final int bulletNumber = 3;
	
	private final static float homePower = 60.0f;
	private final static float fragSpeed = 10.0f;
	private final static int numProj = 6;
	
	private void kamabokoShot(int phase) {
		
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
		EnemyUtils.windupParticles(state, this, bulletWindup1, Particle.CHARGING, ParticleColor.MAGENTA, 80.0f);
		EnemyUtils.windupParticles(state, this, bulletWindup2, Particle.OVERCHARGE, ParticleColor.MAGENTA, 80.0f);
		for (int i = 0; i < bulletNumber; i++) {
			if (phase == 1) {
				shootKamaboko(state, this, bulletDamage, bulletSpeed1, bulletKB, bulletSize, bulletLifespan, bulletInterval1, 1);
			}
			if (phase == 2) {
				shootKamaboko(state, this, bulletDamage, bulletSpeed2, bulletKB, bulletSize, bulletLifespan, bulletInterval2, 2);
			}
			if (phase == 3) {
				shootKamaboko(state, this, bulletDamage, bulletSpeed2, bulletKB, bulletSize, bulletLifespan, bulletInterval3, 3);
			}
		}
		
	}
	
	public void shootKamaboko(final PlayState state, Enemy boss, final float baseDamage, final float projSpeed, final float knockback, final int size, final float lifespan, final float duration, final int type) {
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				SoundEffect.SPIT.playUniversal(state, getPixelPosition(), 0.8f, 0.6f, false);
				
				Vector2 startVelo = new Vector2(0, projSpeed).setAngle(getAttackAngle());
				Hitbox hbox = new Hitbox(state, getProjectileOrigin(startVelo, size), new Vector2(size, size), lifespan, startVelo, getHitboxfilter(), true, true, enemy, Sprite.NOTHING);
				
				hbox.addStrategy(new ControllerDefault(state, hbox, getBodyData()));
				hbox.addStrategy(new DamageStandard(state, hbox, getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
				hbox.addStrategy(new ContactWallDie(state, hbox, getBodyData()));
				hbox.addStrategy(new ContactUnitDie(state, hbox, getBodyData()));
				hbox.addStrategy(new CreateParticles(state, hbox, getBodyData(), Particle.KAMABOKO_SHOWER, 0.0f, 1.0f));
				hbox.addStrategy(new DieParticles(state, hbox, getBodyData(), Particle.KAMABOKO_IMPACT));
				hbox.addStrategy(new ContactUnitSound(state, hbox, getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
				hbox.addStrategy(new DieSound(state, hbox, getBodyData(), SoundEffect.SQUISH, 0.75f).setPitch(0.8f));
				
				if (type >= 2) {
					hbox.addStrategy(new HomingUnit(state, hbox, getBodyData(), homePower, getHitboxfilter()));
				}
				if (type == 3) {
					hbox.addStrategy(new HitboxStrategy(state, hbox, getBodyData()) {
						
						@Override
						public void die() {
							Vector2 fragVelo = new Vector2(0, fragSpeed);
							Vector2 fragPosition = new Vector2(hbox.getPixelPosition());
							for (int i = 0; i < numProj; i++) {
								fragVelo.setAngle(60 * i);
								fragPosition.set(hbox.getPixelPosition()).add(new Vector2(fragVelo).nor().scl(5));
								Hitbox frag = new Hitbox(state, fragPosition, new Vector2(size, size), lifespan, fragVelo, getHitboxfilter(), true, true, enemy, Sprite.NOTHING);
								frag.addStrategy(new ControllerDefault(state, frag, getBodyData()));
								frag.addStrategy(new DamageStandard(state, frag, getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
								frag.addStrategy(new ContactWallDie(state, frag, getBodyData()));
								frag.addStrategy(new ContactUnitDie(state, frag, getBodyData()));
								frag.addStrategy(new CreateParticles(state, frag, getBodyData(), Particle.KAMABOKO_SHOWER, 0.0f, 1.0f));
								frag.addStrategy(new DieParticles(state, frag, getBodyData(), Particle.KAMABOKO_IMPACT));
								frag.addStrategy(new ContactUnitSound(state, hbox, getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
							}
						}
					});
				}
			}
		});
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
		
		int rand = GameStateManager.generator.nextInt(2);
		switch(rand) {
			case 0:
				EnemyUtils.moveToDummy(state, this, "highLeft", thrashSpeed, driftDurationMax);
				EnemyUtils.createSoundEntity(state, this, 0.0f, chargeAttackInterval, 1.0f, 1.5f, SoundEffect.WOOSH, false);
				EnemyUtils.meleeAttackContinuous(state, this, thrash1Damage, chargeAttackInterval, defaultMeleeKB, 0.25f);
				EnemyUtils.moveToDummy(state, this, "platformLeft", thrashDownSpeed, driftDurationMax);
				
				EnemyUtils.moveToDummy(state, this, "highCenter", thrashSpeed, driftDurationMax);
				EnemyUtils.createSoundEntity(state, this, 0.0f, chargeAttackInterval, 1.0f, 1.5f, SoundEffect.WOOSH, false);
				EnemyUtils.meleeAttackContinuous(state, this, thrash1Damage, chargeAttackInterval, defaultMeleeKB, 0.25f);
				EnemyUtils.moveToDummy(state, this, "platformCenter", thrashDownSpeed, driftDurationMax);
				
				EnemyUtils.moveToDummy(state, this, "highRight", thrashSpeed, driftDurationMax);
				EnemyUtils.createSoundEntity(state, this, 0.0f, chargeAttackInterval, 1.0f, 1.5f, SoundEffect.WOOSH, false);
				EnemyUtils.meleeAttackContinuous(state, this, thrash1Damage, chargeAttackInterval, defaultMeleeKB, 0.25f);
				EnemyUtils.moveToDummy(state, this, "platformRight", thrashDownSpeed, driftDurationMax);
				break;
			case 1:
				EnemyUtils.moveToDummy(state, this, "highRight", thrashSpeed, driftDurationMax);
				EnemyUtils.createSoundEntity(state, this, 0.0f, chargeAttackInterval, 1.0f, 1.5f, SoundEffect.WOOSH, false);
				EnemyUtils.meleeAttackContinuous(state, this, thrash1Damage, chargeAttackInterval, defaultMeleeKB, 0.25f);
				EnemyUtils.moveToDummy(state, this, "platformRight", thrashDownSpeed, driftDurationMax);
				
				EnemyUtils.moveToDummy(state, this, "highCenter", thrashSpeed, driftDurationMax);
				EnemyUtils.createSoundEntity(state, this, 0.0f, chargeAttackInterval, 1.0f, 1.5f, SoundEffect.WOOSH, false);
				EnemyUtils.meleeAttackContinuous(state, this, thrash1Damage, chargeAttackInterval, defaultMeleeKB, 0.25f);
				EnemyUtils.moveToDummy(state, this, "platformCenter", thrashDownSpeed, driftDurationMax);
				
				EnemyUtils.moveToDummy(state, this, "highLeft", thrashSpeed, driftDurationMax);
				EnemyUtils.createSoundEntity(state, this, 0.0f, chargeAttackInterval, 1.0f, 1.5f, SoundEffect.WOOSH, false);
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
	private static final float breathWindup = 1.5f;
	private static final float fireLifespan = 1.7f;
	private static final float burnDuration = 4.0f;

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
			EnemyUtils.fireball(state, this, fireballDamage, burnDamage, fireSpeed, fireKB, fireSize, fireLifespan, burnDuration, fireballInterval, Particle.FIRE);
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
		EnemyUtils.moveToDummy(state, this, "back", returnSpeed, driftDurationMax);
		EnemyUtils.moveToDummy(state, this, "neutral", returnSpeed, driftDurationMax);
	}
	
	private static final int slodgeDamage = 6;
	private static final int slodgeSpeed = 10;
	private static final int slodgeKB = 10;
	private static final Vector2 slodgeSize = new Vector2(50, 50);
	private static final float slodgeLifespan = 2.5f;
	private static final float slodgeSlow = 0.8f;
	private static final float slodgeDuration = 3.0f;
	
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
				
				private Vector2 startVelo = new Vector2();
				@Override
				public void execute() {
					startVelo.set(slodgeSpeed, slodgeSpeed).setAngle(getAttackAngle());
					RangedHitbox hbox = new RangedHitbox(state, getProjectileOrigin(startVelo, slodgeSize.x), slodgeSize, slodgeLifespan, startVelo, getHitboxfilter(), false, true, enemy, Sprite.NOTHING);
					hbox.setRestitution(0.5f);
					hbox.setGravity(3.0f);
					hbox.setDurability(3);
					hbox.addStrategy(new ControllerDefault(state, hbox, getBodyData()));
					hbox.addStrategy(new DamageStandard(state, hbox, getBodyData(), slodgeDamage, slodgeKB, DamageTypes.SLODGE, DamageTypes.RANGED));
					hbox.addStrategy(new CreateParticles(state, hbox, getBodyData(), Particle.SLODGE, 0.0f, 1.0f).setParticleSize(90));
					hbox.addStrategy(new DieParticles(state, hbox, getBodyData(), Particle.SLODGE_STATUS));
					hbox.addStrategy(new ContactUnitSlow(state, hbox, getBodyData(), slodgeDuration, slodgeSlow, Particle.SLODGE_STATUS));
				}
			});
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
		EnemyUtils.moveToDummy(state, this, "back", returnSpeed, driftDurationMax);
		EnemyUtils.moveToDummy(state, this, "neutral", returnSpeed, driftDurationMax);
	}
	
	private static final int fuguDamage = 5;
	private static final int fuguSpeed = 18;
	private static final int fuguKB = 5;
	private static final Vector2 fuguSize = new Vector2(70, 70);
	private static final float fuguLifespan = 2.5f;
	private static final int fuguNumber = 3;
	private static final float fuguInterval = 0.25f;
	
	private final static int poisonRadius = 150;
	private final static float poisonDamage = 0.4f;
	private final static float poisonDuration = 4.0f;
	
	private void fuguShots() {
		EnemyUtils.moveToDummy(state, this, "back", returnSpeed, driftDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -210.0f, 0.0f);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, breathWindup, 0.8f, 1.8f, SoundEffect.OOZE, true);
		EnemyUtils.windupParticles(state, this, breathWindup, Particle.POISON, 40.0f);
		
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -260.0f, 0.0f);
		for (int i = 0; i < fuguNumber; i++) {
			
			getActions().add(new EnemyAction(this, fuguInterval) {
				
				private Vector2 startVelo = new Vector2();
				@Override
				public void execute() {
					SoundEffect.LAUNCHER4.playUniversal(state, getPixelPosition(), 0.4f, 0.8f, false);
					
					startVelo.set(fuguSpeed, fuguSpeed).setAngle(getAttackAngle());
					RangedHitbox hbox = new RangedHitbox(state, getProjectileOrigin(startVelo, fuguSize.x), fuguSize, fuguLifespan, startVelo, getHitboxfilter(), false, true, enemy, Sprite.FUGU);
					hbox.setGravity(3.0f);
					hbox.addStrategy(new ControllerDefault(state, hbox, getBodyData()));
					hbox.addStrategy(new ContactUnitDie(state, hbox, getBodyData()));
					hbox.addStrategy(new ContactWallDie(state, hbox, getBodyData()));
					hbox.addStrategy(new DamageStandard(state, hbox, getBodyData(), fuguDamage, fuguKB, DamageTypes.POISON, DamageTypes.RANGED));
					hbox.addStrategy(new DiePoison(state, hbox, getBodyData(), poisonRadius, poisonDamage, poisonDuration, getHitboxfilter()));
					hbox.addStrategy(new DieRagdoll(state, hbox, getBodyData()));
					hbox.addStrategy(new DieSound(state, hbox, getBodyData(), SoundEffect.DEFLATE, 0.25f));
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
