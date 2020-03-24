package com.mygdx.hadal.utils;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * These utilities take strings and filter certain tags into other stuff
 * @author Zachary Tu
 *
 */
public class TextFilterUtil {

	public static String filterText(String text) {
		
		String filteredText = text;
		
		filteredText = filterBodyPart(filteredText);
		filteredText = filterColor(filteredText);
		filteredText = filterParticle(filteredText);
		filteredText = filterProcess(filteredText);
		filteredText = filterRoll(filteredText);
		filteredText = filterHotkeys(filteredText);
		
		return filteredText;
	}
	
	/**
	 * filters every "<body part>" into a random body part
	 * This is used for custom kill messages.
	 */
	private static String filterBodyPart(String text) {
		String filteredText = text;
		
		while (filteredText.contains("<body part>")) {
			JsonValue value = GameStateManager.miscText.get("Body Parts");
			
			int randomIndex = GameStateManager.generator.nextInt(value.asStringArray().length);
			filteredText = filteredText.replaceFirst("<body part>", value.asStringArray()[randomIndex]);
		}
		
		return filteredText;
	}
	
	/**
	 * filters every "<color>" into a random color
	 * This is not used for anything in particular.
	 */
	private static String filterColor(String text) {
		String filteredText = text;
		
		while (filteredText.contains("<color>")) {
			JsonValue value = GameStateManager.miscText.get("Colors");
			
			int randomIndex = GameStateManager.generator.nextInt(value.asStringArray().length);
			filteredText = filteredText.replaceFirst("<color>", value.asStringArray()[randomIndex]);
		}
		
		return filteredText;
	}
	
	private static String filterParticle(String text) {
		String filteredText = text;
		
		JsonValue value = GameStateManager.miscText.get("Particles");
		int randomIndex = GameStateManager.generator.nextInt(value.asStringArray().length);
		filteredText = filteredText.replaceFirst("<particle>", value.asStringArray()[randomIndex]);
		
		return filteredText;
	}
	
	private static String filterProcess(String text) {
		String filteredText = text;
		
		JsonValue value = GameStateManager.miscText.get("Processes");
		int randomIndex = GameStateManager.generator.nextInt(value.asStringArray().length);
		filteredText = filteredText.replaceFirst("<process>", value.asStringArray()[randomIndex]);
		
		return filteredText;
	}
	
	/**
	 * filters every "<roll>" into a random number
	 */
	private static String filterRoll(String text) {
		String filteredText = text;
		filteredText = filteredText.replaceAll("<roll>", "roll: " + String.valueOf(GameStateManager.generator.nextInt(100)));
		
		return filteredText;
	}
	
	private static String filterHotkeys(String text) {
		String filteredText = text;
		filteredText = filteredText.replaceAll("<left>", Input.Keys.toString(PlayerAction.WALK_LEFT.getKey()));
		filteredText = filteredText.replaceAll("<right>", Input.Keys.toString(PlayerAction.WALK_RIGHT.getKey()));
		filteredText = filteredText.replaceAll("<jump>", Input.Keys.toString(PlayerAction.JUMP.getKey()));
		filteredText = filteredText.replaceAll("<crouch>", Input.Keys.toString(PlayerAction.CROUCH.getKey()));
		filteredText = filteredText.replaceAll("<interact>", Input.Keys.toString(PlayerAction.INTERACT.getKey()));
		filteredText = filteredText.replaceAll("<shoot>", Input.Keys.toString(PlayerAction.FIRE.getKey()));
		filteredText = filteredText.replaceAll("<boost>", Input.Keys.toString(PlayerAction.BOOST.getKey()));
		filteredText = filteredText.replaceAll("<active>", Input.Keys.toString(PlayerAction.FREEZE.getKey()));
		filteredText = filteredText.replaceAll("<message>", Input.Keys.toString(PlayerAction.MESSAGE_WINDOW.getKey()));
		filteredText = filteredText.replaceAll("<pause>", Input.Keys.toString(PlayerAction.PAUSE.getKey()));
		filteredText = filteredText.replaceAll("<dialog>", Input.Keys.toString(PlayerAction.DIALOGUE.getKey()));
		
		return filteredText;
	}
}
