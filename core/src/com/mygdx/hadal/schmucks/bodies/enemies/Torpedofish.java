package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.states.PlayState;

public class Torpedofish extends EnemySteering {

	private final static int baseHp = 100;

	private static final int width = 63;
	private static final int height = 40;
	
	private static final int hboxWidth = 40;
	private static final int hboxHeight = 63;
	
	private static final float maxLinearSpeed = 500;
	private static final float maxLinearAcceleration = 800;
	
	private static final float attackCd = 1.0f;
			
	private static final Sprite sprite = Sprite.FISH_TORPEDO;

	public Torpedofish(PlayState state, Vector2 startPos, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), sprite, enemyType.TORPEDOFISH, maxLinearSpeed, maxLinearAcceleration, filter, baseHp, attackCd, spawner);
	}
}
