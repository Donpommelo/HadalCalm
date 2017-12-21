package com.mygdx.hadal.event.userdata;

import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;

public class InteractableEventData extends EventData {

	public InteractableEventData(World world, Event event) {
		super(world, event);
	}
	
	public void onTouch(HadalData fixB) {
		if (fixB != null) {	
			if (fixB.getType().equals(UserDataTypes.BODY)) {
				((PlayerBodyData)fixB).player.currentEvent = event;
			}
		}
		super.onTouch(fixB);
	}
	
	public void onRelease(HadalData fixB) {
		if (fixB != null) {
			if (fixB.getType().equals(UserDataTypes.BODY)) {
				if (((PlayerBodyData)fixB).player.currentEvent == event) {
					((PlayerBodyData)fixB).player.currentEvent = null;
				}
			}
		}
		super.onRelease(fixB);
	}
	
	public void onInteract(Player p) {

	}

}
