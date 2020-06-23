package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DeathRagdoll;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.utils.Stats;

public class Scissorfish extends EnemySwimming {

	private final static int baseHp = 100;
	private final static String name = "SCISSORFISH";
	
	private final static int scrapDrop = 2;
	
	private static final int width = 72;
	private static final int height = 30;
	
	private static final int hboxWidth = 72;
	private static final int hboxHeight = 30;
	
	private static final float attackCd = 1.5f;
	private static final float airSpeed = -0.25f;
	
	private static final float minRange = 0.0f;
	private static final float maxRange = 3.0f;
	
	private static final float noiseRadius = 5.0f;

	private static final Sprite sprite = Sprite.FISH_SCISSOR;
	
	public Scissorfish(PlayState state, Vector2 startPos, float startAngle, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), name, sprite, EnemyType.SCISSORFISH, startAngle, filter, baseHp, attackCd, scrapDrop, spawner);
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		
		setNoiseRadius(noiseRadius);
	}
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.AIR_SPD, airSpeed, getBodyData()));
		getBodyData().addStatus(new DeathRagdoll(state, getBodyData(), sprite, size));
	}
	
	private static final int charge1Speed = 15;
	private static final int charge1Damage = 12;
	private static final int defaultMeleeKB = 25;
	@Override
	public void attackInitiate() {
		EnemyUtils.moveToPlayer(state, this, target, charge1Speed, 0.0f);
		EnemyUtils.meleeAttackContact(state, this, charge1Damage, defaultMeleeKB, 1.5f);
	}
}
