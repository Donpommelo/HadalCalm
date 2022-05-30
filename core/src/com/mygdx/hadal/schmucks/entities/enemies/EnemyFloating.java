package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.enemy.MovementFloat;

/**
 * Floating enemies are the basic fish-enemies of the game.
 * These enemies can rotate to face the player.
 * @author Mebriana Meezy
 */
public class EnemyFloating extends Enemy {

	private final MovementFloat floatStrategy;

	public EnemyFloating(PlayState state, Vector2 startPos, Vector2 size, Vector2 hboxSize, Sprite sprite, EnemyType type, short filter, int hp, float attackCd, int scrapDrop) {
		super(state, startPos, size, hboxSize, type, filter, hp, attackCd, scrapDrop);
		floatStrategy = new MovementFloat(state, this, sprite);
		addStrategy(floatStrategy);
	}

	public MovementFloat getFloatStrategy() { return floatStrategy; }
}
