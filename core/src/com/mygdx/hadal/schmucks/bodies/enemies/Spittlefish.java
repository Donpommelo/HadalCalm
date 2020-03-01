package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
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
	
	private final static int scrapDrop = 2;
	
	private static final int width = 19;
	private static final int height = 49;
	
	private static final int hboxWidth = 19;
	private static final int hboxHeight = 49;
	
	private static final float maxLinearSpeed = 600;
	private static final float maxLinearAcceleration = 200;
	
	private static final float attackCd = 0.5f;
	
	private static final Sprite sprite = Sprite.FISH_SPITTLE;

	private final static Sprite projSprite = Sprite.SPIT;
	
	public Spittlefish(PlayState state, Vector2 startPos, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), sprite, EnemyType.SPITTLEFISH, maxLinearSpeed, maxLinearAcceleration, filter, baseHp, attackCd, scrapDrop, spawner);
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
				
				Vector2 dist = new Vector2(target.getPixelPosition().sub(enemy.getPixelPosition())).nor().scl(projectileSpeed);
				Hitbox hbox = new RangedHitbox(state, enemy.getPixelPosition(), projectileSize, lifespan, dist, enemy.getHitboxfilter(), true, true, enemy, projSprite);
				
				hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new ContactWallDie(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new AdjustAngle(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new DamageStandard(state, hbox, enemy.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
			}
		});
	};
}
