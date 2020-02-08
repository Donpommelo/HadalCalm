package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.BossUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class Spittlefish extends EnemySteering {

	private final static int baseHp = 100;

	private static final int width = 49;
	private static final int height = 19;
	
	private static final int hboxWidth = 19;
	private static final int hboxHeight = 49;
	
	private static final float maxLinearSpeed = 600;
	private static final float maxLinearAcceleration = 200;
	
	private static final float attackCd = 0.5f;
	
	private static final Sprite sprite = Sprite.FISH_SPITTLE;

	private final static Sprite projSprite = Sprite.SPIT;
	
	public Spittlefish(PlayState state, Vector2 startPos, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), sprite, enemyType.SPITTLEFISH, maxLinearSpeed, maxLinearAcceleration, filter, baseHp, attackCd, spawner);
	}
	
	private final static float baseDamage = 5.0f;
	private final static float knockback = 4.5f;
	private final static float projectileSpeed = 12.0f;
	private final static Vector2 projectileSize = new Vector2(30, 20);
	private final static float lifespan = 5.0f;
	@Override
	public void attackInitiate() {
		BossUtils.changeTrackingState(this, BossState.TRACKING_PLAYER, 0, 0.4f);
		getActions().add(new BossAction(this, 0.0f) {
			
			@Override
			public void execute() {
				
				if (target == null) {
					return;
				}
				
				Vector2 dist = new Vector2(target.getPixelPosition().sub(boss.getPixelPosition())).nor().scl(projectileSpeed);
				Hitbox hbox = new RangedHitbox(state, boss.getPixelPosition(), projectileSize, lifespan, dist, boss.getHitboxfilter(), true, true, boss, projSprite);
				
				hbox.addStrategy(new ControllerDefault(state, hbox, boss.getBodyData()));
				hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, boss.getBodyData()));
				hbox.addStrategy(new ContactWallDie(state, hbox, boss.getBodyData()));
				hbox.addStrategy(new AdjustAngle(state, hbox, boss.getBodyData()));
				hbox.addStrategy(new DamageStandard(state, hbox, boss.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
			}
		});
	};
}
