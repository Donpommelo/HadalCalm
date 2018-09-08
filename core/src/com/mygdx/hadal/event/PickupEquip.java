package com.mygdx.hadal.event;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.mods.WeaponMod;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockManager.ModTag;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.WeaponModifier;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.UnlocktoItem;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * This event, when interacted with, will give the player a new weapon.
 * If the player's slots are full, this will replace currently held weapon.
 * 
 * Triggered Behavior: When triggered, this event is toggled on/off to unlock/lock pickup
 * Triggering Behavior: This event will trigger its connected event when picked up.
 * 
 * Fields:
 * pool: String, comma separated list of equipunlock enum names of all equips that could appear here.
 * 	if this is equal to "", return any weapon in the random pool.
 * startOn: boolean of whether the event starts on or off. Optiona;. Default: True.
 * 
 * @author Zachary Tu
 *
 */
public class PickupEquip extends Event {

	//This is the weapon that will be picked up when interacting with this event.
	private Equipable equip;
	
	private static final String name = "Equip Pickup";

	//Can this event be interacted with atm?
	private boolean on;
	
	private ArrayList<WeaponMod> mods;

	//Is the player standing in this event? Will display extra info
	protected boolean open;

	public PickupEquip(PlayState state, int width, int height, int x, int y, int modPow, String pool) {
		super(state, name, width, height, x, y);
		this.on = true;
		
		//Set this pickup to a random weapon in the input pool
		equip = UnlocktoItem.getUnlock(UnlockEquip.valueOf(getRandWeapFromPool(pool)), null);
		
		mods = new ArrayList<WeaponMod>();
		mods.addAll(PickupWeaponMod.getRandMods(modPow, ModTag.RANDOM_POOL));
	}
	
	@Override
	public void create() {
		this.eventData = new InteractableEventData(this) {
			
			@Override
			public void onInteract(Player p) {
				if (isAlive() && on) {
					
					//If player inventory is full, replace their current weapon.
					equip.getWeaponMods().clear();
					Equipable temp = p.getPlayerData().pickup(equip);
					
					for (WeaponMod mod : mods) {
						mod.acquireMod(p.getBodyData(), state, p.getPlayerData().getCurrentTool());
					}
					mods.clear();
					
					//If the player picks this up without dropping anything, delete this event.
					if (temp == null) {
						queueDeletion();
					} else {
						
						//Otherwise set its weapon to the dropped weapon.
						equip = temp;
						setEventSprite(equip.getEventSpriteId());
						
						for (WeaponModifier mod : equip.getWeaponMods()) {
							mods.add(mod.getConstantMod());
						}
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
	public static String getRandWeapFromPool(String pool) {
		
		if (pool.equals("")) {
			return UnlockEquip.getUnlocks(false, UnlockTag.RANDOM_POOL)
					.get(new Random().nextInt(UnlockEquip.getUnlocks(false, UnlockTag.RANDOM_POOL).size)).name();
		}
		
		ArrayList<String> weapons = new ArrayList<String>();
		
		for (String id : pool.split(",")) {
			weapons.add(id);
		}
		return weapons.get(new Random().nextInt(weapons.size()));
	}

	@Override
	public void controller(float delta) {
		if (open && eventData.getSchmucks().isEmpty()) {
			open = false;
		}
		if (!open && !eventData.getSchmucks().isEmpty()) {
			open = true;
		}
	}
	
	@Override
	public void render(SpriteBatch batch) {
		super.render(batch);
		
		if (open) {
			batch.setProjectionMatrix(state.sprite.combined);
			state.font.getData().setScale(1.0f);
			float y = body.getPosition().y * PPM + height / 2;
			for (WeaponMod mod : mods) {
				state.font.draw(batch, mod.getName(), body.getPosition().x * PPM - width / 2, y);
				y += 15;
			}
			state.font.draw(batch, equip.getName(), body.getPosition().x * PPM - width / 2, y);
		}
	}
	
	public Equipable getEquip() {
		return equip;
	}

	public ArrayList<WeaponMod> getMods() {
		return mods;
	}

	@Override
	public void loadDefaultProperties() {
		setEventSprite(equip.getEventSpriteId());
	}

}
