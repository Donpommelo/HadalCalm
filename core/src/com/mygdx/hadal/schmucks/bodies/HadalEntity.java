package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

import java.util.ArrayList;
import java.util.UUID;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * A HadalEntity is anything in the Game world that does stuff.
 * A HadalEntity contains the method to create a Box2d body. It is not a body itself.
 * The entity also runs a method every engine tick. Anything that reacts to anything must be an entity.
 * Children: Schmucks, Hitboxes, Events. Walls are not entities.
 * All entities must have a HadalData. Class hierarchy for userData corresponds to that of entities (so far).
 * @author Zachary Tu
 */
public abstract class HadalEntity {

	//References to game fields.
	protected PlayState state;
	protected World world;
	
	//Fields common to all entities.
	protected Body body;
	protected HadalData hadalData;
	protected Vector2 size;
	protected Vector2 startPos;
	
	//is the entity queued up for deletion? has it been destroyed yet?
	protected boolean alive = true, destroyed = false;
	
	//counter and method to keep up with animation frames. (extra is used for entities that have multiple parts that move at different speeds like the player)
	protected float animationTime, animationTimeExtra;

	//counter of entity's age. this is sent to the client for syncing purposes.
	protected float entityAge;

	//Only used by client. this keeps track of time since last sync to detect if we missed a delete packet.
	protected float timeSinceLastSync;
	
	//On the client, do we expect a sync packet from the server regularly
	protected boolean receivingSyncs;

	//Keeps track of an entity's shader such as when flashing after receiving damage.
	protected float shaderCount = 0;
	protected ShaderProgram shader;
		
	//This is the id that clients use to track synchronized entities
	protected UUID entityID;
	
	//Used by the server. Does this entity send a sync packet periodically (every 1 / 10 sec)? Does this entity send a sync packet at a faster rate? (every 1 / 60 sec) 
	private boolean syncDefault = true, syncInstant = false;
	
	/**
	 * Constructor is called when an entity is created.
	 * @param state: Current playstate
	 * @param startPos: starting position in screen coordinates
	 * @param size: entity's dimensions in screen coordinates
	 */
	public HadalEntity(PlayState state, Vector2 startPos, Vector2 size) {
		this.state = state;
		this.world = state.getWorld();
		
		this.size = new Vector2(size);
		this.startPos = new Vector2(startPos);
		
		//give this entity a random, unique id
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
	 * @return whether this entity was successfully deleted. (false if already deleted)
	 */
	public boolean queueDeletion() {
		
		//check alive to avoid double-deletions
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
		
		//check of destroyed to avoid double-destruction
		if (!destroyed) {
			destroyed = true;
			alive = false;
			if (body != null) {
				world.destroyBody(body);
			}
		}
	}

	private final Vector2 impulse = new Vector2();
	/**
	 * A simple helper method that converts a screen coordinate into an impulse applied to this entity's body.
	 * @param push: vector2 of impulse applied to this entity
	 * @param power: Magnitude of impulse
	 */
	public void recoil(Vector2 push, float power) {
		if (!alive) { return; }
		
		applyLinearImpulse(impulse.set(getPixelPosition()).sub(push).scl(power / getPixelPosition().dst(push)));
	}
	
	public void push(float impulseX, float impulseY) {
		if (!alive) { return; }
		
		applyLinearImpulse(impulse.set(impulseX, impulseY));
	}
	
	public void push(Vector2 push) {
		if (!alive) { return; }

		applyLinearImpulse(push);
	}

	/**
	 * this method does a regular push, except it mitigates existing momentum in the y-direction
	 * used for jumping to feel more fluid and less influenced by physics
	 */
	public void pushMomentumMitigation(float impulseX, float impulseY) {
		if (!alive) { return; }
		
		if (getLinearVelocity().y < 0 && impulseY > 0) {
			setLinearVelocity(getLinearVelocity().x, 0);
		} else if (getLinearVelocity().y > 0 && impulseY < 0) {
			setLinearVelocity(getLinearVelocity().x, 0);
		}
		applyLinearImpulse(impulse.set(impulseX, impulseY));
	}
	
	/**
	 * This is called when the entity is created to return a packet to be sent to the client
	 * Default: no packet is sent for unsynced entities
	 */
	public Object onServerCreate() { return null; }
	
	/**
	 * This is called when the entity is deleted to return a packet to be sent to the client
	 * Default: send a packet telling clients to delete this.
	 */
	public Object onServerDelete() { return new Packets.DeleteEntity(entityID.toString(), state.getTimer()); }

	/**
	 * This is called to send a packet syncing this entity.
	 * onServerSyncFast() is called more frequently than onServerSync().
	 * Default: Track entity's position and angle if it has a body
	 */
	public void onServerSync() {
		if (body != null && syncDefault) {
			state.getSyncPackets().add(new Packets.SyncEntity(entityID.toString(), getPosition(), body.getLinearVelocity(), getAngle(), entityAge, state.getTimer(), false));
		}
	}
	
	public void onServerSyncFast() {
		if (body != null && syncInstant) {
			HadalGame.server.sendToAllUDP(new Packets.SyncEntity(entityID.toString(), getPosition(), body.getLinearVelocity(), getAngle(), entityAge, state.getTimer(), true));
		}
	}
	
	//the position of this entity on the server
	public Vector2 prevPos = new Vector2();
	public Vector2 serverPos = new Vector2();
	
	public Vector2 prevVelo = new Vector2();
	public Vector2 serverVelo = new Vector2();
	
	//the angle of this entity on the server
	public Vector2 serverAngle = new Vector2(1, 0);
	
	//should the client entity lerp to the server's position or just adjust instantly?
	public boolean copyServerInstantly;
	
	//this is a list of the most recent packets that sync this entity as well as their timestamps
	private final ArrayList<Object[]> bufferedTimestamps = new ArrayList<>();
	
	/**
	 * When we receive a packet from the server, we store it alongside its timestamp
	 * @param o: the packet object we are receiving from the server
	 * @param timestamp: the server time of the received packet
	 */
	public void onReceiveSync(Object o, float timestamp) {
		Object[] packet = {o, timestamp};
		bufferedTimestamps.add(packet);
	}
	
	/**
	 * This is called when the client processes stored sync packets
	 * Set the entity's body data
	 * @param o: the packet object we are receiving from the server
	 */
	public void onClientSync(Object o) {
		if (o instanceof Packets.SyncEntity) {
			Packets.SyncEntity p = (Packets.SyncEntity) o;
			if (body != null) {
				copyServerInstantly = p.instant;
				
				//if copying instantly, set transform. Otherwise, save the position, angle, and set the velocity of the most recent snapshot and the one before it
				if (copyServerInstantly) {
					setTransform(p.pos, p.angle);
				} else {
					prevPos.set(serverPos);
					serverPos.set(p.pos);
					
					prevVelo.set(serverVelo);
					serverVelo.set(p.velocity);
					
					serverAngle.setAngleRad(p.angle);			
				}
			}
		} else if (o instanceof Packets.DeleteEntity) {
			((ClientState) state).removeEntity(entityID.toString());
		}
	}
	
	//this vector is used to calculate linear interpolation
	public Vector2 angleAsVector = new Vector2(0, 1);
	
	//the client processes interpolation at this speed regardless of framerate
	public static final float clientSyncTime = 1 / 60f;
	private float clientSyncAccumulator = 0.0f;
	
	//this extra vector is used b/c interpolation updates the start vector
	public Vector2 lerpPos = new Vector2();
	public Vector2 lerpVelo = new Vector2();

	//these are the timestamps of the 2 most recent snapshots
	protected float prevTimeStamp, nextTimeStamp;
	
	/**
	 * This is a replacement to controller() that is run for clients.
	 * This is used for things that have to process stuff for the client, and not just server-side
	 * @param delta: elapsed time
	 */
	public void clientController(float delta) {
		
		//process each buffered snapshot starting from the oldest to the most recent
		while (!bufferedTimestamps.isEmpty()) {
			if (state.getTimer() >= nextTimeStamp) {
				Object[] o = bufferedTimestamps.remove(0);
				
				//check timestamp in case snapshots are sent out of order
				if ((float) o[1] > nextTimeStamp) {
					prevTimeStamp = nextTimeStamp;
					nextTimeStamp = (float) o[1];
				}
				onClientSync(o[0]);
				
			} else {
				break;
			}
		}
		
		//interpolate this entity between most recent snapshots. Use accumulator to be independent from framerate
		clientSyncAccumulator += delta;
		while (clientSyncAccumulator >= clientSyncTime) {
			clientSyncAccumulator -= clientSyncTime;
			clientInterpolation();
		}
	}
	
	/**
	 * This interpolates the entity's position between two timestamps.
	 */
	public void clientInterpolation() {
		//if we are receiving syncs, lerp towards the saved position and angle
		if (body != null && receivingSyncs) {
			if (!copyServerInstantly) {
				
				float elapsedTime = (state.getTimer() - prevTimeStamp) / (nextTimeStamp - prevTimeStamp);
				
				if (elapsedTime <= 1.0f) {
					if (elapsedTime >= 0.0f) {
						lerpPos.set(prevPos);
						lerpVelo.set(prevVelo);
						setTransform(lerpPos.lerp(serverPos, elapsedTime), angleAsVector.setAngleRad(getAngle()).lerp(serverAngle, PlayState.syncInterpolation).angleRad());
						
						//set velocity to make entity move smoother between syncs
						body.setLinearVelocity(lerpVelo.lerp(serverVelo, elapsedTime));
					}
				}
			}
		}
	}
	
	/**
	 * Is this entity on the screen? Used for frustrum culling to avoid rendering off-screen entities
	 */
	private final Vector2 entityLocation = new Vector2();
	public boolean isVisible() {
		if (body == null) {
			return false;
		}
		entityLocation.set(getPixelPosition());
		
		//check the center + 4 corners of the entity to see if we should render this entity
		if (state.getCamera().frustum.pointInFrustum(entityLocation.x, entityLocation.y, 0)) { return true; }
		float bodyAngle = body.getAngle();
		float cosAng = (float) Math.cos(bodyAngle);
		float sinAng = (float) Math.sin(bodyAngle);
		if (state.getCamera().frustum.pointInFrustum(entityLocation.x + size.x / 2 * cosAng - size.y / 2 * sinAng, entityLocation.y + size.x / 2 * sinAng + size.y / 2 * cosAng, 0)) { return true; }
		if (state.getCamera().frustum.pointInFrustum(entityLocation.x - size.x / 2 * cosAng - size.y / 2 * sinAng, entityLocation.y - size.x / 2 * sinAng + size.y / 2 * cosAng, 0)) { return true; }
		if (state.getCamera().frustum.pointInFrustum(entityLocation.x - size.x / 2 * cosAng + size.y / 2 * sinAng, entityLocation.y - size.x / 2 * sinAng - size.y / 2 * cosAng, 0)) { return true; }
		return state.getCamera().frustum.pointInFrustum(entityLocation.x + size.x / 2 * cosAng + size.y / 2 * sinAng,entityLocation.y + size.x / 2 * sinAng - size.y / 2 * cosAng,0);
	}
	
	/**
	 * returns position scaled by pixels per meter.
	 * use when you want screen coordinates instead of getPosition()
	 */
	private final Vector2 pixelPosition = new Vector2();
	public Vector2 getPixelPosition() {	
		if (body != null) {
			return pixelPosition.set(body.getPosition()).scl(PPM); 
		}
		return pixelPosition;
	}
	
	/**
	 * The "main" fixture of an entity is whichever one should be modified by things that affect the entity's body (for effects like bouncy bullets)
	 * @return the fixture that should be modified in case of effects that modify a specific fixture
	 */
	public Fixture getMainFixture() {
		if (body == null) { return null; }
		if (body.getFixtureList().isEmpty()) { return null; }
		return body.getFixtureList().get(0);
	}
	
	public Body getBody() { return body; }
	
	public HadalData getHadalData() { return hadalData; }

	public PlayState getState() { return state; }

	public World getWorld() { return world;	}

	public boolean isAlive() { return alive; }
	
	public void setAlive(boolean alive) { this.alive = alive; }
	
	public UUID getEntityID() { return entityID; }
	
	public void setEntityID(String entityID) { this.entityID = UUID.fromString(entityID); }

	public Vector2 getStartPos() { return startPos;	}
	
	public void setStartPos(Vector2 startPos) {	this.startPos = startPos; }
	
	public Vector2 getSize() { return size; }
	
	public ShaderProgram getShader() { return shader; }
	
	public float getShaderCount() { return shaderCount; }
	
	public void setShader(Shader shader, float shaderCount) { 
		shader.loadShader(state, entityID.toString(), shaderCount);
		this.shader = shader.getShader();
		this.shaderCount = shaderCount;
	}
	
	public void decreaseShaderCount(float i) { shaderCount -= i; }
	
	public void increaseAnimationTime(float i) { animationTime += i; }
	
	public void increaseEntityAge(float i) { entityAge += i; }
	
	/**
	 * This is run by the client and keeps of track of the time since the last sync received from the server.
	 * If this time is too great, we may have missed a delete packet
	 * @param i: the amount of elapsed time
	 */
	public void increaseTimeSinceLastSync(float i) { 
		
		if (receivingSyncs) {
			timeSinceLastSync += i;
			
			if (timeSinceLastSync > ClientState.missedDeleteThreshold && state.getTimer() > ClientState.initialConnectThreshold) {
				timeSinceLastSync = 0;
				HadalGame.client.sendUDP(new Packets.MissedDelete(entityID.toString()));
			}
		}
	}
	
	public void resetTimeSinceLastSync() { timeSinceLastSync = 0; }

	public Vector2 getPosition() { 
		if (body != null) {	return body.getPosition(); }
		return new Vector2();
	}

	public float getAngle() { 
		if (body != null) {	return body.getAngle(); }
		return 0.0f;
	}

	public Vector2 getLinearVelocity() { 
		if (body != null) { return body.getLinearVelocity(); }
		return new Vector2();
	 }

	public float getAngularVelocity() { 
		if (body != null) { return body.getAngularVelocity(); }
		return 0.0f;
	}

	public void setAngle(float angle) { setTransform(getPosition(), angle); }

	public float getMass() { 
		if (body != null) {	return body.getMass(); }
		return 0.0f;
	}

	public void setReceivingSyncs(boolean receivingSyncs) {	this.receivingSyncs = receivingSyncs; }

	public boolean isSyncDefault() { return syncDefault; }
	
	public void setSyncDefault(boolean syncDefault) { this.syncDefault = syncDefault; }

	public boolean isSyncInstant() { return syncInstant; }

	public void setSyncInstant(boolean syncInstant) { this.syncInstant = syncInstant; }

	public void setTransform(Vector2 position, float angle) {
		if (alive && body != null) {
			body.setTransform(position, angle);
			body.setAwake(true);
		}
	}
	
	public void setTransform(float vX, float vY, float angle) {
		if (alive && body != null) {
			body.setTransform(vX, vY, angle);
			body.setAwake(true);
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
	
	public void setRestitution(float scale) {
		if (alive && body != null) {
			getMainFixture().setRestitution(scale);
		}
	}
	
	public void applyLinearImpulse(Vector2 linImpulse) {
		if (alive && body != null) {
			body.applyLinearImpulse(linImpulse, body.getLocalCenter(), true);
		}
	}
	
	public void applyForceToCenter(Vector2 force) {
		if (alive && body != null) {
			body.applyForceToCenter(force, true);
		}
	}
}
