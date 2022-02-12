package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.DeathRagdoll;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;
import com.mygdx.hadal.utils.Stats;

public class Crawler2 extends EnemyCrawling {

	private static final int baseHp = 100;

	private static final int scrapDrop = 2;

	private static final int width = 63;
	private static final int height = 40;
	
	private static final int hboxWidth = 63;
	private static final int hboxHeight = 40;
	
	private static final float attackCd = 1.0f;
	private static final float groundSpeed = 0.1f;
			
	private static final Sprite sprite = Sprite.FISH_TORPEDO;
	
	public Crawler2(PlayState state, Vector2 startPos, float startAngle, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), sprite, EnemyType.CRAWLER2, startAngle, filter, baseHp, attackCd, scrapDrop, spawner);

		EnemyUtils.changeCrawlingState(this, CrawlingState.AVOID_PITS, 0.5f, 0.0f);
	}
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.GROUND_SPD, groundSpeed, getBodyData()));
		getBodyData().addStatus(new DeathRagdoll(state, getBodyData(), sprite, size));
	}
	
	private static final float minRange = 0.0f;
	private static final float maxRange = 100.0f;

	private static final float attackWindup1 = 0.5f;
	private static final float attackWindup2 = 0.1f;
	private static final int attack1Amount = 4;
	private static final int attack1Damage = 12;
	private static final int defaultMeleeKB = 20;
	private static final Vector2 meleeSize = new Vector2(100.0f, 100.0f);
	private static final int meleeRange = 1;
	private static final float meleeInterval = 0.25f;
	@Override
	public void attackInitiate() {
		
		if (attackTarget != null) {
			EnemyUtils.setCrawlingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		} else {
			EnemyUtils.changeCrawlingState(this, CrawlingState.AVOID_PITS, 0.5f, 0.0f);
		}
		
		if (attackTarget != null) {
			
			EnemyUtils.windupParticles(state, this, attackWindup1, Particle.CHARGING, HadalColor.RED, 120.0f);
			EnemyUtils.changeCrawlingState(this, CrawlingState.STILL, 0.0f, 0.0f);
			EnemyUtils.windupParticles(state, this, attackWindup2, Particle.OVERCHARGE, HadalColor.RED, 120.0f);
			
			for (int i = 0; i < attack1Amount; i++) {
				
				getActions().add(new EnemyAction(this, meleeInterval) {
					
					@Override
					public void execute() {
						Hitbox hbox = new Hitbox(state, enemy.getPixelPosition(), meleeSize, meleeInterval, enemy.getLinearVelocity(), enemy.getHitboxfilter(), true, true, enemy, Sprite.IMPACT);
						hbox.makeUnreflectable();
						hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
						hbox.addStrategy(new DamageStandard(state, hbox, enemy.getBodyData(), attack1Damage, defaultMeleeKB, DamageTypes.MELEE).setStaticKnockback(true));
						hbox.addStrategy(new FixedToEntity(state, hbox, enemy.getBodyData(), new Vector2(), new Vector2(meleeRange * getMoveDirection(), 0)).setRotate(true));
					}
				});
			}
			EnemyUtils.setCrawlingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		}
	}
}
