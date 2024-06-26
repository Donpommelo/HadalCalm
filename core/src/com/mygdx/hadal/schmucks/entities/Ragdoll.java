package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * A Ragdoll is a miscellaneous entity that doesn't do a whole heck of a lot.
 * Its main job is to be visible and obey physics. This is useful for on-death ragdoll/frags
 * It also has a couple of other applications like current bubble particle generators
 * @author Pugma Plilzburger
 */
public class Ragdoll extends HadalEntity {

	//spread is for giving the initial ragdoll a random velocity
	private static final int SPREAD = 120;

	//starting multiplier on starting velocity and direction
	private static final float VELO_AMP = 10.0f;
	private static final float ANGLE_AMP = 2.0f;
	private static final float BASE_ANGLE = 8.0f;

	//these control the ragdoll fading before despawning
	private static final float FADE_LIFESPAN = 1.0f;

	//This is the sprite that will be displayed
	private Sprite sprite;
	private TextureRegion ragdollSprite;
	
	//how long does the ragdoll last
	private float ragdollDuration;
	private final float gravity;
	
	private final Vector2 startVelo;
	private final float startAngle;
	
	//is the ragdoll a sensor? (i.e does it have collision)
	private final boolean sensor;
	
	//do we set the velocity of the ragdoll upon spawning or just change its angle? 
	private final boolean setVelo;
	
	//when this ragdoll is created on the server, does the client create a ragdoll of its own (this is false for stuff like currents)
	private final boolean synced;

	//does the ragdoll fade when its lifespan decreases? Only if needed, since fading sets the batch
	private float fadeDuration;
	private boolean fade, fadeStarted;
	private Shader fadeShader;

	private boolean spinning = true;

	public Ragdoll(PlayState state, Vector2 startPos, Vector2 size, Sprite sprite, Vector2 startVelo, float duration, float gravity,
				   boolean setVelo, boolean sensor, boolean synced) {
		super(state, startPos, size);
		this.startVelo = new Vector2(startVelo);
		this.startAngle = BASE_ANGLE * ANGLE_AMP;
		this.ragdollDuration = duration;
		this.gravity = gravity;
		this.sprite = sprite;
		this.sensor = sensor;
		this.setVelo = setVelo;
		this.synced = synced;
		if (!Sprite.NOTHING.equals(sprite)) {
			ragdollSprite = sprite.getFrame();
		}
		
		setSyncDefault(false);
	}

	/**
	 * This alternate constructor is used for ragdolls that do not use a designated sprite (i.e. from a frame buffer)
	 * Because there is no Sprite, these are not serializable and must be made on both client and server.
	 * Also, remember to manually dispose of the frame buffer object that is used for this ragdoll
	 */
	public Ragdoll(PlayState state, Vector2 startPos, Vector2 size, TextureRegion textureRegion, Vector2 startVelo, float duration,
				   float gravity, boolean setVelo, boolean sensor) {
		super(state, startPos, size);
		this.startVelo = startVelo;
		this.ragdollDuration = duration;
		this.gravity = gravity;
		this.sensor = sensor;
		this.setVelo = setVelo;
		ragdollSprite = textureRegion;

		this.synced = false;
		setSyncDefault(false);

		//ragdoll spin direction depends on which way it is moving
		if (startVelo.x >= 0) {
			this.startAngle = -BASE_ANGLE * ANGLE_AMP;
		} else {
			this.startAngle = BASE_ANGLE * ANGLE_AMP;
		}
	}

	private final Vector2 newVelocity = new Vector2();
	@Override
	public void create() {

		this.hadalData = new HadalData(UserDataType.BODY, this);
		this.body = new HadalBody(hadalData, startPos, size, BodyConstants.BIT_SENSOR, (short) (BodyConstants.BIT_WALL | BodyConstants.BIT_SENSOR), (short) -1)
				.setFixedRotate(false)
				.setGravity(gravity)
				.setSensor(sensor)
				.addToWorld(world);

		//this makes ragdolls spin and move upon creation
		if (spinning) {
			setAngularVelocity(startAngle);
		}
		float newDegrees = startVelo.angleDeg() + MathUtils.random(-SPREAD, SPREAD + 1);
		newVelocity.set(startVelo).add(1, 1);
		
		if (setVelo) {
			setLinearVelocity(newVelocity.nor().scl(VELO_AMP).setAngleDeg(newDegrees));
		} else {
			setLinearVelocity(newVelocity.setAngleDeg(newDegrees));
		}
	}

	@Override
	public void controller(float delta) {

		//if ragdoll should fade, set shader once fade delay has passed
		if (ragdollDuration <= fadeDuration && fade && !fadeStarted) {
			getShaderHelper().setShader(fadeShader, ragdollDuration);
			fadeStarted = true;
		}

		ragdollDuration -= delta;
		if (ragdollDuration <= 0) {
			queueDeletion();
		}
	}

	@Override
	public void clientController(float delta) {
		super.clientController(delta);

		if (ragdollDuration <= fadeDuration && fade && !fadeStarted) {
			getShaderHelper().setShader(fadeShader, ragdollDuration);
			fadeStarted = true;
		}

		ragdollDuration -= delta;
		if (ragdollDuration <= 0) {
			((ClientState) state).removeEntity(entityID);
		}
	}
	
	@Override
	public void render(SpriteBatch batch, Vector2 entityLocation) {
		if (ragdollSprite != null) {
			batch.draw(ragdollSprite,
					entityLocation.x - size.x / 2, 
					entityLocation.y - size.y / 2, 
					size.x / 2, size.y / 2,
					size.x, size.y, 1, 1,
				MathUtils.radDeg * getAngle());
		}
	}
	
	/**
	 * As Default: Upon created, the frag tells the client to create a client illusion tracking it
	 */
	@Override
	public Object onServerCreate(boolean catchup) {
		if (synced) {
			return new Packets.CreateRagdoll(entityID, getPixelPosition(), size, sprite, startVelo, ragdollDuration, gravity,
					setVelo, sensor, fade);
		} else {
			return null;
		}
	}

	@Override
	public Object onServerDelete() { return null; }

	public Ragdoll setFade() {
		return setFade(FADE_LIFESPAN, Shader.FADE);
	}

	public Ragdoll setFade(float fadeDuration, Shader fadeShader) {
		this.fade = true;
		this.fadeDuration = fadeDuration;
		this.fadeShader = fadeShader;
		return this;
	}

	public Ragdoll setSpinning(boolean spinning) {
		this.spinning = spinning;
		return this;
	}
}
