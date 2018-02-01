package com.mygdx.hadal.schmucks.bodies.hitboxes;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

/**
 * A HitboxImage is a hitbox that is represented by an animation.
 * @author Zachary Tu
 *
 */
public class HitboxAnimated extends RangedHitbox {
	
	private TextureAtlas atlas;
	private Animation<TextureRegion> projectileSprite;
	private float animCdCount;
	
	private float speed = 0.05f;
	
	/**
	 * Same as normal hitbox man
	 */
	public HitboxAnimated(PlayState state, float x, float y, int width, int height, float grav, float lifespan, int dura, float rest,
			Vector2 startVelo, short filter, boolean sensor, World world, OrthographicCamera camera, RayHandler rays, Schmuck creator,
			String spriteId) {
		super(state, x, y, width / 2, height / 2, grav, lifespan, dura, rest, startVelo, filter, sensor, world, camera, rays, creator);
		atlas = (TextureAtlas) HadalGame.assetManager.get(AssetList.PROJ_1_ATL.toString());
		projectileSprite = new Animation<TextureRegion>(speed, atlas.findRegions(spriteId));
		animCdCount = 0;
	}
	
	public void controller(float delta) {
		super.controller(delta);
		animCdCount += delta;
	}
	
	
	@Override
	public void render(SpriteBatch batch) {
		
		batch.setProjectionMatrix(state.sprite.combined);

		batch.draw((TextureRegion) projectileSprite.getKeyFrame(animCdCount, true), 
				body.getPosition().x * PPM - width / 2, 
				body.getPosition().y * PPM - height / 2, 
				width / 2, height / 2,
				width, height, 1, 1, 
				(float) Math.toDegrees(body.getAngle()) + 180);
	}	

}