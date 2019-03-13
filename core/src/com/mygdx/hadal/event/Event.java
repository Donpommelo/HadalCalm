package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
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
	
	protected float gravity = 0.0f;
	
	private Animation<TextureRegion> eventSprite;
	private int spriteWidth;
	private int spriteHeight;
	private float scale = 0.25f;
    private int scaleAlign = 0;
	
    private final static float animationSpeed = 0.8f;
    
    protected MapObject blueprint;
    
    protected ParticleEntity standardParticle;
    
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
	 * Events with sprites
	 */
	public Event(PlayState state, String name, int width, int height, int x, int y, String sprite, float scale, int scaleAlign) {
		super(state, width, height, x, y);
		this.name = name;
		this.temporary = false;
		this.duration = 0;
		this.scale = scale;
		this.scaleAlign = scaleAlign;
		
		eventSprite = new Animation<TextureRegion>(animationSpeed, GameStateManager.eventAtlas.findRegions(sprite));
		spriteWidth = eventSprite.getKeyFrame(animationTime).getRegionWidth();
		spriteHeight = eventSprite.getKeyFrame(animationTime).getRegionHeight();
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
			batch.setProjectionMatrix(state.sprite.combined);
			switch (scaleAlign) {
			case 0:
				batch.draw((TextureRegion) eventSprite.getKeyFrame(animationTime),
	                    body.getPosition().x * PPM - width / 2,
	                    body.getPosition().y * PPM - height / 2,
	                    width / 2, height / 2,
	                    width, height, 1, 1, 0);
				break;
			case 1:
				batch.draw((TextureRegion) eventSprite.getKeyFrame(animationTime),
	                    body.getPosition().x * PPM - spriteWidth * scale / 2,
	                    body.getPosition().y * PPM - spriteHeight * scale / 2,
	                    spriteWidth * scale / 2, spriteHeight * scale / 2,
	                    spriteWidth * scale, spriteHeight * scale, 1, 1, 0);
				break;
			case 2:
				batch.draw((TextureRegion) eventSprite.getKeyFrame(animationTime),
	                    body.getPosition().x * PPM - spriteWidth * scale / 2,
	                    body.getPosition().y * PPM - height / 2,
	                    spriteWidth * scale / 2, spriteHeight * scale / 2,
	                    spriteWidth * scale, spriteHeight * scale, 1, 1, 0);
				break;
			case 3:
				batch.draw((TextureRegion) eventSprite.getKeyFrame(animationTime),
	                    body.getPosition().x * PPM - spriteWidth * scale / 2,
	                    body.getPosition().y * PPM + height / 2,
	                    spriteWidth * scale / 2, spriteHeight * scale / 2,
	                    spriteWidth * scale, spriteHeight * scale, 1, 1, 0);
				break;
			case 4:
				batch.draw((TextureRegion) eventSprite.getKeyFrame(animationTime),
	                    body.getPosition().x * PPM - width / 2,
	                    body.getPosition().y * PPM - spriteHeight * scale / 2,
	                    spriteWidth * scale / 2, spriteHeight * scale / 2,
	                    spriteWidth * scale, spriteHeight * scale, 1, 1, 0);
				break;
			case 5:
				batch.draw((TextureRegion) eventSprite.getKeyFrame(animationTime),
	                    body.getPosition().x * PPM + width / 2,
	                    body.getPosition().y * PPM - spriteHeight * scale / 2,
	                    spriteWidth * scale / 2, spriteHeight * scale / 2,
	                    spriteWidth * scale, spriteHeight * scale, 1, 1, 0);
				break;
			}            
		}
		
		if (body != null) {			
			batch.setProjectionMatrix(state.sprite.combined);
			HadalGame.SYSTEM_FONT_SPRITE.getData().setScale(0.60f);
			HadalGame.SYSTEM_FONT_SPRITE.draw(batch, getText(), body.getPosition().x * PPM, body.getPosition().y * PPM);
		}
	}
	
	public void loadDefaultProperties() {
		
	}
	
	public void setStandardParticle(Particle particle) {
		this.standardParticle = new ParticleEntity(state, this, particle, 0, 0, false);
	}

	public ParticleEntity getStandardParticle() {
		return standardParticle;
	}

	public void addAmbientParticle(Particle particle) {
		new ParticleEntity(state, this, particle, 0, 0, true);	
	}

	@Override
	public void queueDeletion() {
		super.queueDeletion();
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
		
		if (still) {
			this.eventSprite = new Animation<TextureRegion>(speed, sprite.getFrames().get(frame));
		} else {
			this.eventSprite = new Animation<TextureRegion>(speed, sprite.getFrames());
		}
		this.eventSprite.setPlayMode(mode);
		
		animationTime = 0;

		this.spriteWidth = eventSprite.getKeyFrame(animationTime).getRegionWidth();
		this.spriteHeight = eventSprite.getKeyFrame(animationTime).getRegionHeight();
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}

	public void setScaleAlign(int scaleAlign) {
		this.scaleAlign = scaleAlign;
	}

	public MapObject getBlueprint() {
		return blueprint;
	}

	public void setBlueprint(MapObject blueprint) {
		this.blueprint = blueprint;
	}
}
