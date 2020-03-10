package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
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
 *
 */
public class Spring extends Event {
	
	//The vector of force that will be applied to any touching entity.
	private Vector2 vec;

	public Spring(PlayState state, Vector2 startPos, Vector2 size, Vector2 vec) {
		super(state, startPos, size);
		this.vec = vec;
	}
	
	public Spring(PlayState state, Vector2 startPos, Vector2 size, Vector2 vec, float duration) {
		super(state, startPos, size, duration);
		this.vec = vec;
		
		setEventSprite(Sprite.SPRING);
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onTouch(HadalData fixB) {
				if (fixB != null) {
					fixB.getEntity().pushMomentumMitigation(vec.x, vec.y);
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, Constants.BIT_SENSOR, (short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE),
				(short) 0, true, eventData);
	}	
	
	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.SPRING);
	}
}
