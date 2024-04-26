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

public class Torpedofish extends EnemySwimming {

	private static final int baseHp = 150;

	private static final int scrapDrop = 2;
	
	private static final int width = 81;
	private static final int height = 60;
	
	private static final int hboxWidth = 81;
	private static final int hboxHeight = 60;
	
	private static final float attackCd = 0.35f;
	private static final float airSpeed = -0.4f;
	
	private static final float minRange = 5.0f;
	private static final float maxRange = 10.0f;
	
	private static final float noiseRadius = 15.0f;

	private static final Sprite sprite = Sprite.FISH_TORPEDO_IDLE;

	public Torpedofish(PlayState state, Vector2 startPos, float startAngle, short filter) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), sprite, EnemyType.TORPEDOFISH, startAngle,  filter, baseHp, attackCd, scrapDrop);
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		
		getSwimStrategy().setNoiseRadius(noiseRadius);
		getFloatStrategy().addSprite(MoveState.ANIM1, Sprite.FISH_TORPEDO_ATTACK);
	}
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.AIR_SPD, airSpeed, getBodyData()));
		getBodyData().addStatus(new DeathRagdoll(state, getBodyData(), sprite, size));
	}

	private static final float attackWindup1 = 1.2f;
	private static final float attackWindup2 = 0.4f;
	private static final float attackCooldown = 0.76f;

	private static final float projectileSpeed = 30.0f;
	private static final float range = 900.0f;
	@Override
	public void attackInitiate() {

		EnemyUtils.changeMoveState(this, MoveState.ANIM1, attackWindup1);
		EnemyUtils.changeSwimmingState(this, SwimmingState.STILL, 0.0f, attackWindup2);
		EnemyUtils.changeFloatingFreeAngle(this, 0.0f, 0.0f);

		getActions().add(new EnemyAction(this, attackCooldown) {
			
			private final Vector2 startVelo = new Vector2();
			@Override
			public void execute() {
				
				if (attackTarget == null) {
					return;
				}
				
				startVelo.set(projectileSpeed, projectileSpeed).setAngleDeg(getAttackAngle());
				
				if (startVelo.len2() < range * range) {
					startVelo.nor().scl(projectileSpeed);
					SyncedAttack.TORPEDOFISH_ATTACK.initiateSyncedAttackSingle(state, enemy, enemy.getPixelPosition(), startVelo);
				}
			}
		});
		
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
		EnemyUtils.changeMoveState(this, MoveState.DEFAULT, 0.0f);
	}
}
