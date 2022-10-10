package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.enemy.KamabokoBody;
import com.mygdx.hadal.constants.Stats;

public class Leaper1 extends EnemyCrawling {

	private static final int baseHp = 100;

	private static final int scrapDrop = 1;

	private static final int width = 512;
	private static final int height = 512;
	
	private static final int hboxWidth = 280;
	private static final int hboxHeight = 120;

	private static final float scale = 0.25f;
	
	private static final float attackCd = 2.0f;
	private static final float groundSpeed = -0.75f;
	
	private static final Sprite sprite = Sprite.KAMABOKO_CRAWL;

	public Leaper1(PlayState state, Vector2 startPos, float startAngle, short filter) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), sprite, EnemyType.LEAPER1, startAngle, filter, baseHp, attackCd, scrapDrop);
		addStrategy(new KamabokoBody(state, this, false));
		setCurrentState(CrawlingState.CHASE);
	}
	
	@Override
	public void create() {
		super.create();
		body.setGravityScale(2.0f);
		getBodyData().addStatus(new StatChangeStatus(state, Stats.GROUND_SPD, groundSpeed, getBodyData()));

		EnemyUtils.meleeAttackContinuous(state, this, charge1Damage, attackInterval, defaultMeleeKB, 0.0f, true);
	}
	
	private static final int charge1Damage = 10;
	private static final float attackInterval = 1.0f;
	private static final int defaultMeleeKB = 20;
	private static final int spread = 30;
	@Override
	public void attackInitiate() {
		push(new Vector2(0, 50).rotateDeg(MathUtils.random(-spread, spread + 1)));
	}
}
