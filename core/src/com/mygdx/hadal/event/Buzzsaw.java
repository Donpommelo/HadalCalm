package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * This event damages all schmucks inside of it. It can be spawned as a hazard in a map. 
 * 
 * Triggered Behavior: Toggle whether the poison is on or off
 * Triggering Behavior: N/A
 * 
 * Fields:
 * damage: float damage per 1/60f done by this event
 * startOn: boolean of whether this event starts on or off. Optional. Default: true.
 * 
 * @author Zachary Tu
 *
 */
public class Buzzsaw extends Event {
	
	private float controllerCount = 0;
	
	//Damage done by the saw
	private float dps;
	
	//who does this saw damage?
	private short filter;
	
	//angle the saw is drawn at. Used to make saw spin
	private float angle;
	private final static float spinSpeed = 7.5f;
	private final static float damageInterval = 1 / 60f;
	
	public Buzzsaw(PlayState state, Vector2 startPos, Vector2 size, float dps, short filter) {
		super(state,  startPos, size);
		this.dps = dps;
		this.filter = filter;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this);
		
		this.body = BodyBuilder.createBox(world, startPos, size, 0, 0, 0, false, false, Constants.BIT_SENSOR, (short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY), filter, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		controllerCount += delta;
		
		while (controllerCount >= damageInterval) {
			controllerCount -= damageInterval;
			
			for (HadalEntity entity : eventData.getSchmucks()) {
				if (entity instanceof Schmuck) {
					((Schmuck)entity).getBodyData().receiveDamage(dps, new Vector2(), state.getWorldDummy().getBodyData(), true);
				}
			}
		}
		
		angle += spinSpeed;
	}
	
	/**
	 * Client Poison should randomly spawn poison particles itself to avoid overhead.
	 */
	@Override
	public void clientController(float delta) {
		angle += spinSpeed;
	}
	
	@Override
	public void render(SpriteBatch batch) {
		batch.draw((TextureRegion) eventSprite.getKeyFrame(animationTime),
				getPixelPosition().x - size.x / 2,
				getPixelPosition().y - size.y / 2,
                size.x / 2, size.y / 2,
                size.x, size.y, 1, 1, angle);
	}
	
	@Override
	public void loadDefaultProperties() {
		setSyncType(eventSyncTypes.ALL);
		setEventSprite(Sprite.BUZZSAW);
	}
}
