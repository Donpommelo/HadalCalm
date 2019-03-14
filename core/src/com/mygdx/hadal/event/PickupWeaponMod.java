package com.mygdx.hadal.event;

import java.util.ArrayList;

import com.mygdx.hadal.equip.mods.WeaponMod;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockManager.ModTag;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * This event, when interacted with, will give the player a new weapon mod for their currently held weapon.
 * 
 * Triggered Behavior: When triggered, this event is toggled on/off to unlock/lock pickup
 * Triggering Behavior: This event will trigger its connected event when picked up.
 * 
 * Fields:
 * pool: String, comma separated list of equipunlock enum names of all items that could appear here.
 * 	if this is equal to "", return any weapon mod in the random pool.
 * startOn: boolean of whether the event starts on or off. Optiona;. Default: True.
 * 
 * @author Zachary Tu
 *
 */
public class PickupWeaponMod extends Event {

	//This is the weapon that will be picked up when interacting with this event.
	private WeaponMod mod;
	
	private static final String name = "Weapon Mod Pickup";

	//Can this event be interacted with atm?
	private boolean on;

	public PickupWeaponMod(PlayState state, int width, int height, int x, int y, String pool) {
		super(state, name, width, height, x, y);
		this.on = true;
		this.mod = WeaponMod.valueOf(getRandModFromPool(pool, ModTag.RANDOM_POOL));
	}
	
	@Override
	public void create() {
		this.eventData = new InteractableEventData(this) {
			
			@Override
			public void onInteract(Player p) {
				if (isAlive() && on) {
					
					mod.acquireMod(p.getBodyData(), state, p.getPlayerData().getCurrentTool());
					queueDeletion();
					
					if (event.getConnectedEvent() != null) {
						event.getConnectedEvent().getEventData().preActivate(this, p);
					}
				}
			}
			
			@Override
			public void onActivate(EventData activator, Player p) {
				on = !on;
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),	(short) 0, true, eventData);
	}

	public static String getRandModFromPool(String pool, ModTag... tags) {
		
		if (pool.equals("")) {
			return WeaponMod.getUnlocks(tags)
					.get(GameStateManager.generator.nextInt(WeaponMod.getUnlocks(tags).size)).name();
		}
		
		ArrayList<String> mods = new ArrayList<String>();
		
		for (String id : pool.split(",")) {
			mods.add(id);
		}
		return mods.get(GameStateManager.generator.nextInt(mods.size()));
	}
	
	public static ArrayList<WeaponMod> getRandMods(int modPow, ModTag... tags) {
		
		ArrayList<WeaponMod> mods = new ArrayList<WeaponMod>();
		int modPowLeft = modPow;
		
		while (modPowLeft > 0) {
			WeaponMod newMod = WeaponMod.valueOf(getRandModFromPool("", tags));
			if (newMod.getWeight() <= modPowLeft) {
				mods.add(newMod);
				modPowLeft -= newMod.getWeight();
			}
		}
		
		return mods;
	}
	
	@Override
	public String getText() {
		if (on) {
			return mod.toString() + " (E TO TAKE)";
		} else {
			return mod.toString() + ": LOCKED";
		}
	}
}
