package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

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
	
	/**
	 * Constructor for permanent events.
	 */
	public Event(PlayState state, World world, OrthographicCamera camera, RayHandler rays, String name,
			int width, int height, int x, int y) {
		super(state, world, camera, rays, width, height, x, y);
		this.name = name;
		this.temporary = false;
		this.duration = 0;
	}
	
	/**
	 * Constructor for temporary events.
	 */
	public Event(PlayState state, World world, OrthographicCamera camera, RayHandler rays, String name,
			int width, int height, int x, int y, float duration) {
		super(state, world, camera, rays, width, height, x, y);
		this.name = name;
		this.temporary = true;
		this.duration = duration;
	}
	
	@Override
	public void create() {

	}

	@Override
	public void controller(float delta) {
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
		if (body != null) {
			batch.setProjectionMatrix(state.hud.combined);
			Vector3 bodyScreenPosition = new Vector3(body.getPosition().x, body.getPosition().y, 0);
			camera.project(bodyScreenPosition);
			state.font.draw(batch, getText(), bodyScreenPosition.x, bodyScreenPosition.y);
		}
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
}
