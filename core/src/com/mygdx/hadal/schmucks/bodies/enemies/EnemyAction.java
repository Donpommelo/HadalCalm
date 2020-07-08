package com.mygdx.hadal.schmucks.bodies.enemies;

/**
 * An EnemyAction is any action that the enemy performs. Most attacks are a sequence of EnemyActions performed in succession.
 * @author Zachary Tu
 */
public class EnemyAction {

	//this boss that performs this action
	protected Enemy enemy;
	
	//this is the amount of time that this action will take before the next action in the queue will begin
	private float duration;
	
	public EnemyAction(Enemy enemy, float duration) {
		this.enemy = enemy;
		this.duration = duration;
	}
	
	public void execute() {}

	public float getDuration() { return duration; }	
}
