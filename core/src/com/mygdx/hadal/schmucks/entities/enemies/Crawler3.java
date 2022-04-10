package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.DeathRagdoll;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.utils.Stats;

public class Crawler3 extends EnemyCrawling {

	private static final int baseHp = 100;

	private static final int scrapDrop = 2;

	private static final int width = 63;
	private static final int height = 40;
	
	private static final int hboxWidth = 63;
	private static final int hboxHeight = 40;
	
	private static final float attackCd = 1.0f;
	private static final float groundSpeed = -0.75f;
			
	private static final Sprite sprite = Sprite.FISH_TORPEDO;
	
	public Crawler3(PlayState state, Vector2 startPos, float startAngle, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), sprite, EnemyType.CRAWLER3, startAngle, filter, baseHp, attackCd, scrapDrop, spawner);

		EnemyUtils.changeCrawlingState(this, CrawlingState.AVOID_PITS, 1.0f, 0.0f);
	}
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.GROUND_SPD, groundSpeed, getBodyData()));
		getBodyData().addStatus(new DeathRagdoll(state, getBodyData(), sprite, size));
	}
	
	private static final float minRange = 0.0f;
	private static final float maxRange = 500.0f;
	
	private static final int numProj = 5;
	private static final int spread = 10;
	
	private static final float attackWindup1 = 0.6f;
	private static final float attackWindup2 = 0.2f;
	private static final float baseDamage = 7.0f;
	private static final float knockback = 12.0f;
	private static final float projectileSpeed = 20.0f;
	private static final Vector2 projectileSize = new Vector2(16, 16);
	private static final float lifespan = 1.2f;
	
	private final Vector2 startVelo = new Vector2();
	private final Vector2 spreadVelo = new Vector2();
	@Override
	public void attackInitiate() {
		
		if (attackTarget != null) {
			EnemyUtils.setCrawlingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		} else {
			EnemyUtils.changeCrawlingState(this, CrawlingState.AVOID_PITS, 1.0f, 0.0f);
		}
		
		if (attackTarget != null) {
			EnemyUtils.windupParticles(state, this, attackWindup1, Particle.CHARGING, HadalColor.RED, 120.0f);
			EnemyUtils.changeCrawlingState(this, CrawlingState.STILL, 0.0f, 0.0f);
			EnemyUtils.windupParticles(state, this, attackWindup2, Particle.OVERCHARGE, HadalColor.RED, 120.0f);
			
			for (int i = 0; i < numProj; i++) {
				
				final int index = i;
				
				getActions().add(new EnemyAction(this, 0.0f) {
					
					@Override
					public void execute() {
						
						if (index == 0) {
							SoundEffect.SPIT.playUniversal(state, enemy.getPixelPosition(), 0.8f, false);
						}
						
						startVelo.set(getMoveDirection() * projectileSpeed, projectileSpeed / 2);

						float newDegrees = startVelo.angleDeg() + MathUtils.random(-spread, spread + 1);
						spreadVelo.set(startVelo.setAngleDeg(newDegrees));
						Hitbox hbox = new RangedHitbox(state, enemy.getProjectileOrigin(spreadVelo, size.x), projectileSize, lifespan, spreadVelo, getHitboxfilter(), true, true, enemy, Sprite.ORB_RED);
						hbox.setGravity(3.0f);
						
						hbox.addStrategy(new ControllerDefault(state, hbox, getBodyData()));
						hbox.addStrategy(new ContactWallDie(state, hbox, getBodyData()));
						hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, getBodyData()));
						hbox.addStrategy(new DamageStandard(state, hbox, getBodyData(), baseDamage, knockback,
								DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
					}
				});
			}
			EnemyUtils.setCrawlingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		}
	}
}
