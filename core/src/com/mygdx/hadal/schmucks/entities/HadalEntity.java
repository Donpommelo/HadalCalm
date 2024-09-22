package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.managers.PacketManager;
import com.mygdx.hadal.schmucks.entities.helpers.ShaderHelper;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.PlayState.ObjectLayer;

import java.util.UUID;

import static com.mygdx.hadal.constants.Constants.PPM;

/**
 * A HadalEntity is anything in the Game world that does stuff.
 * A HadalEntity contains the method to create a Box2d body. It is not a body itself.
 * The entity also runs a method every engine tick. Anything that reacts to anything must be an entity.
 * Children: Schmucks, Hitboxes, Events.
 * All entities must have a HadalData. Class hierarchy for userData corresponds to that of entities (so far).
 * @author Wromatillo Wroothpaste
 */
public abstract class HadalEntity {

	//References to game fields.
	protected PlayState state;
	protected final World world;
	
	//Fields common to all entities.
	protected Body body;
	protected HadalData hadalData;
	protected final Vector2 size;
	protected final Vector2 startPos;
	
	//is the entity queued up for deletion? has it been destroyed yet?
	public boolean alive = true, destroyed = false;
	
	//counter and method to keep up with animation frames. (extra is used for entities that have multiple parts that move at different speeds like the player)
	protected float animationTime, animationTimeExtra;

	//On the client, do we expect a sync packet from the server regularly
	protected boolean receivingSyncs;

	//This is the id that clients use to track synchronized entities
	protected UUID entityID;
	
	//Used by the server. Does this entity send a sync packet periodically (every 1 / 10 sec)? Does this entity send a sync packet at a faster rate? (every 1 / 60 sec) 
	private boolean syncDefault = true, syncInstant = false;
	private boolean reliableCreate = false;
	private ObjectLayer layer = ObjectLayer.STANDARD;

	private final ShaderHelper shaderHelper;

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

		this.shaderHelper = new ShaderHelper(state, this);

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
	public abstract void render(SpriteBatch batch, Vector2 entityLocation);
	
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
		
		//check if destroyed to avoid double-destruction
		if (!destroyed) {
			destroyed = true;
			alive = false;
			if (body != null) {
				world.destroyBody(body);
				body = null;
			}
		}
	}

	private final Vector2 impulse = new Vector2();
	/**
	 * A simple helper method that converts a screen coordinate into an impulse applied to this entity's body.
	 * @param push: vector2 of impulse applied to this entity
	 * @param power: Magnitude of impulse
	 */
	public void pushFromLocation(Vector2 push, float power) {
		if (!alive) { return; }
		applyLinearImpulse(impulse.set(getPixelPosition()).sub(push).nor().scl(power));
	}

	public void push(float impulseX, float impulseY, float power) {
		if (!alive) { return; }
		applyLinearImpulse(impulse.set(impulseX, impulseY).nor().scl(power));
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
	 * @param catchup: is this packet sent to catchup a client (on connect or missed create packet)
	 */
	public Object onServerCreate(boolean catchup) { return null; }
	
	/**
	 * This is called when the entity is deleted to return a packet to be sent to the client
	 * Default: send a packet telling clients to delete this.
	 */
	public Object onServerDelete() { return new Packets.DeleteEntity(entityID, state.getTimer()); }

	/**
	 * This is called to send a packet syncing this entity.
	 * onServerSyncFast() is called more frequently than onServerSync().
	 * Default: Track entity's position and angle if it has a body
	 */
	public void onServerSync() {
		if (body != null && syncDefault) {
			state.getSyncPackets().add(new PacketsSync.SyncEntity(entityID, getPosition(), getLinearVelocity(),
					state.getTimer()));
		}
	}
	
	public void onServerSyncFast() {
		if (body != null && syncInstant) {
			PacketManager.serverUDPAll(state, new PacketsSync.SyncEntity(entityID, getPosition(), getLinearVelocity(),
					state.getTimer()));
		}
	}
	
	//the position and velocity of this entity on the server for the 2 most recent snapshots
	public final Vector2 prevPos = new Vector2();
	public final Vector2 serverPos = new Vector2();
	public final Vector2 prevVelo = new Vector2();
	public final Vector2 serverVelo = new Vector2();
	
	//the angle of this entity on the server
	public final Vector2 serverAngle = new Vector2(1, 0);
	
	//should the client entity lerp to the server's position or just adjust instantly?
	public boolean copyServerInstantly;
	
	//this is a list of the most recent packets that sync this entity as well as their timestamps
	protected final Array<Object[]> bufferedTimestamps = new Array<>();
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
		if (o instanceof PacketsSync.SyncEntity p) {
			if (null != body) {

				float angle = 0;
				if (o instanceof PacketsSync.SyncEntityAngled a) {
					angle = a.angle;
				}
				if (o instanceof PacketsSync.SyncSchmuckAngled a) {
					angle = a.angle;
				}
				//if copying instantly, set transform. Otherwise, save the position, angle, and set the velocity of the most recent snapshot and the one before it
				if (copyServerInstantly) {
					setTransform(p.pos, angle);
				} else {
					prevPos.set(serverPos);
					serverPos.set(p.pos);
					
					prevVelo.set(serverVelo);
					serverVelo.set(p.velocity);
					
					serverAngle.setAngleRad(angle);
				}
			}
		} else if (o instanceof Packets.DeleteEntity a) {
			serverDeleteReceived = true;
			serverDeleteTimestamp = a.timestamp;
		}
	}
	
	//this vector is used to calculate linear interpolation
	public final Vector2 angleAsVector = new Vector2(0, 1);
	
	protected float clientSyncAccumulator;
	
	//this extra vector is used b/c interpolation updates the start vector
	public final Vector2 lerpPos = new Vector2();
	public final Vector2 lerpVelo = new Vector2();

	//these are the timestamps of the 2 most recent snapshots
	public float prevTimeStamp, nextTimeStamp;
	public boolean serverDeleteReceived;
	public float serverDeleteTimestamp;

	//is this entity treated by bots as a health pickup? (since these can be a couple of different event types)
	private boolean botHealthPickup = false;
	private boolean botModePickup = false;

	/**
	 * This is a replacement to controller() that is run for clients.
	 * This is used for things that have to process stuff for the client, and not just server-side
	 * @param delta: elapsed time
	 */
	public void clientController(float delta) {
		
		//process each buffered snapshot starting from the oldest to the most recent
		while (!bufferedTimestamps.isEmpty()) {
			if (state.getTimer() >= nextTimeStamp) {
				Object[] o = bufferedTimestamps.removeIndex(0);

				if (null != o) {
					//check timestamp in case snapshots are sent out of order
					if ((float) o[1] > nextTimeStamp) {
						prevTimeStamp = nextTimeStamp;
						nextTimeStamp = (float) o[1];
					}

					//its ok to sync out of order packets, b/c the interpolation won't do anything
					onClientSync(o[0]);
				}
			} else {
				break;
			}
		}

		onClientDelete();
		
		//interpolate this entity between most recent snapshots. Use accumulator to be independent from framerate
		clientSyncAccumulator += delta;
		while (clientSyncAccumulator >= Constants.INTERVAL) {
			clientSyncAccumulator -= Constants.INTERVAL;
			clientInterpolation();
		}
	}

	public void onClientDelete() {
		if (serverDeleteReceived && state.getTimer() >= serverDeleteTimestamp) {
			serverDeleteReceived = false;
			((ClientState) state).removeEntity(entityID);
		}
	}
	
	/**
	 * This interpolates the entity's position between two timestamps.
	 */
	private static final float MAX_LERP_RANGE = 400.0f;
	public void clientInterpolation() {

		//if we are receiving syncs, lerp towards the saved position and angle
		if (body != null && receivingSyncs) {
			if (!copyServerInstantly) {
				
				float elapsedTime = (state.getTimer() - prevTimeStamp) / (nextTimeStamp - prevTimeStamp);

				if (elapsedTime <= 1.0f && elapsedTime >= 0.0f) {
					if (prevPos.dst2(serverPos) > MAX_LERP_RANGE) {
						setTransform(serverPos, serverAngle.angleRad());
					} else {
						lerpPos.set(prevPos);
						setTransform(lerpPos.lerp(serverPos, elapsedTime), angleAsVector.setAngleRad(getAngle()).lerp(serverAngle, PlayState.SYNC_INTERPOLATION).angleRad());
					}

					//set velocity to make entity move smoother between syncs
					lerpVelo.set(prevVelo);
					setLinearVelocity(lerpVelo.lerp(serverVelo, elapsedTime));
				}
			}
		}
	}
	
	/**
	 * Is this entity on the screen? Used for frustrum culling to avoid rendering off-screen entities
	 */
	public boolean isVisible(Vector2 entityLocation) {
		if (body == null) { return false; }

		//check the center + 4 corners of the entity to see if we should render this entity
		if (state.getCamera().frustum.pointInFrustum(entityLocation.x, entityLocation.y, 0)) { return true; }
		float bodyAngle = getAngle();
		float cosAng = MathUtils.cos(bodyAngle);
		float sinAng = MathUtils.sin(bodyAngle);
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
			pixelPosition.set(getPosition()).scl(PPM);
			pixelPosition.set((int) pixelPosition.x, (int) pixelPosition.y);
			return pixelPosition;
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

	public void setState(PlayState state) { this.state = state;}

	public World getWorld() { return world;	}

	public boolean isAlive() { return alive; }
	
	public UUID getEntityID() { return entityID; }
	
	public void setEntityID(UUID entityID) { this.entityID = entityID; }

	public Vector2 getStartPos() { return startPos;	}
	
	public void setStartPos(Vector2 startPos) {	this.startPos.set(startPos); }
	
	public Vector2 getSize() { return size; }

	public ShaderHelper getShaderHelper() { return shaderHelper; }

	//this method exists so it can be overriden by entities with conditional shaders
	public Shader getShaderStatic() { return shaderHelper.getShaderStatic(); }

	public ObjectLayer getLayer() {	return layer; }

	public void setLayer(ObjectLayer layer) { this.layer = layer; }

	public void increaseAnimationTime(float i) { animationTime += i; }

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

	public boolean isReliableCreate() { return reliableCreate; }

	public void setReliableCreate(boolean reliableCreate) { this.reliableCreate = reliableCreate; }

	public boolean isSyncInstant() { return syncInstant; }

	public void setSyncInstant(boolean syncInstant) { this.syncInstant = syncInstant; }

	public boolean isBotHealthPickup() { return botHealthPickup; }

	public void setBotHealthPickup(boolean botHealthPickup) { this.botHealthPickup = botHealthPickup; }

	public boolean isBotModePickup() { return botModePickup; }

	public void setBotModePickup(boolean botModePickup) { this.botModePickup = botModePickup; }

	public float getAnimationTime() { return animationTime; }

	public void resetAnimationTime() { this.animationTime = 0.0f; }

	public void setAlive(boolean alive) { this.alive = alive; }

	public void setTransform(Vector2 position, float angle) {
		if (alive && body != null && Float.isFinite(position.x) && Float.isFinite(position.y)) {
			body.setTransform(position, angle);
			body.setAwake(true);
		}
	}
	
	public void setTransform(float vX, float vY, float angle) {
		if (alive && body != null && Float.isFinite(vX) && Float.isFinite(vY)) {
			body.setTransform(vX, vY, angle);
			body.setAwake(true);
		}
	}
	
	public void setLinearVelocity(Vector2 position) {
		if (alive && body != null && Float.isFinite(position.x) && Float.isFinite(position.y)) {
			body.setLinearVelocity(position);
		}
	}
	
	public void setLinearVelocity(float vX, float vY) {
		if (alive && body != null && Float.isFinite(vX) && Float.isFinite(vY)) {
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
		if (alive && getMainFixture() != null) {
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
