package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.SteeringUtil;

import box2dLight.RayHandler;

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
	protected RayHandler rays;
	
	//Fields common to all entities.
	protected Body body;
	protected HadalData hadalData;
	protected float height, width;
	protected float startX, startY;
	
	private boolean alive = true;
	
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
		this.rays = state.getRays();
		
		this.width = w;
		this.height = h;
		this.startX = startX;
		this.startY = startY;
		
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
	public void queueDeletion() {
		if (alive) {
			alive = false;
			state.destroy(this);
		}
	}
	
	/**
	 * This method is called by the playstate next engine tick after deleting this entity.
	 * This is where the body is actually deleted
	 */
	public void dispose() {
		if (body != null) {
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
		
		Vector3 bodyScreenPosition = new Vector3(body.getPosition().x, body.getPosition().y, 0);
				
		camera.project(bodyScreenPosition);
		
		float powerDiv = bodyScreenPosition.dst(x, y, 0) / power;
		
		float xImpulse = (bodyScreenPosition.x - x) / powerDiv;
		float yImpulse = (bodyScreenPosition.y - y) / powerDiv;
		
		body.applyLinearImpulse(new Vector2(xImpulse, yImpulse), body.getWorldCenter(), true);
	}
	
	public void push(float impulseX, float impulseY) {
		body.applyLinearImpulse(new Vector2(impulseX, impulseY), body.getWorldCenter(), true);
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

	public RayHandler getRays() {
		return rays;
	}

	public boolean isAlive() {
		return alive;
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
	
	/**
	 * This is only used for steering entities.
	 * @param delta
	 */
	
	public void setTarget(HadalEntity target) {
		Arrive<Vector2> arriveSB = new Arrive<Vector2>(this, target)
				.setArrivalTolerance(2f)
				.setDecelerationRadius(decelerationRad);
		
		this.setBehavior(arriveSB);
	}
	
	public void applySteering(float delta) {
		boolean anyAcceleration = false;
		if (!steeringOutput.linear.isZero()) {
			Vector2 force;
			if (this instanceof Schmuck) {
				force = steeringOutput.linear.scl(delta).scl(1 + ((Schmuck)this).getBodyData().getBonusAirSpeed());
			} else {
				force = steeringOutput.linear.scl(delta);
			}
			body.applyForceToCenter(force, true);
			anyAcceleration = true;
		}
		
		if (steeringOutput.angular != 0) {
			body.applyTorque(steeringOutput.angular, true);
			anyAcceleration = true;
		} else {
			Vector2 linVel = getLinearVelocity();
			if (!linVel.isZero()) {
				float newOrientation = vectorToAngle(linVel);
				body.setAngularVelocity((newOrientation - getAngularVelocity()) * delta);
				body.setTransform(body.getPosition(), newOrientation);
			}
		}
		
		if (anyAcceleration) {
			
			Vector2 velocity = body.getLinearVelocity();
			float currentSpeedSquare = velocity.len2();
			
			if (this instanceof Schmuck) {
				if (currentSpeedSquare > maxLinearSpeed * maxLinearSpeed 
						* (1 + ((Schmuck)this).getBodyData().getBonusAirSpeed())
						* (1 + ((Schmuck)this).getBodyData().getBonusAirSpeed())) {
					body.setLinearVelocity(velocity
							.scl(maxLinearSpeed / (float) Math.sqrt(currentSpeedSquare))
							.scl(1 + ((Schmuck)this).getBodyData().getBonusAirSpeed()));
				}
			} else {
				if (currentSpeedSquare > maxLinearSpeed * maxLinearSpeed) {
					body.setLinearVelocity(velocity.scl(maxLinearSpeed / (float) Math.sqrt(currentSpeedSquare)));
				}
			}
			
			if (body.getAngularVelocity() > maxAngularSpeed) {
				body.setAngularVelocity(maxAngularSpeed);
			}
		}
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
		// TODO Auto-generated method stub
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
		return null;//new Location<Vector2>();
	}
	
	@Override
	public float getZeroLinearSpeedThreshold() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setZeroLinearSpeedThreshold(float value) {
		// TODO Auto-generated method stub
		
	}

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
	
	public void setSteeringOutput(SteeringAcceleration<Vector2> steeringOutput) {
		this.steeringOutput = steeringOutput;
	}	
}
