package com.mygdx.hadal.text;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.enemies.EnemyType;
import com.mygdx.hadal.battle.DamageTag;

import static com.mygdx.hadal.constants.Constants.MAX_NAME_LENGTH;

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
	public static String getDeathText(GameStateManager gsm, Player perp, Player vic, EnemyType type, DamageSource source,
									  DamageTag... tags) {
		if (gsm.getSetting().isVerboseDeathMessage()) {
			return getDeathTextVerbose(perp, vic, type, source, tags);
		} else {
			return getDeathTextAbridged(perp, vic, type);
		}
	}
	
	/**
	 * verbose text messages read damage tags and randomly choose a valid string from a file
	 */
	public static String getDeathTextVerbose(Player perp, Player vic, EnemyType type, DamageSource source, DamageTag... tags) {

		Array<String> possibleMessages = new Array<>();
		
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

		possibleMessages.addAll(getValidMessages(source.toString(), namedPerp));

		//iterate through all tags and add all valid messages
		if (tags.length > 0) {
			for (final DamageTag tag : tags) {
				possibleMessages.addAll(getValidMessages(tag.toString(), namedPerp));
			}
		}

		//universal tags exist in case we find no valid tags
		if (possibleMessages.isEmpty() || namedPerp) {
			possibleMessages.addAll(getValidMessages("UNIVERSAL", namedPerp));
		}
		
		//obtain random message and filter tags
		int randomIndex = MathUtils.random(possibleMessages.size - 1);
		return filterDeathMessage(perp, vic, type, possibleMessages.get(randomIndex));
	}
	
	/**
	 * this helper method obtains valid messages for a single damage tag
	 */
	private static Array<String> getValidMessages(String tag, boolean namedPerp) {
		Array<String> possibleMessages = new Array<>();
		
		JsonValue values = GameStateManager.deathMessages.get(tag);
		if (values != null) {
			
			//iterate through all messages that match the input tag
			for (JsonValue d : values) {
				DeathMessage message = GameStateManager.JSON.fromJson(DeathMessage.class, d.toJson(OutputType.json));
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
	public static String filterDeathMessage(Player perp, Player vic, EnemyType type, String message) {

		String vicName = WeaponUtils.getPlayerColorName(vic, MAX_NAME_LENGTH);
		String perpName = "";
		if (type != null) {
			perpName = type.getName();
		}
		if (perp != null) {
			perpName = WeaponUtils.getPlayerColorName(perp, MAX_NAME_LENGTH);
		}

		String filteredMessage = message.replaceAll("<vic>", vicName);
		filteredMessage = filteredMessage.replaceAll("<perp>", perpName);
		return filteredMessage;
	}
	
	/**
	 * Simple death messages only indicate perpetrator and victim
	 */
	public static String getDeathTextAbridged(Player perp, Player vic, EnemyType type) {

		String vicName = WeaponUtils.getPlayerColorName(vic, MAX_NAME_LENGTH);
		String perpName = WeaponUtils.getPlayerColorName(perp, MAX_NAME_LENGTH);

		if (perp != null) {
			if (perp.getConnID() == vic.getConnID()) {
				return UIText.DEATH_SELF.text(vicName);
			} else {
				return UIText.DEATH_KILL.text(perpName, vicName);
			}
		} else if (type != null) {
			return UIText.DEATH_ENEMY.text(vicName, type.getName());
		} else {
			return UIText.DEATH_MISC.text(vicName);
		}
	}
}
