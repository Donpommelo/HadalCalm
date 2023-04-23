package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.enemy.KamabokoBody;
import com.mygdx.hadal.constants.Stats;

public class KBKCrawler2 extends EnemyCrawling {

	private static final int baseHp = 200;

	private static final int scrapDrop = 8;

	private static final int width = 1024;
	private static final int height = 1024;
	
	private static final int hboxWidth = 560;
	private static final int hboxHeight = 240;

	private static final float scale = 0.25f;
	
	private static final float attackCd = 2.0f;
	private static final float groundSpeed = -0.3f;
	
	private static final Sprite sprite = Sprite.KAMABOKO_CRAWL;
	
	public KBKCrawler2(PlayState state, Vector2 startPos, float startAngle, short filter) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), sprite, EnemyType.CRAWLER1BIG, startAngle, filter, baseHp, attackCd, scrapDrop);
		addStrategy(new KamabokoBody(state, this, false));
		setCurrentState(CrawlingState.AVOID_PITS);
	}
	
	private static final int charge1Damage = 15;
	private static final float attackInterval = 1.0f;
	private static final int defaultMeleeKB = 30;
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.GROUND_SPD, groundSpeed, getBodyData()));
	}

	private boolean attackStarted;
	@Override
	public void attackInitiate() {
		if (!attackStarted) {
			attackStarted = true;
			EnemyUtils.meleeAttackContinuous(state, this, charge1Damage, attackInterval, defaultMeleeKB, 0.0f);
		}
	}
}
