package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.utils.Stats;

public class Spittlefish extends EnemySwimming {

	private final static int baseHp = 100;
	
	private final static int scrapDrop = 2;
	
	private static final int width = 49;
	private static final int height = 19;
	
	private static final int hboxWidth = 49;
	private static final int hboxHeight = 19;
	
	private static final float attackCd = 0.5f;
	private static final float airSpeed = -0.5f;
	private static final float kbRes = 0.5f;
	
	private static final float minRange = 6.0f;
	private static final float maxRange = 12.0f;
	
	private static final Sprite sprite = Sprite.FISH_SPITTLE;

	private final static Sprite projSprite = Sprite.SPIT;
	
	public Spittlefish(PlayState state, Vector2 startPos, float startAngle, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), sprite, EnemyType.SPITTLEFISH, startAngle, filter, baseHp, attackCd, scrapDrop, spawner);
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
	}
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.AIR_SPD, airSpeed, getBodyData()));
		getBodyData().addStatus(new StatChangeStatus(state, Stats.KNOCKBACK_RES, kbRes, getBodyData()));
	}
	
	private final static float baseDamage = 5.0f;
	private final static float knockback = 4.5f;
	private final static float projectileSpeed = 12.0f;
	private final static Vector2 projectileSize = new Vector2(30, 20);
	private final static float lifespan = 5.0f;
	@Override
	public void attackInitiate() {
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.4f);
		getActions().add(new EnemyAction(this, 0.0f) {
			
			@Override
			public void execute() {
				
				if (target == null) {
					return;
				}
				Vector2 startVelo = new Vector2(target.getPixelPosition().sub(enemy.getPixelPosition())).nor().scl(projectileSpeed);
				Hitbox hbox = new RangedHitbox(state, enemy.getProjectileOrigin(startVelo, size.x), projectileSize, lifespan, startVelo, enemy.getHitboxfilter(), true, true, enemy, projSprite);
				
				hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new ContactWallDie(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new AdjustAngle(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new DamageStandard(state, hbox, enemy.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
			}
		});
	};
}
