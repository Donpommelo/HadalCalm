package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.SpriteManager;
import com.mygdx.hadal.requests.RagdollCreate;
import com.mygdx.hadal.schmucks.userdata.HadalData;
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

	//This is the sprite that will be displayed
	private TextureRegion ragdollSprite;
	
	//how long does the ragdoll last
	private float ragdollDuration;
	private final float gravity;
	
	private final Vector2 startVelo;
	private final float startAngle, angularDampening, linearDampening;
	
	//is the ragdoll a sensor? (i.e does it have collision)
	private final boolean sensor;
	
	//do we set the velocity of the ragdoll upon spawning or just change its angle? 
	private final boolean setVelo;
	
	//does the ragdoll fade when its lifespan decreases? Only if needed, since fading sets the batch
	private float fadeDuration;
	private boolean fade, fadeStarted;
	private Shader fadeShader;

	private boolean spinning = true;

	public Ragdoll(PlayState state, RagdollCreate ragdollCreate) {
		super(state, ragdollCreate.getPosition(), ragdollCreate.getSize());
		this.startVelo = ragdollCreate.getVelocity();
		this.angularDampening = ragdollCreate.getAngularDampening();
		this.linearDampening = ragdollCreate.getLinearDampening();
		this.ragdollDuration = ragdollCreate.getLifespan();
		this.gravity = ragdollCreate.getGravity();
		this.sensor = ragdollCreate.isSensor();
		this.setVelo = ragdollCreate.isStartVelocity();
		this.spinning = ragdollCreate.isSpinning();

		if (ragdollCreate.getTextureRegion() != null) {
			ragdollSprite = ragdollCreate.getTextureRegion();
		} else if (!Sprite.NOTHING.equals(ragdollCreate.getSprite())) {
			ragdollSprite = SpriteManager.getFrame(ragdollCreate.getSprite());
		}

		if (ragdollCreate.isFade()) {
			this.fade = true;
			this.fadeDuration = ragdollCreate.getFadeDuration();
			this.fadeShader = ragdollCreate.getFadeShader();
		}

		//ragdoll spin direction depends on which way it is moving
		if (startVelo.x >= 0) {
			this.startAngle = -BASE_ANGLE * ANGLE_AMP;
		} else {
			this.startAngle = BASE_ANGLE * ANGLE_AMP;
		}

		setSyncDefault(false);
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

		if (angularDampening != 0.0f) {
			body.setAngularDamping(angularDampening);
		}
		if (linearDampening != 0.0f) {
			body.setLinearDamping(linearDampening);
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

	@Override
	public Object onServerDelete() { return null; }
}
