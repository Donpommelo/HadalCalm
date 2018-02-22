package com.mygdx.hadal.event.userdata;

import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;

/**
 * Interactable events can be interacted with by the player. They should only touch the player.
 * When the player is touching one, pressing the interact button will activate its activate method.
 * @author Zachary Tu
 *
 */
public class InteractableEventData extends EventData {

	public InteractableEventData(World world, Event event) {
		super(world, event);
	}
	
	@Override
	public void onTouch(HadalData fixB) {
		if (fixB != null) {	
			if (fixB.getType().equals(UserDataTypes.BODY)) {
				((PlayerBodyData)fixB).getPlayer().setCurrentEvent(event);
			}
		}
		super.onTouch(fixB);
	}
	
	@Override
	public void onRelease(HadalData fixB) {
		if (fixB != null) {
			if (fixB.getType().equals(UserDataTypes.BODY)) {
				if (((PlayerBodyData)fixB).getPlayer().getCurrentEvent() == event) {
					((PlayerBodyData)fixB).getPlayer().setCurrentEvent(null);
				}
			}
		}
		super.onRelease(fixB);
	}
}
