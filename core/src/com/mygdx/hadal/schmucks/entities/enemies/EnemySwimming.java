package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.enemy.MovementSwim;

/**
 * A swimming enemy is an floating enemy that uses a certain physics system to swim towards or away from a target (usually the player)
 * this physics system works very similarly to the player's own movement
 * @author Grulgolois Ghomato
 */
public class EnemySwimming extends EnemyFloating {

	private final MovementSwim swimStrategy;

	public EnemySwimming(PlayState state, Vector2 startPos, Vector2 size, Vector2 hboxSize, Sprite sprite, EnemyType type, float startAngle, short filter, int hp, float attackCd, int scrapDrop) {
		super(state, startPos, size, hboxSize, sprite, type, filter, hp, attackCd, scrapDrop);
		this.swimStrategy = new MovementSwim(state, this, startAngle);
		addStrategy(swimStrategy);
	}

	public MovementSwim getSwimStrategy() {	return swimStrategy; }
}
