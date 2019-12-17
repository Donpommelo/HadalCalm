package com.mygdx.hadal.schmucks.bodies.enemies;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.ArrayList;

import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.BossUtils;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Ragdoll;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * Enemies are Schmucks that attack the player.
 * Floating enemies are the basic fish-enemies of the game
 * @author Zachary Tu
 *
 */
public class Boss1 extends Enemy {
				
    private static final float aiAttackCd = 2.0f;
    private float aiAttackCdCount = 0.0f;
    private float aiActionCdCount = 0.0f;
    
    private float angle;
	private float desiredAngle;
	
  	//These are used for raycasting to determing whether the player is in vision of the fish.
  	private float shortestFraction;
  	private Schmuck homeAttempt;
	private Fixture closestFixture;
  	
  	private TextureAtlas atlas;
	private TextureRegion fishSprite;
	protected SteeringBehavior<Vector2> face;
	
	
	private static final int width = 250;
	private static final int height = 161;
	
	private static final int hbWidth = 161;
	private static final int hbHeight = 250;
	
	private static final float scale = 1.0f;
	
	private static final String spriteId = "torpedofish_swim";
	
	private int moveSpeed = defaultSpeed;
	private int spinSpeed;
	
	private Event movementTarget;
	
	private ArrayList<BossAction> actions;
	private BossAction currentAction;
	private BossState currentState;
	
	/**
	 * Enemy constructor is run when an enemy spawner makes a new enemy.
	 * @param state: current gameState
	 * @param world: box2d world
	 * @param camera: game camera
	 * @param rays: game rayhandler
	 * @param width: width of enemy
	 * @param height: height of enemy
	 * @param x: enemy starting x position.
	 * @param y: enemy starting x position.
	 */
	public Boss1(PlayState state, int x, int y, enemyType type, short filter) {
		super(state, hbWidth * scale, hbHeight * scale, x, y, type, filter);
		this.angle = 0;
		this.desiredAngle = 0;
		
		atlas = (TextureAtlas) HadalGame.assetManager.get(AssetList.FISH_ATL.toString());
		fishSprite = atlas.findRegion(spriteId);
		
		this.actions = new ArrayList<BossAction>();
		this.currentState = BossState.TRACKING_PLAYER;
	}
	
	/**
	 * Create the enemy's body and initialize enemy's user data.
	 */
	@Override
	public void create() {
		this.bodyData = new BodyData(this);
		this.body = BodyBuilder.createBox(world, startX, startY,  hbWidth * scale, hbHeight * scale, 0, 1, 0, false, true, Constants.BIT_ENEMY, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_ENEMY),
				hitboxfilter, false, bodyData);
		
		//temp way of more Hp
		this.bodyData.addStatus(new StatChangeStatus(state, Stats.MAX_HP, 2000, bodyData));

	}

	/**
	 * Enemy ai goes here. Default enemy behaviour just walks right/left towards player and fires weapon.
	 */
	@Override
	public void controller(float delta) {		
		super.controller(delta);
		
		if (movementTarget != null) {
			if (movementTarget.getBody() != null) {
				Vector2 dist = movementTarget.getPosition().sub(getPosition()).scl(PPM);
				
				if ((int)dist.len2() <= 100) {
					setLinearVelocity(0, 0);
					movementTarget = null;
					
					aiActionCdCount = 0;
					currentAction = null;
					
				} else {
					setLinearVelocity(dist.nor().scl(moveSpeed));
				}
			}
		}
		
		angle = angle + (desiredAngle - angle) * 0.02f;
		setOrientation((float) ((angle + 270) * Math.PI / 180));
		
		switch(currentState) {
			
		case SPINNING:
			desiredAngle += spinSpeed;
			break;
		case TRACKING_PLAYER:
			if (target != null) {				
				if (target.isAlive()) {
					desiredAngle = (float)(Math.atan2(
							target.getPosition().y - getPosition().y ,
							target.getPosition().x - getPosition().x) * 180 / Math.PI);
				}
			}
			break;
		default:
			break;
		
		}
		
		if (aiActionCdCount > 0) {
			aiActionCdCount -= delta;
		} else {
			if (aiAttackCdCount > 0) {
				aiAttackCdCount -= delta;
			}
		}
		
		if (aiAttackCdCount <= 0) {
			aiAttackCdCount = aiAttackCd;
			acquireTarget();
			attackInitiate();
		}

		if (aiActionCdCount <= 0 || currentAction == null) {
			if (!actions.isEmpty()) {
				currentAction = actions.remove(0);
				aiActionCdCount = currentAction.getDuration();
				
				currentAction.execute();
			} else {
				if (aiAttackCdCount <= 0) {
					aiAttackCdCount = aiAttackCd;
					currentState = BossState.TRACKING_PLAYER;
				}
			}
		}
	}
	
	public void attackInitiate() {
		
		int randomIndex = GameStateManager.generator.nextInt(4);
		
		switch(randomIndex) {
		case 0: 
			chargeAttack1();
			break;
		case 1: 
			chargeAttack2();
			break;
		case 2: 
			spawnAdds();
			break;
		case 3: 
			fireBreath();
			break;
		}
	}
	
	private static final int defaultSpeed = 20;
	private static final int defaultSpinSpeed = 40;
	private static final int charge1Speed = 40;
	private static final int charge2Speed = 30;
	private static final int defaultMeleeDamage = 12;
	private static final int defaultMeleeKB = 50;
	
	private void chargeAttack1() {
		BossUtils.moveToRandomCorner(state, this, defaultSpeed);
		BossUtils.changeTrackingState(this, BossState.SPINNING, defaultSpinSpeed, 0.75f);
		BossUtils.changeTrackingState(this, BossState.TRACKING_PLAYER, 0, 0.0f);
		BossUtils.moveToPlayer(state, this, target, charge1Speed, 0.0f);
		BossUtils.meleeAttack(state, this, defaultMeleeDamage,defaultMeleeKB, target, 1.5f);
	}
	
	private void chargeAttack2() {
		int corner = BossUtils.moveToRandomCorner(state, this, defaultSpeed);
		BossUtils.changeTrackingState(this, BossState.SPINNING, defaultSpinSpeed, 0.2f);
		BossUtils.meleeAttack(state, this, defaultMeleeDamage, defaultMeleeKB, target, 2.25f);
		switch (corner) {
		case 0:
			BossUtils.moveToDummy(state, this, "2", charge2Speed);
			BossUtils.moveToDummy(state, this, "8", charge2Speed);
			BossUtils.moveToDummy(state, this, "6", charge2Speed);
			BossUtils.moveToDummy(state, this, "0", charge2Speed);
			break;
		case 1:
			BossUtils.moveToDummy(state, this, "8", charge2Speed);
			BossUtils.moveToDummy(state, this, "6", charge2Speed);
			BossUtils.moveToDummy(state, this, "0", charge2Speed);
			BossUtils.moveToDummy(state, this, "2", charge2Speed);
			break;
		case 2:
			BossUtils.moveToDummy(state, this, "0", charge2Speed);
			BossUtils.moveToDummy(state, this, "2", charge2Speed);
			BossUtils.moveToDummy(state, this, "8", charge2Speed);
			BossUtils.moveToDummy(state, this, "6", charge2Speed);
			break;
		case 3:
			BossUtils.moveToDummy(state, this, "6", charge2Speed);
			BossUtils.moveToDummy(state, this, "0", charge2Speed);
			BossUtils.moveToDummy(state, this, "2", charge2Speed);
			BossUtils.moveToDummy(state, this, "8", charge2Speed);
			break;
		}
		BossUtils.changeTrackingState(this, BossState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private static final int numAdds = 3;
	private void spawnAdds() {
		BossUtils.moveToDummy(state, this, "4", defaultSpeed);
		BossUtils.changeTrackingState(this, BossState.SPINNING, defaultSpinSpeed, 0.75f);
		BossUtils.changeTrackingState(this, BossState.TRACKING_PLAYER, 0, 0.0f);
		for (int i = 0; i < numAdds; i++) {
			BossUtils.spawnAdds(state, this, enemyType.TORPEDOFISH, 1, 1.5f);
		}
	}
	
	private static final int fireballDamage = 1;
	private static final int burnDamage = 1;
	private static final int fireSpeed = 12;
	private static final int fireKB = 10;
	private static final int fireSize = 50;
	private static final float fireLifespan = 1.5f;
	private static final float burnDuration = 4.0f;

	private static final int fireballNumber = 30;
	private static final float fireballInterval = 0.75f;
	
	private void fireBreath() {
		int wall = BossUtils.moveToRandomWall(state, this, defaultSpeed);
		
		switch (wall) {
		case 0 :
			BossUtils.changeTrackingState(this, BossState.FREE, -90.0f, 1.0f);
			BossUtils.changeTrackingState(this, BossState.FREE, 90.0f, 0.0f);
			for (int i = 0; i < fireballNumber; i++) {
				BossUtils.fireball(state, this, fireballDamage, burnDamage, fireSpeed, fireKB, fireSize, 0, fireLifespan, burnDuration, fireballInterval);
			}
			break;
		case 1: 
			BossUtils.changeTrackingState(this, BossState.FREE, -90.0f, 1.0f);
			BossUtils.changeTrackingState(this, BossState.FREE, -270.0f, 0.0f);
			for (int i = 0; i < fireballNumber; i++) {
				BossUtils.fireball(state, this, fireballDamage, burnDamage, fireSpeed, fireKB, fireSize, 0, fireLifespan, burnDuration, fireballInterval);
			}
		}
		BossUtils.changeTrackingState(this, BossState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	public void acquireTarget() {
		world.QueryAABB((new QueryCallback() {

			@Override
			public boolean reportFixture(Fixture fixture) {
				if (fixture.getUserData() instanceof BodyData) {
					homeAttempt = ((BodyData)fixture.getUserData()).getSchmuck();
					shortestFraction = 1.0f;
					
					
				  	if (getPosition().x != homeAttempt.getPosition().x || 
				  			getPosition().y != homeAttempt.getPosition().y) {
				  		world.rayCast(new RayCastCallback() {

							@Override
							public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
								if (fixture.getUserData() == null) {
									if (fraction < shortestFraction) {
										shortestFraction = fraction;
										closestFixture = fixture;
										return fraction;
									}
								} else if (fixture.getUserData() instanceof BodyData) {
									if (((BodyData)fixture.getUserData()).getSchmuck().getHitboxfilter() != hitboxfilter) {
										if (fraction < shortestFraction) {
											shortestFraction = fraction;
											closestFixture = fixture;
											return fraction;
										}
									}
								} 
								return -1.0f;
							}
							
						}, getPosition(), homeAttempt.getPosition());
						if (closestFixture != null) {
							if (closestFixture.getUserData() instanceof BodyData) {
								target = ((BodyData)closestFixture.getUserData()).getSchmuck();
							}
						} 
					}
				}
				return true;
			}
		}), 
			getPosition().x - aiRadius, getPosition().y - aiRadius, 
			getPosition().x + aiRadius, getPosition().y + aiRadius);
	}
	
	/**
	 * draws enemy
	 */
	@Override
	public void render(SpriteBatch batch) {

		boolean flip = false;
		if (getOrientation() > Math.PI && getOrientation() < 2 * Math.PI) {
			flip = true;
		}
		
		if (flashingCount > 0) {
			batch.setShader(HadalGame.shader);
		}
		
		batch.draw(fishSprite, 
				getPosition().x * PPM - hbHeight * scale / 2, 
				(flip ? height * scale : 0) + getPosition().y * PPM - hbWidth * scale / 2, 
				hbHeight * scale / 2, 
				(flip ? -1 : 1) * hbWidth * scale / 2,
				width * scale, (flip ? -1 : 1) * height * scale, 1, 1, 
				(float) Math.toDegrees(getOrientation()) - 90);

		if (flashingCount > 0) {
			batch.setShader(null);
		}
	}
	
	@Override
	public boolean queueDeletion() {
		if (alive) {
			new Ragdoll(state, hbHeight * scale, hbWidth * scale, 
					(int)(getPosition().x * PPM), 
					(int)(getPosition().y * PPM), fishSprite, getLinearVelocity(), 0.5f);
		}
		return super.queueDeletion();
	}
	
	public Event getMovementTarget() {
		return movementTarget;
	}

	public void setMovementTarget(Event movementTarget) {
		this.movementTarget = movementTarget;
	}

	public ArrayList<BossAction> getActions() {
		return actions;
	}

	public void setCurrentState(BossState currentState) {
		this.currentState = currentState;
	}
	
	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public void setDesiredAngle(float desiredAngle) {
		this.desiredAngle = desiredAngle;
	}

	public void setMoveSpeed(int moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	public void setSpinSpeed(int spinSpeed) {
		this.spinSpeed = spinSpeed;
	}

	public enum BossAttack {
		SPAWN_ADDS,
		CHARGE1,
		CHARGE2,
		FIRE_SPIN
	}
	
	public enum BossState {
		TRACKING_PLAYER,
		LOCKED,
		FREE,
		SPINNING
	}
}