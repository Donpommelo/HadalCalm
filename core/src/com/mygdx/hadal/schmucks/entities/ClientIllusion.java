package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.SpriteManager;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * A Client Illusion is an entity created by the client as a default for a synced entity.
 * This entity does nothing itself but display a sprite and sync position/angle data from the server.
 * @author Proggivika Phagwump
 */
public class ClientIllusion extends HadalEntity {

	private static final float SCALE = 0.25f;

	//This is the sprite that will be displayed
	private Animation<TextureRegion> illusionSprite;
	
	//This is the way the sprite should be drawn
	private alignType align;
	
	//dimensions and angle of the illusion
	private int spriteWidth;
	private int spriteHeight;
	private final float startAngle;
	
	public ClientIllusion(PlayState state, Vector2 startPos, Vector2 size, float startAngle, Sprite sprite, alignType align) {
		super(state, startPos, size);
		this.startAngle = startAngle;
		if (!Sprite.NOTHING.equals(sprite)) {
			illusionSprite = SpriteManager.getAnimation(sprite);
			this.align = align;
			spriteWidth = illusionSprite.getKeyFrame(0).getRegionWidth();
			spriteHeight = illusionSprite.getKeyFrame(0).getRegionHeight();
		}
	}

	@Override
	public void create() {
		this.hadalData = new HadalData(UserDataType.EVENT, this);
		this.body = new HadalBody(hadalData, startPos, size, BodyConstants.BIT_SENSOR, (short) 0, (short) 0)
				.addToWorld(world);

		body.setTransform(getPosition(), startAngle);
	}

	@Override
	public void controller(float delta) {}

	@Override
	public void render(SpriteBatch batch, Vector2 entityLocation) {
		if (illusionSprite != null) {
			switch (align) {
			case HITBOX:
				batch.draw(illusionSprite.getKeyFrame(animationTime, false),
						entityLocation.x - size.x / 2, 
						entityLocation.y - size.y / 2, 
						size.x / 2, size.y / 2,
						size.x, size.y, -1, 1, 
						MathUtils.radDeg * getAngle());
				break;
			case CENTER:
				batch.draw(illusionSprite.getKeyFrame(animationTime, false),
						entityLocation.x - size.x / 2, 
						entityLocation.y - size.y / 2, 
						spriteWidth * SCALE / 2, spriteHeight * SCALE / 2,
						spriteWidth * SCALE, spriteHeight * SCALE, 1, 1, 0);
				break;
			case CENTER_STRETCH:
				batch.draw(illusionSprite.getKeyFrame(animationTime, false),
						entityLocation.x - size.x / 2, 
						entityLocation.y - size.y / 2, 
						size.x / 2, size.y / 2,
						size.x, size.y, 1, 1, 0);
				break;
			case CENTER_BOTTOM:
				batch.draw(illusionSprite.getKeyFrame(animationTime, false),
						entityLocation.x - spriteWidth * SCALE / 2,
						entityLocation.y - size.y / 2,
	                    spriteWidth * SCALE / 2, spriteHeight * SCALE / 2,
	                    spriteWidth * SCALE, spriteHeight * SCALE, 1, 1, 0);
				break;
			case ROTATE:
				batch.draw(illusionSprite.getKeyFrame(animationTime, false),
						entityLocation.x - size.x / 2, 
						entityLocation.y - size.y / 2, 
						size.x / 2, size.y / 2,
						size.x, size.y, 1, 1, 
						MathUtils.radDeg * getAngle());
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
