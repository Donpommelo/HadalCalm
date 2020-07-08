package com.mygdx.hadal.utils;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.PlayState.TransitionState;

/**
 * This utility manages the players console commands
 * enter console commands in the message window (default binding: m)
 * @author Zachary Tu
 */
public class ConsoleCommandUtil {

	/**
	 * This attempts to parse an input line of text into a command
	 */
	public static int parseCommand(PlayState state, String command) {
		String[] commands = command.split(" ");
		
		if (commands.length == 0) {
			return -1;
		}
		
		switch(commands[0]) {
		case "hp":
			if (commands.length > 1) {
				return setHp(state, commands[1]);
			}
			break;
		case "am":
			if (commands.length > 1) {
				return setAmmo(state, commands[1]);
			}
			break;
		case "act":
			if (commands.length > 1) {
				return setActiveCharge(state, commands[1]);
			}
			break;
		case "eq":
			if (commands.length > 1) {
				return setEquip(state, commands[1]);
			}
			break;
		case "scr":
			if (commands.length > 1) {
				return setScrap(state, commands[1]);
			}
			break;
		case "warp":
			if (commands.length > 1) {
				return warp(state, commands[1]);
			}
			break;
		case "print":
			if (commands.length > 1) {
				return print(state, commands[1]);
			}
			break;
		}
		
		return -1;
	}
	
	/**
	 * This lets the player display certain information in the text log using "print <>"
	 */
	public static int print(PlayState state, String command) {
		
		switch(command) {
		case "camera":
			HadalGame.server.addNotificationToAll(state, "GAEM", "CAMERA TARGET: " + state.getCameraTarget());
			break;
		case "cameraBounds":
			HadalGame.server.addNotificationToAll(state, "GAEM", "CAMERA BOUNDS: " + state.getCameraBounds());
			break;
		case "playerLoc":
			HadalGame.server.addNotificationToAll(state, "GAEM", "PLAYER LOCATION: " + state.getPlayer().getPosition());
			break;
		}
		return -1;
	}
	
	/**
	 * The player enters "hp x" to set their hp to x amount
	 */
	public static int setHp(PlayState state, String command) {
		
		try {
			float hp = Float.parseFloat(command);
			if (state.getPlayer().isAlive() && hp >= 0.0f) {
				state.getPlayer().getPlayerData().setCurrentHp(hp);
				return 0;
			}
		} catch (NumberFormatException e) {}
		
		return -1;
	}
	
	/**
	 * The player enters "am x" to set their ammo to x amount
	 */
	public static int setAmmo(PlayState state, String command) {
		
		try {
			int ammo = Integer.parseInt(command);
			if (state.getPlayer().isAlive() && ammo >= 0.0f) {
				state.getPlayer().getPlayerData().getCurrentTool().setAmmoLeft(ammo);
				return 0;
			}
		} catch (NumberFormatException e) {}
		
		return -1;
	}
	
	/**
	 * The player enters "act x" to set their active charge amount to x amount
	 */
	public static int setActiveCharge(PlayState state, String command) {
		
		try {
			float charge = Float.parseFloat(command);
			if (state.getPlayer().isAlive() && charge >= 0.0f) {
				state.getPlayer().getPlayerData().getActiveItem().setCurrentChargePercent(charge);
				return 0;
			}
		} catch (NumberFormatException e) {}
		
		return -1;
	}
	
	/**
	 * The player enters "eq x" to set their current weapon or active item to x (where x is the enum name of the equipable)
	 */
	public static int setEquip(PlayState state, String command) {
		
		try {
			UnlockEquip equip = UnlockEquip.valueOf(command.toUpperCase());
			if (state.getPlayer().isAlive()) {
				state.getPlayer().getPlayerData().pickup(UnlocktoItem.getUnlock(equip, state.getPlayer()));
			}
		} catch (IllegalArgumentException  e) {}
		
		try {
			UnlockActives active = UnlockActives.valueOf(command.toUpperCase());
			if (state.getPlayer().isAlive()) {
				state.getPlayer().getPlayerData().pickup(UnlocktoItem.getUnlock(active, state.getPlayer()));
			}
		} catch (IllegalArgumentException e) {}
		
		return -1;
	}
	
	/**
	 * The player enters "scr x" to set their scrap amount to x amount
	 */
	public static int setScrap(PlayState state, String command) {
		
		try {
			int scrap = Integer.parseInt(command);
			if (scrap >= 0) {
				state.getGsm().getRecord().setScrap(scrap);
				state.getUiExtra().syncData();
				return 0;
			}
		} catch (NumberFormatException e) {}
		
		return -1;
	}
	
	/**
	 * The player enters "warp x" to teleport to x level (where x is the enum name of the level)
	 */
	public static int warp(PlayState state, String command) {
		
		try {
			UnlockLevel level = UnlockLevel.valueOf(command.toUpperCase());
			state.loadLevel(level, TransitionState.NEWLEVEL, "");
			return 0;
		} catch (IllegalArgumentException e) {}
		
		return -1;
	}
}
