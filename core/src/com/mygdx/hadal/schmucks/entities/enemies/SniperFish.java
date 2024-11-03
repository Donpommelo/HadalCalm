package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.MoveState;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DeathRagdoll;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.enemy.MovementFloat.FloatingState;
import com.mygdx.hadal.strategies.enemy.MovementSwim.SwimmingState;

public class SniperFish extends EnemySwimming {

	private static final int baseHp = 500;

	private static final int scrapDrop = 8;

	private static final int width = 315;
	private static final int height = 210;

	private static final int hboxWidth = 165;
	private static final int hboxHeight = 165;

	private static final float attackCd = 0.35f;
	private static final float airSpeed = -0.4f;

	private static final float minRange = 20.0f;
	private static final float maxRange = 40.0f;

	private static final float noiseRadius = 15.0f;

	private static final Sprite sprite = Sprite.FISH_SNIPER_IDLE;

	public SniperFish(PlayState state, Vector2 startPos, float startAngle, short filter) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), sprite, EnemyType.SNIPERFISH, startAngle,  filter, baseHp, attackCd, scrapDrop);
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		
		getSwimStrategy().setNoiseRadius(noiseRadius);
		getFloatStrategy().addSprite(MoveState.ANIM1, Sprite.FISH_SNIPER_ATTACK);
	}
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.AIR_SPD, airSpeed, getBodyData()));
		getBodyData().addStatus(new DeathRagdoll(state, getBodyData(), sprite, size));
	}

	private static final float attackWindup1 = 2.0f;
	private static final float attackWindup2 = 0.3f;
	private static final float attackCooldown = 0.56f;

	private static final float projectileSpeed = 60.0f;
	private static final float range = 4000.0f;
	private Hitbox reticleTarget;
	@Override
	public void attackInitiate() {

		getActions().add(new EnemyAction(this, attackWindup1) {
			@Override
			public void execute() {
				reticleTarget = null;
				if (attackTarget == null) {
					return;
				}
				if (attackTarget instanceof Player player) {
					reticleTarget = SyncedAttack.SNIPER_RETICLE.initiateSyncedAttackSingle(state, enemy, new Vector2(), new Vector2(),
							player.getUser().getConnID());
				}
			}
		});

		EnemyUtils.changeMoveState(this, MoveState.ANIM1, 0);
		EnemyUtils.changeSwimmingState(this, SwimmingState.STILL, 0.0f, attackWindup2);
		EnemyUtils.changeFloatingFreeAngle(this, 0.0f, 0.0f);

		getActions().add(new EnemyAction(this, attackCooldown) {
			
			private final Vector2 startVelo = new Vector2();
			@Override
			public void execute() {
				
				if (attackTarget == null) {
					return;
				}

				if (null != reticleTarget) {
					startVelo.set(reticleTarget.getPixelPosition()).sub(enemy.getPixelPosition());
				} else {
					startVelo.set(projectileSpeed, projectileSpeed).setAngleDeg(getAttackAngle());
				}

				if (startVelo.len2() < range * range) {
					startVelo.nor().scl(projectileSpeed);
					SyncedAttack.SNIPERFISH_ATTACK.initiateSyncedAttackSingle(state, enemy, enemy.getPixelPosition(), startVelo);
				}
			}
		});
		
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
		EnemyUtils.changeMoveState(this, MoveState.DEFAULT, 0.0f);
	}

	private final Vector2 reticlePosition = new Vector2();
	@Override
	public void controller(float delta) {
		super.controller(delta);

		//we keep track of the reticle target because we needs its position after it dies.
		//headless server never queries pixel position for rendering, so it never gets updated after creation.
		if (null != reticleTarget) {
			reticlePosition.set(reticleTarget.getPixelPosition());
		}
	}

	private final Vector2 originPt = new Vector2();
	private final Vector2 addVelo = new Vector2();
	@Override
	public Vector2 getProjectileOrigin(Vector2 startVelo, float projSize) {
		boolean flip = true;
		float realAngle = getAngle() % (MathUtils.PI * 2);
		if ((realAngle > MathUtils.PI / 2 && realAngle < 3 * MathUtils.PI / 2) || (realAngle < -MathUtils.PI / 2 && realAngle > -3 * MathUtils.PI / 2)) {
			flip = false;
		}

		originPt.set(getPixelPosition())
				.add(addVelo.set(startVelo).nor().scl(getHboxSize().x / 2))
				.add(addVelo.set(startVelo).rotate90(flip ? 1 : -1).nor().scl(getHboxSize().y / 4));
		return originPt;
	}
}
