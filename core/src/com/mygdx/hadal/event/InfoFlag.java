package com.mygdx.hadal.event;

import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * An info flag displays text whe nthe player walks over it. This is a temporary means of information until more sophisticated ui is done
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
public class InfoFlag extends Event {

	private static final String name = "Current";

	private String text;
	
	public InfoFlag(PlayState state, int width, int height, int x, int y, String text) {
		super(state, name, width, height, x, y);
		this.text = text;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this);
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),	(short) 0, true, eventData);
	}
	
	@Override
	public String getText() {
		if (eventData.getSchmucks().isEmpty()) {
			return "";
		} else {
			return text;
		}
	}

}
