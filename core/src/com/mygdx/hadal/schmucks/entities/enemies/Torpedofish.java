package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DeathRagdoll;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.strategies.enemy.MovementFloat.FloatingState;
import com.mygdx.hadal.strategies.enemy.MovementSwim.SwimmingState;
import com.mygdx.hadal.utils.Stats;

public class Torpedofish extends EnemySwimming {

	private static final int baseHp = 100;

	private static final int scrapDrop = 2;
	
	private static final int width = 63;
	private static final int height = 40;
	
	private static final int hboxWidth = 63;
	private static final int hboxHeight = 40;
	
	private static final float attackCd = 0.75f;
	private static final float airSpeed = -0.4f;
	
	private static final float minRange = 5.0f;
	private static final float maxRange = 10.0f;
	
	private static final float noiseRadius = 15.0f;

	private static final Sprite sprite = Sprite.FISH_TORPEDO;
	private static final Sprite projSprite = Sprite.MISSILE_A;
	
	public Torpedofish(PlayState state, Vector2 startPos, float startAngle, short filter) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), sprite, EnemyType.TORPEDOFISH, startAngle,  filter, baseHp, attackCd, scrapDrop);
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

	private static final float baseDamage = 5.0f;
	private static final float knockback = 0.5f;
	private static final float projectileSpeed = 30.0f;
	private static final Vector2 projectileSize = new Vector2(56, 22);
	private static final float lifespan = 5.0f;
	
	private static final int explosionRadius = 100;
	private static final float explosionDamage = 15.0f;
	private static final float explosionKnockback = 35.0f;
	
	private static final float range = 900.0f;
	@Override
	public void attackInitiate() {
		
		EnemyUtils.windupParticles(state, this, attackWindup1, Particle.CHARGING, HadalColor.RED, 120.0f);
		EnemyUtils.changeSwimmingState(this, SwimmingState.STILL, 0.0f, 0.0f);
		EnemyUtils.changeFloatingFreeAngle(this, 0.0f, 0.0f);
		EnemyUtils.windupParticles(state, this, attackWindup2, Particle.OVERCHARGE, HadalColor.RED, 120.0f);

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
					SoundEffect.SPIT.playUniversal(state, enemy.getPixelPosition(), 1.0f, 0.75f, false);
					
					Hitbox hbox = new RangedHitbox(state, enemy.getProjectileOrigin(startVelo, size.x), projectileSize, lifespan, startVelo, enemy.getHitboxfilter(), true, true, enemy, projSprite);
					
					hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
					hbox.addStrategy(new AdjustAngle(state, hbox, enemy.getBodyData()));
					hbox.addStrategy(new ContactUnitDie(state, hbox, enemy.getBodyData()));
					hbox.addStrategy(new ContactWallDie(state, hbox, enemy.getBodyData()));
					hbox.addStrategy(new DamageStandard(state, hbox, enemy.getBodyData(), baseDamage, knockback,
							DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
					hbox.addStrategy(new CreateParticles(state, hbox, enemy.getBodyData(), Particle.BUBBLE_TRAIL, 0.0f, 1.0f));
					hbox.addStrategy(new DieExplode(state, hbox, enemy.getBodyData(), explosionRadius, explosionDamage,
							explosionKnockback, (short) 0, true, DamageSource.ENEMY_ATTACK));
					hbox.addStrategy(new DieSound(state, hbox, enemy.getBodyData(), SoundEffect.EXPLOSION6, 0.6f).setPitch(1.2f));
				}
			}
		});
		
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
	}
}
