package com.mygdx.hadal.event.utility;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

/**
 * The SpriteChanger changes the sprite of a connected event
 * 
 * Triggered Behavior: When triggered, this event changes some property of the connected event's sprite.
 * Triggering Behavior: N/A. This is the event whose sprite we wish to change
 * 
 * Fields:
 * 
 * sprite: name of the new sprite
 * still: boolean. will the new sprite be animated or not?
 * frame: If the sprite is still, which frame?
 * Align: how is the event aligned? default 0; centered on the point and resized to fit the area
 * Scale: float. how big is the new sprite?
 * 
 * @author Zachary Tu
 *
 */
public class SpriteChanger extends Event {

	private static final String name = "Sprite Changer";

	private String newSprite;
	private boolean still;
	private int frame;
	private int align;
	private float scale;
	
	public SpriteChanger(PlayState state, String sprite, boolean still, int frame, int align, float scale) {
		super(state, name);
		this.newSprite = sprite;
		this.still = still;
		this.frame = frame;
		this.align = align;
		this.scale = scale;
	}
	
	@Override
	public void create() {
		
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator) {
				if (event.getConnectedEvent() != null) {
					event.getConnectedEvent().setEventSprite(newSprite, still, frame);
					
					if (align != -1) {
						event.getConnectedEvent().setScaleAlign(align);
					}
					
					if (scale != -1) {
						event.getConnectedEvent().setScale(scale);
					}
				}
			}
		};
	}
}
