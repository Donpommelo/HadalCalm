package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.states.PlayState;

/**
 * A Physics schmuck runs custom acceleration physics for their movement.
 * atm, this includes only the player
 * @author Zachary Tu
 *
 */
public class PhysicsSchmuck extends Schmuck {

	private final static float controllerInterval = 1 / 60f;
	
	public PhysicsSchmuck(PlayState state, Vector2 startPos, Vector2 size, short hitboxFilter, int baseHp) {
		super(state, startPos, size, hitboxFilter, baseHp);
	}
	
	private Vector2 force = new Vector2();
	@Override
	public void controller(float delta) {
		super.controller(delta);
		
		//This line ensures that this runs every 1/60 second regardless of computer speed.
		controllerCount += delta;
		while (controllerCount >= controllerInterval) {
			controllerCount -= controllerInterval;
						
			Vector2 currentVel = getLinearVelocity();
			float desiredXVel = 0.0f;
			float desiredYVel = 0.0f;
			
			//set desired velocity depending on move states.
			switch(moveState) {
			case MOVE_LEFT:
				desiredXVel = grounded ? 
						-getBodyData().getXGroundSpeed() : -getBodyData().getXAirSpeed();
				break;
			case MOVE_RIGHT:
				desiredXVel = grounded ? 
						getBodyData().getXGroundSpeed() : getBodyData().getXAirSpeed();
				break;
			default:
				break;
			}
			
			float accelX = 0.0f;
			float accelY = 0.0f;
			
			//Process acceleration based on bodyData stats.
			if (Math.abs(desiredXVel) > Math.abs(currentVel.x)) {
				accelX = grounded ? 
						getBodyData().getXGroundAccel(): getBodyData().getXAirAccel();
			} else {
				accelX = grounded ? 
						getBodyData().getXGroundDeaccel() : getBodyData().getXAirDeaccel();
			}
			
			float newX = accelX * desiredXVel + (1 - accelX) * currentVel.x;
			
			if (Math.abs(desiredYVel) > Math.abs(currentVel.y)) {
				accelY = grounded ? 
						getBodyData().getYGroundAccel(): getBodyData().getYAirAccel();
			} else {
				accelY = grounded ? 
						getBodyData().getYGroundDeaccel() : getBodyData().getYAirDeaccel();
			}
			
			float newY = accelY * desiredYVel + (1 - accelY) * currentVel.y;
			
			force.set(newX - currentVel.x, newY - currentVel.y).scl(getMass());
			applyLinearImpulse(force);
		}
	}
}
