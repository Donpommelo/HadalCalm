package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.enemy.KamabokoBody;
import com.mygdx.hadal.utils.Stats;

public class KBKBouncer extends EnemyCrawling {

	private static final int baseHp = 100;

	private static final int scrapDrop = 1;

	private static final int width = 512;
	private static final int height = 512;
	
	private static final int hboxWidth = 280;
	private static final int hboxHeight = 120;

	private static final float scale = 0.25f;

	private static final float attackCd = 2.0f;
	private static final float groundSpeed = -0.25f;
	private static final float drag = -1.0f;
	
	private static final Sprite sprite = Sprite.KAMABOKO_CRAWL;
	
	public KBKBouncer(PlayState state, Vector2 startPos, float startAngle, short filter) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), sprite, EnemyType.BOUNCER, startAngle, filter, baseHp, attackCd, scrapDrop);
		addStrategy(new KamabokoBody(state, this, false));
		setCurrentState(CrawlingState.BACK_FORTH);
	}
	
	@Override
	public void create() {
		super.create();
		getMainFixture().setRestitution(1.0f);
		getBodyData().addStatus(new StatChangeStatus(state, Stats.GROUND_SPD, groundSpeed, getBodyData()));
		getBodyData().addStatus(new StatChangeStatus(state, Stats.AIR_DRAG, drag, getBodyData()));
	}
	
	private static final int charge1Damage = 10;
	private static final float attackInterval = 1.0f;
	private static final int defaultMeleeKB = 20;
	@Override
	public void attackInitiate() {
		EnemyUtils.meleeAttackContinuous(state, this, charge1Damage, attackInterval, defaultMeleeKB, attackCd);
	}
}
