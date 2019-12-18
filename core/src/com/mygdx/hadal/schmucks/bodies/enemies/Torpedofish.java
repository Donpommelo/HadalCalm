package com.mygdx.hadal.schmucks.bodies.enemies;

import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.enemy.TorpedofishAttack;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.states.PlayState;

public class Torpedofish extends FloatingEnemy {

	private final static int baseHp = 100;

	private static final int width = 250;
	private static final int height = 161;
	
	private static final int hbWidth = 161;
	private static final int hbHeight = 250;
	
	private static final float scale = 0.25f;
	
	private static final float maxLinearSpeed = 25;
	private static final float maxLinearAcceleration = 800;
	private static final float maxAngularSpeed = 4320;
	private static final float maxAngularAcceleration = 3240;
	
	private static final int boundingRadius = 500;
	private static final int decelerationRadius = 100;
	
	private static final Sprite sprite = Sprite.FISH_TORPEDO;

	public Torpedofish(PlayState state, int x, int y, short filter, SpawnerSchmuck spawner) {
		super(state, x, y, width, height, hbWidth, hbHeight, scale, sprite, enemyType.TORPEDOFISH,
				maxLinearSpeed, maxLinearAcceleration, maxAngularSpeed, maxAngularAcceleration, 
				boundingRadius, decelerationRadius, filter, baseHp, spawner);
		
		this.weapon = new TorpedofishAttack(this);	
	}
}
