package com.mygdx.hadal.event;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.server.EventDto;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * A Spring is an event that, when touched, will push an entity in a set direction
 * 
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * 
 * Fields:
 * vec: vector2 of force that is applied to entities that touch this.
 * 
 * @author Zachary Tu
 */
public class Spring extends Event {
	
	//The vector of force that will be applied to any touching entity.
	private final Vector2 vec = new Vector2();

	//this is the frequency that the spring sound can play
	private static final float procCd = 0.25f;
	private float procCdCount = procCd;

	public Spring(PlayState state, Vector2 startPos, Vector2 size, Vector2 vec) {
		super(state, startPos, size);
		this.vec.set(vec);
	}
	
	public Spring(PlayState state, Vector2 startPos, Vector2 size, Vector2 vec, float duration) {
		super(state, startPos, size, duration);
		this.vec.set(vec);
		setEventSprite(Sprite.SPRING);
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onTouch(HadalData fixB) {
				if (fixB != null) {
					fixB.getEntity().pushMomentumMitigation(vec.x, vec.y);
					
					if (procCdCount >= procCd) {
						procCdCount = 0;
						
						SoundEffect.SPRING.playUniversal(state, getPixelPosition(), 0.25f, false);
						new ParticleEntity(state, getPixelPosition(), Particle.MOMENTUM, 1.0f, true, particleSyncType.CREATESYNC);
					}
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, Constants.BIT_SENSOR, (short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE),
				(short) 0, true, eventData);
	}	
	
	@Override
	public void controller(float delta) {
		super.controller(delta);
		
		if (procCdCount < procCd) {
			procCdCount += delta;
		}
	}
	
	@Override
	public void clientController(float delta) {
		super.controller(delta);
	}
	
	@Override
	public Object onServerCreate() {
		if (blueprint == null) {
			blueprint = new RectangleMapObject(getPixelPosition().x - size.x / 2, getPixelPosition().y - size.y / 2, size.x, size.y);
			blueprint.setName("SpringTemp");
			blueprint.getProperties().put("springX", vec.x);
			blueprint.getProperties().put("springY", vec.y);
			blueprint.getProperties().put("duration", duration);
		}
		return new Packets.CreateEvent(entityID.toString(), new EventDto(blueprint), synced);
	}
	
	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.SPRING);
	}
}
