package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class SteeringEnemy extends Enemy {
	
	public SteeringEnemy(PlayState state, World world, OrthographicCamera camera, RayHandler rays, float width, float height, int x, int y, 
			float maxLinSpd, float maxLinAcc, float maxAngSpd, float maxAngAcc, float boundingRad, float decelerationRad) {
		super(state, world, camera, rays, width, height, x, y);
		
		this.maxLinearSpeed = maxLinSpd;
		this.maxLinearAcceleration = maxLinAcc;
		this.maxAngularSpeed = maxAngSpd;
		this.maxAngularAcceleration = maxAngAcc;
		
		this.boundingRadius = boundingRad;
		this.decelerationRad = decelerationRad;
		
		this.tagged = false;
		
		this.steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
	}

	@Override
	public void create() {
		this.bodyData = new BodyData(world, this);
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 0, 1, 0f, false, true, Constants.BIT_ENEMY, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_PLAYER | Constants.BIT_ENEMY),
				Constants.ENEMY_HITBOX, false, bodyData);	
		
		Arrive<Vector2> arriveSB = new Arrive<Vector2>(this, state.getPlayer())
				.setArrivalTolerance(2f)
				.setDecelerationRadius(decelerationRad);
		
		this.setBehavior(arriveSB);
	}
	
	@Override
	public void controller (float delta) {
		if (behavior != null && bodyData != null) {
			behavior.calculateSteering(steeringOutput);
			applySteering(delta);
		}
	}
}
