package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
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
 * mode: string the type of play mode (NORMAL, LOOP, LOOP_PINGPONG etc)
 * still: boolean. will the new sprite be animated or not?
 * frame: If the sprite is still, which frame?
 * speed: animation speed of the sprite
 * align: how is the event aligned? default 0; centered on the point and resized to fit the area
 * scale: float. how big is the new sprite? if -1, don't change the scale
 * 
 * @author Zachary Tu
 */
public class SpriteChanger extends Event {

	private final Sprite newSprite;
	private final String mode;
	private final boolean still;
	private final int frame;
	private final float speed, scale;
	private final String align;

	public SpriteChanger(PlayState state, String sprite, String mode, boolean still, int frame, float speed, String align, float scale) {
		super(state);
		this.newSprite = Sprite.valueOf(sprite);
		this.mode = mode;
		this.still = still;
		this.frame = frame;
		this.speed = speed;
		this.align = align;
		this.scale = scale;
	}
	
	@Override
	public void create() {
		
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (event.getConnectedEvent() != null) {
					event.getConnectedEvent().setEventSprite(newSprite, still, frame, speed, PlayMode.valueOf(mode));
					
					if (!align.equals("NONE")) {
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
