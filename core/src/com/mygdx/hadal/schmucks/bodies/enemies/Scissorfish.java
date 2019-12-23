package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.enemy.ScissorfishAttack;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.states.PlayState;

public class Scissorfish extends FloatingEnemy {

	private final static int baseHp = 100;

	private static final int width = 288;
	private static final int height = 119;
	
	private static final int hboxWidth = 119;
	private static final int hboxHeight = 288;
	
	private static final float maxLinearSpeed = 50;
	private static final float maxLinearAcceleration = 1000;
	private static final float maxAngularSpeed = 1080;
	private static final float maxAngularAcceleration = 720;
	
	private static final int boundingRadius = 500;
	private static final int decelerationRadius = 0;
	
	private static final Sprite sprite = Sprite.FISH_SCISSOR;
	
	public Scissorfish(PlayState state, Vector2 startPos, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), sprite, enemyType.SCISSORFISH,
				maxLinearSpeed, maxLinearAcceleration, maxAngularSpeed, maxAngularAcceleration, 
				boundingRadius, decelerationRadius, filter, baseHp, spawner);
		
		this.weapon = new ScissorfishAttack(this);	
	}
}
