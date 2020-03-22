package com.mygdx.hadal.utils;

import java.util.ArrayList;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.mygdx.hadal.dialog.DeathMessage;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.statuses.DamageTypes;

public class DeathTextUtil {

	public static String getDeathText(GameStateManager gsm, Schmuck perp, Player vic, DamageTypes... tags) {
		if (gsm.getSetting().isVerboseDeathMessage()) {
			return getDeathTextVerbose(perp, vic, tags);
		} else {
			return getDeathTextAbridged(perp, vic, tags);
		}
	}
	
	public static String getDeathTextVerbose(Schmuck perp, Player vic, DamageTypes... tags) {
		
		ArrayList<String> possibleMessages = new ArrayList<String>();
		
		boolean namedPerp = false;
		
		if (perp.equals(vic)) {
			possibleMessages.addAll(getValidMessages("SUICIDE", namedPerp));
		} else if (perp instanceof Player) {
			namedPerp = true;
		} else if (perp instanceof Enemy) {
			possibleMessages.addAll(getValidMessages("ENEMY", namedPerp));
		} 
		

		if (tags.length > 0) {
			for (int i = 0; i < tags.length; i++) {
				possibleMessages.addAll(getValidMessages(tags[i].toString(), namedPerp));
			}
		} else {
			possibleMessages.addAll(getValidMessages("UNKNOWN", namedPerp));
		}

		if (possibleMessages.isEmpty() || namedPerp) {
			possibleMessages.addAll(getValidMessages("UNIVERSAL", namedPerp));
		}
		
		int randomIndex = GameStateManager.generator.nextInt(possibleMessages.size());
		return filterDeathMessage(perp, vic, possibleMessages.get(randomIndex));
	}
	
	public static ArrayList<String> getValidMessages(String tag, boolean namedPerp) {
		ArrayList<String> possibleMessages = new ArrayList<String>();
		
		JsonValue values = GameStateManager.deathMessages.get(tag);
		if (values != null) {
			for (JsonValue d : values) {
				DeathMessage message = GameStateManager.json.fromJson(DeathMessage.class, d.toJson(OutputType.minimal));
				if (message != null) {
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
	
	public static String filterDeathMessage(Schmuck perp, Player vic, String message) {
		String filteredMessage = message.replaceAll("<vic>", vic.getName());
		filteredMessage = filteredMessage.replaceAll("<perp>", perp.getName());
		filteredMessage = TextFilterUtil.filterText(filteredMessage);
		return filteredMessage;
	}
	
	public static String getDeathTextAbridged(Schmuck perp, Player vic, DamageTypes... tags) {
		
		if (tags.length > 0) {
			switch (tags[0]) {
			case DISCONNECT:
				return vic.getName() + " disconnected.";
			case LIVES_OUT:
				return vic.getName() + " ran out of lives.";
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
