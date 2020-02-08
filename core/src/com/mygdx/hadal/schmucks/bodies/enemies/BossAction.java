package com.mygdx.hadal.schmucks.bodies.enemies;

/**
 * A BossAction is any action that the boss performs. Most attacks are a sequence of BossActions performed in succession.
 * @author Zachary Tu
 *
 */
public class BossAction {

	//this boss that performs this action
	protected Enemy boss;
	
	//this is the amount of time that this action will take before the next action in the queue will begin
	private float duration;
	
	public BossAction(Enemy boss, float duration) {
		this.boss = boss;
		this.duration = duration;
	}
	
	public void execute() {}

	public float getDuration() { return duration; }	
}
