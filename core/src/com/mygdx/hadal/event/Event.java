package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.ClientIllusion.alignType;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.ClientState.ObjectSyncLayers;

/**
 * An Event is an entity that acts as a catch-all for all misc entities that do not share qualities with schmucks or hitboxes.
 * Events include hp/fuel/weapon pickups, currents, schmuck spawners, springs, literally anything else.
 * @author Zachary Tu
 */
public class Event extends HadalEntity {
	
	//The event's data
	protected EventData eventData;
	
	//If this event triggers another event, this is a local reference to it
	private Event connectedEvent;

	//Whether the event will despawn after time.
	private final boolean temporary;
	protected float duration;
	
	//Is this event affected by gravity?
	protected float gravity = 0.0f;
	
	//Event sprite and rendering information
	private Sprite sprite = Sprite.NOTHING;
	protected Animation<TextureRegion> eventSprite;
	private int spriteWidth;
	private int spriteHeight;
	private float scale = 0.25f;
   
	//align type is how the sprite is drawn in relation to the event's body
	private alignType scaleAlign = alignType.CENTER_BOTTOM;
    
    /* How will this event be synced?
     * ILLUSION: Create a client illusion if it has a body
     * USER: Create this event for clients. When activated on server, activate it for the user who activated it.
     * ALL: Create this event for clients. When activated on server, activate it for all players.
     * SERVER: Create this event for clients. When activated on server, activate it for the server only.
     */
    private eventSyncTypes syncType = eventSyncTypes.ILLUSION;
	
    //speed of animation for events
    private static final float animationSpeed = 0.1f;
    
    //all pickups will have this height and width as default.
    public static final int defaultPickupEventSize = 160;
    
    //this is the map object from Tiled that this event was read from.
    protected MapObject blueprint;
    
    //This particle is turned on when the event is interacted with in a specific way (differs for each event)
    protected ParticleEntity standardParticle;

    //Does this event send a sync packet to client every engine tick?
    //Default is no with the exception of moving platforms and connected events. (+specifically chosen events in the map, like nasu)
    protected boolean synced = false;
    
    //will the event not be drawn when off screen?
    private boolean cullable = true;
    
	/**
	 * Constructor for permanent events.
	 */
	public Event(PlayState state, Vector2 startPos, Vector2 size) {
		super(state, startPos, size);
		this.temporary = false;
		this.duration = 0;
	}
	
	/**
	 * Constructor for temporary events.
	 */
	public Event(PlayState state, Vector2 startPos, Vector2 size, float duration) {
		super(state, startPos, size);
		this.temporary = true;
		this.duration = duration;
	}
	
	/**
	 * Constructor for events that do not take up space.
	 */
	public Event(PlayState state) {
		super(state, new Vector2(), new Vector2(1, 1));
		this.temporary = false;
		this.duration = 0;
	}
	
	@Override
	public void create() {}

	@Override
	public void controller(float delta) {
		if (temporary) {
			duration -= delta;
			if (duration <= 0) {
				if (state.isServer()) {
					this.queueDeletion();
				} else {
					((ClientState) state).removeEntity(entityID.toString());
				}
			}
		}
	}

	private final Vector2 entityLocation = new Vector2();
	@Override
	public void render(SpriteBatch batch) {
		
		if (eventSprite != null) {
			entityLocation.set(getPixelPosition());
			switch (scaleAlign) {
			case CENTER:
				batch.draw(eventSprite.getKeyFrame(animationTime),
						entityLocation.x - spriteWidth * scale / 2,
						entityLocation.y - spriteHeight * scale / 2,
	                    spriteWidth * scale / 2, spriteHeight * scale / 2,
	                    spriteWidth * scale, spriteHeight * scale, 1, 1, 0);
				break;
			case CENTER_STRETCH:
				batch.draw(eventSprite.getKeyFrame(animationTime),
						entityLocation.x - size.x / 2,
						entityLocation.y - size.y / 2,
	                    size.x / 2, size.y / 2,
	                    size.x, size.y, 1, 1, 0);
				break;
			case CENTER_BOTTOM:
				batch.draw(eventSprite.getKeyFrame(animationTime),
						entityLocation.x - spriteWidth * scale / 2,
						entityLocation.y - size.y / 2,
	                    spriteWidth * scale / 2, spriteHeight * scale / 2,
	                    spriteWidth * scale, spriteHeight * scale, 1, 1, 0);
				break;
			case ROTATE:
				batch.draw(eventSprite.getKeyFrame(animationTime),
						entityLocation.x - size.x / 2,
						entityLocation.y - size.y / 2,
						size.x / 2, size.y / 2,
	                    size.x, size.y, 
	                    1, 1, (float) Math.toDegrees(getAngle()));
				break;
			default:
				break;
			}            
		}
	}
	
	@Override
	public boolean isVisible() {
		if (cullable) {
			return super.isVisible();
		} else {
			return true;
		}
	}
	
	/**
	 * This method is run after reading the event from Tiled to load default properties like sync type and align type.
	 * An event with a default property set to false will not run this method
	 */
	public void loadDefaultProperties() {}
	
	public void setStandardParticle(Particle particle) {
		this.standardParticle = new ParticleEntity(state, this, particle, 0, 0, false, particleSyncType.TICKSYNC);
	}

	public ParticleEntity getStandardParticle() { return standardParticle; }

	public void addAmbientParticle(Particle particle) {
		new ParticleEntity(state, this, particle, 0, 0, true, particleSyncType.CREATESYNC);	
	}

	/**
	 * This is used for default animations with multiple frames. The result is a looping animation.
	 * @param sprite: the event's new sprite
	 */
	public void setEventSprite(Sprite sprite) {
		setEventSprite(sprite, false, 0, animationSpeed, PlayMode.LOOP);
	}
	
	/**
	 * Set the sprite of the event
	 * @param sprite: sprite to be set
	 * @param still: do we animate the sprite or not?
	 * @param frame: what frame should be displayed?
	 * @param speed: speed of the animation
	 * @param mode: the playback mode of the animation (does it loop/seesaw/whatever)
	 */
	public void setEventSprite(Sprite sprite, boolean still, int frame, float speed, PlayMode mode) {
		
		this.sprite = sprite;
		animationTime = 0;
		
		if (sprite.equals(Sprite.NOTHING)) {
			this.eventSprite = null;
			return;
		}
		
		if (still) {
			this.eventSprite = new Animation<>(speed, sprite.getFrames().get(frame));
		} else {
			this.eventSprite = new Animation<>(speed, sprite.getFrames());
		}
		this.eventSprite.setPlayMode(mode);
		
		this.spriteWidth = eventSprite.getKeyFrame(0).getRegionWidth();
		this.spriteHeight = eventSprite.getKeyFrame(0).getRegionHeight();
	}
	
	/**
	 * When this event is created, tell the client to create an illusion or event, depending on the syncType
	 * ILLUSION and SERVER only run on the server, so the client just receives an an illusion
	 * USER and ALL can run on all players, so clients need a copy of the event blueprints to make their own version of the event
	 */
	@Override
	public Object onServerCreate() {
		switch(syncType) {
		case ILLUSION:
		case SERVER:
			if (body != null && !sprite.equals(Sprite.NOTHING)) {
				return new Packets.CreateEntity(entityID.toString(), size, getPixelPosition(), getAngle(), sprite, synced, ObjectSyncLayers.STANDARD, scaleAlign);
			} else {
				return null;
			}
		case USER:
		case ALL:
			return new Packets.CreateEvent(entityID.toString(), blueprint, synced);
		default:
			return null;
		}
	}
	
	@Override
	public Object onServerDelete() {
		if (synced) {
			return new Packets.DeleteEntity(entityID.toString(), state.getTimer());
		} else {
			return null;
		}
	}
	
	@Override
	public void onServerSync() {
		if (synced) {
			super.onServerSync();
		}
	}
	
	@Override
	public HadalData getHadalData() { return eventData; }
	
	public EventData getEventData() { return eventData; }

	public void setEventData(EventData eventData) {	this.eventData = eventData;	}

	public Event getConnectedEvent() { return connectedEvent; }

	public void setConnectedEvent(Event connectedEvent) { this.connectedEvent = connectedEvent; }
	
	public float getGravity() {	return gravity; }

	public void setGravity(float gravity) {	this.gravity = gravity;	}
	
	public void setScale(float scale) {	this.scale = scale;	}

	public void setScaleAlign(String scaleAlign) { this.scaleAlign = alignType.valueOf(scaleAlign); }

	public eventSyncTypes getSyncType() { return syncType; }

	public void setSyncType(eventSyncTypes syncType) { this.syncType = syncType; }

	public void setSynced(boolean synced) {	this.synced = synced; }

	public void setCullable(boolean cullable) {	this.cullable = cullable; }
	
	public MapObject getBlueprint() { return blueprint; }

	public void setBlueprint(MapObject blueprint) {	this.blueprint = blueprint; }

	public enum eventSyncTypes {
		ILLUSION,
		USER,
		ALL,
		SERVER
	}
}
