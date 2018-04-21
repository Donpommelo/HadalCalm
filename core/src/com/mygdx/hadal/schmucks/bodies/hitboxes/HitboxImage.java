package com.mygdx.hadal.schmucks.bodies.hitboxes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * A HitboxImage is a hitbox that is represented by a still image sprite.
 * @author Zachary Tu
 *
 */
public class HitboxImage extends RangedHitbox {
	
	protected TextureRegion projectileSprite;
	
	/**
	 * Same as normal hitbox 
	 */
	public HitboxImage(PlayState state, float x, float y, int width, int height, float grav, float lifespan, int dura, float rest,
			Vector2 startVelo, short filter, boolean sensor, Schmuck creator, String spriteId) {
		super(state, x, y, width / 2, height / 2, grav, lifespan, dura, rest, startVelo, filter, sensor, creator);
		projectileSprite = GameStateManager.projectileAtlas.findRegion(spriteId);
	}
	
	@Override
	public void render(SpriteBatch batch) {
			
		batch.setProjectionMatrix(state.sprite.combined);

		batch.draw(getProjectileSprite(), 
				body.getPosition().x * PPM - width / 2, 
				body.getPosition().y * PPM - height / 2, 
				width / 2, height / 2,
				width, height, 1, 1, 
				(float) Math.toDegrees(body.getAngle()) + 180);
	}

	public TextureRegion getProjectileSprite() {
		return projectileSprite;
	}

	public void setProjectileSprite(TextureRegion projectileSprite) {
		this.projectileSprite = projectileSprite;
	}	

}
