package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.states.PlayState;

/**
 * A swimming enemy is an floating enemy that uses a certain physics system to swim towards or away from a target (usually the player)
 * this physics system works very similarly to the player's own movement
 * @author Grulgolois Ghomato
 */
public class EnemySwimming extends EnemyFloating {
	
	//this the frequency that the physics occurs
	private static final float controllerInterval = 1 / 60f;
	
	//this is the enemy's ai state
	private SwimmingState currentState;
	
	//the speed of the enemy and the ranges that it tries to stay between from its target
	private float moveSpeed, minRange, maxRange;
	
	//noise determines the amount of randomness there is to this enemy's movement. (cd = frequency of noise, radius = magnitude of noise)
	private static final float noiseCd = 0.75f;
	private float noiseCdCount = noiseCd;
	private float noiseRadius = 8.0f;
	
	//this vector controls the movement of the enemy
	private final Vector2 moveDirection = new Vector2();
	
	public EnemySwimming(PlayState state, Vector2 startPos, Vector2 size, Vector2 hboxSize, Sprite sprite, EnemyType type, float startAngle, short filter, int hp, float attackCd, int scrapDrop, SpawnerSchmuck spawner) {
		super(state, startPos, size, hboxSize, sprite, type, filter, hp, attackCd, scrapDrop, spawner);
		this.moveSpeed = 1.0f;
		this.moveDirection.set(1, 0).setAngle(startAngle);
		currentState = SwimmingState.STILL;
	}
	
	private final Vector2 force = new Vector2();
	private final Vector2 currentVel = new Vector2();
	private final Vector2 currentDirection = new Vector2();
	private final Vector2 currentNoise = new Vector2();
	@Override
	public void controller(float delta) {		
		super.controller(delta);
		
		switch(currentState) {
		case CHASE:
			//chasing enemies set their move direction towards their target if far away and away if too close.
			if (getMoveTarget() != null) {				
				if (getMoveTarget().isAlive()) {
					moveSpeed = 1.0f;
					
					moveDirection.set(getPosition()).sub(getMoveTarget().getPosition());
					float dist = moveDirection.len2();

					 if (dist > maxRange * maxRange) {
						moveDirection.scl(-1.0f);
					} else if (dist < maxRange * maxRange && dist > minRange * minRange) {
						moveSpeed = 0.0f;
					}
				}
			}
			break;
		case STILL:
			moveSpeed = 0;
			break;
		case WANDER:
			moveDirection.set(currentNoise);
			break;
		default:
			break;
		}
		
		//process enemy movement noise
		noiseCdCount += delta;
		while (noiseCdCount >= noiseCd) {
			noiseCdCount -= noiseCd;
			currentNoise.setToRandomDirection().scl(noiseRadius);
		}
		moveDirection.add(currentNoise);
		
		//process enemy swimming physics
		controllerCount += delta;
		while (controllerCount >= controllerInterval) {
			controllerCount -= controllerInterval;
			
			//set desired velocity depending on move states.
			currentVel.set(getLinearVelocity());
			currentDirection.set(moveDirection).nor().scl(moveSpeed);
			float desiredXVel = getBodyData().getXAirSpeed() * currentDirection.x;
			float desiredYVel = getBodyData().getXAirSpeed() * currentDirection.y ;

			float accelX;
			float accelY;
			
			//Process acceleration based on bodyData stats.
			if (Math.abs(desiredXVel) > Math.abs(currentVel.x)) {
				accelX = getBodyData().getXAirAccel();
			} else {
				accelX = getBodyData().getXAirDeaccel();
			}
			float newX = accelX * desiredXVel + (1 - accelX) * currentVel.x;
			
			if (Math.abs(desiredYVel) > Math.abs(currentVel.y)) {
				accelY = getBodyData().getYAirAccel();
			} else {
				accelY = getBodyData().getYAirDeaccel();
			}
			float newY = accelY * desiredYVel + (1 - accelY) * currentVel.y;
			
			//apply resulting force
			force.set(newX - currentVel.x, newY - currentVel.y).scl(getMass());
			applyLinearImpulse(force);
		}
	}
	
	public Vector2 getMoveDirection() { return this.moveDirection; }
	
	public void setMoveSpeed(float moveSpeed) { this.moveSpeed = moveSpeed; }
	
	public SwimmingState getCurrentState() { return currentState; }

	public void setCurrentState(SwimmingState currentState) { this.currentState = currentState; }

	public void setMinRange(float minRange) { this.minRange = minRange; }

	public void setMaxRange(float maxRange) { this.maxRange = maxRange; }
	
	public void setNoiseRadius(float noiseRadius) { this.noiseRadius = noiseRadius; }
	
	public enum SwimmingState {
		STILL,
		WANDER,
		CHASE
	}
}
