package com.mygdx.hadal.schmucks.entities.enemies;

/**
 * An EnemyAction is any action that the enemy performs. Most attacks are a sequence of EnemyActions performed in succession.
 * @author Loshire Lishire
 */
public class EnemyAction {

	//this boss that performs this action
	protected final Enemy enemy;
	
	//this is the amount of time that this action will take before the next action in the queue will begin
	private final float duration;
	
	public EnemyAction(Enemy enemy, float duration) {
		this.enemy = enemy;
		this.duration = duration;
	}
	
	public void execute() {}

	public float getDuration() { return duration; }	
}
