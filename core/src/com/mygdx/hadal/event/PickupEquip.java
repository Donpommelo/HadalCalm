package com.mygdx.hadal.event;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.misc.NothingWeapon;
import com.mygdx.hadal.equip.mods.WeaponMod;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.event.utility.TriggerAlt;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockManager.ModTag;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.Packets;
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
 * modPow: Int: number of mods to spawn with. Optional. Default: 0
 * pool: String, comma separated list of equipunlock enum names of all equips that could appear here.
 * 	if this is equal to "", return any weapon in the random pool.
 * 
 * @author Zachary Tu
 *
 */
public class PickupEquip extends Event {

	//This is the weapon that will be picked up when interacting with this event.
	private Equipable equip;
	private UnlockEquip unlock;
	
	private static final String name = "Equip Pickup";

	private ArrayList<WeaponMod> mods;

	private int modPow;
	private String pool;
	
	public PickupEquip(PlayState state, int x, int y, int modPow, String pool) {
		super(state, name, Event.defaultPickupEventSize, Event.defaultPickupEventSize, x, y);
		this.modPow = modPow;
		this.pool = pool;
		
		rollWeapon();
	}
	
	@Override
	public void create() {
		this.eventData = new InteractableEventData(this) {
			
			@Override
			public void onInteract(Player p) {
				preActivate(null, p);
			}
			
			@Override
			public void onActivate(EventData activator, Player p) {
				
				if (activator != null) {
					if (activator.getEvent() instanceof TriggerAlt) {
						String msg = ((TriggerAlt)activator.getEvent()).getMessage();
						if (msg.equals("roll")) {
							rollWeapon();
						} else {
							unlock = UnlockEquip.valueOf(getRandWeapFromPool(msg));
							setEquip(UnlocktoItem.getUnlock(unlock, null));
						}
					}
					return;
				}
				
				if (equip instanceof NothingWeapon) {
					return;
				}
				
				//If player inventory is full, replace their current weapon.
				equip.getWeaponMods().clear();
				Equipable temp = p.getPlayerData().pickup(equip);
				
				for (WeaponMod mod : mods) {
					mod.acquireMod(p.getBodyData(), state, p.getPlayerData().getCurrentTool());
				}
				mods.clear();
				
				setEquip(temp);
				for (WeaponModifier mod : equip.getWeaponMods()) {
					mods.add(mod.getConstantMod());
				}
			}
			
			@Override
			public void preActivate(EventData activator, Player p) {
				onActivate(activator, p);
				HadalGame.server.sendToAllTCP(new Packets.SyncPickup(entityID.toString(), UnlockEquip.getUnlockFromEquip(equip.getClass()).toString(), mods));
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),	(short) 0, true, eventData);
	}
	
	@Override
	public Object onServerCreate() {
		return new Packets.CreatePickup(entityID.toString(), getPosition().scl(PPM), PickupType.WEAPON, unlock.toString(), mods);
	}
	
	@Override
	public void onClientSync(Object o) {
		if (o instanceof Packets.SyncPickup) {
			Packets.SyncPickup p = (Packets.SyncPickup) o;
			setEquip(UnlocktoItem.getUnlock(UnlockEquip.valueOf(p.newPickup), null));
			mods = p.mods;
		} else {
			super.onClientSync(o);
		}
	}
	
	/**
	 * This method returns the name of a weapon randomly selected from the pool.
	 * @param pool: comma separated list of names of weapons to choose from. if set to "", return any weapon in the random pool.
	 * @return
	 */
	public static String getRandWeapFromPool(String pool) {
		
		if (pool.equals("")) {
			return UnlockEquip.getUnlocks(false, UnlockTag.RANDOM_POOL)
					.get(GameStateManager.generator.nextInt(UnlockEquip.getUnlocks(false, UnlockTag.RANDOM_POOL).size)).name();
		}
		
		ArrayList<String> weapons = new ArrayList<String>();
		
		for (String id : pool.split(",")) {
			weapons.add(id);
		}
		return weapons.get(GameStateManager.generator.nextInt(weapons.size()));
	}
	
	public void rollWeapon() {
		unlock = UnlockEquip.valueOf(getRandWeapFromPool(pool));
		setEquip(UnlocktoItem.getUnlock(unlock, null));
		
		mods = new ArrayList<WeaponMod>();
		mods.addAll(PickupWeaponMod.getRandMods(modPow, ModTag.RANDOM_POOL));
	}
	
	@Override
	public void render(SpriteBatch batch) {
		if (!(equip instanceof NothingWeapon)) {
			super.render(batch);
		}
		
		batch.setProjectionMatrix(state.sprite.combined);
		HadalGame.SYSTEM_FONT_SPRITE.getData().setScale(1.0f);
		float y = getPosition().y * PPM + height / 2;
		for (WeaponMod mod : mods) {
			HadalGame.SYSTEM_FONT_SPRITE.draw(batch, mod.getName(), getPosition().x * PPM - width / 2, y);
			y += 15;
		}
		HadalGame.SYSTEM_FONT_SPRITE.draw(batch, equip.getName(), getPosition().x * PPM - width / 2, y);
	}
	
	public Equipable getEquip() {
		return equip;
	}

	public void setEquip(Equipable equip) {
		this.equip = equip;
		setEventSprite(equip.getEventSprite());
		
		if (equip instanceof NothingWeapon) {
			if (standardParticle != null) {
				standardParticle.turnOff();
			}
		} else {
			if (standardParticle != null) {
				standardParticle.turnOn();
			}
		}
	}

	public ArrayList<WeaponMod> getMods() {
		return mods;
	}
	
	public void setMods(ArrayList<WeaponMod> mods) {
		this.mods = mods;
	}

	@Override
	public void loadDefaultProperties() {
		setEventSprite(equip.getEventSprite());
	}
}
