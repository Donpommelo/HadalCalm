package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * Pushers apply a push to another event or the player if there are no connected events
 * <p>
 * Triggered Behavior: When triggered, apply a push to the target
 * Triggering Behavior: If existent, the event that a push is applied to. 
 * <p>
 * Fields:
 * xPush: float x component of push applied. Default: 0.0f
 * yPush: float y component of push applied. Default: 0.0f
 * 
 * @author Twiduh Trurlotte
 */
public class Pusher extends Event {

	private final Vector2 push = new Vector2();
	
	public Pusher(PlayState state, float xPush, float yPush) {
		super(state);
		this.push.set(xPush, yPush);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (event.getConnectedEvent() == null) {
					if (p != null) {
						p.push(push);
					}
				} else {
					event.getConnectedEvent().push(push);
				}
			}
		};
	}
}
