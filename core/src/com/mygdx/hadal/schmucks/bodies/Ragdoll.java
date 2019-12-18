package com.mygdx.hadal.schmucks.bodies;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.ClientIllusion.alignType;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.ClientState.ObjectSyncLayers;
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
	private Sprite sprite;
	private TextureRegion ragdollSprite;
	
	private final static int spread = 60;
	
	private float ragdollDuration;
	
	private float veloAmp = 5.0f;
	private float baseAngle = 5.0f;
	
	private Vector2 startVelo;
	private float startAngle;
	
	private boolean sensor;
	
	public Ragdoll(PlayState state, float w, float h, int x, int y, Sprite sprite, Vector2 startVelo, float duration, boolean sensor) {
		super(state, w, h, x, y);
		this.startVelo = startVelo;
		this.startAngle = baseAngle;
		this.ragdollDuration = duration;
		this.sprite = sprite;
		this.sensor = sensor;
		if (sprite != null) {
			ragdollSprite = sprite.getFrame();
		}
	}

	private Vector2 newVelocity = new Vector2();
	@Override
	public void create() {
		this.hadalData = new HadalData(UserDataTypes.BODY, this);
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0.5f, false, false, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR), (short) 0, sensor, hadalData);
		
		setAngularVelocity(startAngle * veloAmp);
		
		float newDegrees = (float) (startVelo.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
		newVelocity.set(startVelo);
		
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
		
		if (ragdollSprite != null) {
			batch.draw(ragdollSprite, 
					getPosition().x * PPM - width / 2, 
					getPosition().y * PPM - height / 2, 
					width / 2, height / 2,
					width, height, 1, 1, 
					(float) Math.toDegrees(getOrientation()));
		}
	}
	
	/**
	 * As Default: Upon created, the frag tells the client to create a client illusion tracking it
	 */
	@Override
	public Object onServerCreate() {
		return new Packets.CreateEntity(entityID.toString(), new Vector2(width, height), getPosition().scl(PPM), sprite, ObjectSyncLayers.STANDARD, alignType.CENTER);
	}
}
