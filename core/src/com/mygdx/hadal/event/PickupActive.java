package com.mygdx.hadal.event;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.actives.NothingActive;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.event.utility.TriggerAlt;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.Packets;
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
	private UnlockActives unlock;
	
	private static final String name = "Item Pickup";

	private String pool;
	
	public PickupActive(PlayState state, int x, int y, String pool) {
		super(state, name, Event.defaultPickupEventSize, Event.defaultPickupEventSize, x, y);
		this.pool = pool;
		
		//Set this pickup to a random weapon in the input pool
		unlock = UnlockActives.valueOf(getRandItemFromPool(pool));
		setActive(UnlocktoItem.getUnlock(unlock, null));
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
							unlock = UnlockActives.valueOf(getRandItemFromPool(pool));
							setActive(UnlocktoItem.getUnlock(unlock, null));
						} else {
							unlock = UnlockActives.valueOf(getRandItemFromPool(pool));
							setActive(UnlocktoItem.getUnlock(unlock, null));
						}
					}
					return;
				}
				
				if (item instanceof NothingActive) {
					return;
				}
				
				//If player inventory is full, replace their current weapon.
				item.setUser(p);
				ActiveItem temp = p.getPlayerData().pickup(item);
				setActive(temp);
			}
			
			@Override
			public void preActivate(EventData activator, Player p) {
				onActivate(activator, p);
				HadalGame.server.server.sendToAllTCP(new Packets.SyncPickup(entityID.toString(),
						UnlockActives.getUnlockFromActive(item.getClass()).toString()));
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
	public void render(SpriteBatch batch) {
		if (!(item instanceof NothingActive)) {
			super.render(batch);
		}
		
		batch.setProjectionMatrix(state.sprite.combined);
		HadalGame.SYSTEM_FONT_SPRITE.getData().setScale(1.0f);
		float y = getPosition().y * PPM + height / 2;
		HadalGame.SYSTEM_FONT_SPRITE.draw(batch, item.getName(), getPosition().x * PPM - width / 2, y);
	}
	
	@Override
	public Object onServerCreate() {
		return new Packets.CreatePickup(entityID.toString(), getPosition().scl(PPM), PickupType.ACTIVE, unlock.toString());
	}
	
	@Override
	public void onClientSync(Object o) {
		if (o instanceof Packets.SyncPickup) {
			Packets.SyncPickup p = (Packets.SyncPickup) o;
			setActive(UnlocktoItem.getUnlock(UnlockActives.valueOf(p.newPickup), null));
		} else {
			super.onClientSync(o);
		}
	}
	
	public void setActive(ActiveItem item) {
		this.item = item;
		if (item instanceof NothingActive) {
			if (standardParticle != null) {
				standardParticle.turnOff();
			}
		} else {
			if (standardParticle != null) {
				standardParticle.turnOn();
			}
		}
	}
	
	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.CUBE);
	}
}
