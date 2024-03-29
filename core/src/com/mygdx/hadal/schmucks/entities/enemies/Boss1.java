package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DeathRagdoll;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.enemy.CreateMultiplayerHpScaling;
import com.mygdx.hadal.strategies.enemy.MovementFloat.FloatingState;

/**
 * This is a boss in the game
 * @author Briburger Blurnip
 */
public class Boss1 extends EnemyFloating {
	
    private static final float aiAttackCd = 3.0f;
    private static final float aiAttackCd2 = 2.2f;
    private static final float aiAttackCd3 = 1.5f;
	
    private static final int scrapDrop = 15;
    
	private static final int width = 300;
	private static final int height = 192;
	
	private static final int hbWidth = 300;
	private static final int hbHeight = 192;
	
	private static final float scale = 1.0f;
	
	private static final int hp = 7000;
	private static final int moveSpeed = 20;
	private static final int spinSpeed = 40;
	
	private static final Sprite sprite = Sprite.FISH_TORPEDO_IDLE;
	
	private int phase = 1;
	private static final float phaseThreshold2 = 0.8f;
	private static final float phaseThreshold3 = 0.4f;
	
	public Boss1(PlayState state, Vector2 startPos, short filter) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hbWidth, hbHeight).scl(scale), sprite, EnemyType.BOSS1, filter, hp, aiAttackCd, scrapDrop);
		addStrategy(new CreateMultiplayerHpScaling(state, this, 1700));
	}
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.KNOCKBACK_RES, 1.0f, getBodyData()));
		getBodyData().addStatus(new DeathRagdoll(state, getBodyData(), sprite, size));
	}

	private int attackNum = 0;
	@Override
	public void attackInitiate() {
		attackNum++;
		if (phase == 1) {
			if (getBodyData().getCurrentHp() <= phaseThreshold2 * getBodyData().getStat(Stats.MAX_HP)) {
				phase = 2;
				setAttackCd(aiAttackCd2);
				spawnAdds();
			} else {
				int randomIndex = MathUtils.random(3);
				switch (randomIndex) {
					case 0 -> chargeAttack1();
					case 1 -> chargeAttack2();
					case 2 -> fireBreath();
					case 3 -> fallingDebris();
				}
			}
		}
		
		if (phase == 2) {
			if (getBodyData().getCurrentHp() <= phaseThreshold3 * getBodyData().getStat(Stats.MAX_HP)) {
				phase = 3;
				setAttackCd(aiAttackCd3);
				spawnAdds();
			} else if (attackNum % 2 == 0) {
				int randomIndex = MathUtils.random(4);
				switch (randomIndex) {
					case 0 -> chargeAttack3();
					case 1 -> chargeAttack4();
					case 2 -> fireBreath2();
					case 3 -> horizontalLaser();
					case 4 -> sweepingLaser();
				}
			} else {
				int randomIndex = MathUtils.random(3);
				switch (randomIndex) {
					case 0 -> bouncyBall();
					case 1 -> vengefulSpirit();
					case 2 -> poisonCloud();
					case 3 -> fallingDebris();
				}
			}
		}
		
		if (phase == 3) {
			if (attackNum % 2 == 0) {
				int randomIndex = MathUtils.random(5);
				switch (randomIndex) {
					case 0 -> chargeAttack3();
					case 1 -> chargeAttack5();
					case 2 -> fireBreath2();
					case 3 -> sweepingLaser();
					case 4 -> horizontalLaser();
					case 5 -> rotatingLaser();
				}
			} else {
				int randomIndex = MathUtils.random(3);
				switch (randomIndex) {
					case 0 -> bouncyBall();
					case 1 -> vengefulSpirit();
					case 2 -> fallingDebris();
					case 3 -> poisonCloud();
				}
			}
			fallingDebrisPassive();
		}
	}
	
	private static final int charge1Speed = 40;
	private static final int charge2Speed = 30;
	private static final float charge1Damage = 2.5f;
	private static final float charge2Damage = 1.5f;
	private static final int charge1Knockback = 12;
	private static final int charge2Knockback = 12;

	private static final float moveDurationMax = 5.0f;
	
	private static final float chargeAttackInterval = 1 / 60.0f;
	private static final float charge1AttackDuration = 1.25f;
	private static final float charge2AttackDuration = 2.75f;
	
	private void chargeAttack1() {
		EnemyUtils.moveToRandomCorner(state, this, moveSpeed, moveDurationMax);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, 1.2f, 1.0f, 2.0f, SoundEffect.WOOSH, true);
		
		EnemyUtils.changeFloatingState(this, FloatingState.SPINNING, spinSpeed, 1.2f);
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.4f);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, charge1AttackDuration, 1.0f, 1.5f, SoundEffect.BOOMERANG_WHIZ, false);
		EnemyUtils.moveToPlayer(this, attackTarget, charge1Speed, 0.0f);
		EnemyUtils.meleeAttackContinuous(state, this, charge1Damage, chargeAttackInterval, charge1Knockback, charge1AttackDuration);
	}
	
	private void chargeAttack2() {
		int corner = EnemyUtils.moveToRandomCorner(state, this, moveSpeed, moveDurationMax);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, 0.5f, 1.0f, 2.0f, SoundEffect.WOOSH, true);
		
		EnemyUtils.changeFloatingState(this, FloatingState.SPINNING, spinSpeed, 0.5f);
		EnemyUtils.meleeAttackContinuous(state, this, charge2Damage, chargeAttackInterval, charge2Knockback, charge2AttackDuration);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, charge2AttackDuration, 1.0f, 0.5f, SoundEffect.WOOSH, true);

		switch (corner) {
			case 0 -> {
				EnemyUtils.moveToDummy(state, this, "2", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "8", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "6", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "0", charge2Speed, moveDurationMax);
			}
			case 1 -> {
				EnemyUtils.moveToDummy(state, this, "8", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "6", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "0", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "2", charge2Speed, moveDurationMax);
			}
			case 2 -> {
				EnemyUtils.moveToDummy(state, this, "0", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "2", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "8", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "6", charge2Speed, moveDurationMax);
			}
			case 3 -> {
				EnemyUtils.moveToDummy(state, this, "6", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "0", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "2", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "8", charge2Speed, moveDurationMax);
			}
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private void chargeAttack3() {
		EnemyUtils.moveToRandomCorner(state, this, moveSpeed, moveDurationMax);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, 1.0f, 1.0f, 2.0f, SoundEffect.WOOSH, true);
		EnemyUtils.changeFloatingState(this, FloatingState.SPINNING, spinSpeed, 1.0f);
		
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.4f);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, charge1AttackDuration, 1.0f, 1.5f, SoundEffect.BOOMERANG_WHIZ, false);
		EnemyUtils.moveToPlayer(this, attackTarget, charge1Speed, 0.0f);
		EnemyUtils.meleeAttackContinuous(state, this, charge1Damage, chargeAttackInterval, charge1Knockback, charge1AttackDuration);
		
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 2.0f);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, charge1AttackDuration, 1.0f, 1.5f, SoundEffect.BOOMERANG_WHIZ, false);
		EnemyUtils.moveToPlayer(this, attackTarget, charge1Speed, 0.0f);
		EnemyUtils.meleeAttackContinuous(state, this, charge1Damage, chargeAttackInterval, charge1Knockback, charge1AttackDuration);
	}
	
	private void chargeAttack4() {
		int corner = EnemyUtils.moveToRandomCorner(state, this, moveSpeed, moveDurationMax);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, 0.5f, 1.0f, 2.0f, SoundEffect.WOOSH, true);
		EnemyUtils.changeFloatingState(this, FloatingState.SPINNING, spinSpeed, 0.5f);
		
		EnemyUtils.meleeAttackContinuous(state, this, charge2Damage, chargeAttackInterval, charge2Knockback, charge2AttackDuration);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, charge2AttackDuration, 1.0f, 0.5f, SoundEffect.WOOSH, true);
		switch (corner) {
			case 0 -> {
				EnemyUtils.moveToDummy(state, this, "3", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "7", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "8", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "5", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "1", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "0", charge2Speed, moveDurationMax);
			}
			case 1 -> {
				EnemyUtils.moveToDummy(state, this, "1", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "3", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "6", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "7", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "5", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "2", charge2Speed, moveDurationMax);
			}
			case 2 -> {
				EnemyUtils.moveToDummy(state, this, "7", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "5", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "2", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "1", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "3", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "6", charge2Speed, moveDurationMax);
			}
			case 3 -> {
				EnemyUtils.moveToDummy(state, this, "5", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "1", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "0", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "3", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "7", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "8", charge2Speed, moveDurationMax);
			}
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private void chargeAttack5() {
		int corner = EnemyUtils.moveToRandomCorner(state, this, moveSpeed, moveDurationMax);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, 0.5f, 1.0f, 2.0f, SoundEffect.WOOSH, true);
		EnemyUtils.changeFloatingState(this, FloatingState.SPINNING, spinSpeed, 0.5f);
		
		EnemyUtils.meleeAttackContinuous(state, this, charge2Damage, chargeAttackInterval, charge2Knockback, charge2AttackDuration);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, charge2AttackDuration, 1.0f, 0.5f, SoundEffect.WOOSH, true);
		switch (corner) {
			case 0 -> {
				EnemyUtils.moveToDummy(state, this, "6", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "7", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "1", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "2", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "8", charge2Speed, moveDurationMax);
			}
			case 1 -> {
				EnemyUtils.moveToDummy(state, this, "8", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "7", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "1", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "0", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "6", charge2Speed, moveDurationMax);
			}
			case 2 -> {
				EnemyUtils.moveToDummy(state, this, "0", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "1", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "7", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "8", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "2", charge2Speed, moveDurationMax);
			}
			case 3 -> {
				EnemyUtils.moveToDummy(state, this, "2", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "1", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "7", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "6", charge2Speed, moveDurationMax);
				EnemyUtils.moveToDummy(state, this, "0", charge2Speed, moveDurationMax);
			}
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private static final float fireWindup = 1.5f;
	private static final int fireSpeed = 13;
	private static final int fireballNumber = 40;
	private static final float fireballInterval = 0.02f;
	private void fireBreath() {
		int wall = EnemyUtils.moveToRandomWall(state, this, moveSpeed, moveDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -90.0f, 0.0f);

		EnemyUtils.createSoundEntity(state, this, 0.0f, fireWindup, 0.6f, 0.5f, SoundEffect.FLAMETHROWER, true);
		EnemyUtils.windupParticles(state, this, fireWindup, Particle.FIRE, 40.0f);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, fireballNumber * fireballInterval, 0.6f, 1.5f, SoundEffect.FLAMETHROWER, true);
		switch (wall) {
			case 0 -> {
				EnemyUtils.changeFloatingState(this, FloatingState.FREE, 0.0f, 0.0f);
				for (int i = 0; i < fireballNumber; i++) {
					EnemyUtils.fireball(state, this, fireSpeed, fireballInterval, 1);
				}
			}
			case 1 -> {
				EnemyUtils.changeFloatingState(this, FloatingState.FREE, -180.0f, 0.0f);
				for (int i = 0; i < fireballNumber; i++) {
					EnemyUtils.fireball(state, this, fireSpeed, fireballInterval, 1);
				}
			}
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private void fireBreath2() {
		int wall = EnemyUtils.moveToRandomWall(state, this, moveSpeed, moveDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -90.0f, 0.0f);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, fireWindup, 0.6f, 0.5f, SoundEffect.FLAMETHROWER, true);
		EnemyUtils.windupParticles(state, this, fireWindup, Particle.FIRE, 40.0f);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, fireballNumber * fireballInterval, 0.6f, 1.5f, SoundEffect.FLAMETHROWER, true);
		switch (wall) {
			case 0 -> {
				EnemyUtils.changeFloatingState(this, FloatingState.FREE, 0.0f, 0.0f);
				for (int i = 0; i < fireballNumber; i++) {
					EnemyUtils.fireball(state, this, fireSpeed,  fireballInterval, 1);
				}
				EnemyUtils.changeFloatingState(this, FloatingState.FREE, 90.0f, 0.0f);
				EnemyUtils.createSoundEntity(state,this,0.0f,fireballNumber * fireballInterval * 2,
					0.6f, 2.0f, SoundEffect.FLAMETHROWER,true);
				for (int i = 0; i < fireballNumber; i++) {
					EnemyUtils.fireball(state, this, fireSpeed,  fireballInterval, 1);
				}
				EnemyUtils.changeFloatingState(this, FloatingState.FREE, -60.0f, 0.0f);
				for (int i = 0; i < fireballNumber; i++) {
					EnemyUtils.fireball(state, this, fireSpeed,  fireballInterval, 1);
				}
			}
			case 1 -> {
				EnemyUtils.changeFloatingState(this, FloatingState.FREE, -180.0f, 0.0f);
				for (int i = 0; i < fireballNumber; i++) {
					EnemyUtils.fireball(state, this, fireSpeed,  fireballInterval, 1);
				}
				EnemyUtils.changeFloatingState(this, FloatingState.FREE, -270.0f, 0.0f);
				EnemyUtils.createSoundEntity(state,this,0.0f,fireballNumber * fireballInterval * 2,
					0.6f, 2.0f, SoundEffect.FLAMETHROWER,true);
				for (int i = 0; i < fireballNumber; i++) {
					EnemyUtils.fireball(state, this, fireSpeed,  fireballInterval, 1);
				}
				EnemyUtils.changeFloatingState(this, FloatingState.FREE, -120.0f, 0.0f);
				for (int i = 0; i < fireballNumber; i++) {
					EnemyUtils.fireball(state, this, fireSpeed,  fireballInterval, 1);
				}
			}
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private static final float laser1Windup = 0.2f;

	private static final float trackInterval = 0.25f;
	private static final int trackAmount = 12;
	private static final int trackSpeed = 60;
	
	private static final float laser1Interval = 0.03f;
	private static final int laser1Amount = 40;
	private static final float laser1Speed = 55.0f;

	private void horizontalLaser() {
		int wall = EnemyUtils.moveToRandomWall(state, this, moveSpeed, moveDurationMax);
		switch (wall) {
			case 0 -> {
				EnemyUtils.changeFloatingState(this, FloatingState.FREE, 0.0f, 0.0f);
				for (int i = 0; i < trackAmount; i++) {
					EnemyUtils.trackPlayerXY(this, attackTarget, trackSpeed, trackInterval, false);
				}
				EnemyUtils.createSoundEntity(state, this, 0.0f, laser1Windup, 1.0f, 0.5f, SoundEffect.BEAM3, true);
				EnemyUtils.stopStill(this, laser1Windup);
				EnemyUtils.createSoundEntity(state,this,0.0f,laser1Amount * laser1Interval,
					1.0f,2.0f,	SoundEffect.BEAM3,true);
				for (int i = 0; i < laser1Amount; i++) {
					getActions().add(new EnemyAction(this, laser1Interval) {

						@Override
						public void execute() {
							Vector2 startVelo = new Vector2(laser1Speed, laser1Speed).setAngleDeg(getAttackAngle());
							SyncedAttack.BOSS_TRACKING_BEAM.initiateSyncedAttackSingle(state, enemy, getPixelPosition(), startVelo);
						}
					});
				}
			}
			case 1 -> {
				EnemyUtils.changeFloatingState(this, FloatingState.FREE, -180.0f, 0.0f);
				for (int i = 0; i < trackAmount; i++) {
					EnemyUtils.trackPlayerXY(this, attackTarget, trackSpeed, trackInterval, false);
				}
				EnemyUtils.createSoundEntity(state, this, 0.0f, laser1Windup, 1.0f, 0.5f, SoundEffect.BEAM3, true);
				EnemyUtils.stopStill(this, laser1Windup);
				EnemyUtils.createSoundEntity(state,this,0.0f,laser1Amount * laser1Interval,
					1.0f,2.0f,	SoundEffect.BEAM3,true);
				for (int i = 0; i < laser1Amount; i++) {
					getActions().add(new EnemyAction(this, laser1Interval) {

						@Override
						public void execute() {
							Vector2 startVelo = new Vector2(laser1Speed, laser1Speed).setAngleDeg(getAttackAngle());
							SyncedAttack.BOSS_TRACKING_BEAM.initiateSyncedAttackSingle(state, enemy, getPixelPosition(), startVelo);
						}
					});
				}
			}
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private static final float laser2Windup = 2.0f;
	private static final float rotateSpeed = 2.5f;
	private static final float laser2Interval = 0.02f;
	private static final int laser2Amount = 100;
	private static final float laser2Speed = 15.0f;
	
	private void rotatingLaser() {
		EnemyUtils.moveToDummy(state, this, "4", moveSpeed, moveDurationMax);
		
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -90.0f, 0.0f);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, laser2Windup, 1.0f, 0.5f, SoundEffect.BEAM3, true);
		EnemyUtils.windupParticles(state, this, laser2Windup, Particle.LASER_PULSE, 40.0f);

		boolean random = MathUtils.randomBoolean();
		
		if (random) {
			EnemyUtils.changeFloatingState(this, FloatingState.ROTATING, rotateSpeed, 0.0f);
		} else {
			EnemyUtils.changeFloatingState(this, FloatingState.ROTATING, -rotateSpeed, 0.0f);
		}
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, laser2Amount * laser2Interval, 1.0f, 2.0f, SoundEffect.BEAM3, true);
		for (int i = 0; i < laser2Amount; i++) {
			getActions().add(new EnemyAction(this, laser2Interval) {

				@Override
				public void execute() {
					Vector2 startVelo = new Vector2(laser2Speed, laser2Speed).setAngleDeg(getAttackAngle());
					SyncedAttack.BOSS_ROTATING_BEAM.initiateSyncedAttackSingle(state, enemy, getPixelPosition(), startVelo);
				}
			});
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 1.0f);
	}

	private static final float laser3Windup = 0.75f;
	private static final int laser3Amount = 25;
	private static final float laser3Speed = 55.0f;
	private static final int explosionNumber = 7;
	private static final float explosionSize = 300;
	private static final float explosionInterval = 0.25f;
	
	private void sweepingLaser() {
		int wall = EnemyUtils.moveToRandomWall(state, this, moveSpeed, moveDurationMax);
		
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -90.0f, 0.0f);
		EnemyUtils.windupParticles(state, this, laser3Windup, Particle.LASER_PULSE, 40.0f);

		switch (wall) {
			case 0 -> {
				EnemyUtils.changeFloatingState(this, FloatingState.FREE, -15.0f, 0.0f);
				EnemyUtils.createSoundEntity(state,this,0.0f,laser3Amount * laser2Interval,
					1.0f,2.0f, SoundEffect.BEAM3,true);
				for (int i = 0; i < laser3Amount; i++) {
					getActions().add(new EnemyAction(this, laser2Interval) {

						@Override
						public void execute() {
							Vector2 startVelo = new Vector2(laser3Speed, laser3Speed).setAngleDeg(getAttackAngle());
							SyncedAttack.BOSS_SWEEPING_BEAM.initiateSyncedAttackSingle(state, enemy, getPixelPosition(), startVelo);
						}
					});
				}
				for (int i = 1; i <= explosionNumber; i++) {
					final int index = i;
					getActions().add(new EnemyAction(this, explosionInterval) {

						private final Vector2 location = new Vector2();

						@Override
						public void execute() {
							location.set(EnemyUtils.getLeftSide(state) + index * explosionSize / 2,
								EnemyUtils.floorHeight(state));
							SyncedAttack.BOSS_SWEEPING_EXPLOSION.initiateSyncedAttackNoHbox(state, enemy, location, true);
						}
					});
				}
			}
			case 1 -> {
				EnemyUtils.changeFloatingState(this, FloatingState.FREE, -165.0f, 0.0f);
				EnemyUtils.createSoundEntity(state,this,0.0f,laser3Amount * laser2Interval,
					1.0f,2.0f, SoundEffect.BEAM3,true);
				for (int i = 0; i < laser3Amount; i++) {
					getActions().add(new EnemyAction(this, laser2Interval) {

						  @Override
						  public void execute() {
							  Vector2 startVelo = new Vector2(laser3Speed, laser3Speed).setAngleDeg(getAttackAngle());
							  SyncedAttack.BOSS_SWEEPING_BEAM.initiateSyncedAttackSingle(state, enemy, getPixelPosition(), startVelo);
						  }
				  	});
				}
				for (int i = 1; i <= explosionNumber; i++) {
					final int index = i;
					getActions().add(new EnemyAction(this, explosionInterval) {

						private final Vector2 location = new Vector2();
						@Override
						public void execute() {
							location.set(EnemyUtils.getRightSide(state) - index * explosionSize / 2,
								EnemyUtils.floorHeight(state));
							SyncedAttack.BOSS_SWEEPING_EXPLOSION.initiateSyncedAttackNoHbox(state, enemy, location, true);
						}
					});
				}
			}
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private static final int numBalls = 3;
	private static final int spread = 15;
	private static final float ballSpeed = 10.0f;
	private static final float ballInterval= 1.0f;
	private void bouncyBall() {
		float baseAngle;
		if (MathUtils.randomBoolean()) {
			EnemyUtils.moveToDummy(state, this, "0", moveSpeed, moveDurationMax);
			baseAngle = -60.0f;
		} else {
			EnemyUtils.moveToDummy(state, this, "2", moveSpeed, moveDurationMax);
			baseAngle = -120.0f;
		}
		
		for (int i = 0; i < numBalls; i++) {
			EnemyUtils.changeFloatingState(this, FloatingState.FREE,
					baseAngle + MathUtils.random(-spread, spread + 1), ballInterval);
			
			getActions().add(new EnemyAction(this, 0.0f) {
				
				@Override
				public void execute() {
					Vector2 startVelo = new Vector2(ballSpeed, ballSpeed).setAngleDeg(getAttackAngle());
					SyncedAttack.BOSS_BOUNCY_BALL.initiateSyncedAttackSingle(state, enemy, enemy.getPixelPosition(), startVelo);
				}
			});
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 1.0f);
	}
	
	private static final float spiritWindup = 0.75f;
	private static final float spiritDamage= 15.0f;
	private void vengefulSpirit() {
		EnemyUtils.changeFloatingState(this, FloatingState.SPINNING, spinSpeed, 0.0f);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, spiritWindup, 1.0f, 2.0f, SoundEffect.WOOSH, true);
		EnemyUtils.windupParticles(state, this, spiritWindup, Particle.BRIGHT, HadalColor.RANDOM, 40.0f);
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
		
		getActions().add(new EnemyAction(this, 0.0f) {
			
			@Override
			public void execute() {
				Vector2[] positions = new Vector2[3];
				positions[0] = new Vector2(getPixelPosition()).add(0, 100);
				positions[1] = new Vector2(getPixelPosition()).add(100, 0);
				positions[2] = new Vector2(getPixelPosition()).add(-100, 0);
				SyncedAttack.VENGEFUL_SPIRIT.initiateSyncedAttackMulti(state, enemy, new Vector2(), positions,
						new Vector2[] {}, 0.0f, 0.0f, spiritDamage);
			}
		});
	}
	
	private static final int numPoison = 9;
	private static final float poisonInterval = 0.75f;
	private static final Vector2 poisonSize = new Vector2(150, 280);

	private void poisonCloud() {
		int wall = EnemyUtils.moveToRandomWall(state, this, moveSpeed, moveDurationMax);
		
		final Enemy me = this;
		
		if (wall == 0) {
			EnemyUtils.changeFloatingState(this, FloatingState.FREE, -75.0f, 2.0f);
			
			getActions().add(new EnemyAction(this, 0.0f) {
				
				@Override
				public void execute() {

					for (int i = 0; i < numPoison; i++) {
						final int index = i;
						getSecondaryActions().add(new EnemyAction(me, poisonInterval) {
							
							private final Vector2 location = new Vector2();
							@Override
							public void execute() {
								location.set(EnemyUtils.getLeftSide(state) + index * poisonSize.x, EnemyUtils.floorHeight(state) + poisonSize.y / 2);
								SyncedAttack.BOSS_POISON_CLOUD.initiateSyncedAttackNoHbox(state, enemy, location, true, index);
							}
						});
					}
				}
			});

		} else {
			EnemyUtils.changeFloatingState(this, FloatingState.FREE, -105.0f, 2.0f);
			
			getActions().add(new EnemyAction(this, 0.0f) {
				
				@Override
				public void execute() {
					for (int i = 0; i < numPoison; i++) {
						final int index = i;
						getSecondaryActions().add(new EnemyAction(me, poisonInterval) {
							
							private final Vector2 location = new Vector2();
							@Override
							public void execute() {
								location.set(EnemyUtils.getRightSide(state) - index * poisonSize.x, EnemyUtils.floorHeight(state) + poisonSize.y / 2);
								SyncedAttack.BOSS_POISON_CLOUD.initiateSyncedAttackNoHbox(state, enemy, location, true, index);
							}
						});
					}
				}
			});
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private static final int numAdds = 3;
	private void spawnAdds() {
		EnemyUtils.moveToDummy(state, this, "4", moveSpeed, moveDurationMax);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, 0.75f, 1.0f, 2.0f, SoundEffect.WOOSH, true);
		EnemyUtils.changeFloatingState(this, FloatingState.SPINNING, spinSpeed, 0.75f);
		
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
		for (int i = 0; i < numAdds; i++) {
			EnemyUtils.spawnAdds(state, this, EnemyType.TORPEDOFISH, 1, 0.0f, 0.75f);
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 1.0f);
	}
	
	private static final int numDebris = 18;
	private static final int numDebrisPassive = 8;
	private static final float debrisInterval = 0.25f;
	private void fallingDebris() {
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, 1.0f, 1.0f, 2.0f, SoundEffect.WOOSH, true);
		EnemyUtils.changeFloatingState(this, FloatingState.SPINNING, spinSpeed, 1.0f);
		
		for (int i = 0; i < numDebris; i++) {
			EnemyUtils.fallingDebris(state, this, debrisInterval);
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private void fallingDebrisPassive() {
		for (int i = 0; i < numDebrisPassive; i++) {
			EnemyUtils.fallingDebris(state, this, debrisInterval);
		}
	}
}
