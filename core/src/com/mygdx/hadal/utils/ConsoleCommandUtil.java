package com.mygdx.hadal.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.UITagType;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.managers.PacketManager;
import com.mygdx.hadal.managers.TransitionManager.TransitionState;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.TextFilterUtil;
import com.mygdx.hadal.text.UIText;

import java.util.Arrays;

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

	public static final int MAX_ROLL = 100;
	/**
	 * This parses a string input and executes a chat command.
	 * @param state: current state
	 * @param player: the player executing the command
	 * @param command: the string text to be interpreted
	 * @return an int to indicate whether this was parsed as a command (0) or not (-1)
	 */
	public static int parseChatCommand(PlayState state, Player player, String command) {

		if (null != player) {
			if (null != player.getPlayerData()) {
				Loadout loadout = player.getUser().getLoadoutManager().getActiveLoadout();
				if ("/weapon".equals(command)) {
					StringBuilder message = new StringBuilder("Weapons: ");

					for (int i = 0; i < Math.min(Loadout.MAX_WEAPON_SLOTS, Loadout.BASE_WEAPON_SLOTS + player.getPlayerData().getStat(Stats.WEAPON_SLOTS)); i++) {
						if (!UnlockEquip.NOTHING.equals(loadout.multitools[i])) {
							message.append(loadout.multitools[i].getName()).append(" ");
						}
					}
					emitMessage(state, message.toString());
					return 0;
				}

				if ("/artifact".equals(command)) {
					StringBuilder message = new StringBuilder("Artifacts: ");

					for (int i = 0; i < loadout.artifacts.length; i++) {

						if (!UnlockArtifact.NOTHING.equals(loadout.artifacts[i])) {
							message.append(loadout.artifacts[i].getName()).append(" ");
						}
					}
					emitMessage(state, message.toString());
					return 0;
				}

				if ("/active".equals(command)) {
					emitMessage(state, "Active Item: " + loadout.activeItem.getName());
					return 0;
				}

				if ("/team".equals(command)) {
					emitMessage(state, "Team: " + loadout.team.getTeamName());
					return 0;
				}

				if ("/killme".equals(command)) {
					if (state.isServer()) {
						player.getPlayerData().receiveDamage(9999, new Vector2(), player.getPlayerData(), false,
								null, DamageSource.MISC);
					} else {
						PacketManager.clientTCP(new Packets.ClientYeet());
					}
					return 0;
				}
			}

			if ("/roll".equals(command)) {
				emitMessage(state, "Rolled A Number: " + MathUtils.random(MAX_ROLL));
				return 0;
			}

			if ("/help".equals(command)) {
				if (state.isServer()) {
					state.getUIManager().getMessageWindow().addText(TextFilterUtil.filterHotkeys(UIText.INFO_HELP.text()), DialogType.SYSTEM, 0);
				} else {
					state.getUIManager().getMessageWindow().addText(TextFilterUtil.filterHotkeys(UIText.INFO_HELP.text()), DialogType.SYSTEM, player.getUser().getConnID());
				}
				return 0;
			}
		}

		return -1;
	}

	private static void emitMessage(PlayState state, String message) {
		if (state.isServer()) {
			HadalGame.server.addChatToAll(state, message, DialogType.SYSTEM, 0);
		} else {
			PacketManager.clientTCP(new Packets.ClientChat(message, DialogType.SYSTEM));
		}
	}

	/**
	 * This attempts to parse an input line of text into a command
	 */
	public static int parseConsoleCommand(PlayState state, String command) {
		String[] commands = command.split(" ");
		
		if (0 == commands.length) {
			return -1;
		}

		if (null == HadalGame.usm.getOwnPlayer()) {
			return 0;
		}

		if (1 < commands.length) {
			switch (commands[0]) {
				case "hp":
					return setHp(commands[1]);
				case "am":
					return setAmmo(commands[1]);
				case "act":
					return setActiveCharge(commands[1]);
				case "eq":
					return setEquip(commands[1]);
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
				"CAMERA TARGET: " + state.getCameraManager().getCameraTarget(), false, DialogType.SYSTEM);
			case "cameraBounds" -> HadalGame.server.addNotificationToAll(state, "GAEM",
				"CAMERA BOUNDS: " + Arrays.toString(state.getCameraManager().getCameraBounds()), false, DialogType.SYSTEM);
		}
		return -1;
	}
	
	/**
	 * The player enters "hp x" to set their hp to x amount
	 */
	public static int setHp(String command) {
		
		try {
			float hp = Float.parseFloat(command);
			if (HadalGame.usm.getOwnPlayer().isAlive() && 0.0 <= hp) {
				HadalGame.usm.getOwnPlayer().getPlayerData().setCurrentHp(hp);
				return 0;
			}
		} catch (NumberFormatException ignored) {}
		
		return -1;
	}
	
	/**
	 * The player enters "am x" to set their ammo to x amount
	 */
	public static int setAmmo(String command) {
		
		try {
			int ammo = Integer.parseInt(command);
			if (HadalGame.usm.getOwnPlayer().isAlive() && 0.0f <= ammo) {
				HadalGame.usm.getOwnPlayer().getEquipHelper().getCurrentTool().setAmmoLeft(ammo);
				return 0;
			}
		} catch (NumberFormatException ignored) {}
		
		return -1;
	}
	
	/**
	 * The player enters "act x" to set their active charge amount to x amount
	 */
	public static int setActiveCharge(String command) {
		
		try {
			float charge = Float.parseFloat(command);
			if (HadalGame.usm.getOwnPlayer().isAlive() && 0.0f <= charge) {
				HadalGame.usm.getOwnPlayer().getMagicHelper().getMagic().setCurrentChargePercent(charge);
				return 0;
			}
		} catch (NumberFormatException ignored) {}
		
		return -1;
	}
	
	/**
	 * The player enters "eq x" to set their current weapon or active item to x (where x is the enum name of the equippable)
	 */
	public static int setEquip(String command) {

		UnlockEquip equip = UnlockEquip.getByName(command.toUpperCase());
		if (HadalGame.usm.getOwnPlayer().isAlive()) {
			HadalGame.usm.getOwnPlayer().getEquipHelper().pickup(UnlocktoItem.getUnlock(equip, HadalGame.usm.getOwnPlayer()));
		}

		UnlockActives active = UnlockActives.getByName(command.toUpperCase());
		if (HadalGame.usm.getOwnPlayer().isAlive()) {
			HadalGame.usm.getOwnPlayer().getMagicHelper().pickup(UnlocktoItem.getUnlock(active, HadalGame.usm.getOwnPlayer()));
		}
		
		return -1;
	}
	
	/**
	 * The player enters "scr x" to set their scrap amount to x amount
	 */
	public static int setScrap(PlayState state, String command) {
		
		try {
			int scrap = Integer.parseInt(command);
			if (0 <= scrap) {
				JSONManager.record.setScrap(scrap);
				state.getUIManager().getUiExtra().syncUIText(UITagType.SCRAP);
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
			state.getTransitionManager().loadLevel(level, TransitionState.NEWLEVEL, "");
			return 0;
		} catch (IllegalArgumentException ignored) {}
		
		return -1;
	}
}
