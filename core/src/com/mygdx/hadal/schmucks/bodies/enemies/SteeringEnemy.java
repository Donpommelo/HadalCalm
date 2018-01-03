package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.SteeringUtil;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class SteeringEnemy extends Enemy implements Steerable<Vector2>{

	boolean tagged;
	float boundingRadius;
	float maxLinearSpeed, maxLinearAcceleration;
	float maxAngularSpeed, maxAngularAcceleration;
	
	SteeringBehavior<Vector2> behavior;
	SteeringAcceleration<Vector2> steeringOutput;
	
	public SteeringEnemy(PlayState state, World world, OrthographicCamera camera, RayHandler rays, float width,
			float height, int x, int y) {
		super(state, world, camera, rays, width, height, x, y);
		
		this.boundingRadius = 500;
		
		this.maxLinearSpeed = 100;
		this.maxLinearAcceleration = 1000;
		this.maxAngularSpeed = 10;
		this.maxAngularAcceleration = 5;
		
		this.tagged = false;
		
		this.steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
		
	}

	public void create() {
		this.bodyData = new BodyData(world, this);
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 0, 1, 0f, false, false, Constants.BIT_ENEMY, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_PLAYER | Constants.BIT_ENEMY),
				Constants.ENEMY_HITBOX, false, bodyData);	
		
		Arrive<Vector2> arriveSB = new Arrive<Vector2>(this, state.getPlayer())
				.setArrivalTolerance(2f)
				.setDecelerationRadius(10);
		
		this.setBehavior(arriveSB);
	}
	
	public void controller (float delta) {
		if (behavior != null) {
			behavior.calculateSteering(steeringOutput);
			applySteering(delta);
		}
	}
	
	public void applySteering(float delta) {
		boolean anyAcceleration = false;
		
		if (!steeringOutput.linear.isZero()) {
			Vector2 force = steeringOutput.linear.scl(delta);
			body.applyForceToCenter(force, true);
			anyAcceleration = true;
		}
		
		if (steeringOutput.angular != 0) {
			body.applyTorque(steeringOutput.angular, true);
			anyAcceleration = true;
		} else {
			Vector2 linVel = getLinearVelocity();
			if (!linVel.isZero()) {
				float newOrientation = vectorToAngle(linVel);
				body.setAngularVelocity((newOrientation - getAngularVelocity()) * delta);
				body.setTransform(body.getPosition(), newOrientation);
			}
		}
		
		if (anyAcceleration) {
			Vector2 velocity = body.getLinearVelocity();
			float currentSpeedSquare = velocity.len2();
			if (currentSpeedSquare > maxLinearSpeed * maxLinearSpeed) {
				body.setLinearVelocity(velocity.scl(maxLinearSpeed / (float) Math.sqrt(currentSpeedSquare)));
			}
			
			if (body.getAngularVelocity() > maxAngularSpeed) {
				body.setAngularVelocity(maxAngularSpeed);
			}
		}
	}
	
	@Override
	public Vector2 getPosition() {
		return body.getPosition();
	}

	@Override
	public float getOrientation() {
		return body.getAngle();
	}

	@Override
	public void setOrientation(float orientation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float vectorToAngle(Vector2 vector) {
		return SteeringUtil.vectorToAngle(vector);
	}

	@Override
	public Vector2 angleToVector(Vector2 outVector, float angle) {
		return SteeringUtil.angleToVector(outVector, angle);
	}

	@Override
	public Location<Vector2> newLocation() {
		System.out.println("newLocation was run?");
		return null;//new Location<Vector2>();
	}

	@Override
	public float getZeroLinearSpeedThreshold() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setZeroLinearSpeedThreshold(float value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getMaxLinearSpeed() {
		return maxLinearSpeed;
	}

	@Override
	public void setMaxLinearSpeed(float maxLinearSpeed) {
		this.maxLinearSpeed = maxLinearSpeed;
	}

	@Override
	public float getMaxLinearAcceleration() {
		return maxLinearAcceleration;
	}

	@Override
	public void setMaxLinearAcceleration(float maxLinearAcceleration) {
		this.maxLinearAcceleration = maxLinearAcceleration;
	}

	@Override
	public float getMaxAngularSpeed() {
		return maxAngularSpeed;
	}

	@Override
	public void setMaxAngularSpeed(float maxAngularSpeed) {
		this.maxAngularSpeed = maxAngularSpeed;
	}

	@Override
	public float getMaxAngularAcceleration() {
		return maxAngularAcceleration;
	}

	@Override
	public void setMaxAngularAcceleration(float maxAngularAcceleration) {
		this.maxAngularAcceleration = maxAngularAcceleration;
	}

	@Override
	public Vector2 getLinearVelocity() {
		return body.getLinearVelocity();
	}

	@Override
	public float getAngularVelocity() {
		return body.getAngularVelocity();
	}

	@Override
	public float getBoundingRadius() {
		return boundingRadius;
	}

	@Override
	public boolean isTagged() {
		return tagged;
	}

	@Override
	public void setTagged(boolean tagged) {
		this.tagged = tagged;
	}
	
	public SteeringBehavior<Vector2> getBehavior() {
		return behavior;
	}
	
	public void setBehavior(SteeringBehavior<Vector2> behavior) {
		this.behavior = behavior;
	}

}
