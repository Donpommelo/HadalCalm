package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * This strategy makes a hbox fixed to a unit's projectile origin.
 * This is used for enemies, to create particles/sound before they are about to attack based on their attack angle
 * @author Slimph Skobird
 */
public class FixedToOrigin extends HitboxStrategy {
	
	//does this hbox rotate when the user does?
	private final boolean rotate;
	
	//the enemy that this hbox tracks
	private final Enemy enemy;
	
	public FixedToOrigin(PlayState state, Hitbox proj, Enemy enemy, boolean rotate) {
		super(state, proj, enemy.getBodyData());
		this.rotate = rotate;
		this.enemy = enemy;
	}
	
	private final Vector2 attackAngle = new Vector2(0, 1);
	private final Vector2 hbLocation = new Vector2();
	@Override
	public void create() {
		if (enemy.isAlive()) {
			hbLocation.set(enemy.getProjectileOrigin(attackAngle.setAngleDeg(enemy.getAttackAngle()), hbox.getSize().x)).scl(1 / PPM);
			if (rotate) {
				hbox.setTransform(hbLocation, hbox.getStartVelo().angleRad());
			} else {
				hbox.setTransform(hbLocation, 0);
			}
		}
	}
	
	@Override
	public void controller(float delta) {
		if (!enemy.isAlive()) {
			hbox.die();
		} else {
			hbLocation.set(enemy.getProjectileOrigin(attackAngle.setAngleDeg(enemy.getAttackAngle()), hbox.getSize().x)).scl(1 / PPM);
			if (rotate) {
				hbox.setTransform(hbLocation, hbox.getStartVelo().angleRad());
			} else {
				hbox.setTransform(hbLocation, 0);
			}
		}
	}
}
