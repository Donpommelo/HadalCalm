package com.mygdx.hadal.utils;

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
		
		if (text == null) {
			return text;
		}
		
		filteredText = filterTag(filteredText, "<body part>");
		filteredText = filterTag(filteredText, "<color>");
		filteredText = filterTag(filteredText, "<particle>");
		filteredText = filterTag(filteredText, "<process>");
		filteredText = filterTag(filteredText, "<cook>");
		filteredText = filterTag(filteredText, "<fruit>");
		filteredText = filterTag(filteredText, "<vegetable>");
		filteredText = filterTag(filteredText, "<sauce>");
		filteredText = filterTag(filteredText, "<herb>");
		filteredText = filterTag(filteredText, "<wine>");
		filteredText = filterTag(filteredText, "<dish>");
		filteredText = filterTag(filteredText, "<adjective1>");
		filteredText = filterTag(filteredText, "<noun1>");
		filteredText = filterTag(filteredText, "<material>");
		
		filteredText = filterRoll(filteredText);
		filteredText = filterHotkeys(filteredText);
		
		return filteredText;
	}
	
	public static String filterGameText(GameStateManager gsm, String text) {
		String filteredText = text;
		filteredText = filterText(filteredText);
		
		filteredText = filterName(gsm, filteredText);

		return filteredText;
	}
	
	/**
	 * filters every tag into a random color
	 * This is not used for anything in particular.
	 */	
	private static String filterTag(String text, String tag) {
		String filteredText = text;
		
		while (filteredText.contains(tag)) {
			JsonValue value = GameStateManager.miscText.get(tag);
			
			int randomIndex = GameStateManager.generator.nextInt(value.asStringArray().length);
			filteredText = filteredText.replaceFirst(tag, value.asStringArray()[randomIndex]);
		}
		
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
	
	private static String filterName(GameStateManager gsm, String text) {
		String filteredText = text;
		
		filteredText = filteredText.replaceAll("<name>", gsm.getLoadout().getName());
		
		return filteredText;
	}
	
	/**
	 * The reason we have the fugs here is b/c tiled doesn't like '<', '>' characters
	 */
	private static String filterHotkeys(String text) {
		String filteredText = text;
		filteredText = filteredText.replaceAll("fugdialogfug", PlayerAction.DIALOGUE.getKeyText());
		filteredText = filteredText.replaceAll("fugpausefug", PlayerAction.PAUSE.getKeyText());
		filteredText = filteredText.replaceAll("fugleftfug", PlayerAction.WALK_LEFT.getKeyText());
		filteredText = filteredText.replaceAll("fugrightfug", PlayerAction.WALK_RIGHT.getKeyText());
		filteredText = filteredText.replaceAll("fuginteractfug", PlayerAction.INTERACT.getKeyText());
		filteredText = filteredText.replaceAll("fugboostfug", PlayerAction.BOOST.getKeyText());
		filteredText = filteredText.replaceAll("fugjumpfug", PlayerAction.JUMP.getKeyText());
		filteredText = filteredText.replaceAll("fugcrouchfug", PlayerAction.CROUCH.getKeyText());
		filteredText = filteredText.replaceAll("fugshootfug", PlayerAction.FIRE.getKeyText());
		filteredText = filteredText.replaceAll("fugswitch1fug", PlayerAction.SWITCH_TO_1.getKeyText());
		filteredText = filteredText.replaceAll("fugswitch2fug", PlayerAction.SWITCH_TO_2.getKeyText());
		filteredText = filteredText.replaceAll("fugswitch3fug", PlayerAction.SWITCH_TO_3.getKeyText());
		filteredText = filteredText.replaceAll("fugreloadfug", PlayerAction.RELOAD.getKeyText());
		
		filteredText = filteredText.replaceAll("fugactivefug", PlayerAction.FREEZE.getKeyText());
		filteredText = filteredText.replaceAll("fugmessagefug", PlayerAction.MESSAGE_WINDOW.getKeyText());
		
		
		return filteredText;
	}
}
