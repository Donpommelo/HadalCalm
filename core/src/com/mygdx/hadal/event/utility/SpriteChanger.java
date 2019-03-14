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

	private Sprite newSprite;
	private String mode;
	private boolean still;
	private int frame;
	private float speed;
	private int align;
	private float scale;
	
	public SpriteChanger(PlayState state, String sprite, String mode, boolean still, int frame, float speed, int align, float scale) {
		super(state, name);
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
