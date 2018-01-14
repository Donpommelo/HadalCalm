package com.mygdx.hadal.equip;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public abstract class Equipable {	
	
	//The Schmuck that is using this tool
	public Schmuck user;
	
	//The name of this tool
	public String name;
	
	//The delay in seconds after using this tool before you can use a tool again.
	public float useCd;
	
	//The delay in seconds between pressing the button for this tool and it activating. 
	public float useDelay;

	//Whether this tool is currently in the process of reloading or not.
	public boolean reloading;
	
	//Counter for how much longer this tool needs to be reloaded before it gets more ammo
	public float reloadCd;
	
	/**
	 * Equipables are constructed when creating tool spawns or default schmuck loadouts
	 * @param user: Schmuck that is using this tool.
	 * @param name: Name of the weapon
	 * @param useCd: The delay after using this tool before you can use a tool again.
	 * @param shootDelay: The delay between pressing the button for this tool and it activating. 
	 */
	public Equipable(Schmuck user, String name, float useCd, float useDelay) {
		this.user = user;
		this.name = name;
		this.useCd = useCd;
		this.useDelay = useDelay;
		this.reloading = false;
		this.reloadCd = 0;
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
	 * @param world: box2d world
	 * @param camera: game camera
	 * @param rays: game rayhandler
	 */
	public abstract void mouseClicked(float delta, PlayState state, BodyData bodyData, short faction, int x, int y, World world, OrthographicCamera camera, RayHandler rays);
	
	/**
	 * This method is called useDelay seconds after mouseClicked(). This involves the tool actually firing off in a direction
	 * that should be set in mouseClicked().
	 * @param state: The play state
	 * @param bodyData: user data of he schmuck using this tool
	 * @param world: box2d world
	 * @param camera: game camera
	 * @param rays: game rayhandler
	 */
	public abstract void execute(PlayState state, BodyData bodyData, World world, OrthographicCamera camera, RayHandler rays);
	
	/**
	 * This method is called when the player releases the mouse button for using this tool.
	 * Default does nothing. Used mostly for charge weapons. Enemies will not care about this method.
	 * @param state: The play state
	 * @param bodyData: user data of he schmuck using this tool
	 * @param world: box2d world
	 * @param camera: game camera
	 * @param rays: game rayhandler
	 */
	abstract public void release(PlayState state, BodyData bodyData, World world, OrthographicCamera camera, RayHandler rays);

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
	
	public float getUseCd() {
		return useCd;
	}
	
	public float getUseDelay() {
		return useDelay;
	}
}
