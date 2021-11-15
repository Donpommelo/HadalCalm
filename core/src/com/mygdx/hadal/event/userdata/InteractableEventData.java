package com.mygdx.hadal.event.userdata;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;

/**
 * Interactable events can be interacted with by the player. They should only touch the player.
 * When the player is touching one, pressing the interact button will activate its activate method.
 * @author Phincisco Pleffonso
 */
public class InteractableEventData extends EventData {

	public InteractableEventData(Event event) {
		super(event);
	}
	
	@Override
	public void onTouch(HadalData fixB) {
		if (fixB != null) {	
			if (fixB instanceof PlayerBodyData playerData) {
				playerData.getPlayer().setCurrentEvent(event);
			}
		}
		super.onTouch(fixB);
	}
	
	@Override
	public void onRelease(HadalData fixB) {
		if (fixB != null) {
			if (fixB instanceof PlayerBodyData playerData) {
				if (playerData.getPlayer().getCurrentEvent() == event) {
					playerData.getPlayer().setCurrentEvent(null);
				}
			}
		}
		super.onRelease(fixB);
	}
}
