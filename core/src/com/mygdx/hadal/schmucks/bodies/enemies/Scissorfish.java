package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.states.PlayState;

public class Scissorfish extends EnemySteering {

	private final static int baseHp = 100;

	private final static int scrapDrop = 2;
	
	private static final int width = 30;
	private static final int height = 72;
	
	private static final int hboxWidth = 30;
	private static final int hboxHeight = 72;
	
	private static final float maxLinearSpeed = 800;
	private static final float maxLinearAcceleration = 1000;
	
	private static final float attackCd = 3.0f;
	
	private static final Sprite sprite = Sprite.FISH_SCISSOR;
	
	public Scissorfish(PlayState state, Vector2 startPos, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), sprite, EnemyType.SCISSORFISH, maxLinearSpeed, maxLinearAcceleration, filter, baseHp, attackCd, scrapDrop, spawner);
	}
	
	private static final int charge1Speed = 10;
	private static final int charge1Damage = 10;
	private static final int defaultMeleeKB = 20;
	@Override
	public void attackInitiate() {
		EnemyUtils.moveToPlayer(state, this, target, charge1Speed, 0.0f);
		EnemyUtils.meleeAttackContact(state, this, charge1Damage, defaultMeleeKB, 1.5f);
	}
}
