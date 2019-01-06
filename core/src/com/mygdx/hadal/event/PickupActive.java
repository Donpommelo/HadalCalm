package com.mygdx.hadal.event;

import java.util.ArrayList;

import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.UnlocktoItem;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * This event, when interacted with, will give the player a new active item.
 * If the player's slots are full, this will replace currently held active item.
 * 
 * Triggered Behavior: When triggered, this event is toggled on/off to unlock/lock pickup
 * Triggering Behavior: This event will trigger its connected event when picked up.
 * 
 * Fields:
 * pool: String, comma separated list of equipunlock enum names of all items that could appear here.
 * 	if this is equal to "", return any weapon in the random pool.
 * startOn: boolean of whether the event starts on or off. Optiona;. Default: True.
 * 
 * @author Zachary Tu
 *
 */
public class PickupActive extends Event {

	//This is the weapon that will be picked up when interacting with this event.
	private ActiveItem item;
	
	private static final String name = "Item Pickup";

	//Can this event be interacted with atm?
	private boolean on;
	
	public PickupActive(PlayState state, int width, int height, int x, int y, String pool) {
		super(state, name, width, height, x, y);
		this.on = true;
		
		//Set this pickup to a random weapon in the input pool
		item = UnlocktoItem.getUnlock(UnlockActives.valueOf(getRandItemFromPool(pool)), null);
	}
	
	@Override
	public void create() {
		this.eventData = new InteractableEventData(this) {
			
			@Override
			public void onInteract(Player p) {
				if (isAlive() && on) {
					
					//If player inventory is full, replace their current weapon.
					item.setUser(p);
					ActiveItem temp = p.getPlayerData().pickup(item);
					
					//If the player picks this up without dropping anything, delete this event.
					if (temp == null) {
						queueDeletion();
					} else {
						
						//Otherwise set its weapon to the dropped weapon.
						item = temp;
					}
					
					if (event.getConnectedEvent() != null) {
						event.getConnectedEvent().getEventData().onActivate(this);
					}
				}
			}
			
			@Override
			public void onActivate(EventData activator) {
				on = !on;
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),	(short) 0, true, eventData);
	}
	
	/**
	 * This method returns the name of a weapon randomly selected from the pool.
	 * @param pool: comma separated list of names of weapons to choose from. if set to "", return any weapon in the random pool.
	 * @return
	 */
	public static String getRandItemFromPool(String pool) {
		
		if (pool.equals("")) {
			return UnlockActives.getUnlocks(false, UnlockTag.RANDOM_POOL)
					.get(GameStateManager.generator.nextInt(UnlockActives.getUnlocks(false, UnlockTag.RANDOM_POOL).size)).name();
		}
		
		ArrayList<String> weapons = new ArrayList<String>();
		
		for (String id : pool.split(",")) {
			weapons.add(id);
		}
		return weapons.get(GameStateManager.generator.nextInt(weapons.size()));
	}

	@Override
	public String getText() {
		if (on) {
			return item.getName() + " (E TO TAKE)";
		} else {
			return item.getName() + ": LOCKED";
		}
	}

}
