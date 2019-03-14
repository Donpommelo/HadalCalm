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

public class ClientIllusion extends HadalEntity {
	
	private Animation<TextureRegion> projectileSprite;
	
	//Speed of the animation. Make this an input?
	private float speed = 0.05f;
		
	public ClientIllusion(PlayState state, float w, float h, Sprite sprite) {
		super(state, w, h, 0, 0);
		if (sprite != null) {
			projectileSprite = new Animation<TextureRegion>(speed, sprite.getFrames());
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
		
		batch.setProjectionMatrix(state.sprite.combined);

		if (projectileSprite != null) {
			batch.draw((TextureRegion) projectileSprite.getKeyFrame(animationTime, true), 
					body.getPosition().x * PPM - width / 2, 
					body.getPosition().y * PPM - height / 2, 
					width / 2, height / 2,
					width, height, 1, 1, 
					(float) Math.toDegrees(body.getAngle()) + 180);
		}
	}
}
