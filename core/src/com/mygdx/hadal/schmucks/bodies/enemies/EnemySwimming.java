package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Zachary Tu
 *
 */
public class EnemySwimming extends EnemyFloating {
	
	//this the frequency that the physics occurs
	private final static float controllerInterval = 1 / 60f;
		
	private SwimmingState currentState;
	
	private float moveSpeed, minRange, maxRange;
	private Vector2 moveDirection = new Vector2();
	
	public EnemySwimming(PlayState state, Vector2 startPos, Vector2 size, Vector2 hboxSize, Sprite sprite, EnemyType type, float startAngle, short filter, int hp, float attackCd, int scrapDrop, SpawnerSchmuck spawner) {
		super(state, startPos, size, hboxSize, sprite, type, filter, hp, attackCd, scrapDrop, spawner);
		this.moveSpeed = 1.0f;
		this.moveDirection = new Vector2(1, 0).setAngle(startAngle);
		currentState = SwimmingState.STILL;
	}
	
	private Vector2 force = new Vector2();
	private Vector2 currentVel = new Vector2();
	private Vector2 currentDirection = new Vector2();
	@Override
	public void controller(float delta) {		
		super.controller(delta);
		
		switch(currentState) {
		case CHASE:
			if (target != null) {				
				if (target.isAlive()) {
					moveSpeed = 1.0f;
					
					moveDirection.set(getPosition()).sub(target.getPosition());
					float dist = moveDirection.len2();

					 if (dist > maxRange * maxRange) {
						moveDirection.scl(-1.0f);
					} else if (dist < maxRange * maxRange && dist > minRange * minRange){
						moveSpeed = 0.0f;
					}
				}
			}
			break;
		case STILL:
			moveSpeed = 0;
			break;
		case WANDER:
			break;
		default:
			break;
		}
		
		controllerCount += delta;
		while (controllerCount >= controllerInterval) {
			controllerCount -= controllerInterval;
						
			currentVel.set(getLinearVelocity());
			currentDirection.set(moveDirection).nor().scl(moveSpeed);
			float desiredXVel = getBodyData().getXAirSpeed() * currentDirection.x;
			float desiredYVel = getBodyData().getXAirSpeed() * currentDirection.y ;

			float accelX = 0.0f;
			float accelY = 0.0f;
			
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
	
	public enum SwimmingState {
		STILL,
		WANDER,
		CHASE
	}
}
