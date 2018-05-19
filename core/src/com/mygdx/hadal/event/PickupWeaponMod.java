package com.mygdx.hadal.event;

import java.util.ArrayList;
import java.util.Random;

import com.mygdx.hadal.equip.mods.WeaponMod;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.save.UnlockManager.ModTag;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * TODO
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
		this.mod = WeaponMod.valueOf(getRandModFromPool(pool));
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

	public static String getRandModFromPool(String pool) {
		
		if (pool.equals("")) {
			return WeaponMod.getUnlocks(ModTag.RANDOM_POOL)
					.get(new Random().nextInt(WeaponMod.getUnlocks(ModTag.RANDOM_POOL).size)).name();
		}
		
		ArrayList<String> mods = new ArrayList<String>();
		
		for (String id : pool.split(",")) {
			mods.add(id);
		}
		return mods.get(new Random().nextInt(mods.size()));
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
