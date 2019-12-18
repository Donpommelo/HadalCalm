package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * A Steering enemy is an enemy with ai that steers it, probably towards the player.
 * @author Zachary Tu
 *
 */
public class SteeringEnemy extends Enemy {
	
	public SteeringEnemy(PlayState state, float width, float height, int x, int y, enemyType type, 
			float maxLinSpd, float maxLinAcc, float maxAngSpd, float maxAngAcc, float boundingRad, float decelerationRad, short filter, int baseHp, SpawnerSchmuck spawner) {
		super(state, width, height, x, y, type, filter, baseHp, spawner);
		
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
		super.create();
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 0, 1, 0f, false, true, Constants.BIT_ENEMY, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_PLAYER | Constants.BIT_ENEMY),
				hitboxfilter, false, bodyData);
	}
	
	@Override
	public void controller (float delta) {
		super.controller(delta);
		if (behavior != null && bodyData != null) {
			behavior.calculateSteering(steeringOutput);
			applySteering(delta);
		}
	}
}
