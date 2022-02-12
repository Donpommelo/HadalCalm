package com.mygdx.hadal.event.saves;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.save.UnlockManager;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * An UnlockChecker event. This checks to see if a specified item is locked/unlocked. If so, activate the next item in the chain
 * 
 * Triggered Behavior: When triggered, this event checks a quest flag. If the item unlock status matches, chain to...
 * Triggering Behavior: If the quest is at the specified point, the next event is activated,
 *
 * Fields:
 * 
 * type: String UnlockType of the unlock, (EQUIP, ARTIFACT, ACTIVE, CHARACTER, LEVEL)
 * item: String id of item unlock checked
 * unlock: we chain to the next event if this is equal to the item's unlock status. Default: false
 * 
 * @author Hinklin Hadragon
 */
public class UnlockChecker extends Event {

	private final String item;
	private final UnlockType type;
	private final boolean unlock;
	
	public UnlockChecker(PlayState state, String type, String item, boolean unlock) {
		super(state);
		this.item = item;
		this.unlock = unlock;
		this.type = UnlockType.valueOf(type);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (UnlockManager.checkUnlock(state, type, item) == unlock && event.getConnectedEvent() != null) {
					event.getConnectedEvent().getEventData().preActivate(this, p);
				}
			}
		};
	}
}
