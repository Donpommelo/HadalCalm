package com.mygdx.hadal.schmucks.bodies;

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
 * A Ragdoll is a miscellaneous entity that doesn't do a whole heck of a lot.
 * Its main job is to be visible and obey physics. This is useful for on-death ragdoll/frags
 * It also has a couple of other applications like current bubble particle generators
 * @author Zachary Tu
 *
 */
public class Ragdoll extends HadalEntity {
	
	//This is the sprite that will be displayed
	private Sprite sprite;
	private TextureRegion ragdollSprite;
	
	//spread is for giving the initial ragdool a random velocity
	private final static int spread = 60;
	
	//how long does the ragdoll last
	private float ragdollDuration;
	
	//starting multiplier on starting velocity and direction
	private float veloAmp = 5.0f;
	private float baseAngle = 5.0f;
	
	private Vector2 startVelo;
	private float startAngle;
	
	//is the ragdoll a sensor? (i.e does it have collision)
	private boolean sensor;
	
	public Ragdoll(PlayState state, Vector2 startPos, Vector2 size, Sprite sprite, Vector2 startVelo, float duration, boolean sensor) {
		super(state, startPos, size);
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
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0.5f, false, false, Constants.BIT_SENSOR, 
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
					getPixelPosition().x - size.x / 2, 
					getPixelPosition().y - size.y / 2, 
					size.x / 2, size.y / 2,
					size.x, size.y, 1, 1, 
					(float) Math.toDegrees(getOrientation()));
		}
	}
	
	/**
	 * As Default: Upon created, the frag tells the client to create a client illusion tracking it
	 */
	@Override
	public Object onServerCreate() {
		return new Packets.CreateEntity(entityID.toString(), new Vector2(size), getPixelPosition(), sprite, ObjectSyncLayers.STANDARD, alignType.CENTER);
	}
}
