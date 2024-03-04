package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.enemy.KamabokoBody;
import com.mygdx.hadal.constants.Stats;

public class KBKMedium extends EnemySwimming {

	private static final int baseHp = 60;

	private static final int scrapDrop = 0;

	private static final int width = 512;
	private static final int height = 512;
	
	private static final int hboxWidth = 280;
	private static final int hboxHeight = 120;
	
	private static final float attackCd = 2.0f;
	private static final float airSpeed = -0.2f;
	
	private static final float scale = 0.25f;
	private static final float noiseRadius = 4.0f;

	private static final Sprite sprite = Sprite.KAMABOKO_SWIM;
	
	public KBKMedium(PlayState state, Vector2 startPos, float startAngle, short filter) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), sprite, EnemyType.SPLITTER_MEDIUM, startAngle, filter, baseHp, attackCd, scrapDrop);
		addStrategy(new KamabokoBody(state, this, true));
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);

		getSwimStrategy().setNoiseRadius(noiseRadius);
	}
	
	private static final float minRange = 0.0f;
	private static final float maxRange = 2.0f;
	
	private static final int charge1Damage = 6;
	private static final float attackInterval = 0.2f;
	private static final int defaultMeleeKB = 12;
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new Invulnerability(state, 0.1f, getBodyData(), getBodyData()));
		getBodyData().addStatus(new StatChangeStatus(state, Stats.AIR_SPD, airSpeed, getBodyData()) {
			
			@Override
			public void onDeath(BodyData perp, DamageSource source) {
				EnemyType.SPLITTER_SMALL.generateEnemy(state, inflicted.getSchmuck().getPixelPosition(), getHitboxFilter(), 0.0f);
				EnemyType.SPLITTER_SMALL.generateEnemy(state, inflicted.getSchmuck().getPixelPosition(), getHitboxFilter(), 0.0f);
				EnemyType.SPLITTER_SMALL.generateEnemy(state, inflicted.getSchmuck().getPixelPosition(), getHitboxFilter(), 0.0f);
			}
		});
	}

	@Override
	public void attackInitiate() {
		EnemyUtils.meleeAttackContinuous(state, this, charge1Damage, attackInterval, defaultMeleeKB, attackCd);
	}
}
