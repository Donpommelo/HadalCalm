package com.mygdx.hadal.strategies.hitbox;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy creates a number of fiery projectiles when its hbox dies
 * @author Zachary Tu
 *
 */
public class DieFrag extends HitboxStrategy {
	
	private int numFrag;

	private final static Vector2 projectileSize = new Vector2(25, 25);
	private final static float lifespan = 0.25f;
	private final static float fragSpeed = 10.0f;
	
	private final static float baseDamage = 2.5f;
	private final static float knockback = 5.0f;
	
	private final static Sprite[] projSprites = {Sprite.SCRAP_A, Sprite.SCRAP_B, Sprite.SCRAP_C, Sprite.SCRAP_D};
	
	public DieFrag(PlayState state, Hitbox proj, BodyData user, int numFrag) {
		super(state, proj, user);
		this.numFrag = numFrag;
	}
	
	@Override
	public void die() {
		for (int i = 0; i < numFrag; i++) {
			float newDegrees = (ThreadLocalRandom.current().nextInt(0, 360));
			Vector2 fragVelo = new Vector2(0, fragSpeed).setAngle(newDegrees);

			int randomIndex = GameStateManager.generator.nextInt(projSprites.length);
			Sprite projSprite = projSprites[randomIndex];
			
			Hitbox frag = new Hitbox(state, hbox.getPixelPosition(), projectileSize, lifespan, fragVelo, hbox.getFilter(), true, false, creator.getSchmuck(), projSprite);
			
			frag.addStrategy(new ControllerDefault(state, frag, creator));
			frag.addStrategy(new DamageStandard(state, frag, creator, baseDamage, knockback, DamageTypes.RANGED));
		}
	}
}
