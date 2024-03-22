package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.misc.NothingWeapon;
import com.mygdx.hadal.equip.ranged.SpeargunNerfed;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.event.utility.TriggerAlt;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.UnlocktoItem;
import com.mygdx.hadal.utils.b2d.HadalBody;
import com.mygdx.hadal.utils.b2d.HadalFixture;

import static com.mygdx.hadal.managers.SkinManager.FONT_SPRITE;

/**
 * This event, when interacted with, will give the player a new weapon.
 * If the player's slots are full, this will replace currently held weapon.
 * <p>
 * Triggered Behavior: When triggered, this event is toggled on/off to unlock/lock pickup
 * Triggering Behavior: This event will trigger its connected event when picked up.
 * <p>
 * Fields:
 * pool: String, comma separated list of equipUnlock enum names of all equips that could appear here.
 * 	if this is equal to "", return any weapon in the random pool.
 * 
 * @author Blalexander Bligmac
 */
public class PickupEquip extends Event {

	//when about to despawn, pickups flash
	private static final float FLASH_LIFESPAN = 1.0f;

	//When equip is changed, there is a cooldown before sending packets to sync other players
	private static final float SYNC_CD = 0.1f;

	//This is the weapon that will be picked up when interacting with this event.
	private Equippable equip;
	private UnlockEquip unlock;

	//csv list of equips that this can be
	private final String pool;

	//is this a temporary weapon drop?
	private boolean drop;

	//has the equip changed since the last sync was sent?
	private boolean equipChanged;

	public PickupEquip(PlayState state, Vector2 startPos, String pool) {
		super(state, startPos, new Vector2(Event.DEFAULT_PICKUP_EVENT_SIZE, Event.DEFAULT_PICKUP_EVENT_SIZE));
		this.pool = pool;
		
		unlock = UnlockEquip.NOTHING;
		setEquip(UnlocktoItem.getUnlock(unlock, null));
	}

	public PickupEquip(PlayState state, Vector2 startPos, UnlockEquip equip, float lifespan) {
		super(state, startPos, new Vector2(Event.DEFAULT_PICKUP_EVENT_SIZE, Event.DEFAULT_PICKUP_EVENT_SIZE), lifespan);
		this.pool = "";
		this.drop = true;
		unlock = equip;

		setEquip(UnlocktoItem.getUnlock(unlock, null));
		setSynced(true);
		setFlashLifespan(FLASH_LIFESPAN);
	}

	@Override
	public void create() {
		this.eventData = new InteractableEventData(this) {
			
			@Override
			public void onInteract(Player p) {
				preActivate(null, p);
			}

			@Override
			public void preActivate(EventData activator, Player p) { onActivate(activator, p); }

			@Override
			public void onActivate(EventData activator, Player p) {
				if (activator != null) {
					
					//alt messages can be used to reroll weapon or set it to a specific weapon
					if (activator.getEvent() instanceof TriggerAlt trigger) {
						String msg = trigger.getMessage();
						if ("roll".equals(msg)) {
							rollWeapon();
							standardParticle.turnOn();

							equipChanged = true;
						} else {
							unlock = UnlockEquip.getRandWeapFromPool(state, msg);
							setEquip(UnlocktoItem.getUnlock(unlock, null));
						}
					}
					return;
				}

				if (equip instanceof NothingWeapon) { return; }
				
				//If player inventory is full, replace their current weapon.
				Equippable temp = p.getEquipHelper().pickup(equip);
				setEquip(temp);
				equipChanged = true;
			}
		};

		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR,
				(short) (BodyConstants.BIT_PLAYER | BodyConstants.BIT_SENSOR), (short) 0)
				.setGravity(1.0f)
				.addToWorld(world);

		if (drop) {
			new HadalFixture(new Vector2(), new Vector2(size),
					BodyConstants.BIT_PROJECTILE, (short) (BodyConstants.BIT_DROPTHROUGHWALL | BodyConstants.BIT_WALL), (short) 0)
					.setFriction(1.0f)
					.setSensor(false)
					.addToBody(body)
					.setUserData(eventData);
		} else {
			this.body.setType(BodyType.KinematicBody);
		}
	}

	private float syncAccumulator;
	@Override
	public void controller(float delta) {
		super.controller(delta);
		if (equipChanged) {
			syncAccumulator += delta;

			if (syncAccumulator > SYNC_CD) {
				syncAccumulator = 0.0f;
				equipChanged = false;
				if (state.isServer()) {
					HadalGame.server.sendToAllTCP(getActivationPacket());
				} else {
					HadalGame.client.sendTCP(getActivationPacket());
				}
			}
		}
	}

	@Override
	public void clientController(float delta) {
		super.clientController(delta);
		controller(delta);
	}

	@Override
	public Object onServerCreate(boolean catchup) {
		if (synced) {
			return new Packets.CreatePickup(entityID, getPixelPosition(), UnlockEquip.getUnlockFromEquip(equip.getClass()), duration);
		} else {

			//client has already created their own pickups with the same triggeredID; just need to sync weapons
			return new Packets.SyncPickupTriggered(triggeredID, UnlockEquip.getUnlockFromEquip(equip.getClass()));
		}
	}

	@Override
	public void onServerSync() {
		//we only want to sync position data if the pickup is from a weapon drop
		if (drop || synced) {
			super.onServerSync();
		}
	}

	/**
	 * this rolls a random weapon
	 */
	public void rollWeapon() {
		if (!state.isServer()) { return; }
		unlock = UnlockEquip.getRandWeapFromPool(state, pool);
		setEquip(UnlocktoItem.getUnlock(unlock, null));
	}

	private Object getActivationPacket() {
		if (null == triggeredID) {
			return new Packets.SyncPickup(entityID, UnlockEquip.getUnlockFromEquip(equip.getClass()));
		} else {
			return new Packets.SyncPickupTriggered(triggeredID, UnlockEquip.getUnlockFromEquip(equip.getClass()));
		}
	}

	@Override
	public void render(SpriteBatch batch, Vector2 entityLocation) {
		if (!(equip instanceof NothingWeapon)) {
			super.render(batch, entityLocation);
			FONT_SPRITE.draw(batch, equip.getName(), entityLocation.x - size.x / 2, entityLocation.y + size.y / 2);
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
				if (state.isServer()) {
					queueDeletion();
				} else {
					((ClientState) state).removeEntity(entityID);
				}
			} else if (equip instanceof SpeargunNerfed) {
				this.equip = UnlocktoItem.getUnlock(UnlockEquip.NOTHING, null);
			}
		} else {
			if (standardParticle != null) {
				standardParticle.turnOn();
			}
		}
	}

	@Override
	public Object onServerDelete() {
		return null;
	}

	public Equippable getEquip() { return equip; }

	public void setEquipChanged(boolean equipChanged) { this.equipChanged = equipChanged; }
}
