package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.states.PlayState;

/**
 * Enemies are Schmucks that attack the player.
 * Steering enemies use gdx ai to move towards a targer
 * @author Zachary Tu
 *
 */
public class EnemySteering extends EnemyFloating {
				
	protected SteeringBehavior<Vector2> roam;

	public EnemySteering(PlayState state, Vector2 startPos, Vector2 size, Vector2 hboxSize, Sprite sprite, enemyType type, float maxLinSpd, float maxLinAcc, short filter, int hp, float attackCd, SpawnerSchmuck spawner) {
		super(state, startPos, size, hboxSize, sprite, type, filter, hp, attackCd, spawner);
		
		this.maxLinearSpeed = maxLinSpd;
		this.maxLinearAcceleration = maxLinAcc;
		
		this.tagged = false;
		
		this.steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
	}

	/**
	 * Create the enemy's body and initialize enemy's user data.
	 */
	@Override
	public void create() {
		super.create();
		
		//default action is to roam
		roam = new Wander<Vector2>(this)
				.setWanderOffset(100)
				.setWanderRadius(100)
				.setWanderRate(25)
				.setWanderOrientation(10);
		setTarget(state.getPlayer(), roam);
	}
	
	@Override
	public void controller(float delta) {		
		super.controller(delta);
		
		//if a steering enemy finds a target, it pursues it
		if (target != null) {
			setTarget(target, new Pursue<Vector2>(this, target));
		}
		
		if (behavior != null && getBodyData() != null) {
			behavior.calculateSteering(steeringOutput);
			applySteering(delta);
		}
	}
}
