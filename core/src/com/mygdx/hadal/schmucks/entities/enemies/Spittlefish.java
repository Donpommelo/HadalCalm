package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.MoveState;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DeathRagdoll;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.enemy.MovementFloat.FloatingState;
import com.mygdx.hadal.strategies.enemy.MovementSwim.SwimmingState;

public class Spittlefish extends EnemySwimming {

	private static final int baseHp = 100;

	private static final int scrapDrop = 2;
	
	private static final int width = 75;
	private static final int height = 30;
	
	private static final int hboxWidth = 75;
	private static final int hboxHeight = 30;
	
	private static final float attackCd = 0.5f;
	private static final float airSpeed = -0.4f;
	private static final float kbRes = 0.5f;
	
	private static final float minRange = 6.0f;
	private static final float maxRange = 12.0f;
	
	private static final float noiseRadius = 15.0f;

	private static final Sprite sprite = Sprite.FISH_SPITTLE_IDLE;

	public Spittlefish(PlayState state, Vector2 startPos, float startAngle, short filter) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), sprite, EnemyType.SPITTLEFISH, startAngle, filter, baseHp, attackCd, scrapDrop);
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		
		getSwimStrategy().setNoiseRadius(noiseRadius);
		getFloatStrategy().addSprite(MoveState.ANIM1, Sprite.FISH_SPITTLE_ATTACK);
	}
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.AIR_SPD, airSpeed, getBodyData()));
		getBodyData().addStatus(new StatChangeStatus(state, Stats.KNOCKBACK_RES, kbRes, getBodyData()));
		getBodyData().addStatus(new DeathRagdoll(state, getBodyData(), sprite, size));
	}

	private static final float attackWindup1 = 0.39f;
	private static final float attackWindup2 = 0.13f;
	private static final float attackCooldown = 0.28f;

	private static final float projectileSpeed = 15.0f;
	private static final float range = 900.0f;
	
	private final Vector2 startVelo = new Vector2();
	@Override
	public void attackInitiate() {

		EnemyUtils.changeMoveState(this, MoveState.ANIM1, attackWindup1);
		EnemyUtils.changeSwimmingState(this, SwimmingState.STILL, 0.0f, attackWindup2);
		EnemyUtils.changeFloatingFreeAngle(this, 0.0f, 0.0f);

		getActions().add(new EnemyAction(this, attackCooldown) {
			
			@Override
			public void execute() {
				
				if (attackTarget == null) {
					return;
				}
				
				startVelo.set(projectileSpeed, projectileSpeed).setAngleDeg(getAttackAngle());
				
				if (startVelo.len2() < range * range) {
					startVelo.nor().scl(projectileSpeed);
					SyncedAttack.SPITTLEFISH_ATTACK.initiateSyncedAttackSingle(state, enemy, enemy.getPixelPosition(), startVelo);
				}
			}
		});
		
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
		EnemyUtils.changeMoveState(this, MoveState.DEFAULT, 0.0f);
	}
}
