package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.UserDataType;
import com.mygdx.hadal.schmucks.userdata.FeetData;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.WorldUtil;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

/**
 * Enemies are Schmucks that attack the player.
 * Crawling enemies move right and left along the floor.
 * These enemies can rotate to face the player.
 * @author Brenkuchary Brarkiful
 */
public class EnemyCrawling extends Enemy {
	
	//this the frequency that the physics occurs
	private static final float controllerInterval = 1 / 60f;
		
	//this is the boss's sprite
	private Animation<TextureRegion> floatingSprite;

	//is this enemy moving left or right? what speed? what are the distances that a chasing enemy will attempt to maintain from its target
	private float moveDirection, moveSpeed, minRange, maxRange;
	private CrawlingState currentState;
	
	//feet data used to process enemy groundedness
	private FeetData feetData;
	
	public EnemyCrawling(PlayState state, Vector2 startPos, Vector2 size, Vector2 hboxSize, Sprite sprite, EnemyType type, float startAngle, short filter, int hp, float attackCd, int scrapDrop) {
		super(state, startPos, size, hboxSize, type, filter, hp, attackCd, scrapDrop);
		
		this.moveDirection = startAngle;
		this.moveSpeed = 1.0f;
		this.currentState = CrawlingState.STILL;
		
		if (!sprite.equals(Sprite.NOTHING)) {
			this.floatingSprite = new Animation<>(PlayState.spriteAnimationSpeedFast, sprite.getFrames());
			this.floatingSprite.setPlayMode(PlayMode.LOOP_PINGPONG);
		}
	}

	@Override
	public void create() {
		super.create();
		body.setGravityScale(1.0f);
		
		Filter filter = getMainFixture().getFilterData();
		filter.maskBits = (short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_DROPTHROUGHWALL);
		getMainFixture().setFilterData(filter);
		
		if (state.isServer()) {
			this.feetData = new FeetData(UserDataType.FEET, this);
			
			Fixture feet = FixtureBuilder.createFixtureDef(body, new Vector2(0.5f,  - hboxSize.y / 2), new Vector2(hboxSize.x - 2, hboxSize.y / 8), true, 0, 0, 0, 0,
					Constants.BIT_SENSOR, (short)(Constants.BIT_WALL | Constants.BIT_DROPTHROUGHWALL), hitboxfilter);
			
			feet.setUserData(feetData);
		}
	}

	private final Vector2 force = new Vector2();
	private final Vector2 currentVel = new Vector2();
	private float controllerCount;
	@Override
	public void controller(float delta) {		
		super.controller(delta);
		
		grounded = feetData.getNumContacts() > 0;
		
		switch (currentState) {
		case AVOID_PITS:
			if (grounded) {
				processCollision(true);
			}
			break;
		case BACK_FORTH:
			processCollision(false);
			break;
		case CHASE:
			if (getMoveTarget() != null) {				
				if (getMoveTarget().isAlive()) {
					moveSpeed = 1.0f;
					float dist = getPixelPosition().x - getMoveTarget().getPixelPosition().x;
					
					//attempt to move towards target if too far away and away if target is too close
					if (dist > maxRange) {
						moveDirection = -1.0f;
					} else if (dist < -maxRange) {
						moveDirection = 1.0f;
					} else if (dist < minRange && dist > 0) {
						moveDirection = 1.0f;
					} else if (dist > -minRange && dist < 0) {
						moveDirection = -1.0f;
					} else {
						if (dist > 0) {
							moveDirection = -1.0f;
						} else {
							moveDirection = 1.0f;
						}
						moveSpeed = 0.0f;
					}
				}
			}
			break;
		case STILL:
			moveSpeed = 0;
			break;
		default:
			break;
		}

		//This line ensures that this runs every 1/60 second regardless of computer speed.
		controllerCount += delta;
		while (controllerCount >= controllerInterval) {
			controllerCount -= controllerInterval;
						
			//set desired velocity depending on move states.
			currentVel.set(getLinearVelocity());
			float desiredXVel = getBodyData().getXGroundSpeed() * moveDirection * moveSpeed;
			
			//Process acceleration based on bodyData stats.
			float accelX;
			if (Math.abs(desiredXVel) > Math.abs(currentVel.x)) {
				accelX = getBodyData().getXGroundAccel();
			} else {
				accelX = getBodyData().getXGroundDeaccel();
			}
			float newX = accelX * desiredXVel + (1 - accelX) * currentVel.x;
			
			//apply resulting force
			force.set(newX - currentVel.x, 0).scl(getMass());
			applyLinearImpulse(force);
		}
	}
	
	/**
	 * This method is used by crawling enemies to process running into walls/edges of platforms
	 */
	private final Vector2 endPt = new Vector2();
	private final Vector2 entityWorldLocation = new Vector2();
	private static final float distCheck = 3.0f;
	private float shortestFraction;
	private void processCollision(boolean avoidPits) {
		
		//if not moving, treat enemy as if it were facing forwards
		if (moveDirection == 0) { moveDirection = 1.0f; }
		entityWorldLocation.set(getPosition());
		endPt.set(entityWorldLocation).add(distCheck * moveDirection, 0);
		shortestFraction = 1.0f;
		
		//raycast in the direction we are walking.
		if (WorldUtil.preRaycastCheck(entityWorldLocation, endPt)) {
			state.getWorld().rayCast((fixture, point, normal, fraction) -> {
				if (fixture.getFilterData().categoryBits == Constants.BIT_WALL) {
					if (fraction < shortestFraction) {
						shortestFraction = fraction;
						return fraction;
					}
				}
				return -1.0f;
			}, entityWorldLocation, endPt);
		}
		
		//if we are running into a wall, we turn around
		if (shortestFraction < 1.0f) {
			moveDirection = -moveDirection;
		} else if (avoidPits) {
			
			//if we avoid pits, raycast in the direction we are walking downwards
			endPt.set(entityWorldLocation).add(moveDirection * distCheck, -distCheck);
			shortestFraction = 1.0f;
			if (WorldUtil.preRaycastCheck(entityWorldLocation, endPt)) {
				state.getWorld().rayCast((fixture, point, normal, fraction) -> {
					if (fixture.getFilterData().categoryBits == Constants.BIT_WALL ||  fixture.getFilterData().categoryBits == Constants.BIT_DROPTHROUGHWALL) {
						if (fraction < shortestFraction) {
							shortestFraction = fraction;
							return fraction;
						}
					}
					return -1.0f;
				}, entityWorldLocation, endPt);
			}
			
			//if we see nothing, we are standing next to a pit and we turn around
			if (shortestFraction == 1.0f) {
				moveDirection = -moveDirection;
			}
		}
	}

	private final Vector2 entityLocation = new Vector2();
	@Override
	public void render(SpriteBatch batch) {
		
		boolean flip = moveDirection < 0;

		entityLocation.set(getPixelPosition());
		batch.draw(floatingSprite.getKeyFrame(animationTime, true),
				(flip ? 0 : size.x) + entityLocation.x - size.x / 2, 
				entityLocation.y - getHboxSize().y / 2, 
				size.x / 2,
				(flip ? 1 : -1) * size.y / 2, 
				(flip ? 1 : -1) * size.x, size.y, 1, 1, 0);
		super.render(batch);
	}
	
	@Override
	public void onServerSync() {
		state.getSyncPackets().add(new PacketsSync.SyncSchmuckAngled(entityID, getPosition(), currentVel, entityAge,
				state.getTimer(), moveState, getBodyData().getCurrentHp(), moveDirection));
	}
	
	@Override
	public void onClientSync(Object o) {
		super.onClientSync(o);
		if (o instanceof PacketsSync.SyncSchmuckAngled p) {
			serverAngle.set(0, 0);
			moveDirection = p.angle;
		}
	}
	
	private final Vector2 originPt = new Vector2();
	private final Vector2 addVelo = new Vector2();
	@Override
	public Vector2 getProjectileOrigin(Vector2 startVelo, float projSize) {
		
		if (moveDirection > 0) {
			originPt.set(getPixelPosition()).add(addVelo.set(getHboxSize().x / 2, 0));
		} else {
			originPt.set(getPixelPosition()).add(addVelo.set(getHboxSize().x / 2, 0).scl(-1.0f));
		}
		
		return originPt;
	}
	
	public float getMoveDirection() { return moveDirection; }

	public void setMoveSpeed(float moveSpeed) { this.moveSpeed = moveSpeed; }

	public void setCurrentState(CrawlingState currentState) { this.currentState = currentState; }

	public void setMinRange(float minRange) { this.minRange = minRange; }

	public void setMaxRange(float maxRange) { this.maxRange = maxRange; }

	public enum CrawlingState {
		BACK_FORTH,
		AVOID_PITS,
		CHASE,
		STILL
	}
}
