package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public class PhysicsSchmuck extends Schmuck {

	public PhysicsSchmuck(PlayState state, World world, OrthographicCamera camera, RayHandler rays, float w, float h,
			float startX, float startY, float hitboxFilter) {
		super(state, world, camera, rays, w, h, startX, startY, hitboxFilter);
	}
	
	@Override
	public void controller(float delta) {
		super.controller(delta);
		
		//This line ensures that this runs every 1/60 second regardless of computer speed.
		controllerCount+=delta;
		if (controllerCount >= 1/60f) {
			controllerCount -= 1/60f;
						
			Vector2 currentVel = body.getLinearVelocity();
			float desiredXVel = 0.0f;
			float desiredYVel = 0.0f;
			
			//set desired velocity depending on move states. TODO: add movestates for schmucks not affected by gravity.
			switch(moveState) {
			case MOVE_LEFT:
				desiredXVel = grounded ? 
						-bodyData.maxGroundXSpeed * (1 + bodyData.getBonusGroundSpeed()) :
						-bodyData.maxAirXSpeed * (1 + bodyData.getBonusAirSpeed());
				break;
			case MOVE_RIGHT:
				desiredXVel = grounded ? 
						bodyData.maxGroundXSpeed * (1 + bodyData.getBonusGroundSpeed()) : 
						bodyData.maxAirXSpeed * (1 + bodyData.getBonusAirSpeed());
				break;
			default:
				break;
			}
			
			float accelX = 0.0f;
			float accelY = 0.0f;
			
			//Process acceleration based on bodyData stats.
			if (Math.abs(desiredXVel) > Math.abs(currentVel.x)) {
				accelX = grounded ? 
						bodyData.groundXAccel * (1 + bodyData.getBonusGroundAccel()): 
						bodyData.airXAccel * (1 + bodyData.getBonusAirAccel());
			} else {
				accelX = grounded ? 
						bodyData.groundXDeaccel * (1 + bodyData.getBonusGroundDrag()) : 
						bodyData.airXDeaccel * (1 + bodyData.getBonusAirDrag());
			}
			
			float newX = accelX * desiredXVel + (1 - accelX) * currentVel.x;
			
			if (Math.abs(desiredYVel) > Math.abs(currentVel.y)) {
				accelY = grounded ? 
						bodyData.groundYAccel * (1 + bodyData.getBonusGroundDrag()): 
						bodyData.airYAccel * (1 + bodyData.getBonusAirDrag());
			} else {
				accelY = grounded ? 
						bodyData.groundYDeaccel * (1 + bodyData.getBonusGroundDrag()):
						bodyData.airYDeaccel * (1 + bodyData.getBonusAirDrag());
			}
			
			float newY = accelY * desiredYVel + (1 - accelY) * currentVel.y;
			
			Vector2 force = new Vector2(newX - currentVel.x, newY - currentVel.y).scl(body.getMass());
			body.applyLinearImpulse(force, body.getWorldCenter(), true);
		}
		
	}

}
