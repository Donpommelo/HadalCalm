package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.misc.NothingWeapon;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.event.utility.TriggerAlt;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
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
	
	private String pool;
	
	public PickupEquip(PlayState state, Vector2 startPos, String pool) {
		super(state, startPos, new Vector2(Event.defaultPickupEventSize, Event.defaultPickupEventSize));
		this.pool = pool;
		
		unlock = UnlockEquip.NOTHING;
		setEquip(UnlocktoItem.getUnlock(unlock, null));
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
							standardParticle.turnOn();
						} else {
							unlock = UnlockEquip.valueOf(UnlockEquip.getRandWeapFromPool(state, msg));
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
				
				setEquip(temp);
			}
			
			@Override
			public void preActivate(EventData activator, Player p) {
				onActivate(activator, p);
				HadalGame.server.sendToAllTCP(new Packets.SyncPickup(entityID.toString(), UnlockEquip.getUnlockFromEquip(equip.getClass()).toString()));
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, Constants.BIT_SENSOR, (short) (Constants.BIT_PLAYER),	(short) 0, true, eventData);
	}
	
	@Override
	public Object onServerCreate() {
		return new Packets.CreatePickup(entityID.toString(), getPixelPosition());
	}
	
	@Override
	public void onClientSync(Object o) {
		if (o instanceof Packets.SyncPickup) {
			Packets.SyncPickup p = (Packets.SyncPickup) o;
			setEquip(UnlocktoItem.getUnlock(UnlockEquip.valueOf(p.newPickup), null));
		} else {
			super.onClientSync(o);
		}
	}
	
	public void rollWeapon() {
		unlock = UnlockEquip.valueOf(UnlockEquip.getRandWeapFromPool(state, pool));
		setEquip(UnlocktoItem.getUnlock(unlock, null));
	}
	
	@Override
	public void render(SpriteBatch batch) {
		if (!(equip instanceof NothingWeapon)) {
			super.render(batch);
		}
		
		HadalGame.SYSTEM_FONT_SPRITE.getData().setScale(1.0f);
		float y = getPixelPosition().y + size.y / 2;
		
		HadalGame.SYSTEM_FONT_SPRITE.draw(batch, equip.getName(), getPixelPosition().x - size.x / 2, y);
	}
	
	public Equipable getEquip() { return equip; }

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
}
