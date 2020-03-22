package com.mygdx.hadal.utils;

import com.badlogic.gdx.utils.JsonValue;
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
}
