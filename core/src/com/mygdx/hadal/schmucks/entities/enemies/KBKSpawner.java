package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.enemy.KamabokoBody;
import com.mygdx.hadal.strategies.enemy.MovementFloat.FloatingState;
import com.mygdx.hadal.strategies.enemy.MovementSwim.SwimmingState;

public class KBKSpawner extends EnemySwimming {

	private static final int baseHp = 200;

	private static final int scrapDrop = 4;

	private static final int width = 1024;
	private static final int height = 1024;
	
	private static final int hboxWidth = 560;
	private static final int hboxHeight = 240;
	
	private static final float attackCd = 2.0f;
	private static final float airSpeed = -0.4f;
	
	private static final float scale = 0.25f;
	private static final float noiseRadius = 2.0f;

	private static final Sprite sprite = Sprite.KAMABOKO_SWIM;

	public KBKSpawner(PlayState state, Vector2 startPos, float startAngle, short filter) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), sprite, EnemyType.SPAWNER, startAngle, filter, baseHp, attackCd, scrapDrop);
		addStrategy(new KamabokoBody(state, this, true));
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		
		getSwimStrategy().setNoiseRadius(noiseRadius);
	}
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.AIR_SPD, airSpeed, getBodyData()));
	}
	
	private static final float attackWindup1 = 0.9f;
	private static final float attackWindup2 = 0.3f;
	
	private static final float minRange = 5.0f;
	private static final float maxRange = 10.0f;
	
	private static final float projectileSpeed = 25.0f;
	private static final float range = 900.0f;
	@Override
	public void attackInitiate() {
		
		EnemyUtils.windupParticles(state, this, attackWindup1, Particle.CHARGING, HadalColor.MAGENTA, 100.0f);
		EnemyUtils.changeSwimmingState(this, SwimmingState.STILL, 0.0f, 0.0f);
		EnemyUtils.changeFloatingFreeAngle(this, 0.0f, 0.0f);
		EnemyUtils.windupParticles(state, this, attackWindup2, Particle.OVERCHARGE, HadalColor.MAGENTA, 100.0f);
		
		getActions().add(new EnemyAction(this, 0.0f) {
			
			private final Vector2 startVelo = new Vector2();
			@Override
			public void execute() {
				
				if (attackTarget == null) {
					return;
				}
				
				startVelo.set(projectileSpeed, projectileSpeed).setAngleDeg(getAttackAngle());
				
				if (startVelo.len2() < range * range) {
					startVelo.nor().scl(projectileSpeed);
					SyncedAttack.ENEMY_KAMABOKO_SPAWN.initiateSyncedAttackSingle(state, enemy, enemy.getPixelPosition(), startVelo);
				}
			}
		});
		
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
	}
}
