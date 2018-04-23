package com.mygdx.hadal.equip;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public abstract class Equipable {	
	
	//The Schmuck that is using this tool
	protected Schmuck user;
	
	//The name of this tool
	protected String name;
	
	//The delay in seconds after using this tool before you can use a tool again.
	protected float useCd;
	
	//The delay in seconds between pressing the button for this tool and it activating. 
	protected float useDelay;

	//Whether this tool is currently in the process of reloading or not.
	protected boolean reloading;
	
	//Counter for how much longer this tool needs to be reloaded before it gets more ammo
	protected float reloadCd;
	
	//The amount of time it takes to reload this weapon. (default = 0 for non-ranged)
	protected float reloadTime = 0;
	
	private TextureAtlas atlas;
	private TextureRegion equipSprite;
	
	protected Vector3 mouseLocation;
	protected Vector2 weaponVelo;
	
	protected ArrayList<Status> weaponMods;
	
	public Equipable(Schmuck user, String name, float useCd, float useDelay, String spriteId) {
		this.user = user;
		this.name = name;
		this.useCd = useCd;
		this.useDelay = useDelay;
		this.reloading = false;
		this.reloadCd = 0;
		
		this.weaponMods = new ArrayList<Status>();
		
		atlas = (TextureAtlas) HadalGame.assetManager.get(AssetList.MULTITOOL_ATL.toString());
		equipSprite = atlas.findRegion(spriteId);
		mouseLocation = new Vector3();
		weaponVelo = new Vector2();
	}
	
	/**
	 * Equipables are constructed when creating tool spawns or default schmuck loadouts
	 * @param user: Schmuck that is using this tool.
	 * @param name: Name of the weapon
	 * @param useCd: The delay after using this tool before you can use a tool again.
	 * @param shootDelay: The delay between pressing the button for this tool and it activating. 
	 */
	public Equipable(Schmuck user, String name, float useCd, float useDelay) {
		this(user, name, useCd, useDelay, "default");
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
	 * This method is called useDelay seconds after mouseClicked(). This involves the tool actually firing off in a direction
	 * that should be set in mouseClicked().
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
	 * Get the string representing the weapon in the ui.
	 * @return
	 */
	public abstract String getText();
	
	public void gainAmmo(int gained) {
		
	}

	public float getUseCd() {
		return useCd;
	}
	
	public float getUseDelay() {
		return useDelay;
	}
	
	public TextureRegion getEquipSprite() {
		return equipSprite;
	}
	
	public float getReloadTime() {
		return reloadTime * (1 - user.getBodyData().getReloadRate());
	}
	
	public String getName() {
		return name;
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

	public ArrayList<Status> getWeaponMods() {
		return weaponMods;
	}	
}
