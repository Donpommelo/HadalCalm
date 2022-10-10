package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.constants.Constants;

/**
 * A Physics schmuck runs custom acceleration physics for their movement.
 * atm, this includes only the player
 * @author Duldwin Droshibish
 */
public class PhysicsSchmuck extends Schmuck {

	public PhysicsSchmuck(PlayState state, Vector2 startPos, Vector2 size, String name, short hitboxFilter, int baseHp) {
		super(state, startPos, size, name, hitboxFilter, baseHp);
	}
	
	private final Vector2 force = new Vector2();
	private final Vector2 currentVel = new Vector2();
	@Override
	public void controller(float delta) {
		super.controller(delta);
		applyForce(delta);
	}
	
	//client players also process force for prediction purposes
	@Override
	public void clientController(float delta) {
		super.clientController(delta);
		applyForce(delta);
	}

	private float controllerCount;
	protected void applyForce(float delta) {
		//This line ensures that this runs every 1/60 second regardless of computer speed.
		controllerCount += delta;
		while (controllerCount >= Constants.INTERVAL) {
			controllerCount -= Constants.INTERVAL;
						
			currentVel.set(getLinearVelocity());
			float desiredXVel = 0.0f;
			float desiredYVel = 0.0f;
			
			//set desired velocity depending on move states.
			switch (moveState) {
			case MOVE_LEFT:
				desiredXVel = grounded ? -getBodyData().getXGroundSpeed() : -getBodyData().getXAirSpeed();
				break;
			case MOVE_RIGHT:
				desiredXVel = grounded ? getBodyData().getXGroundSpeed() : getBodyData().getXAirSpeed();
				break;
			default:
				break;
			}
			
			float accelX;
			float accelY;
			
			//Process acceleration based on bodyData stats.
			if (Math.abs(desiredXVel) > Math.abs(currentVel.x)) {
				accelX = grounded ? getBodyData().getXGroundAccel(): getBodyData().getXAirAccel();
			} else {
				accelX = grounded ? getBodyData().getXGroundDeaccel() : getBodyData().getXAirDeaccel();
			}
			float newX = accelX * desiredXVel + (1 - accelX) * currentVel.x;
			
			if (Math.abs(desiredYVel) > Math.abs(currentVel.y)) {
				accelY = grounded ? getBodyData().getYGroundAccel(): getBodyData().getYAirAccel();
			} else {
				accelY = grounded ? getBodyData().getYGroundDeaccel() : getBodyData().getYAirDeaccel();
			}
			float newY = accelY * desiredYVel + (1 - accelY) * currentVel.y;

			//apply resulting force
			force.set(newX - currentVel.x, newY - currentVel.y).scl(getMass());
			applyLinearImpulse(force);
		}
	}
}
