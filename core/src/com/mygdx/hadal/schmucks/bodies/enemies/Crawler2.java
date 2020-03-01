package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStatic;
import com.mygdx.hadal.strategies.hitbox.FixedToUser;
import com.mygdx.hadal.utils.Stats;

public class Crawler2 extends EnemyCrawling {

	private final static int baseHp = 100;

	private final static int scrapDrop = 1;

	private static final int width = 63;
	private static final int height = 40;
	
	private static final int hboxWidth = 63;
	private static final int hboxHeight = 40;
	
	private static final float attackCd = 2.0f;
	private static final float groundSpeed = -0.2f;
			
	private static final Sprite sprite = Sprite.FISH_TORPEDO;
	
	public Crawler2(PlayState state, Vector2 startPos, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), sprite, EnemyType.CRAWLER1, filter, baseHp, attackCd, scrapDrop, spawner);

		EnemyUtils.changeCrawlingState(this, CrawlingState.AVOID_PITS, 0.5f, 0.0f);
	}
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.GROUND_SPD, groundSpeed, getBodyData()));
	}
	
	@Override
	public void controller(float delta) {
		super.controller(delta);
		if (target != null) {
			EnemyUtils.changeCrawlingState(this, CrawlingState.CHASE_PLAYER, 1.0f, 0.0f);
		} else {
			EnemyUtils.changeCrawlingState(this, CrawlingState.AVOID_PITS, 0.5f, 0.0f);
		}
	}
	
	private static final int attack1Amount = 4;
	private static final int attack1Damage = 8;
	private static final int defaultMeleeKB = 20;
	private static final int meleeSize = 80;
	private static final int meleeRange = 1;
	private static final float meleeInterval = 0.25f;
	@Override
	public void attackInitiate() {
		
		if (target != null) {
			EnemyUtils.changeCrawlingState(this, CrawlingState.STILL, 0.0f, 0.4f);
			for (int i = 0; i < attack1Amount; i++) {
				
				getActions().add(new EnemyAction(this, 0) {
					
					@Override
					public void execute() {
						
						Hitbox hbox = new Hitbox(state, enemy.getPixelPosition(), new Vector2(meleeSize, meleeSize), meleeInterval, enemy.getLinearVelocity(), enemy.getHitboxfilter(), true, true, enemy, Sprite.IMPACT);
						hbox.makeUnreflectable();
						hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
						hbox.addStrategy(new DamageStatic(state, hbox, enemy.getBodyData(), attack1Damage, defaultMeleeKB, DamageTypes.MELEE));
						hbox.addStrategy(new FixedToUser(state, hbox, enemy.getBodyData(), new Vector2(), new Vector2(meleeRange * getMoveDirection(), 0), true));
					}
				});
				EnemyUtils.changeCrawlingState(this, CrawlingState.STILL, 0.0f, meleeInterval);
				
			}
			EnemyUtils.changeCrawlingState(this, CrawlingState.CHASE_PLAYER, 1.0f, 0.0f);
		}
	};
}
