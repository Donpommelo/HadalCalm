package com.mygdx.hadal.schmucks.bodies;

import java.util.UUID;

import static com.mygdx.hadal.utils.Constants.PPM;

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

/**
 * A HadalEntity is enything in the Game world that does stuff.
 * A HadalEntity contains the method to create a Box2d body. It is not a body itself.
 * The entity also runs a method every engine tick. Anything that reacts to anything must be an entity.
 * Children: Schmucks, Hitboxes, Events. Walls are not entities.
 * All entities must have a HadalData. Class hierarchy for userData corresponds to that of entities (so far).
 * @author Zachary Tu
 *
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
	
	//counter and method to keep up with animation frames
	protected float animationTime = 0;
	protected float getAnimationTime() { return animationTime; }
	
	//counter of entity's age. this is sent to the client for syncing purposes.
	protected float entityAge = 0;

	//Only used by client. this keeps track of time since last sync to detect if we missed a delete packet.
	protected float timeSinceLastSync = 0;
	
	//On the client, do we expect a sync packet from the server regularly
	protected boolean receivingSyncs;

	//Keeps track of an entity's shader such as when flashing after receiving damage.
	protected float shaderCount = 0;
	protected ShaderProgram shader;
		
	//This is the id that clients use to track synchronized entities
	protected UUID entityID;
	
	private boolean syncDefault = true;
	private boolean syncInstant = false;
	
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
		
		//check of alive to avoid double-deletions
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
		if (destroyed == false) {
			destroyed = true;
			alive = false;
			if (body != null) {
				world.destroyBody(body);
			}
		}
	}	
	
	/**
	 * A simple helper method that converts a screen coordinate into an impulse applied to this entity's body.
	 * @param push: vector2 of impulse applied to this entity
	 * @param power: Magnitude of impulse
	 */
	private Vector2 impulse = new Vector2();
	public void recoil(Vector2 push, float power) {
		if (!alive) {
			return;
		}
		applyLinearImpulse(impulse.set(getPixelPosition()).sub(push).scl(power / getPixelPosition().dst(push)));
	}
	
	public void push(float impulseX, float impulseY) {
		if (!alive) {
			return;
		}
		applyLinearImpulse(impulse.set(impulseX, impulseY));
	}
	
	public void push(Vector2 push) {
		if (!alive) {
			return;
		}
		applyLinearImpulse(push);
	}

	/**
	 * this method does a regular push, except it mitigates existing momentum
	 */
	public void pushMomentumMitigation(float impulseX, float impulseY) {
		if (!alive) {
			return;
		}
		if (getLinearVelocity().y < 0 && impulseY > 0) {
			setLinearVelocity(getLinearVelocity().x, 0);
		}
		if (getLinearVelocity().y > 0 && impulseY < 0) {
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
	public Object onServerDelete() { return new Packets.DeleteEntity(entityID.toString()); }

	/**
	 * This is called every engine tick to send a packet syncing this entity.
	 * Default: Track entity's position and angle if it has a body
	 */
	public void onServerSync() {
		if (body != null && syncDefault) {
			HadalGame.server.sendToAllUDP(new Packets.SyncEntity(entityID.toString(), getPosition(), body.getLinearVelocity(), getAngle(), entityAge, false));
		}
	}
	
	public void onServerSyncFast() {
		if (body != null && syncInstant) {
			HadalGame.server.sendToAllUDP(new Packets.SyncEntity(entityID.toString(), getPosition(), body.getLinearVelocity(), getAngle(), entityAge, true));
		}
	}
	
	public Vector2 serverPos = new Vector2();
	public boolean copyServerInstantly;
	/**
	 * This is called when the client receives the above packet.
	 * Set the entity's body data
	 */
	public void onClientSync(Object o) {
		Packets.SyncEntity p = (Packets.SyncEntity) o;
		if (body != null) {
			copyServerInstantly = p.instant;
			
			if (copyServerInstantly) {
				setTransform(p.pos, p.angle);
			} else {
				serverPos.set(p.pos);
				serverAngle.setAngleRad(p.angle);			
				body.setLinearVelocity(p.velocity);
			}
		}
	}
	
	public Vector2 serverAngle = new Vector2(0, 1);
	public Vector2 angleAsVector = new Vector2(0, 1);
	/**
	 * This is a replacement to controller() that is run for clients.
	 * This is used for things that have to process stuff for the client, and not just server-side
	 */
	private static float maxDist = 5;
	private static float maxAngleDist = 0.1f;
	public void clientController(float delta) {
		if (body != null && receivingSyncs) {
			if (!copyServerInstantly) {
				setTransform(
						body.getPosition().dst(serverPos) > maxDist ? serverPos : body.getPosition().lerp(serverPos, PlayState.syncInterpolation), 
						Math.abs(body.getAngle() - serverAngle.angleRad()) > maxAngleDist ? serverAngle.angleRad() : angleAsVector.setAngleRad(getAngle()).lerp(serverAngle, PlayState.syncInterpolation).angleRad());
			}
		}
	}
	
	/**
	 * Is this entity on the screen? Used for frustrum culling to avoid rending off-screen entities
	 */
	public boolean isVisible() {
		if (body == null) {
			return false;
		} else {
			if (
					state.camera.frustum.pointInFrustum(getPixelPosition().x + size.x / 2, getPixelPosition().y + size.y / 2, 0) || 
					state.camera.frustum.pointInFrustum(getPixelPosition().x - size.x / 2, getPixelPosition().y + size.y / 2, 0) ||
					state.camera.frustum.pointInFrustum(getPixelPosition().x + size.x / 2, getPixelPosition().y - size.y / 2, 0) ||
					state.camera.frustum.pointInFrustum(getPixelPosition().x - size.x / 2, getPixelPosition().y - size.y / 2, 0)) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * returns position scaled by pixels per meter.
	 * use when you want screen coordinates instead of getPosition()
	 */
	private Vector2 pixelPosition = new Vector2();
	public Vector2 getPixelPosition() {	
		if (body != null) {
			return pixelPosition.set(body.getPosition()).scl(PPM); 
		} else { return pixelPosition.scl(PPM); }
	}
	
	public Fixture getMainFixture() {
		if (body == null) {
			return null;
		}
		if (body.getFixtureList().isEmpty()) {
			return null;
		} else {
			return body.getFixtureList().get(0); 
		}
	}
	
	public Body getBody() { return body; }
	
	public HadalData getHadalData() { return hadalData; }

	public PlayState getState() { return state; }

	public World getWorld() { return world;	}

	public boolean isAlive() { return alive; }
	
	public UUID getEntityID() { return entityID; }
	
	public void setEntityID(String entityID) { 
		this.entityID = UUID.fromString(entityID); 
	}

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
	
	public void endShader(Shader shader) {
		shaderCount = 0;
		if (state.isServer()) {
			HadalGame.server.sendToAllTCP(new Packets.SyncShader(entityID.toString(), shader, 0.0f));
		}
	}
	
	public void decreaseShaderCount(float i) { shaderCount -= i; }
	
	public void increaseAnimationTime(float i) { animationTime += i; }
	
	public void increaseEntityAge(float i) { entityAge += i; }
	
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

	public Vector2 getPosition() { return body.getPosition(); }

	public float getAngle() { return body.getAngle(); }

	public Vector2 getLinearVelocity() { return body.getLinearVelocity(); }

	public float getAngularVelocity() { return body.getAngularVelocity(); }

	public void setAngle(float angle) { setTransform(getPosition(), angle); }

	public float getMass() { return body.getMass(); }

	public void setReceivingSyncs(boolean receivingSyncs) {	this.receivingSyncs = receivingSyncs; }

	public void setSyncDefault(boolean syncDefault) { this.syncDefault = syncDefault; }

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
}
