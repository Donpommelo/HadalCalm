package com.mygdx.hadal.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.actors.UITag;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.PlayState.TransitionState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.text.TextFilterUtil;

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
 * /team: display your current team color
 * /help: display a help message
 * /killme: kills the player
 *
 * @author Handwort Hockett
 */
public class ConsoleCommandUtil {

	public static final int maxRoll = 100;
	/**
	 * This parses a string input and executes a chat command.
	 * @param state: current state
	 * @param player: the player executing the command
	 * @param command: the string text to be interpreted
	 * @return an int to indicate whether this was parsed as a command (0) or not (-1)
	 */
	public static int parseChatCommand(PlayState state, Player player, String command) {
		
		if (player.getPlayerData() != null) {
			if (command.equals("/weapon")) {
				StringBuilder message = new StringBuilder("Weapons: ");

				for (int i = 0; i < Math.min(Loadout.maxWeaponSlots, Loadout.baseWeaponSlots + player.getPlayerData().getStat(Stats.WEAPON_SLOTS)); i++) {
					if (!player.getPlayerData().getLoadout().multitools[i].equals(UnlockEquip.NOTHING)) {
						message.append(player.getPlayerData().getLoadout().multitools[i].getName()).append(" ");
					}
				}
				emitMessage(state, message.toString());
				return 0;
			}

			if (command.equals("/artifact")) {
				StringBuilder message = new StringBuilder("Artifacts: ");

				for (int i = 0; i < player.getPlayerData().getLoadout().artifacts.length; i++) {

					if (!player.getPlayerData().getLoadout().artifacts[i].equals(UnlockArtifact.NOTHING)) {
						message.append(player.getPlayerData().getLoadout().artifacts[i].getName()).append(" ");
					}
				}
				emitMessage(state, message.toString());
				return 0;
			}

			if (command.equals("/active")) {
				emitMessage(state, "Active Item: " + player.getPlayerData().getLoadout().activeItem.getName());
				return 0;
			}

			if (command.equals("/team")) {
				emitMessage(state, "Team: " + player.getPlayerData().getLoadout().team.getTeamName());
				return 0;
			}

			if (command.equals("/killme")) {
				if (state.isServer()) {
					player.getPlayerData().receiveDamage(9999, new Vector2(), player.getPlayerData(), false,
							null, DamageSource.MISC);
				} else {
					HadalGame.client.sendTCP(new Packets.ClientYeet());
				}
				return 0;
			}
		}

		if (command.equals("/roll")) {
			emitMessage(state, "Rolled A Number: " + MathUtils.random(maxRoll));
			return 0;
		}

		if (command.equals("/help")) {
			if (state.isServer()) {
				state.getMessageWindow().addText(TextFilterUtil.filterHotkeys(UIText.INFO_HELP.text()), DialogType.SYSTEM, 0);
			} else {
				state.getMessageWindow().addText(TextFilterUtil.filterHotkeys(UIText.INFO_HELP.text()), DialogType.SYSTEM, player.getConnId());
			}
			return 0;
		}

		return -1;
	}

	private static void emitMessage(PlayState state, String message) {
		if (state.isServer()) {
			HadalGame.server.addChatToAll(state, message, DialogType.SYSTEM, 0);
		} else {
			HadalGame.client.sendTCP(new Packets.ClientChat(message, DialogType.SYSTEM));
		}
	}

	/**
	 * This attempts to parse an input line of text into a command
	 */
	public static int parseConsoleCommand(PlayState state, String command) {
		String[] commands = command.split(" ");
		
		if (commands.length == 0) {
			return -1;
		}

		if (commands.length > 1) {
			switch (commands[0]) {
				case "hp":
					return setHp(state, commands[1]);
				case "am":
					return setAmmo(state, commands[1]);
				case "act":
					return setActiveCharge(state, commands[1]);
				case "eq":
					return setEquip(state, commands[1]);
				case "scr":
					return setScrap(state, commands[1]);
				case "warp":
					return warp(state, commands[1]);
				case "print":
					return print(state, commands[1]);
			}
		}
		return -1;
	}
	
	/**
	 * This lets the player display certain information in the text log using "print x"
	 */
	public static int print(PlayState state, String command) {

		switch (command) {
			case "camera" -> HadalGame.server.addNotificationToAll(state,"GAEM",
				"CAMERA TARGET: " + state.getCameraTarget(), false, DialogType.SYSTEM);
			case "cameraBounds" -> HadalGame.server.addNotificationToAll(state, "GAEM",
				"CAMERA BOUNDS: " + Arrays.toString(state.getCameraBounds()), false, DialogType.SYSTEM);
			case "playerLoc" -> HadalGame.server.addNotificationToAll(state,"GAEM",
				"PLAYER LOCATION: " + state.getPlayer().getPosition(),	false, DialogType.SYSTEM);
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

		UnlockEquip equip = UnlockEquip.getByName(command.toUpperCase());
		if (state.getPlayer().isAlive()) {
			state.getPlayer().getPlayerData().pickup(
				Objects.requireNonNull(UnlocktoItem.getUnlock(equip, state.getPlayer())));
		}

		UnlockActives active = UnlockActives.getByName(command.toUpperCase());
		if (state.getPlayer().isAlive()) {
			state.getPlayer().getPlayerData().pickup(
				Objects.requireNonNull(UnlocktoItem.getUnlock(active, state.getPlayer())));
		}
		
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
				state.getUiExtra().syncUIText(UITag.uiType.SCRAP);
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
			UnlockLevel level = UnlockLevel.getByName(command.toUpperCase());
			state.loadLevel(level, TransitionState.NEWLEVEL, "");
			return 0;
		} catch (IllegalArgumentException ignored) {}
		
		return -1;
	}
}
