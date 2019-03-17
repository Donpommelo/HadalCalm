package com.mygdx.hadal.event;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.mods.WeaponMod;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockManager.ModTag;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.Packets;
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

	public PickupWeaponMod(PlayState state, int x, int y, String pool) {
		super(state, name, Event.defaultPickupEventSize, Event.defaultPickupEventSize, x, y);
		this.mod = WeaponMod.valueOf(getRandModFromPool(pool, ModTag.RANDOM_POOL));
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
				if (isAlive()) {
					
					mod.acquireMod(p.getBodyData(), state, p.getPlayerData().getCurrentTool());
					queueDeletion();
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),	(short) 0, true, eventData);
	}
	
	@Override
	public void render(SpriteBatch batch) {
		super.render(batch);
		
		batch.setProjectionMatrix(state.sprite.combined);
		HadalGame.SYSTEM_FONT_SPRITE.getData().setScale(1.0f);
		float y = body.getPosition().y * PPM + height / 2;
		HadalGame.SYSTEM_FONT_SPRITE.draw(batch, mod.getName(), body.getPosition().x * PPM - width / 2, y);
	}

	@Override
	public Object onServerCreate() {
		return new Packets.CreatePickup(entityID.toString(), body.getPosition().scl(PPM), PickupType.MOD, mod.toString());
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
	
	public void setWeaponMod(WeaponMod mod) {
		this.mod = mod;
	}
	
	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.CUBE);
	}
}
