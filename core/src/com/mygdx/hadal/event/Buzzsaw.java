package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * This event damages all schmucks inside of it. It can be spawned as a hazard in a map. 
 * 
 * Triggered Behavior: N/A but will often be used as an id to attach to a move point
 * Triggering Behavior: N/A
 * 
 * Fields:
 * damage: float damage per 1/60f done by this event
 * filter: hitbox filter of who this event will damage. default: 0 (hits everyone)
 * 
 * @author Phurrault Pognatio
 */
public class Buzzsaw extends Event {

	private static final float SPIN_SPEED = 7.5f;
	private static final float SPRITE_SCALE = 1.4f;

	private float controllerCount;
	
	//Damage done by the saw
	private final float dps;
	
	//who does this saw damage?
	private final short filter;
	
	//angle the saw is drawn at. Used to make saw spin
	private float angle;

	public Buzzsaw(PlayState state, Vector2 startPos, Vector2 size, float dps, short filter) {
		super(state,  startPos, size);
		this.dps = dps;
		this.filter = filter;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this);
		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR,
				(short) (BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY), filter)
				.addToWorld(world);
	}
	
	@Override
	public void controller(float delta) {
		controllerCount += delta;
		
		while (controllerCount >= Constants.INTERVAL) {
			controllerCount -= Constants.INTERVAL;
			
			for (HadalEntity entity : eventData.getSchmucks()) {
				if (entity instanceof Schmuck schmuck) {
					schmuck.getBodyData().receiveDamage(dps, new Vector2(), state.getWorldDummy().getBodyData(), true,
							null, DamageSource.MAP_BUZZSAW, DamageTag.CUTTING);
				}
			}
		}
		angle += SPIN_SPEED;
	}
	
	/**
	 * Client buzz saws should also rotate
	 */
	@Override
	public void clientController(float delta) {
		controller(delta);
		super.clientController(delta);
	}
	
	/**
	 * We draw the sprite a bit larger than normal to make its hitbox feel more generous to players
	 */
	@Override
	public void render(SpriteBatch batch, Vector2 entityLocation) {
		batch.draw(eventSprite.getKeyFrame(animationTime),
				entityLocation.x - size.x / 2 * SPRITE_SCALE,
				entityLocation.y - size.y / 2 * SPRITE_SCALE,
                size.x / 2 * SPRITE_SCALE, size.y / 2 * SPRITE_SCALE,
                size.x * SPRITE_SCALE, size.y * SPRITE_SCALE, 1, 1, angle);
	}
	
	/**
	 * visibility check compensating for the increased sprite size
	 */
	@Override
	public boolean isVisible(Vector2 entityLocation) {
		if (body == null) {
			return false;
		} else {
			return (
					state.getCamera().frustum.pointInFrustum(entityLocation.x + size.x * SPRITE_SCALE / 2, entityLocation.y + size.y * SPRITE_SCALE / 2, 0) ||
					state.getCamera().frustum.pointInFrustum(entityLocation.x - size.x * SPRITE_SCALE / 2, entityLocation.y + size.y * SPRITE_SCALE / 2, 0) ||
					state.getCamera().frustum.pointInFrustum(entityLocation.x + size.x * SPRITE_SCALE / 2, entityLocation.y - size.y * SPRITE_SCALE / 2, 0) ||
					state.getCamera().frustum.pointInFrustum(entityLocation.x - size.x * SPRITE_SCALE / 2, entityLocation.y - size.y * SPRITE_SCALE / 2, 0));
		}
	}
	
	@Override
	public void loadDefaultProperties() {
		setSyncType(eventSyncTypes.ALL);
		setEventSprite(Sprite.BUZZSAW);
	}
}
