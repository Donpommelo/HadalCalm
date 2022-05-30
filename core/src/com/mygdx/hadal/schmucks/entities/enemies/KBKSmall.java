package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.enemy.KamabokoBody;
import com.mygdx.hadal.utils.Stats;

public class KBKSmall extends EnemySwimming {

	private static final int baseHp = 10;

	private static final int scrapDrop = 1;

	private static final int width = 256;
	private static final int height = 256;
	
	private static final int hboxWidth = 140;
	private static final int hboxHeight = 60;
	
	private static final float attackCd = 1.0f;
	private static final float airSpeed = 1.1f;
	
	private static final float scale = 0.25f;
	private static final float noiseRadius = 5.0f;

	private static final Sprite sprite = Sprite.KAMABOKO_SWIM;
	
	public KBKSmall(PlayState state, Vector2 startPos, float startAngle, short filter) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), sprite, EnemyType.SPLITTER_SMALL, startAngle, filter, baseHp, attackCd, scrapDrop);
		addStrategy(new KamabokoBody(state, this, true));
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);

		getSwimStrategy().setNoiseRadius(noiseRadius);
	}
	
	private static final float minRange = 0.0f;
	private static final float maxRange = 2.0f;
	
	private static final int charge1Damage = 3;
	private static final float attackInterval = 1.0f;
	private static final int defaultMeleeKB = 15;
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new Invulnerability(state, 0.1f, getBodyData(), getBodyData()));
		getBodyData().addStatus(new StatChangeStatus(state, Stats.AIR_SPD, airSpeed, getBodyData()));

		EnemyUtils.meleeAttackContinuous(state, this, charge1Damage, attackInterval, defaultMeleeKB, 0.0f, true);
	}
}
