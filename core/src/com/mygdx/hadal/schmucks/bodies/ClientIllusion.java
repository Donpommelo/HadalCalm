package com.mygdx.hadal.schmucks.bodies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
 *
 */
public class ClientIllusion extends HadalEntity {
	
	//This is the sprite that will be displayed
	private Animation<TextureRegion> illusionSprite;
	
	//This is the way the sprite should be drawn
	private alignType align;
	
	private int spriteWidth;
	private int spriteHeight;
	private float scale = 0.25f;
	
	public ClientIllusion(PlayState state, float w, float h, int x, int y, Sprite sprite, alignType align) {
		super(state, w, h, x, y);
		if (sprite != null) {
			illusionSprite = new Animation<TextureRegion>(PlayState.spriteAnimationSpeed, sprite.getFrames());
			this.align = align;
			spriteWidth = illusionSprite.getKeyFrame(0).getRegionWidth();
			spriteHeight = illusionSprite.getKeyFrame(0).getRegionHeight();
		}
	}

	@Override
	public void create() {
		this.hadalData = new HadalData(UserDataTypes.EVENT, this);
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (0), (short) 0, true, hadalData);
	}

	@Override
	public void controller(float delta) {}

	@Override
	public void render(SpriteBatch batch) {
		
		if (illusionSprite != null) {
			
			switch (align) {
			case HITBBOX:
				batch.draw((TextureRegion) illusionSprite.getKeyFrame(animationTime, true), 
						getPosition().x * PPM - width / 2, 
						getPosition().y * PPM - height / 2, 
						width / 2, height / 2,
						width, height, 1, 1, 
						(float) Math.toDegrees(getOrientation()) + 180);
				break;
			case CENTER:
				batch.draw((TextureRegion) illusionSprite.getKeyFrame(animationTime), 
						getPosition().x * PPM - width / 2, 
						getPosition().y * PPM - height / 2, 
						width / 2, height / 2,
						width, height, 1, 1, 
						(float) Math.toDegrees(getOrientation()) + 180);
				break;
			case CENTER_STRETCH:
				batch.draw((TextureRegion) illusionSprite.getKeyFrame(animationTime), 
						getPosition().x * PPM - width / 2, 
						getPosition().y * PPM - height / 2, 
						width / 2, height / 2,
						width, height, 1, 1, 
						(float) Math.toDegrees(getOrientation()) + 180);
				break;
			case CENTER_BOTTOM:
				batch.draw((TextureRegion) illusionSprite.getKeyFrame(animationTime),
	                    getPosition().x * PPM - spriteWidth * scale / 2,
	                    getPosition().y * PPM - height / 2,
	                    spriteWidth * scale / 2, spriteHeight * scale / 2,
	                    spriteWidth * scale, spriteHeight * scale, 1, 1, 0);
				break;
			default:
				break;
			}
		}
	}
	
	public enum alignType {
		HITBBOX,
		CENTER,
		CENTER_STRETCH,
		CENTER_BOTTOM,
		NONE
	}
}
