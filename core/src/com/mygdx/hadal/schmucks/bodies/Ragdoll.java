package com.mygdx.hadal.schmucks.bodies;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.concurrent.ThreadLocalRandom;

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
 *
 */
public class Ragdoll extends HadalEntity {
	
	//This is the sprite that will be displayed
	private TextureRegion illusionSprite;
	
	private final static int spread = 60;
	
	private float ragdollDuration;
	
	private float veloAmp = 5.0f;
	private float baseAngle = 5.0f;
	
	private Vector2 startVelo;
	private float startAngle;
	
	public Ragdoll(PlayState state, float w, float h, int x, int y, Sprite sprite, Vector2 startVelo, float duration) {
		super(state, w, h, x, y);
		this.startVelo = startVelo;
		this.startAngle = baseAngle;
		this.ragdollDuration = duration;
		if (sprite != null) {
			illusionSprite = sprite.getFrame();
		}
	}

	@Override
	public void create() {
		this.hadalData = new HadalData(UserDataTypes.BODY, this);
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0.5f, false, false, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_WALL), (short) 0, false, hadalData);
		
		setAngularVelocity(startAngle * veloAmp);
		
		float newDegrees = (float) (startVelo.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
		Vector2 newVelocity = new Vector2(startVelo);
		
		setLinearVelocity(newVelocity.nor().scl(veloAmp).setAngle(newDegrees));
	}

	@Override
	public void controller(float delta) {
		ragdollDuration -= delta;
		
		if (ragdollDuration <= 0) {
			queueDeletion();
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		
		if (illusionSprite != null) {
			batch.draw(illusionSprite, 
					getPosition().x * PPM - width / 2, 
					getPosition().y * PPM - height / 2, 
					width / 2, height / 2,
					width, height, 1, 1, 
					(float) Math.toDegrees(getOrientation()) + 180);
		}
	}
}
