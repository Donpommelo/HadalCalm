package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.ContactUnitDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DieExplode;

public class Torpedofish extends EnemySteering {

	private final static int baseHp = 100;

	private static final int width = 63;
	private static final int height = 40;
	
	private static final int hboxWidth = 40;
	private static final int hboxHeight = 63;
	
	private static final float maxLinearSpeed = 600;
	private static final float maxLinearAcceleration = 800;
	
	private static final float attackCd = 2.0f;
			
	private static final Sprite sprite = Sprite.FISH_TORPEDO;
	private final static Sprite projSprite = Sprite.ORB_RED;
	
	public Torpedofish(PlayState state, Vector2 startPos, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), sprite, enemyType.TORPEDOFISH, maxLinearSpeed, maxLinearAcceleration, filter, baseHp, attackCd, spawner);
	}
	
	private final static float baseDamage = 5.0f;
	private final static float knockback = 0.5f;
	private final static float projectileSpeed = 16.0f;
	private final static Vector2 projectileSize = new Vector2(20, 20);
	private final static float lifespan = 5.0f;
	
	private final static int explosionRadius = 100;
	private final static float explosionDamage = 10.0f;
	private final static float explosionKnockback = 25.0f;
	@Override
	public void attackInitiate() {
		EnemyUtils.changeTrackingState(this, BossState.TRACKING_PLAYER, 0, 0.4f);
		getActions().add(new EnemyAction(this, 0.0f) {
			
			@Override
			public void execute() {
				
				if (target == null) {
					return;
				}
				
				Vector2 dist = new Vector2(target.getPixelPosition().sub(enemy.getPixelPosition())).nor().scl(projectileSpeed);
				Hitbox hbox = new RangedHitbox(state, enemy.getPixelPosition(), projectileSize, lifespan, dist, enemy.getHitboxfilter(), true, true, enemy, projSprite);
				
				hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new ContactUnitDie(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new ContactWallDie(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new DamageStandard(state, hbox, enemy.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));	
				hbox.addStrategy(new DieExplode(state, hbox, enemy.getBodyData(), explosionRadius, explosionDamage, explosionKnockback, (short)0));
			}
		});
	};
}
