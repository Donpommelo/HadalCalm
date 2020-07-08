package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * A Client Illusion is an eneity created by the client as a default for a synced entity.
 * This entity does nothing itself but display a sprite and sync position/angle data from the server.
 * @author Zachary Tu
 */
public class ClientIllusion extends HadalEntity {
	
	//This is the sprite that will be displayed
	private Animation<TextureRegion> illusionSprite;
	
	//This is the way the sprite should be drawn
	private alignType align;
	
	//dimensions and angle of the illusion
	private int spriteWidth;
	private int spriteHeight;
	private float scale = 0.25f;
	private float startAngle;
	
	public ClientIllusion(PlayState state, Vector2 startPos, Vector2 size, float startAngle, Sprite sprite, alignType align) {
		super(state, startPos, size);
		this.startAngle = startAngle;
		if (!sprite.equals(Sprite.NOTHING)) {
			illusionSprite = new Animation<TextureRegion>(PlayState.spriteAnimationSpeed, sprite.getFrames());
			this.align = align;
			spriteWidth = illusionSprite.getKeyFrame(0).getRegionWidth();
			spriteHeight = illusionSprite.getKeyFrame(0).getRegionHeight();
		}
	}

	@Override
	public void create() {
		this.hadalData = new HadalData(UserDataTypes.EVENT, this);
		this.body = BodyBuilder.createBox(world, startPos, size, 0, 1, 0, false, true, Constants.BIT_SENSOR, (short) (0), (short) 0, true, hadalData);
		body.setTransform(body.getPosition(), startAngle);
	}

	@Override
	public void controller(float delta) {}

	@Override
	public void render(SpriteBatch batch) {
		if (illusionSprite != null) {
			
			switch (align) {
			case HITBOX:
				batch.draw((TextureRegion) illusionSprite.getKeyFrame(animationTime, true), 
						getPixelPosition().x - size.x / 2, 
						getPixelPosition().y - size.y / 2, 
						size.x / 2, size.y / 2,
						size.x, size.y, -1, 1, 
						(float) Math.toDegrees(getAngle()));
				break;
			case CENTER:
				batch.draw((TextureRegion) illusionSprite.getKeyFrame(animationTime, true), 
						getPixelPosition().x - size.x / 2, 
						getPixelPosition().y - size.y / 2, 
						spriteWidth * scale / 2, spriteHeight * scale / 2,
						spriteWidth * scale, spriteHeight * scale, 1, 1, 0);
				break;
			case CENTER_STRETCH:
				batch.draw((TextureRegion) illusionSprite.getKeyFrame(animationTime, true), 
						getPixelPosition().x - size.x / 2, 
						getPixelPosition().y - size.y / 2, 
						size.x / 2, size.y / 2,
						size.x, size.y, 1, 1, 0);
				break;
			case CENTER_BOTTOM:
				batch.draw((TextureRegion) illusionSprite.getKeyFrame(animationTime, true),
						getPixelPosition().x - spriteWidth * scale / 2,
	                    getPixelPosition().y - size.y / 2,
	                    spriteWidth * scale / 2, spriteHeight * scale / 2,
	                    spriteWidth * scale, spriteHeight * scale, 1, 1, 0);
				break;
			case ROTATE:
				batch.draw((TextureRegion) illusionSprite.getKeyFrame(animationTime, true),
						getPixelPosition().x - size.x / 2, 
						getPixelPosition().y - size.y / 2, 
						size.x / 2, size.y / 2,
						size.x, size.y, 1, 1, 
						(float) Math.toDegrees(getAngle()));
			default:
				break;
			}
		}
	}

	/**
	 * HITBOX: centered and flipped
	 * CENTER: drawn in middle of body
	 * CENTER_STRETCH: centered and stretched to fit
	 * CENTER_BOTTOM: centered at bottom of body (used for spawners and stuff like that)
	 * ROTATE: this is a center-stretch that can rotate
	 * @author Zachary Tu
	 *
	 */
	public enum alignType {
		HITBOX,
		CENTER,
		CENTER_STRETCH,
		CENTER_BOTTOM,
		ROTATE,
		NONE
	}
}
