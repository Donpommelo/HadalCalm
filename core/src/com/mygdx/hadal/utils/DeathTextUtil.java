package com.mygdx.hadal.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.mygdx.hadal.dialog.DeathMessage;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.enemies.EnemyType;
import com.mygdx.hadal.statuses.DamageTypes;

import java.util.ArrayList;

/**
 * This utility generates custom kill text when a player is killed
 * 
 * @author Harlsberg Huniqua
 */
public class DeathTextUtil {

	/**
	 * This returns a death message for a specific kill
	 * The player can toggle on "verbose death messages" in settings for simple or complex death messages
	 */
	public static String getDeathText(GameStateManager gsm, Player perp, Player vic, EnemyType type, DamageTypes... tags) {
		if (gsm.getSetting().isVerboseDeathMessage()) {
			return getDeathTextVerbose(perp, vic, type, tags);
		} else {
			return getDeathTextAbridged(perp, vic, type, tags);
		}
	}
	
	/**
	 * verbose text messages read damage tags and randomly choose a valid string from a file
	 */
	public static String getDeathTextVerbose(Player perp, Player vic, EnemyType type, DamageTypes... tags) {
		
		ArrayList<String> possibleMessages = new ArrayList<>();
		
		boolean namedPerp = false;

		//in the case or suicide or death to an enemy, obtain valid messages.
		//set 'namedPerp' to only search for messages that specify a victim and a perpetrator.
		if (perp != null && vic != null) {
			if (perp.getConnID() == vic.getConnID()) {
				possibleMessages.addAll(getValidMessages("SUICIDE", false));
			} else {
				namedPerp = true;
			}
		} else if (type != null) {
			possibleMessages.addAll(getValidMessages("ENEMY", false));
		}

		//iterate through all tags and add all valid messages
		if (tags.length > 0) {
			for (final DamageTypes tag : tags) {
				possibleMessages.addAll(getValidMessages(tag.toString(), namedPerp));
			}
		} else {
			possibleMessages.addAll(getValidMessages("UNKNOWN", namedPerp));
		}

		//universal tags exist in case we find no valid tags
		if (possibleMessages.isEmpty() || namedPerp) {
			possibleMessages.addAll(getValidMessages("UNIVERSAL", namedPerp));
		}
		
		//obtain random message and filter tags
		int randomIndex = MathUtils.random(possibleMessages.size() - 1);
		return filterDeathMessage(perp, vic, type, possibleMessages.get(randomIndex));
	}
	
	/**
	 * this helper method obtains valid messages for a single damage tag
	 */
	private static ArrayList<String> getValidMessages(String tag, boolean namedPerp) {
		ArrayList<String> possibleMessages = new ArrayList<>();
		
		JsonValue values = GameStateManager.deathMessages.get(tag);
		if (values != null) {
			
			//iterate through all messages that match the input tag
			for (JsonValue d : values) {
				DeathMessage message = GameStateManager.json.fromJson(DeathMessage.class, d.toJson(OutputType.minimal));
				if (message != null) {
					
					//add multiple instances of the message according to its weight
					for (int j = 0; j < message.getWeight(); j++) {
						if (namedPerp == message.isNamedPerp()) {
							possibleMessages.add(message.getMessage());
						}
					}
				}
			}
		}
		return possibleMessages;
	}


	private static final int maxNameLength = 25;
	/**
	 * filter a death message to include perp and vic names.
	 */
	public static String filterDeathMessage(Player perp, Player vic, EnemyType type, String message) {

		String vicName = WeaponUtils.getPlayerColorName(vic, maxNameLength);
		String perpName = "";
		if (type != null) {
			perpName = type.getName();
		}
		if (perp != null) {
			perpName = WeaponUtils.getPlayerColorName(perp, maxNameLength);
		}

		String filteredMessage = message.replaceAll("<vic>", vicName);
		filteredMessage = filteredMessage.replaceAll("<perp>", perpName);
		return filteredMessage;
	}
	
	/**
	 * Simple death messages only indicate perpetrator and victim
	 */
	public static String getDeathTextAbridged(Player perp, Player vic, EnemyType type, DamageTypes... tags) {

		String vicName = WeaponUtils.getPlayerColorName(vic, maxNameLength);
		String perpName = WeaponUtils.getPlayerColorName(perp, maxNameLength);

		if (tags.length > 0) {
			switch (tags[0]) {
			case LIVES_OUT:
				return vicName + " ran out of lives.";
			case DISCONNECT:
				return vicName + " disconnected.";
			default:
				break;
			}
		}

		if (perp != null) {
			if (perp.getConnID() == vic.getConnID()) {
				return vicName + " killed themself.";
			} else {
				return perpName + " killed " + vicName + ".";
			}
		} else if (type != null) {
			return vicName + " was killed by a " + type.name() + ".";
		} else {
			return vicName + " died.";
		}
	}
}
