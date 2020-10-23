package com.mygdx.hadal.event.saves;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.save.UnlockManager;
import com.mygdx.hadal.save.UnlockManager.UnlockType;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * ItemUnlocker unlocks a specified equip, artifact, active item, level or character
 * 
 * Fields:
 * type: type of item to unlock (EQUIP, ARTIFACT, ACTIVE, CHARACTER, LEVEL)
 * item: String name of the item to unlock
 * @author Trurdelia Tabanero
 */
public class ItemUnlocker extends Event {

	private final UnlockType type;
	private final String item;
	
	public ItemUnlocker(PlayState state, String type, String item) {
		super(state);
		this.type = UnlockType.valueOf(type);
		this.item = item;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				UnlockManager.setUnlock(state, type, item, true);
			}
		};
	}
}
