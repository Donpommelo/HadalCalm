package com.mygdx.hadal.schmucks.bodies.hitboxes;

import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public class SteeringHitbox extends HitboxAnimated {

	public SteeringHitbox(PlayState state, float x, float y, int width, int height, float grav, float lifespan,
			int dura, float rest, Vector2 startVelo, short filter, boolean sensor, World world,
			OrthographicCamera camera, RayHandler rays, Schmuck creator, String spriteId,
			float maxLinSpd, float maxLinAcc, float maxAngSpd, float maxAngAcc, float boundingRad, float decelerationRad) {
		super(state, x, y, width, height, grav, lifespan, dura, rest, startVelo, filter, sensor, world, camera, rays, creator,
				spriteId);
		
		this.maxLinearSpeed = maxLinSpd;
		this.maxLinearAcceleration = maxLinAcc;
		this.maxAngularSpeed = maxAngSpd;
		this.maxAngularAcceleration = maxAngAcc;
		
		this.boundingRadius = boundingRad;
		this.decelerationRad = decelerationRad;
		
		this.tagged = false;
		
		this.steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
	}
	
	public void controller (float delta) {
		super.controller(delta);
		if (behavior != null) {
			behavior.calculateSteering(steeringOutput);
			applySteering(delta);
		}
	}

	public void setTarget(HadalEntity target) {
		Arrive<Vector2> arriveSB = new Arrive<Vector2>(this, target)
				.setArrivalTolerance(2f)
				.setDecelerationRadius(decelerationRad);
		
		this.setBehavior(arriveSB);
	}
}
