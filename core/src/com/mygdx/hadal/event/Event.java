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
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.ClientState.ObjectSyncLayers;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * An Event is an entity that acts as a catch-all for all misc entities that do not share qualities with schmucks or hitboxes.
 * Events include hp/fuel/weapon pickups, currents, schmuck spawners, springs, literally anything else.
 * @author Zachary Tu
 *
 */
public class Event extends HadalEntity {
	
	//The event's data
	protected EventData eventData;
	
	//The event's name
	private String name;
	
	//If this event triggers another event, this is a local reference to it
	private Event connectedEvent;

	//Whether the event will despawn after time.
	private boolean temporary;
	private float duration;
	
	//Is this event affected by gravity?
	protected float gravity = 0.0f;
	
	//Event sprite and rendering information
	private Sprite sprite;
	private Animation<TextureRegion> eventSprite;
	private int spriteWidth;
	private int spriteHeight;
	private float scale = 0.25f;
   
	private alignType scaleAlign = alignType.CENTER_BOTTOM;
    
    /* How will this event be synced?
     * ILLUSION: Create a client illusion if it has a body
     * USER: Create this event for clients. When activated on server, activate it for the user who activated it.
     * ALL: Create this event for clients. When activated on server, activate it for all players.
     * SERVER: Create this event for clients. When activated on server, activate it for the server only.
     */
    private eventSyncTypes syncType = eventSyncTypes.ILLUSION;
	
    private final static float animationSpeed = 0.8f;
    
    public final static int defaultPickupEventSize = 96;
    
    protected MapObject blueprint;
    
    protected ParticleEntity standardParticle;

    //Does this event send a sync packet to client every engine tick?
    //Default is no with the exception of moving platforms and connected events. (+specifically chosen events in the map, like nasu)
    private boolean synced = false;
    
	/**
	 * Constructor for permanent events.
	 */
	public Event(PlayState state, String name, int width, int height, int x, int y) {
		super(state, width, height, x, y);
		this.name = name;
		this.temporary = false;
		this.duration = 0;
	}
	
	/**
	 * Constructor for temporary events.
	 */
	public Event(PlayState state, String name, int width, int height, int x, int y, float duration) {
		super(state, width, height, x, y);
		this.name = name;
		this.temporary = true;
		this.duration = duration;
	}
	
	/**
	 * Constructor for events that do not take up space.
	 */
	public Event(PlayState state, String name) {
		super(state, 1, 1, 0, 0);
		this.name = name;
		this.temporary = false;
		this.duration = 0;
	}
	
	@Override
	public void create() {

	}

	@Override
	public void controller(float delta) {
		
		increaseAnimationTime(delta);
		
		if (temporary) {
			duration -= delta;
			if (duration <= 0) {
				this.queueDeletion();
			}
		}
	}

	/**
	 * Tentatively, we want to display the event's name information next to the event
	 */
	@Override
	public void render(SpriteBatch batch) {
		
		if (eventSprite != null) {

			switch (scaleAlign) {
			case CENTER:
				batch.draw((TextureRegion) eventSprite.getKeyFrame(animationTime),
	                    getPosition().x * PPM - spriteWidth * scale / 2,
	                    getPosition().y * PPM - spriteHeight * scale / 2,
	                    spriteWidth * scale / 2, spriteHeight * scale / 2,
	                    spriteWidth * scale, spriteHeight * scale, 1, 1, 0);
				break;
			case CENTER_STRETCH:
				batch.draw((TextureRegion) eventSprite.getKeyFrame(animationTime),
	                    getPosition().x * PPM - width / 2,
	                    getPosition().y * PPM - height / 2,
	                    width / 2, height / 2,
	                    width, height, 1, 1, 0);
				break;
			case CENTER_BOTTOM:
				batch.draw((TextureRegion) eventSprite.getKeyFrame(animationTime),
	                    getPosition().x * PPM - spriteWidth * scale / 2,
	                    getPosition().y * PPM - height / 2,
	                    spriteWidth * scale / 2, spriteHeight * scale / 2,
	                    spriteWidth * scale, spriteHeight * scale, 1, 1, 0);
				break;
			default:
				break;
			}            
		}
	}
	
	public void loadDefaultProperties() {
		
	}
	
	public void setStandardParticle(Particle particle) {
		this.standardParticle = new ParticleEntity(state, this, particle, 0, 0, false, particleSyncType.TICKSYNC);
	}

	public ParticleEntity getStandardParticle() {
		return standardParticle;
	}

	public void addAmbientParticle(Particle particle) {
		new ParticleEntity(state, this, particle, 0, 0, true, particleSyncType.TICKSYNC);	
	}
	
	@Override
	public HadalData getHadalData() {
		return eventData;
	}
	
	public EventData getEventData() {
		return eventData;
	}

	public void setEventData(EventData eventData) {
		this.eventData = eventData;
	}

	public String getText() {
		return name;
	}

	public Event getConnectedEvent() {
		return connectedEvent;
	}

	public void setConnectedEvent(Event connectedEvent) {
		this.connectedEvent = connectedEvent;
	}
	
	public float getGravity() {
		return gravity;
	}

	public void setGravity(float gravity) {
		this.gravity = gravity;
	}

	/**
	 * This is used for default animations with multiple frames. The result is a looping animation.
	 * @param sprite
	 */
	public void setEventSprite(Sprite sprite) {
		setEventSprite(sprite, false, 0, animationSpeed, PlayMode.LOOP);
	}
	
	public void setEventSprite(Sprite sprite, boolean still, int frame, float speed, PlayMode mode) {
		
		this.sprite = sprite; 
		
		if (still) {
			this.eventSprite = new Animation<TextureRegion>(speed, sprite.getFrames().get(frame));
		} else {
			this.eventSprite = new Animation<TextureRegion>(speed, sprite.getFrames());
		}
		this.eventSprite.setPlayMode(mode);
		
		animationTime = 0;

		this.spriteWidth = eventSprite.getKeyFrame(0).getRegionWidth();
		this.spriteHeight = eventSprite.getKeyFrame(0).getRegionHeight();
	}
	
	/**
	 * When this event is created, tell the client to create an illusion or event, depending on the syncType
	 */
	@Override
	public Object onServerCreate() {
		switch(syncType) {
		case ILLUSION:
			if (body != null) {
				return new Packets.CreateEntity(entityID.toString(), new Vector2(width, height), getPosition().scl(PPM), sprite, ObjectSyncLayers.STANDARD, scaleAlign);
			} else {
				return null;
			}
		case USER:
		case ALL:
		case SERVER:
			return new Packets.CreateEvent(entityID.toString(), blueprint);
		default:
			return null;
		}
	}
	
	@Override
	public void onServerSync() {
		if (synced) {
			super.onServerSync();
		}
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}

	public void setScaleAlign(String scaleAlign) {
		this.scaleAlign = alignType.valueOf(scaleAlign);
	}

	public eventSyncTypes getSyncType() {
		return syncType;
	}

	public void setSyncType(eventSyncTypes syncType) {
		this.syncType = syncType;
	}

	public boolean isSynced() {
		return synced;
	}

	public void setSynced(boolean synced) {
		this.synced = synced;
	}

	public MapObject getBlueprint() {
		return blueprint;
	}

	public void setBlueprint(MapObject blueprint) {
		this.blueprint = blueprint;
	}

	public enum eventSyncTypes {
		ILLUSION,
		USER,
		ALL,
		SERVER
	}
}
