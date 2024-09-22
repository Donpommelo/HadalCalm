package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.managers.PacketManager;
import com.mygdx.hadal.schmucks.entities.ClientIllusion.alignType;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.server.EventDto;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.PlayState.ObjectLayer;

/**
 * An Event is an entity that acts as a catch-all for all misc entities that do not share qualities with schmucks or hitboxes.
 * Events include hp/fuel/weapon pickups, currents, schmuck spawners, springs, literally anything else.
 * @author Bloburger Blunzo
 */
public class Event extends HadalEntity {

	//speed of animation for events
	private static final float ANIMATION_SPEED = 0.1f;

	//all pickups will have this height and width as default.
	public static final int DEFAULT_PICKUP_EVENT_SIZE = 160;

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
     * IGNORE: Do nothing when activated
	 * SELF: Will activate if activator is own player, otherwise do nothing
	 * USER: Will activate if activator is own player (or null), otherwise echo activation to clients (if server)
	 * ECHO_ACTIVATE: Will activate, then echo activation to clients (or server if client)
	 * ECHO: Will echo activation to clients (or server if client)
	 * ACTIVATE: Will activate
     */
	private eventSyncTypes serverSyncType = eventSyncTypes.ACTIVATE;
	private eventSyncTypes clientSyncType = eventSyncTypes.ACTIVATE;

    //this is the map object from Tiled that this event was read from.
    protected RectangleMapObject blueprint;
    private EventDto dto;
    
    //This particle is turned on when the event is interacted with in a specific way (differs for each event)
    protected ParticleEntity standardParticle;

    protected String triggeredID;

    //Does this event send a sync packet to client every engine tick?
    //Default is no with the exception of moving platforms and connected events. (+specifically chosen events in the map, like nasu)
    protected boolean synced;

    //independent events are created on both client and server and never sync. These are usually static events
    protected boolean independent = true;

    //will the event not be drawn when off screen?
    private boolean cullable = true;

	private float flashLifespan;
	private float flashCount;
    
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
		this.duration = duration;
		this.temporary = true;
		this.independent = false;
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

			if (duration <= flashLifespan && flashLifespan != 0.0f) {
				flashCount -= delta;
				if (flashCount < -Constants.FLASH) {
					flashCount = Constants.FLASH;
				}
			}

			if (duration <= 0) {
				if (state.isServer()) {
					this.queueDeletion();
				} else {
					((ClientState) state).removeEntity(entityID);
				}
			}
		}
	}

	@Override
	public void render(SpriteBatch batch, Vector2 entityLocation) {
		//this makes the event flash when its lifespan is low
		if (flashCount > 0.0f && flashLifespan != 0.0f) { return; }

		if (eventSprite != null) {
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
	                    1, 1, MathUtils.radDeg * getAngle());
				break;
			default:
				break;
			}            
		}
	}
	
	@Override
	public boolean isVisible(Vector2 objectiveLocation) {
		if (cullable) {
			return super.isVisible(objectiveLocation);
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
		this.standardParticle = new ParticleEntity(state, this, particle, 0, 0, false, SyncType.NOSYNC);
		if (!state.isServer()) {
			((ClientState) state).addEntity(standardParticle.getEntityID(), standardParticle, false, ObjectLayer.EFFECT);
		}
	}

	public ParticleEntity getStandardParticle() { return standardParticle; }

	public void addAmbientParticle(Particle particle, float xOffset, float yOffset) {
		ParticleEntity ambientParticle = new ParticleEntity(state, this, particle, 0, 0, true,
				SyncType.NOSYNC);
		ambientParticle.setOffset(xOffset, yOffset);

		if (!state.isServer()) {
			((ClientState) state).addEntity(ambientParticle.getEntityID(), ambientParticle, false, ObjectLayer.EFFECT);
		}
	}

	public void addAmbientParticle(Particle particle) {
		addAmbientParticle(particle, 0, 0);
	}

	/**
	 * This is used for default animations with multiple frames. The result is a looping animation.
	 * @param sprite: the event's new sprite
	 */
	public void setEventSprite(Sprite sprite) {
		setEventSprite(sprite, false, 0, ANIMATION_SPEED, PlayMode.LOOP);
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
		
		if (Sprite.NOTHING.equals(sprite)) {
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
	 * When this event is created, tell the client to create an illusion or event, depending on whether the event has a dto
	 * If independent, never send anything
	 */
	@Override
	public Object onServerCreate(boolean catchup) {

		//independent events do not send a create packet when created, b/c the client creates it themselves
		if (independent) { return null; }
		if (null == dto) {
			if (body != null && !Sprite.NOTHING.equals(sprite)) {
				return new Packets.CreateEntity(entityID, size, getPixelPosition(), getAngle(), sprite,	synced,
						isSyncInstant(), ObjectLayer.STANDARD, scaleAlign);
			} else {
				return null;
			}
		} else {
			return new Packets.CreateEvent(entityID, dto, synced);
		}
	}
	
	@Override
	public Object onServerDelete() {
		if (synced) {
			return new Packets.DeleteEntity(entityID, state.getTimer());
		} else {
			return null;
		}
	}

	@Override
	public void onServerSync() {
		if (synced && body != null && isSyncDefault()) {
			float angle = getAngle();
			if (angle == 0.0f) {
				state.getSyncPackets().add(new PacketsSync.SyncEntity(entityID, getPosition(), getLinearVelocity(),
						state.getTimer()));
			} else {
				state.getSyncPackets().add(new PacketsSync.SyncEntityAngled(entityID, getPosition(), getLinearVelocity(),
						state.getTimer(), angle));
			}
		}
	}

	@Override
	public void onServerSyncFast() {
		if (synced && body != null && isSyncInstant()) {
			float angle = getAngle();
			if (angle == 0.0f) {
				PacketManager.serverUDPAll(state, new PacketsSync.SyncEntity(entityID, getPosition(), getLinearVelocity(),
						state.getTimer()));
			} else {
				PacketManager.serverUDPAll(state, new PacketsSync.SyncEntityAngled(entityID, getPosition(), getLinearVelocity(),
						state.getTimer(), angle));
			}
		}
	}

	public Object onServerSyncInitial() { return null; }

	public void onClientSyncInitial(float timer, Event target, Vector2 position, Vector2 velocity) {}

	@Override
	public HadalData getHadalData() { return eventData; }
	
	public EventData getEventData() { return eventData; }

	public Event getConnectedEvent() { return connectedEvent; }

	public void setConnectedEvent(Event connectedEvent) { this.connectedEvent = connectedEvent; }
	
	public void setGravity(float gravity) {	this.gravity = gravity;	}
	
	public void setScale(float scale) {	this.scale = scale;	}

	public void setScaleAlign(alignType scaleAlign) { this.scaleAlign = scaleAlign; }

	public eventSyncTypes getServerSyncType() { return serverSyncType; }

	public void setServerSyncType(eventSyncTypes serverSyncType) { this.serverSyncType = serverSyncType; }

	public eventSyncTypes getClientSyncType() { return clientSyncType; }

	public void setClientSyncType(eventSyncTypes clientSyncType) { this.clientSyncType = clientSyncType; }

	public void setSynced(boolean synced) {	this.synced = synced; }

	public void setCullable(boolean cullable) {	this.cullable = cullable; }

	public void setIndependent(boolean independent) { this.independent = independent; }

	public RectangleMapObject getBlueprint() { return blueprint; }

	public void setBlueprint(RectangleMapObject blueprint) { this.blueprint = blueprint; }

	public void setDto(EventDto dto) { this.dto = dto; }

	public void setFlashLifespan(float flashLifespan) { this.flashLifespan = flashLifespan; }

	public String getTriggeredID() { return triggeredID; }

	public void setTriggeredID(String triggeredID) { this.triggeredID = triggeredID; }

	public enum eventSyncTypes {
		IGNORE,
		SELF,
		USER,
		ECHO_ACTIVATE,
		ECHO_ACTIVATE_EXCLUDE,
		ECHO,
		ACTIVATE
	}
}
