package com.mygdx.hadal.event;

import java.util.Arrays;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * This event is a heavy object that doesn't do anything. It is easily pushed by the player airblast.
 * 
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * 
 * Fields:
 * N/A
 * 
 * @author Zachary Tu
 *
 */
public class AirblastableRock extends Event {

	private static final String name = "Rock";

	public AirblastableRock(PlayState state, int width, int height, int x, int y) {
		super(state, name, width, height, x, y);
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this, UserDataTypes.WALL) {
			
			@Override
			public void onTouch(HadalData fixB) {
				//TODO: make this damage if body ismoving fast enough?
			}
			
			@Override
			public void receiveDamage(float basedamage, Vector2 knockback, BodyData perp, Equipable tool, Boolean procEffects, DamageTypes... tags) {
				if (Arrays.asList(tags).contains(DamageTypes.AIR)) {
					event.push(knockback.x * 200, knockback.y * 200);
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 50, 0, false, true, Constants.BIT_WALL, 
				(short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_SENSOR),
				(short) 0, false, eventData);
	}
}
