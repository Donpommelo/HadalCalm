package com.mygdx.hadal.schmucks.bodies.hitboxes;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.ClientState.ObjectSyncLayers;

/**
 * A HitboxImage is a hitbox that is represented by an animation.
 * @author Zachary Tu
 *
 */
public class HitboxSprite extends RangedHitbox {
	
	//This is the animation of this sprite
	protected Animation<TextureRegion> projectileSprite;
	private Sprite sprite;
	
	//Speed of the animation. Make this an input?
	private float speed = 0.05f;
	
	/**
	 * Same as normal hitbox man
	 */
	public HitboxSprite(PlayState state, float x, float y, int width, int height, float grav, float lifespan, int dura, float rest,
			Vector2 startVelo, short filter, boolean sensor, boolean procEffects, Schmuck creator, Sprite sprite) {
		super(state, x, y, width / 2, height / 2, grav, lifespan, dura, rest, startVelo, filter, sensor, procEffects, creator);
		
		this.sprite = sprite;
		projectileSprite = new Animation<TextureRegion>(speed, sprite.getFrames());
	}
	
	@Override
	public void controller(float delta) {
		super.controller(delta);
		increaseAnimationTime(delta);
	}
	
	@Override
	public void render(SpriteBatch batch) {
		
		batch.setProjectionMatrix(state.sprite.combined);

		batch.draw((TextureRegion) projectileSprite.getKeyFrame(animationTime, true), 
				body.getPosition().x * PPM - width / 2, 
				body.getPosition().y * PPM - height / 2, 
				width / 2, height / 2,
				width, height, 1, 1, 
				(float) Math.toDegrees(body.getAngle()) + 180);
	}
	
	@Override
	public Object onServerCreate() {
		return new Packets.CreateEntity(entityID.toString(), new Vector2(width, height), body.getPosition().scl(PPM), sprite, ObjectSyncLayers.HBOX);
	}
}