package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DeathRagdoll;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.enemy.MovementFloat.FloatingState;
import com.mygdx.hadal.strategies.enemy.MovementSwim.SwimmingState;

public class Scissorfish extends EnemySwimming {

	private static final int baseHp = 100;

	private static final int scrapDrop = 2;
	
	private static final int width = 72;
	private static final int height = 30;
	
	private static final int hboxWidth = 72;
	private static final int hboxHeight = 30;
	
	private static final float attackCd = 1.0f;
	private static final float airSpeed = -0.25f;
	
	private static final float minRange = 0.0f;
	private static final float maxRange = 3.0f;
	
	private static final float noiseRadius = 5.0f;

	private static final Sprite sprite = Sprite.FISH_SCISSOR;
	
	public Scissorfish(PlayState state, Vector2 startPos, float startAngle, short filter) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), sprite, EnemyType.SCISSORFISH, startAngle, filter, baseHp, attackCd, scrapDrop);
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		
		getSwimStrategy().setNoiseRadius(noiseRadius);
	}
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.AIR_SPD, airSpeed, getBodyData()));
		getBodyData().addStatus(new DeathRagdoll(state, getBodyData(), sprite, size));
	}
	
	private static final float attackWindup1 = 0.6f;
	private static final float attackWindup2 = 0.2f;
	
	private static final int attack1Amount = 4;
	private static final float meleeInterval = 0.25f;
	
	private static final int charge1Speed = 15;

	@Override
	public void attackInitiate() {
		
		EnemyUtils.windupParticles(state, this, attackWindup1, Particle.CHARGING, HadalColor.RED, 120.0f);
		EnemyUtils.changeSwimmingState(this, SwimmingState.STILL, 0.0f, 0.0f);
		EnemyUtils.changeFloatingFreeAngle(this, 0.0f, 0.0f);
		EnemyUtils.windupParticles(state, this, attackWindup2, Particle.OVERCHARGE, HadalColor.RED, 120.0f);
		
		EnemyUtils.moveToPlayer(this, attackTarget, charge1Speed, 0.0f);
		
		for (int i = 0; i < attack1Amount; i++) {
			
			getActions().add(new EnemyAction(this, meleeInterval) {
				
				@Override
				public void execute() {
					SyncedAttack.SCISSORFISH_ATTACK.initiateSyncedAttackSingle(state, enemy, enemy.getPixelPosition(),
							new Vector2(0, 1).setAngleDeg(getAttackAngle()));
				}
			});
		}
		
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
	}
}
