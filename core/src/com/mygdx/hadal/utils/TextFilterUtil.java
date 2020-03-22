package com.mygdx.hadal.utils;

import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.hadal.managers.GameStateManager;

public class TextFilterUtil {

	public static String filterText(String text) {
		
		String filteredText = text;
		
		filteredText = filterBodyPart(filteredText);
		
		return filteredText;
	}
	
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
