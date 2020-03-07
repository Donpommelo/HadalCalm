package com.mygdx.hadal.utils;

import com.mygdx.hadal.states.PlayState;

public class ConsoleCommandUtil {

	public static int parseCommand(PlayState state, String command) {
		String[] commands = command.split(" ");
		
		if (commands.length == 0) {
			return -1;
		}
		
		switch(commands[0]) {
		case "killme":
			return die(state);
		case "hp":
			if (commands.length > 1) {
				return setHp(state, commands[1]);
			}
			break;
		case "ammo":
			if (commands.length > 1) {
				return setAmmo(state, commands[1]);
			}
			break;
		case "charge":
			if (commands.length > 1) {
				return setActiveCharge(state, commands[1]);
			}
			break;
		}
		
		return -1;
	}
	
	public static int die(PlayState state) {
		if (state.getPlayer().isAlive()) {
			state.getPlayer().getPlayerData().die(state.getPlayer().getPlayerData());
			return 0;
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
		} catch(NumberFormatException e) {}
		
		return -1;
	}
	
	public static int setAmmo(PlayState state, String command) {
		
		try {
			int ammo = Integer.parseInt(command);
			if (state.getPlayer().isAlive() && ammo >= 0.0f) {
				state.getPlayer().getPlayerData().getCurrentTool().setAmmoLeft(ammo);
				return 0;
			}
		} catch(NumberFormatException e) {}
		
		return -1;
	}
	
	public static int setActiveCharge(PlayState state, String command) {
		
		try {
			float charge = Float.parseFloat(command);
			if (state.getPlayer().isAlive() && charge >= 0.0f) {
				state.getPlayer().getPlayerData().getActiveItem().setCurrentChargePercent(charge);
				return 0;
			}
		} catch(NumberFormatException e) {}
		
		return -1;
	}
	
	
}
