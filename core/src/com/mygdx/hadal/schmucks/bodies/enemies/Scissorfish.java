package com.mygdx.hadal.schmucks.bodies.enemies;

import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.enemy.ScissorfishAttack;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.states.PlayState;

public class Scissorfish extends FloatingEnemy {

	private final static int baseHp = 100;

	private static final int width = 288;
	private static final int height = 119;
	
	private static final int hbWidth = 119;
	private static final int hbHeight = 288;
	
	private static final float scale = 0.25f;
	
	private static final float maxLinearSpeed = 50;
	private static final float maxLinearAcceleration = 1000;
	private static final float maxAngularSpeed = 1080;
	private static final float maxAngularAcceleration = 720;
	
	private static final int boundingRadius = 500;
	private static final int decelerationRadius = 0;
	
	private static final Sprite sprite = Sprite.FISH_SCISSOR;
	
	public Scissorfish(PlayState state, int x, int y, short filter, SpawnerSchmuck spawner) {
		super(state, x, y, width, height, hbWidth, hbHeight, scale, sprite, enemyType.SCISSORFISH,
				maxLinearSpeed, maxLinearAcceleration, maxAngularSpeed, maxAngularAcceleration, 
				boundingRadius, decelerationRadius, filter, baseHp, spawner);
		
		this.weapon = new ScissorfishAttack(this);	
	}
}
