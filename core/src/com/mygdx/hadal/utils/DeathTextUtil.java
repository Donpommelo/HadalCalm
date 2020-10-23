package com.mygdx.hadal.utils;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.mygdx.hadal.dialog.DeathMessage;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.statuses.DamageTypes;

import java.util.ArrayList;

/**
 * This utility generates custom kill text when a player is killed
 * 
 * @author Harlsberg Huniqua
 */
public class DeathTextUtil {

	/**
	 * The player can toggle on "verbose death messages" in settings for simple or complex death messages
	 */
	public static String getDeathText(GameStateManager gsm, Schmuck perp, Player vic, DamageTypes... tags) {
		if (gsm.getSetting().isVerboseDeathMessage()) {
			return getDeathTextVerbose(perp, vic, tags);
		} else {
			return getDeathTextAbridged(perp, vic, tags);
		}
	}
	
	/**
	 * verbose text messages read damage tags and randomly choose a valid string from a file
	 */
	public static String getDeathTextVerbose(Schmuck perp, Player vic, DamageTypes... tags) {
		
		ArrayList<String> possibleMessages = new ArrayList<>();
		
		boolean namedPerp = false;
		
		//in the case or suicide or death to an enemy, obtain valid messages.
		//set 'namedPerp' to only search for messages that specify a victim and a perpetrator.
		if (perp.equals(vic)) {
			possibleMessages.addAll(getValidMessages("SUICIDE", false));
		} else if (perp instanceof Player) {
			namedPerp = true;
		} else if (perp instanceof Enemy) {
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
		int randomIndex = GameStateManager.generator.nextInt(possibleMessages.size());
		return filterDeathMessage(perp, vic, possibleMessages.get(randomIndex));
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
	
	/**
	 * filter a death message to include perp and vic names.
	 */
	public static String filterDeathMessage(Schmuck perp, Player vic, String message) {
		String filteredMessage = message.replaceAll("<vic>", vic.getName());
		filteredMessage = filteredMessage.replaceAll("<perp>", perp.getName());
		return filteredMessage;
	}
	
	/**
	 * Simple death messages only indicate perpetrator and victim
	 */
	public static String getDeathTextAbridged(Schmuck perp, Player vic, DamageTypes... tags) {
		
		if (tags.length > 0) {
			switch (tags[0]) {
			case LIVES_OUT:
				return vic.getName() + " ran out of lives.";
			case DISCONNECT:
				return vic.getName() + " disconnected.";
			default:
				break;
			}
		}

		if (perp.equals(vic)) {
			return vic.getName() + " killed themself.";
		} else if (perp instanceof Player) {
			return perp.getName() + " killed " + vic.getName() + ".";
		} else if (perp instanceof Enemy) {
			return vic.getName() + " was killed by a " + perp.getName() + ".";
		} else {
			return vic.getName() + " died.";
		}
	}
}
