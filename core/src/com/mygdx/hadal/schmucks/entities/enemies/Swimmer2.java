package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.enemy.KamabokoBody;
import com.mygdx.hadal.strategies.enemy.MovementFloat.FloatingState;
import com.mygdx.hadal.strategies.enemy.MovementSwim.SwimmingState;

public class Swimmer2 extends EnemySwimming {

	private static final int baseHp = 174;

	private static final int scrapDrop = 1;

	private static final int width = 512;
	private static final int height = 512;
	
	private static final int hboxWidth = 280;
	private static final int hboxHeight = 120;
	
	private static final float attackCd = 2.0f;
	private static final float airSpeed = 0.1f;
	
	private static final float scale = 0.25f;
	private static final float noiseRadius = 6.0f;

	private static final Sprite sprite = Sprite.KAMABOKO_SWIM;

	public Swimmer2(PlayState state, Vector2 startPos, float startAngle, short filter) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), sprite, EnemyType.SWIMMER2, startAngle, filter, baseHp, attackCd, scrapDrop);
		addStrategy(new KamabokoBody(state, this, true));
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		
		getSwimStrategy().setNoiseRadius(noiseRadius);
	}
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.AIR_SPD, airSpeed, getBodyData()));
	}
	
	private static final float minRange = 3.0f;
	private static final float maxRange = 8.0f;
	
	private static final float defaultTrack = 0.04f;
	private static final float attackTrack = 0.01f;

	private static final float attackWindup = 1.0f;
	private static final float attackSwingAngle = 30.0f;
	
	private static final int fireballNumber = 8;
	private static final int fireSpeed = 10;
	private static final float fireballInterval = 0.15f;

	@Override
	public void attackInitiate() {
		
		EnemyUtils.changeSwimmingState(this, SwimmingState.STILL, 0.0f, 0.0f);
		
		EnemyUtils.windupParticles(state, this, attackWindup, Particle.KAMABOKO_SHOWER, 120.0f);
		
		EnemyUtils.changeFloatingTrackSpeed(this, attackTrack, 0.0f);
		
		EnemyUtils.changeFloatingFreeAngle(this, attackSwingAngle, 0.0f);
		for (int i = 0; i < fireballNumber; i++) {
			kamabokoSpray(state, this, i);
		}
		
		EnemyUtils.changeFloatingFreeAngle(this, - 2 * attackSwingAngle, 0.0f);
		for (int i = 0; i < fireballNumber; i++) {
			kamabokoSpray(state, this, i + fireballNumber);
		}
		
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		EnemyUtils.changeFloatingTrackSpeed(this, defaultTrack, 0.0f);
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0.0f, 0.0f);
	}

	private void kamabokoSpray(final PlayState state, Enemy boss, int projNum) {

		boss.getActions().add(new EnemyAction(boss, fireballInterval) {

			@Override
			public void execute() {
				Vector2 startVelo = new Vector2(fireSpeed, fireSpeed).setAngleDeg(enemy.getAttackAngle());
				SyncedAttack.ENEMY_KAMABOKO_SPRAY.initiateSyncedAttackSingle(state, enemy, enemy.getPixelPosition(), startVelo, projNum);
			}
		});
	}
}
