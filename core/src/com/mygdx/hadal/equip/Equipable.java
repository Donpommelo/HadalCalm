package com.mygdx.hadal.equip;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.WeaponModifier;
import com.mygdx.hadal.utils.Stats;

public abstract class Equipable {	
	
	//The Schmuck that is using this tool
	protected Schmuck user;
	
	//The name of this tool
	protected String name;
	private String descr = "";
	
	//The delay in seconds after using this tool before you can use a tool again.
	protected float useCd;
	
	//The delay in seconds between pressing the button for this tool and it activating. 
	protected float useDelay;

	//Whether this tool is currently in the process of reloading or not.
	protected boolean reloading;
	
	//Counter for how much longer this tool needs to be reloaded before it gets more ammo
	protected float reloadCd = 0;
	
	//The amount of time it takes to reload this weapon. (default = 0 for non-ranged)
	protected float reloadTime = 0;
	
	//Whether this tool is currently in the process of charging or not.
	protected boolean charging;
	
	//Counter for how much longer this tool needs to be charged before it will be at max charge
	protected float chargeCd = 0;
	
	//The amount of time it takes to charge this weapon. (default = 0 for non-charge equips)
	protected float chargeTime = 0;
		
	private Sprite equipSprite, eventSprite;
	
	protected Vector2 weaponVelo = new Vector2();
	
	protected int x, y;
	protected short faction;
	
	protected ArrayList<WeaponModifier> weaponMods = new ArrayList<WeaponModifier>();
	
	/**
	 * Equipables are constructed when creating tool spawns or default schmuck loadouts
	 * @param user: Schmuck that is using this tool.
	 * @param name: Name of the weapon
	 * @param useCd: The delay after using this tool before you can use a tool again.
	 * @param shootDelay: The delay between pressing the button for this tool and it activating. 
	 */
	public Equipable(Schmuck user, String name, float useCd, float useDelay, Sprite equipSprite, Sprite eventSprite, float chargeTime) {
		this.user = user;
		this.name = name;
		this.useCd = useCd;
		this.useDelay = useDelay;
		this.reloading = false;
		this.charging = false;
		this.chargeTime = chargeTime;
		
		this.equipSprite = equipSprite;
		this.eventSprite = eventSprite;
	}

	public Equipable(Schmuck user, String name, float useCd, float useDelay, Sprite equipSprite, Sprite eventSprite) {
		this(user, name, useCd, useDelay, equipSprite, eventSprite, 0);
	}
	
	/**
	 * This method is run when a schmuck attempts to use a tool on a specific location.
	 * The tool is not actually fired with this method but a vector representing the target is set.
	 * @param delta: The time in seconds since this tool was last attempted to used. (Mostly used for charge weapons)
	 * @param state: The play state
	 * @param shooter: user data of he schmuck using this tool
	 * @param faction: Filter of the tool. (player, enemy, neutral)
	 * @param x: x coordinate of the target. (screen coordinates)
	 * @param y: y coordinate of the target. (screen coordinates)
	 */
	public abstract void mouseClicked(float delta, PlayState state, BodyData bodyData, short faction, int x, int y);
	
	/**
	 * This method is called useDelay seconds after mouseClicked(). 
	 * This involves the tool actually firing off in a direction by calling fire(), usually.
	 * that should be set in mouseClicked() and ammo calculations.
	 * @param state: The play state
	 * @param bodyData: user data of he schmuck using this tool
	 */
	public abstract void execute(PlayState state, BodyData bodyData);
	
	/**
	 * This method is called when the player releases the mouse button for using this tool.
	 * Default does nothing. Used mostly for charge weapons. Enemies will not care about this method.
	 * @param state: The play state
	 * @param bodyData: user data of he schmuck using this tool
	 */
	abstract public void release(PlayState state, BodyData bodyData);
	
	/**
	 * This method will be called every engine tick if the player is reloading.
	 * If the weapon is reloadable, this method will probably count down some timer and add ammo when done.
	 * @param delta: elapsed time in seconds since last engine tick
	 */
	public abstract void reload(float delta);

	/**
	 * This is the method called when a weapon actually fires off. This usually involves shooty stuff.
	 * This is abstracted away from execute for weapons that fire weirdly (like charge weapons)
	 * @param state: state the weapon is fired in. Playstate often used for creating new hitboxes
	 * @param user: This is the guy who shot this weapon. If a weapon is fired by the map, use the WorldDummy
	 * @param startVelocity: The starting directional velocity of a projectile spawned. Typically vector pointing to mouse.
	 * @param x: x coordinate of the hbox to spawn. (screen coordinates)
	 * @param y: y coordinate of the hbox to spawn. (screen coordinates) 
	 * @param filter: this is the hitbox filter that decides who gets hit by this
	 * @param procEffects: Does firing this shot activate on-shoot effects?
	 */
	public void fire(PlayState state, Schmuck user, Vector2 startVelocity, float x, float y, short filter) {}
	
	/**
	 * Get the string representing the weapon in the ui.
	 * @return
	 */
	public String getText() {
		return "";
	};
	
	/**
	 * Get an extra string representing the weapon in the ui.
	 * @return
	 */
	public String getAmmoText() {
		return "";
	};
	
	public void gainClip(int gained) {}

	public int getClipSize() {
		return 0;
	}
	
	public int getClipLeft() {
		return 0;
	}
	
	public void gainAmmo(float gainedPercent) {}
	
	public int getAmmoSize() {
		return 0;
	}
	
	public int getAmmoLeft() {
		return 0;
	}
	
	public void setClipLeft(int clipLeft) {}
	
	public void setAmmoLeft(int clipLeft) {}
	
	public float getUseCd() {
		return useCd;
	}
	
	public float getUseDelay() {
		return useDelay;
	}
	
	public Sprite getWeaponSprite() {
		return equipSprite;
	}
	
	public Sprite getEventSprite() {
		return eventSprite;
	}
	
	public String getName() {
		return name;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public Schmuck getUser() {
		return user;
	}

	public void setUser(Schmuck user) {
		this.user = user;
	}

	public boolean isReloading() {
		return reloading;
	}

	public void setReloading(boolean reloading) {
		this.reloading = reloading;
	}

	public float getReloadCd() {
		return reloadCd;
	}
	
	public void setReloadCd(float reloadCd) {
		this.reloadCd = reloadCd;
	}

	public float getReloadTime() {
		return reloadTime * (1 - user.getBodyData().getStat(Stats.RANGED_RELOAD));
	}

	public boolean isCharging() {
		return charging;
	}

	public void setCharging(boolean charging) {
		this.charging = charging;
	}
	
	public float getChargeCd() {
		return chargeCd;
	}
	
	public void setChargeCd(float chargeCd) {
		this.chargeCd = chargeCd;
	}
	
	public float getChargeTime() {
		return chargeTime * (1 - user.getBodyData().getStat(Stats.EQUIP_CHARGE_RATE));
	}

	public ArrayList<WeaponModifier> getWeaponMods() {
		return weaponMods;
	}

	public Vector2 getWeaponVelo() {
		return weaponVelo;
	}

	public void setWeaponVelo(Vector2 weaponVelo) {
		this.weaponVelo = weaponVelo;
	}
}
