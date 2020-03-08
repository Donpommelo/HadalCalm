package com.mygdx.hadal.utils;

import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.PlayState.TransitionState;

public class ConsoleCommandUtil {

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
		}
		
		return -1;
	}
	
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
	
	public static int warp(PlayState state, String command) {
		
		try {
			UnlockLevel level = UnlockLevel.valueOf(command.toUpperCase());
			state.loadLevel(level, TransitionState.NEWLEVEL, "");
			return 0;
		} catch (IllegalArgumentException e) {}
		
		return -1;
	}
}
