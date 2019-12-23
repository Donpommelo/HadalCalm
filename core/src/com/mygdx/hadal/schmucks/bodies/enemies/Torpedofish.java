package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.enemy.TorpedofishAttack;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.states.PlayState;

public class Torpedofish extends FloatingEnemy {

	private final static int baseHp = 100;

	private static final int width = 250;
	private static final int height = 161;
	
	private static final int hboxWidth = 161;
	private static final int hboxHeight = 250;
	
	private static final float maxLinearSpeed = 25;
	private static final float maxLinearAcceleration = 800;
	private static final float maxAngularSpeed = 4320;
	private static final float maxAngularAcceleration = 3240;
	
	private static final int boundingRadius = 500;
	private static final int decelerationRadius = 100;
	
	private static final Sprite sprite = Sprite.FISH_TORPEDO;

	public Torpedofish(PlayState state, Vector2 startPos, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), sprite, enemyType.TORPEDOFISH, 
				maxLinearSpeed, maxLinearAcceleration, maxAngularSpeed, maxAngularAcceleration, 
				boundingRadius, decelerationRadius, filter, baseHp, spawner);
		
		this.weapon = new TorpedofishAttack(this);	
	}
}
