package com.mygdx.hadal.utils;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.PlayState.TransitionState;

import java.util.Arrays;
import java.util.Objects;

/**
 * This utility manages the players console commands
 * enter console commands in the message window (default binding: 't')
 * Command List:
 * /roll: roll a random number
 * /weapon: display your currently equipped weapons
 * /artifact: display your currently equipped artifacts
 * /active: display your currently equipped active item
 * @author Handwort Hockett
 */
public class ConsoleCommandUtil {

	private static final int maxRoll = 100;
	/**
	 * This parses a string input and executes a chat command.
	 * @param state: current state
	 * @param player: the player executing the command
	 * @param command: the string text to be interpreted
	 * @return an int to indicate whether this was parsed as a command (0) or not (-1)
	 */
	public static int parseChatCommand(PlayState state, Player player, String command) {
		
		if (command.equals("/roll")) {
			HadalGame.server.addChatToAll(state,"Rolled A Number: "
				+ GameStateManager.generator.nextInt(maxRoll), DialogType.SYSTEM, player.getConnID());
			return 0;
		}
		
		if (command.equals("/weapon")) {
			
			StringBuilder message = new StringBuilder("Weapons: ");
			
			for (int i = 0; i < Math.min(Loadout.maxWeaponSlots, Loadout.baseWeaponSlots + player.getPlayerData().getStat(Stats.WEAPON_SLOTS)); i++) {

				if (!player.getPlayerData().getLoadout().multitools[i].equals(UnlockEquip.NOTHING)) {
					message.append(player.getPlayerData().getLoadout().multitools[i].name()).append(" ");
				}
			}
			
			HadalGame.server.addChatToAll(state, message.toString(), DialogType.SYSTEM, 0);
			return 0;
		}
		
		if (command.equals("/artifact")) {
			
			StringBuilder message = new StringBuilder("Artifacts: ");
			
			for (int i = 0; i < player.getPlayerData().getLoadout().artifacts.length; i++) {
				
				if (!player.getPlayerData().getLoadout().artifacts[i].equals(UnlockArtifact.NOTHING)) {
					message.append(player.getPlayerData().getLoadout().artifacts[i].name()).append(" ");
				}
			}
			
			HadalGame.server.addChatToAll(state,message.toString(), DialogType.SYSTEM, 0);
			return 0;
		}

		if (command.equals("/active")) {
			HadalGame.server.addChatToAll(state,"Active Item: " + player.getPlayerData().getLoadout().activeItem.name(), DialogType.SYSTEM, 0);
			return 0;
		}

		if (command.equals("/team")) {
			HadalGame.server.addChatToAll(state, "Team: " + player.getPlayerData().getLoadout().team.name(), DialogType.SYSTEM, 0);
			return 0;
		}

		if (command.equals("/help")) {
			state.getMessageWindow().addText(TextFilterUtil.filterHotkeys(GameStateManager.miscText.getString("help")), DialogType.SYSTEM, 0);
			return 0;
		}

		return -1;
	}
	
	public static int parseChatCommandClient(ClientState state, Player player, String command) {
		
		if (command.equals("/roll")) {
			HadalGame.client.sendTCP(new Packets.ClientChat("Rolled A Number: " + GameStateManager.generator.nextInt(100), DialogType.SYSTEM));
			return 0;
		}
		
		if (command.equals("/weapon")) {
			
			StringBuilder message = new StringBuilder("Weapons: ");
			
			for (int i = 0; i < Math.min(Loadout.maxWeaponSlots, state.getUiPlay().getOverrideWeaponSlots()); i++) {
				
				if (!player.getPlayerData().getLoadout().multitools[i].equals(UnlockEquip.NOTHING)) {
					message.append(player.getPlayerData().getLoadout().multitools[i].name()).append(" ");
				}
			}
			
			HadalGame.client.sendTCP(new Packets.ClientChat(message.toString(), DialogType.SYSTEM));
			return 0;
		}
		
		if (command.equals("/artifact")) {
			
			StringBuilder message = new StringBuilder("Artifacts: ");
			
			for (int i = 0; i < player.getPlayerData().getLoadout().artifacts.length; i++) {
				
				if (!player.getPlayerData().getLoadout().artifacts[i].equals(UnlockArtifact.NOTHING)) {
					message.append(player.getPlayerData().getLoadout().artifacts[i].name()).append(" ");
				}
			}
			
			HadalGame.client.sendTCP(new Packets.ClientChat(message.toString(), DialogType.SYSTEM));
			return 0;
		}

		if (command.equals("/active")) {
			HadalGame.client.sendTCP(new Packets.ClientChat("Active Item: " + player.getPlayerData().getLoadout().activeItem.name(), DialogType.SYSTEM));
			return 0;
		}

		if (command.equals("/team")) {
			HadalGame.client.sendTCP(new Packets.ClientChat("Team: " + player.getPlayerData().getLoadout().team.name(), DialogType.SYSTEM));
			return 0;
		}

		if (command.equals("/help")) {
			state.getMessageWindow().addText(TextFilterUtil.filterHotkeys(GameStateManager.miscText.getString("help")), DialogType.SYSTEM, player.getConnID());
			return 0;
		}

		return -1;
	}
	
	/**
	 * This attempts to parse an input line of text into a command
	 */
	public static int parseConsoleCommand(PlayState state, String command) {
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
			HadalGame.server.addNotificationToAll(state, "GAEM", "CAMERA TARGET: " + state.getCameraTarget(), DialogType.SYSTEM);
			break;
		case "cameraBounds":
			HadalGame.server.addNotificationToAll(state, "GAEM", "CAMERA BOUNDS: " +
					Arrays.toString(state.getCameraBounds()), DialogType.SYSTEM);
			break;
		case "playerLoc":
			HadalGame.server.addNotificationToAll(state, "GAEM", "PLAYER LOCATION: " + state.getPlayer().getPosition(), DialogType.SYSTEM);
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
		} catch (NumberFormatException ignored) {}
		
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
		} catch (NumberFormatException ignored) {}
		
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
		} catch (NumberFormatException ignored) {}
		
		return -1;
	}
	
	/**
	 * The player enters "eq x" to set their current weapon or active item to x (where x is the enum name of the equippable)
	 */
	public static int setEquip(PlayState state, String command) {
		
		try {
			UnlockEquip equip = UnlockEquip.valueOf(command.toUpperCase());
			if (state.getPlayer().isAlive()) {
				state.getPlayer().getPlayerData().pickup(
					Objects.requireNonNull(UnlocktoItem.getUnlock(equip, state.getPlayer())));
			}
		} catch (IllegalArgumentException ignored) {}
		
		try {
			UnlockActives active = UnlockActives.valueOf(command.toUpperCase());
			if (state.getPlayer().isAlive()) {
				state.getPlayer().getPlayerData().pickup(
					Objects.requireNonNull(UnlocktoItem.getUnlock(active, state.getPlayer())));
			}
		} catch (IllegalArgumentException ignored) {}
		
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
		} catch (NumberFormatException ignored) {}
		
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
		} catch (IllegalArgumentException ignored) {}
		
		return -1;
	}
}
