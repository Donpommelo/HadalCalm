package com.mygdx.hadal.equip;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

/**
 * An equippable is anything that takes up an equip slot. This includes melee and ranged weapons
 * @author Zuvinsky Zidborough
 */
public abstract class Equippable {
	
	//The Schmuck that is using this tool
	protected Player user;
	
	//The name of this tool
	protected String name;
	
	//The delay in seconds after using this tool before you can use a tool again.
	protected final float useCd;
	
	//Whether this tool is currently in the process of reloading or not.
	protected boolean reloading;
	
	//Counter for how much longer this tool needs to be reloaded before it gets more ammo
	protected float reloadCd;
	
	//The amount of time it takes to reload this weapon. (default = 0 for non-ranged)
	protected float reloadTime;
	
	//Whether this tool is currently in the process of charging or not.
	protected boolean charging;
	
	//Counter for how much longer this tool needs to be charged before it will be at max charge
	protected float chargeCd;
	
	//The amount of time it takes to charge this weapon. (default = 0 for non-charge equips)
	protected final float chargeTime;
	
	//These sprites are how the equip looks when equipped and as a pickup
	private final Sprite equipSprite, eventSprite;
	
	//this is the filter that describes who this equippable can hit
	protected short faction;
	
	protected final Vector2 weaponVelo = new Vector2();
	protected final Vector2 mouseLocation = new Vector2();
	
	/**
	 * Equippables are constructed when creating tool spawns or default schmuck loadouts
	 * @param user: Schmuck that is using this tool.
	 * @param useCd: The delay after using this tool before you can use a tool again.
	 * @param equipSprite: The equip's sprite when equipped
	 * @param eventSprite: The equip's sprite as a pickup
	 * @param chargeTime: If a charge weapon, how long does it take to fully charge?
	 */
	public Equippable(Player user, float useCd, Sprite equipSprite, Sprite eventSprite, float chargeTime) {
		this.user = user;
		this.useCd = useCd;
		this.reloading = false;
		this.charging = false;
		this.chargeTime = chargeTime;
		
		this.equipSprite = equipSprite;
		this.eventSprite = eventSprite;
	}

	/**
	 * Default charge time is 0 for non-charge weapons
	 */
	public Equippable(Player user, float useCd, Sprite equipSprite, Sprite eventSprite) {
		this(user, useCd, equipSprite, eventSprite, 0);
	}
	
	/**
	 * This method is run when a schmuck attempts to use a tool on a specific location.
	 * The tool is not actually fired with this method but a vector representing the target is set.
	 * @param delta: The time in seconds since this tool was last attempted to used. (Mostly used for charge weapons)
	 * @param state: The play state
	 * @param shooter: user data of he schmuck using this tool
	 * @param faction: Filter of the tool. (player, enemy, neutral)
	 * @param mouseLocation: screen coordinates of the target
	 */
	public abstract void mouseClicked(float delta, PlayState state, PlayerBodyData shooter, short faction, Vector2 mouseLocation);
	
	/**
	 * This method is called useDelay seconds after mouseClicked(). (unless overridden)
	 * This involves the tool actually firing off in a direction by calling fire(), usually.
	 * that should be set in mouseClicked() and ammo calculations.
	 * @param state: The play state
	 * @param bodyData: user data of he schmuck using this tool
	 */
	public abstract void execute(PlayState state, PlayerBodyData bodyData);
	
	/**
	 * This method is called when the player releases the mouse button for using this tool.
	 * Default does nothing. Used mostly for charge weapons. Enemies will not care about this method.
	 * @param state: The play state
	 * @param bodyData: user data of he schmuck using this tool
	 */
	abstract public void release(PlayState state, PlayerBodyData bodyData);
	
	/**
	 * This method will be called every engine tick if the player is reloading.
	 * If the weapon is reloadable, this method will probably count down some timer and add ammo when done.
	 * @param delta: elapsed time in seconds since last engine tick
	 * @return whether the weapon just finished reloading or not
	 */
	public abstract boolean reload(float delta);

	/**
	 * This is the method called when a weapon actually fires off. This usually involves shooty stuff.
	 * This is abstracted away from execute for weapons that fire weirdly (like charge weapons)
	 * @param state: state the weapon is fired in. Playstate often used for creating new hitboxes
	 * @param user: This is the guy who shot this weapon. If a weapon is fired by the map, use the WorldDummy
	 * @param startPosition: The starting location of the spawned hbox.
	 * @param startVelocity: The starting directional velocity of a projectile spawned. Typically vector pointing to mouse.
	 * @param filter: this is the hitbox filter that decides who gets hit by this
	 */
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {}
	
	/**
	 * This is run when this equippable is unequipped
	 */
	public void unequip(PlayState state) {}
	
	/**
	 * this is run when an equippable is equipped. reset charge + reload
	 */
	public void equip(PlayState state) {
		setReloading(getClipLeft() == 0 && this instanceof RangedWeapon, false);
		setCharging(false);
		setReloadCd(0);
		setChargeCd(0);
	}
	
	/**
	 * This is run every engine tick for weapons that have an effect that activates over time.
	 */
	public void update(PlayState state, float delta) {}

	public void processEffects(PlayState state) {}

	/**
	 * @return the string representing the weapon in the ui.
	 */
	public String getText() { return ""; }
	
	public boolean isOutofAmmo() { return false; }
	
	/**
	 * @return an extra string representing the weapon in the ui.
	 */
	public String getAmmoText() { return ""; }
	
	public void gainClip(int gained) {}

	public int getClipSize() { return 0; }
	
	public int getClipLeft() { return 0; }

	public void setClipLeft(int clipLeft) {}

	public void gainAmmo(float gainedPercent) {}
	
	public int getAmmoSize() { return 0; }
	
	public int getAmmoLeft() { return 0; }
	
	public void setAmmoLeft(int ammoLeft) {}
	
	public float getUseCd() { return useCd;	}
	
	public Sprite getWeaponSprite() { return equipSprite; }
	
	public Sprite getEventSprite() { return eventSprite; }
	
	public String getName() { return name; }
	
	public void setName(String name) { this.name = name; }

	public void setUser(Player user) {	this.user = user; }

	public boolean isReloading() { return reloading; }

	public void setReloading(boolean reloading, boolean override) {
		if ((getAmmoLeft() > 0 && getClipLeft() < getClipSize()) || override) {
		    this.reloading = reloading;
        }
	}

	public float getReloadCd() { return reloadCd; }
	
	public void setReloadCd(float reloadCd) { this.reloadCd = reloadCd; }

	public float getReloadTime() { return reloadTime * (1 - user.getBodyData().getStat(Stats.RANGED_RELOAD)); }

	public boolean isCharging() { return charging; }

	public void setCharging(boolean charging) {	this.charging = charging; }
	
	public float getChargeCd() { return chargeCd; }
	
	public void setChargeCd(float chargeCd) { 
		float chargeTime = getChargeTime();
		this.chargeCd = Math.min(chargeCd, chargeTime);
	}
	
	public float getChargeTime() { return chargeTime * (1 - user.getBodyData().getStat(Stats.EQUIP_CHARGE_RATE)); }

	public Vector2 getWeaponVelo() { return weaponVelo; }

	public void setWeaponVelo(Vector2 weaponVelo) {	this.weaponVelo.set(weaponVelo); }
	
	public String getChargeText() {	return UIText.CHARGING.text(); }

	public float getBotRangeMin() { return 0.0f; }

	public float getBotRangeMax() { return 0.0f; }

	/**
	 * These fields represent the item's stats to appear in its description
	 */
	public String[] getDescFields() { return new String[] {}; }
}
