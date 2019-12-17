package com.mygdx.hadal.schmucks.bodies;

import java.util.UUID;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.SteeringUtil;

/**
 * A HadalEntity is enything in the Game world that does stuff.
 * A HadalEntity contains the method to create a Box2d body. It is not a body itself.
 * The entity also runs a method every engine tick. Anything that reacts to anything must be an entity.
 * Children: Schmucks, Hitboxes, Events. Walls are not entities.
 * All entities must have a HadalData. Class hierarchy for userData corresponds to that of entities (so far).
 * @author Zachary Tu
 *
 */
public abstract class HadalEntity implements Steerable<Vector2> {

	//References to game fields.
	protected PlayState state;
	protected World world;
	protected OrthographicCamera camera;
	
	//Fields common to all entities.
	protected Body body;
	protected HadalData hadalData;
	protected float height, width;
	protected float startX, startY;
	
	protected boolean alive = true;
	
	//The below fields are only used for steering entities. most things will ignore these
	protected boolean tagged;
	protected float boundingRadius;
	protected float maxLinearSpeed, maxLinearAcceleration;
	protected float maxAngularSpeed, maxAngularAcceleration;
	
	protected float decelerationRad;
	
	protected SteeringBehavior<Vector2> behavior;
	protected SteeringAcceleration<Vector2> steeringOutput;
	
	protected float animationTime = 0;
	protected void increaseAnimationTime(float i) { animationTime += i; }
	protected float getAnimationTime() { return animationTime; }
	
	//This is the id that clients use to track synchronized entities
	protected UUID entityID;
	
	/**
	 * Constructor is called when an entity is created.
	 * @param state: Current playstate
	 * @param world: Current game world
	 * @param camera: Current game camera
	 * @param rays: Current rayhandler
	 * @param w: Width
	 * @param h: Height
	 * @param startX: Starting x position
	 * @param startY: Starting y position
	 */
	public HadalEntity(PlayState state, float w, float h, float startX, float startY) {
		this.state = state;
		this.camera = state.camera;
		this.world = state.getWorld();
		
		this.width = w;
		this.height = h;
		this.startX = startX;
		this.startY = startY;
		
		this.entityID = UUID.randomUUID();
		
		//Queue this entity up for creating in the world next engine tick
		state.create(this);
	}
		
	/**
	 * This method is called by the playstate next engine tick after initializing this entity.
	 * Usually, this is where the entity's body + data is created.
	 */
	public abstract void create();

	/**
	 * This method is run every engine tick. Here goes the entities game logic like enemy ai or player input.
	 * @param delta: time elapsed since last engine tick.
	 */
	public abstract void controller(float delta);
	
	/**
	 * Draw the entity
	 * @param batch: SpriteBatch for rendering
	 */
	public abstract void render(SpriteBatch batch);
	
	/**
	 * Call this method to delete a body. NOT dispose().
	 * This tells the playstate to remove this entity next engine tick.
	 */
	public boolean queueDeletion() {
		if (alive) {
			alive = false;
			state.destroy(this);
			return true;
		}
		return false;
	}
	
	/**
	 * This method is called by the playstate next engine tick after deleting this entity.
	 * This is where the body is actually deleted
	 */
	public void dispose() {
		if (body != null) {
			alive = false;
			world.destroyBody(body);
		}
	}	
	
	/**
	 * A simple helper method that converts a screen coordinate into an impulse applied to this entity's body.
	 * @param x: x position in screen coordinates
	 * @param y: y position in screen coordinates
	 * @param power: Magnitude of impulse
	 */
	public void recoil(int x, int y, float power) {
		
		if (!alive) {
			return;
		}
		
		float powerDiv = getPosition().dst(x, y) / power;
		
		float xImpulse = (getPosition().x - x) / powerDiv;
		float yImpulse = (getPosition().y - y) / powerDiv;
		
		applyLinearImpulse(new Vector2(xImpulse, yImpulse));
	}
	
	public void push(float impulseX, float impulseY) {
		
		if (!alive) {
			return;
		}
		
		applyLinearImpulse(new Vector2(impulseX, impulseY));
	}

	public void pushMomentumMitigation(float impulseX, float impulseY) {
		
		if (!alive) {
			return;
		}
		
		if (getLinearVelocity().y < 0) {
			setLinearVelocity(getLinearVelocity().x, 0);
		}
		
		applyLinearImpulse(new Vector2(impulseX, impulseY));
	}
	
	/**
	 * This is called when the entity is created to return a packet to be sent to the client
	 * Default: no packet is sent for unsynced entities
	 */
	public Object onServerCreate() {
		return null;
	}
	
	/**
	 * This is called every engine tick to send a packet syncing this entity.
	 * Default: Track entity's position and angle if it has a body
	 */
	public void onServerSync() {
		if (body != null) {
			HadalGame.server.sendToAllUDP(new Packets.SyncEntity(entityID.toString(), getPosition(), getOrientation()));
		}
	}
	
	/**
	 * This is called when the client receives the above packet.
	 * Set the entity's body data
	 */
	public void onClientSync(Object o) {
		Packets.SyncEntity p = (Packets.SyncEntity) o;
		if (body != null) {
			setTransform(p.pos, p.angle);
		}
	}
	
	/**
	 * This is a replacement to controller() that is run for clients.
	 * This is used for things that have to process stuff for the client, and not just server-side
	 */
	public void clientController(float delta) {
		increaseAnimationTime(delta);
	}
	
	/**
	 * Is this entity on the screen?
	 * @return
	 */
	public boolean isVisible() {
		if (body == null) {
			return false;
		} else {
			if (
					camera.frustum.pointInFrustum(body.getPosition().x * PPM + width, body.getPosition().y * PPM + height, 0) || 
					camera.frustum.pointInFrustum(body.getPosition().x * PPM - width, body.getPosition().y * PPM + height, 0) ||
					camera.frustum.pointInFrustum(body.getPosition().x * PPM + width, body.getPosition().y * PPM - height, 0) ||
					camera.frustum.pointInFrustum(body.getPosition().x * PPM - width, body.getPosition().y * PPM - height, 0)) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * Getter method for the entity's body.
	 * @return: Entity's body.
	 */
	public Body getBody() {
		return body;
	}
	
	public HadalData getHadalData() {
		return hadalData;
	}

	public PlayState getState() {
		return state;
	}

	public World getWorld() {
		return world;
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public boolean isAlive() {
		return alive;
	}
	
	public UUID getEntityID() {
		return entityID;
	}

	public float getStartX() {
		return startX;
	}
	public void setStartX(float startX) {
		this.startX = startX;
	}
	public float getStartY() {
		return startY;
	}
	public void setStartY(float startY) {
		this.startY = startY;
	}
	
	public void applySteering(float delta) {
		boolean anyAcceleration = false;
		if (!steeringOutput.linear.isZero()) {
			Vector2 force;
			if (this instanceof Schmuck) {
				force = steeringOutput.linear.scl(delta)
						.scl(1 + ((Schmuck)this).getBodyData().getBonusAirSpeed())
						.scl(1 + ((Schmuck)this).getBodyData().getBonusAirSpeed());
			} else {
				force = steeringOutput.linear.scl(delta);
			}
			applyForceToCenter(force);
			anyAcceleration = true;
		}
		
		if (steeringOutput.angular != 0) {
			body.applyTorque(steeringOutput.angular, true);
			anyAcceleration = true;
		} else {
			Vector2 linVel = getLinearVelocity();
			if (!linVel.isZero()) {
				float newOrientation = vectorToAngle(linVel);
				setAngularVelocity((newOrientation - getAngularVelocity()) * delta);
				setTransform(getPosition(), newOrientation);
			}
		}
		
		if (anyAcceleration) {
			
			Vector2 velocity = getLinearVelocity();
			float currentSpeedSquare = velocity.len2();
			
			if (this instanceof Schmuck) {
				if (currentSpeedSquare > maxLinearSpeed * maxLinearSpeed) {
					setLinearVelocity(velocity
							.scl(maxLinearSpeed / (float) Math.sqrt(currentSpeedSquare))
							.scl(1 + ((Schmuck)this).getBodyData().getBonusAirSpeed()));
				}
			} else {
				if (currentSpeedSquare > maxLinearSpeed * maxLinearSpeed) {
					setLinearVelocity(velocity.scl(maxLinearSpeed / (float) Math.sqrt(currentSpeedSquare)));
				}
			}
			
			if (getAngularVelocity() > maxAngularSpeed) {
				setAngularVelocity(maxAngularSpeed);
			}
		}
	}
	
	public float getHeight() {
		return height;
	}
	
	public float getWidth() {
		return width;
	}
	
	@Override
	public Vector2 getPosition() {
		return body.getPosition();
	}

	@Override
	public float getOrientation() {
		return body.getAngle();
	}

	@Override
	public void setOrientation(float orientation) {
		setTransform(getPosition(), orientation);
	}

	@Override
	public float vectorToAngle(Vector2 vector) {
		return SteeringUtil.vectorToAngle(vector);
	}

	@Override
	public Vector2 angleToVector(Vector2 outVector, float angle) {
		return SteeringUtil.angleToVector(outVector, angle);
	}

	@Override
	public Location<Vector2> newLocation() {
		System.out.println("newLocation was run?");
		return null;
	}
	
	@Override
	public float getZeroLinearSpeedThreshold() {
		return 0;
	}

	@Override
	public void setZeroLinearSpeedThreshold(float value) {}

	@Override
	public float getMaxLinearSpeed() {
		return maxLinearSpeed;
	}

	@Override
	public void setMaxLinearSpeed(float maxLinearSpeed) {
		this.maxLinearSpeed = maxLinearSpeed;
	}

	@Override
	public float getMaxLinearAcceleration() {
		return maxLinearAcceleration;
	}

	@Override
	public void setMaxLinearAcceleration(float maxLinearAcceleration) {
		this.maxLinearAcceleration = maxLinearAcceleration;
	}

	@Override
	public float getMaxAngularSpeed() {
		return maxAngularSpeed;
	}

	@Override
	public void setMaxAngularSpeed(float maxAngularSpeed) {
		this.maxAngularSpeed = maxAngularSpeed;
	}

	@Override
	public float getMaxAngularAcceleration() {
		return maxAngularAcceleration;
	}

	@Override
	public void setMaxAngularAcceleration(float maxAngularAcceleration) {
		this.maxAngularAcceleration = maxAngularAcceleration;
	}

	@Override
	public Vector2 getLinearVelocity() {
		return body.getLinearVelocity();
	}

	@Override
	public float getAngularVelocity() {
		return body.getAngularVelocity();
	}

	@Override
	public float getBoundingRadius() {
		return boundingRadius;
	}

	@Override
	public boolean isTagged() {
		return tagged;
	}

	@Override
	public void setTagged(boolean tagged) {
		this.tagged = tagged;
	}
	
	public SteeringBehavior<Vector2> getBehavior() {
		return behavior;
	}
	
	public void setBehavior(SteeringBehavior<Vector2> behavior) {
		this.behavior = behavior;
	}
	
	public void setBoundingRadius(float radius) {
		this.boundingRadius = radius;
	}
	
	public void setDecelerationRad(float radius) {
		this.decelerationRad = radius;
	}
	
	public SteeringAcceleration<Vector2> getSteeringOutput() {
		return steeringOutput;
	}
	
	public float getMass() {
		return body.getMass();
	}
	
	public void setSteeringOutput(SteeringAcceleration<Vector2> steeringOutput) {
		this.steeringOutput = steeringOutput;
	}
	
	public void setTransform(Vector2 position, float angle) {
		if (alive && body != null) {
			body.setTransform(position, angle);
		}
	}
	
	public void setTransform(float vX, float vY, float angle) {
		if (alive && body != null) {
			body.setTransform(vX, vY, angle);
		}
	}
	
	public void setLinearVelocity (Vector2 position) {
		if (alive && body != null) {
			body.setLinearVelocity(position);
		}
	}
	
	public void setLinearVelocity(float vX, float vY) {
		if (alive && body != null) {
			body.setLinearVelocity(vX, vY);
		}
	}
	
	public void setAngularVelocity(float omega) {
		if (alive && body != null) {
			body.setAngularVelocity(omega);
		}
	}
	
	public void setGravityScale(float scale) {
		if (alive && body != null) {
			body.setGravityScale(scale);
		}
	}
	
	public void applyLinearImpulse(Vector2 impulse) {
		if (alive && body != null) {
			body.applyLinearImpulse(impulse, body.getLocalCenter(), true);
		}
	}
	
	public void applyForceToCenter(Vector2 force) {
		if (alive && body != null) {
			body.applyForceToCenter(force, true);
		}
	}
	
	public void applyForceToCenter(float forceX, float forceY) {
		if (alive && body != null) {
			body.applyForceToCenter(forceX, forceY, true);
		}
	}
}
