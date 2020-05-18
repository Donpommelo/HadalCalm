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
