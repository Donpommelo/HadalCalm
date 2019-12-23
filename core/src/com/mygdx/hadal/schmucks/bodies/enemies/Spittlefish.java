package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.enemy.SpittlefishAttack;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.states.PlayState;

public class Spittlefish extends FloatingEnemy {

	private final static int baseHp = 100;

	private static final int width = 197;
	private static final int height = 76;
	
	private static final int hboxWidth = 76;
	private static final int hboxHeight = 197;
	
	private static final float maxLinearSpeed = 10;
	private static final float maxLinearAcceleration = 200;
	private static final float maxAngularSpeed = 3240;
	private static final float maxAngularAcceleration = 2160;
	
	private static final int boundingRadius = 500;
	private static final int decelerationRadius = 100;
	
	private static final Sprite sprite = Sprite.FISH_SPITTLE;

	public Spittlefish(PlayState state, Vector2 startPos, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), sprite, enemyType.SPITTLEFISH,
				maxLinearSpeed, maxLinearAcceleration, maxAngularSpeed, maxAngularAcceleration, 
				boundingRadius, decelerationRadius, filter, baseHp, spawner);
		
		this.weapon = new SpittlefishAttack(this);	
	}
}
