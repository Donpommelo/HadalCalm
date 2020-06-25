package com.mygdx.hadal.schmucks.bodies.enemies;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.DeathRagdoll;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.utils.Stats;

public class Crawler3 extends EnemyCrawling {

	private final static int baseHp = 100;
	private final static String name = "SPITTING CRAWLER";

	private final static int scrapDrop = 1;

	private static final int width = 63;
	private static final int height = 40;
	
	private static final int hboxWidth = 63;
	private static final int hboxHeight = 40;
	
	private static final float attackCd = 2.0f;
	private static final float groundSpeed = -0.75f;
			
	private static final Sprite sprite = Sprite.FISH_TORPEDO;
	
	public Crawler3(PlayState state, Vector2 startPos, float startAngle, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), name, sprite, EnemyType.CRAWLER3, startAngle, filter, baseHp, attackCd, scrapDrop, spawner);

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
	
	private final static int numProj = 5;
	private final static int spread = 10;
	
	private final static float baseDamage = 10.0f;
	private final static float knockback = 12.0f;
	private final static float projectileSpeed = 20.0f;
	private final static Vector2 projectileSize = new Vector2(16, 16);
	private final static float lifespan = 1.2f;
	
	private Vector2 startVelocity = new Vector2();
	@Override
	public void attackInitiate() {
		
		if (attackTarget != null) {
			EnemyUtils.setCrawlingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		} else {
			EnemyUtils.changeCrawlingState(this, CrawlingState.AVOID_PITS, 1.0f, 0.0f);
		}
		
		if (attackTarget != null) {
			EnemyUtils.changeCrawlingState(this, CrawlingState.STILL, 0.0f, 0.4f);
			
			for (int i = 0; i < numProj; i++) {
				
				getActions().add(new EnemyAction(this, 0) {
					
					@Override
					public void execute() {
						startVelocity.set(getMoveDirection() * projectileSpeed, projectileSpeed / 2);

						float newDegrees = (float) (startVelocity.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
						Vector2 startVelo = new Vector2(startVelocity.setAngle(newDegrees));
						Hitbox hbox = new RangedHitbox(state, enemy.getProjectileOrigin(startVelo, size.x), projectileSize, lifespan, startVelo, getHitboxfilter(), true, true, enemy, Sprite.ORB_RED);
						hbox.setGravity(3.0f);
						
						hbox.addStrategy(new ControllerDefault(state, hbox, getBodyData()));
						hbox.addStrategy(new ContactWallDie(state, hbox, getBodyData()));
						hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, getBodyData()));
						hbox.addStrategy(new DamageStandard(state, hbox, getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
					}
				});
			}
			EnemyUtils.setCrawlingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		}
	}
}
