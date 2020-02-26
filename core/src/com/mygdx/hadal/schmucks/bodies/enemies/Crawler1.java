package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Stats;

public class Crawler1 extends EnemyCrawling {

	private final static int baseHp = 100;

	private static final int width = 63;
	private static final int height = 40;
	
	private static final int hboxWidth = 63;
	private static final int hboxHeight = 40;
	
	private static final float attackCd = 2.0f;
	private static final float groundSpeed = -0.75f;
			
	private static final Sprite sprite = Sprite.FISH_TORPEDO;
	
	public Crawler1(PlayState state, Vector2 startPos, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), sprite, EnemyType.CRAWLER1, filter, baseHp, attackCd, spawner);

		setCurrentState(CrawlingState.AVOID_PITS);
	}
	
	@Override
	public void create() {
		super.create();
		getBodyData().setStat(Stats.GROUND_SPD, groundSpeed);
	}
	
	@Override
	public void attackInitiate() {
		
	};
}
