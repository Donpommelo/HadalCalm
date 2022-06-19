package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.misc.NothingWeapon;
import com.mygdx.hadal.equip.ranged.SpeargunNerfed;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.event.utility.TriggerAlt;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.UnlocktoItem;
import com.mygdx.hadal.utils.b2d.BodyBuilder;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

import java.util.Objects;

/**
 * This event, when interacted with, will give the player a new weapon.
 * If the player's slots are full, this will replace currently held weapon.
 * 
 * Triggered Behavior: When triggered, this event is toggled on/off to unlock/lock pickup
 * Triggering Behavior: This event will trigger its connected event when picked up.
 * 
 * Fields:
 * pool: String, comma separated list of equipUnlock enum names of all equips that could appear here.
 * 	if this is equal to "", return any weapon in the random pool.
 * 
 * @author Blalexander Bligmac
 */
public class PickupEquip extends Event {

	//This is the weapon that will be picked up when interacting with this event.
	private Equippable equip;
	private UnlockEquip unlock;
	
	private final String pool;

	private boolean drop;

	//when about to despawn, pickups flash
	private static final float flashLifespan = 1.0f;

	public PickupEquip(PlayState state, Vector2 startPos, String pool) {
		super(state, startPos, new Vector2(Event.defaultPickupEventSize, Event.defaultPickupEventSize));
		this.pool = pool;
		
		unlock = UnlockEquip.NOTHING;
		setEquip(Objects.requireNonNull(UnlocktoItem.getUnlock(unlock, null)));
	}

	public PickupEquip(PlayState state, Vector2 startPos, UnlockEquip equip, float lifespan) {
		super(state, startPos, new Vector2(Event.defaultPickupEventSize, Event.defaultPickupEventSize), lifespan);
		this.pool = "";
		this.drop = true;
		unlock = equip;
		setEquip(Objects.requireNonNull(UnlocktoItem.getUnlock(unlock, null)));
		setSynced(true);
		setFlashLifespan(flashLifespan);
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
					
					//alt messages can be used to reroll weapon or set it to a specific weapon
					if (activator.getEvent() instanceof TriggerAlt trigger) {
						String msg = trigger.getMessage();
						if (msg.equals("roll")) {
							rollWeapon();
							standardParticle.turnOn();
						} else {
							unlock = UnlockEquip.getRandWeapFromPool(state, msg);
							setEquip(Objects.requireNonNull(UnlocktoItem.getUnlock(unlock, null)));
						}
					}
					return;
				}

				if (equip instanceof NothingWeapon) { return; }
				
				//If player inventory is full, replace their current weapon.
				Equippable temp = p.getPlayerData().pickup(equip);
				setEquip(temp);
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, false, true,
			Constants.BIT_SENSOR, (short) (Constants.BIT_PLAYER | Constants.BIT_SENSOR), (short) 0, true, eventData);

		if (drop) {
			FixtureBuilder.createFixtureDef(body, new Vector2(), new Vector2(size), false, 0, 0, 0.0f, 1.0f,
				Constants.BIT_PROJECTILE, (short) (Constants.BIT_DROPTHROUGHWALL | Constants.BIT_WALL), (short) 0).setUserData(eventData);
		} else {
			this.body.setType(BodyType.KinematicBody);
		}
	}

	@Override
	public void clientController(float delta) {
		super.clientController(delta);
		controller(delta);
	}

	@Override
	public Object onServerCreate(boolean catchup) {
		return new Packets.CreatePickup(entityID, getPixelPosition(), UnlockEquip.getUnlockFromEquip(equip.getClass()), synced, duration);
	}

	@Override
	public void onServerSync() {

		//we only want to sync position data if the pickup is from a weapon drop
		if (drop || synced) {
			super.onServerSync();
		}
		state.getSyncPackets().add(new Packets.SyncPickup(entityID, UnlockEquip.getUnlockFromEquip(equip.getClass()),
				entityAge, state.getTimer()));
	}

	@Override
	public void onClientSync(Object o) {
		if (o instanceof Packets.SyncPickup p) {
			setEquip(Objects.requireNonNull(UnlocktoItem.getUnlock(p.newPickup, null)));
		} else {
			super.onClientSync(o);
		}
	}

	/**
	 * this rolls a random weapon
	 */
	public void rollWeapon() {
		unlock = UnlockEquip.getRandWeapFromPool(state, pool);
		setEquip(Objects.requireNonNull(UnlocktoItem.getUnlock(unlock, null)));
	}
	
	private final Vector2 entityLocation = new Vector2();
	@Override
	public void render(SpriteBatch batch) {
		if (!(equip instanceof NothingWeapon)) {
			super.render(batch);

			entityLocation.set(getPixelPosition());
			HadalGame.FONT_SPRITE.draw(batch, equip.getName(), entityLocation.x - size.x / 2, entityLocation.y + size.y / 2);
		}
	}
	
	/**
	 * This sets the weapon pickup to a specific equippable
	 */
	public void setEquip(Equippable equip) {
		this.equip = equip;
		setEventSprite(equip.getEventSprite());

		if (equip instanceof NothingWeapon || equip instanceof SpeargunNerfed) {
			if (standardParticle != null) {
				standardParticle.turnOff();
			}
			if (drop) {
				queueDeletion();
			} else if (equip instanceof SpeargunNerfed) {
				this.equip = Objects.requireNonNull(UnlocktoItem.getUnlock(UnlockEquip.NOTHING, null));
			}
		} else {
			if (standardParticle != null) {
				standardParticle.turnOn();
			}
		}
	}

	public Equippable getEquip() { return equip; }
}
