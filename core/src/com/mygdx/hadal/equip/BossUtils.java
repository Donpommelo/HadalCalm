package com.mygdx.hadal.equip;

import com.mygdx.hadal.schmucks.bodies.enemies.Scissorfish;
import com.mygdx.hadal.schmucks.bodies.enemies.Spittlefish;
import com.mygdx.hadal.schmucks.bodies.enemies.Torpedofish;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.enemies.Boss1Test;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy.enemyType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.MeleeHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.utils.Constants;

public class BossUtils {

	public static void spawnAdds(PlayState state, int spawnX, int spawnY, enemyType type, int amount, int spread) {
		int randX = spawnX + ((int)( (Math.random() - 0.5) * spread));
		int randY = spawnY + ((int)( (Math.random() - 0.5) * spread));
		
		switch (type) {
		case SCISSORFISH:
			new Scissorfish(state, randX, randY, Constants.ENEMY_HITBOX);
			break;
		case SPITTLEFISH:
			new Spittlefish(state, randX, randY, Constants.ENEMY_HITBOX);
			break;
		case TORPEDOFISH:
			new Torpedofish(state, randX, randY, Constants.ENEMY_HITBOX);
			break;
		default:
			break;
		}
	}
	
	public static void moveToDummy(PlayState state, Boss1Test boss, String dummyId) {
		Event dummy = state.getDummyPoint(dummyId);
		
		if (dummy != null) {
			boss.setMovementTarget(dummy);
		}
	}
	
	public static void charge(PlayState state, Boss1Test boss, HadalEntity target, int moveSpeed, float damage, float knockback) {
		Vector2 dist = target.getPosition().sub(boss.getPosition()).scl(PPM);
		boss.setLinearVelocity(dist.nor().scl(moveSpeed));
		meleeAttack(state, boss, damage, knockback, dist);
	}
	
	public static void moveToPlayer(PlayState state, Boss1Test boss, HadalEntity target, int moveSpeed) {
		Vector2 dist = target.getPosition().sub(boss.getPosition()).scl(PPM);
		boss.setLinearVelocity(dist.nor().scl(moveSpeed));
	}
	
	public static void meleeAttack(PlayState state, Boss1Test boss, float damage, float knockback, Vector2 angle) {
		Hitbox hbox = new MeleeHitbox(state, boss.getPosition().x * PPM, boss.getPosition().y * PPM, (int)boss.getHeight(), (int)boss.getWidth(), 1.0f, 1.0f, angle, 
				new Vector2(0, 0), true, boss.getHitboxfilter(), boss);
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, boss.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, boss.getBodyData(), null, damage, knockback, DamageTypes.MELEE));	
	}
	
	
}
