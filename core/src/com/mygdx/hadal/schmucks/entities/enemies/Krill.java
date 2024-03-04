package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.enemy.TargetNoPathfinding;

public class Krill extends EnemySwimming {

	private static final int baseHp = 80;

	private static final int scrapDrop = 0;

	private static final int width = 256;
	private static final int height = 256;

	private static final int hboxWidth = 120;
	private static final int hboxHeight = 120;

	private static final float attackCd = 2.0f;
	private static final float airSpeed = 0.0f;

	private static final float scale = 0.25f;
	private static final float noiseRadius = 3.0f;

	private static final Sprite sprite = Sprite.KRILL;

	public Krill(PlayState state, Vector2 startPos, float startAngle, short filter) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), sprite, EnemyType.KRILL, startAngle, filter, baseHp, attackCd, scrapDrop);
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);

		getSwimStrategy().setNoiseRadius(noiseRadius);
	}
	
	private static final float minRange = 0.0f;
	private static final float maxRange = 2.0f;
	
	private static final float charge1Damage = 1.5f;
	private static final float attackInterval = 0.2f;
	private static final int defaultMeleeKB = 3;
	@Override
	public void create() {
		super.create();

		Filter filter = getMainFixture().getFilterData();
		filter.maskBits = (short) (BodyConstants.BIT_SENSOR | BodyConstants.BIT_ENEMY);
		getMainFixture().setFilterData(filter);

		getBodyData().addStatus(new StatChangeStatus(state, Stats.AIR_SPD, airSpeed, getBodyData()));
	}

	@Override
	public void attackInitiate() {
		EnemyUtils.meleeAttackContinuous(state, this, charge1Damage, attackInterval, defaultMeleeKB, attackCd);
	}

	@Override
	public void setupPathingStrategies() {
		addStrategy(new TargetNoPathfinding(state, this, true));
	}
}
