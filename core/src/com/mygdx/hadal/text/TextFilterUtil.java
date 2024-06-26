package com.mygdx.hadal.text;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.utils.ConsoleCommandUtil;

/**
 * These utilities take strings and filter certain tags into other stuff
 * @author Druduh Dilpbrooke
 */
public class TextFilterUtil {

	/**
	 * This filters a single input string
	 */
	public static String filterText(String text) {
		
		String filteredText = text;
		
		if (null == text) {	return ""; }
		
		filteredText = filterTag(filteredText, "<body part>");
		filteredText = filterTag(filteredText, "<color>");
		filteredText = filterTag(filteredText, "<particle>");
		filteredText = filterTag(filteredText, "<process>");
		filteredText = filterTag(filteredText, "<cook>");
		filteredText = filterTag(filteredText, "<noun_fruit>");
		filteredText = filterTag(filteredText, "<noun_vegetable>");
		filteredText = filterTag(filteredText, "<sauce>");
		filteredText = filterTag(filteredText, "<herb>");
		filteredText = filterTag(filteredText, "<wine>");
		filteredText = filterTag(filteredText, "<dish>");
		filteredText = filterTag(filteredText, "<adjective>");
		filteredText = filterTag(filteredText, "<noun1>");
		filteredText = filterTag(filteredText, "<noun_abstract>");
		filteredText = filterTag(filteredText, "<noun_material>");

		filteredText = filterTips(filteredText);
		filteredText = filterRoll(filteredText);
		filteredText = filterHotkeys(filteredText);
		filteredText = filterPoem(filteredText);

		return filteredText;
	}
	
	/**
	 * this is similar to filterText, except is used exclusively for poetry.
	 */
	public static String filterPoemTags(String text) {
		String filteredText = text;
		
		if (null == text) {	return ""; }
		
		filteredText = filterTag(filteredText, "<preposition>");
		filteredText = filterTag(filteredText, "<noun_abstract>");
		filteredText = filterTag(filteredText, "<noun_animal>");
		filteredText = filterTag(filteredText, "<noun_clothing>");
		filteredText = filterTag(filteredText, "<noun_fruit>");
		filteredText = filterTag(filteredText, "<noun_furniture>");
		filteredText = filterTag(filteredText, "<noun_instrument>");
		filteredText = filterTag(filteredText, "<noun_material>");
		filteredText = filterTag(filteredText, "<noun_object>");
		filteredText = filterTag(filteredText, "<noun_people>");
		filteredText = filterTag(filteredText, "<noun_vegetable>");
		filteredText = filterTag(filteredText, "<adjective>");
		filteredText = filterTag(filteredText, "<verb>");
		filteredText = filterTag(filteredText, "<adverb>");
		filteredText = filterTag(filteredText, "<coordinating_conjunction>");
		filteredText = filterTag(filteredText, "<subordinating_conjunction>");
		filteredText = filterTag(filteredText, "<conjunctive_adverb>");
		filteredText = filterTag(filteredText, "<verse_enders>");
		filteredText = filterTag(filteredText, "<color>");
		
		return filteredText;
	}
	
	/**
	 * This filters ingame text like and dialog. This is used exclusively to use the player's name in dialog.
	 */
	public static String filterGameText(String text) {
		String filteredText = text;
		filteredText = filterText(filteredText);
		filteredText = filterName(filteredText);

		return filteredText;
	}
	
	/**
	 * filters every tag into a random word that matches that tag
	 */	
	private static String filterTag(String text, String tag) {
		String filteredText = text;
		
		while (filteredText.contains(tag)) {
			JsonValue value = JSONManager.randomText.get(tag);
			
			int randomIndex = MathUtils.random(value.asStringArray().length - 1);
			filteredText = filteredText.replaceFirst(tag, value.asStringArray()[randomIndex]);
		}
		
		return filteredText;
	}
	
	/**
	 * filters every "<roll>" into a random number
	 */
	private static String filterRoll(String text) {
		String filteredText = text;
		filteredText = filteredText.replaceAll("<roll>", "roll: " + MathUtils.random(ConsoleCommandUtil.MAX_ROLL));
		
		return filteredText;
	}
	
	/**
	 * filters every "<name>" into the player's chosen name
	 */
	private static String filterName(String text) {
		String filteredText = text;
		
		filteredText = filteredText.replaceAll("<name>", JSONManager.loadout.getName());
		
		return filteredText;
	}
	
	/**
	 * filters every "<poem>" into a randomly generated poem
	 */
	private static String filterPoem(String text) {
		String filteredText = text;
		
		if (text.contains("<poem>")) {
			filteredText = filteredText.replaceAll("<poem>", PoetryGenerator.generatePoetry());
		}
		
		return filteredText;
	}

	private static String filterTips(String text) {
		String filteredText = text;

		if (text.contains("<tip>")) {
			int randomIndex = MathUtils.random(JSONManager.tips.asStringArray().length - 1);
			filteredText = filteredText.replaceAll("<tip>", JSONManager.tips.asStringArray()[randomIndex]);
		}

		return filteredText;
	}
	
	/**
	 * This filters tags into chosen hotkeys. used in the tutorial
	 * The reason we have the fugs here is b/c tiled doesn't like greater/less than characters
	 */
	public static String filterHotkeys(String text) {
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
		filteredText = filteredText.replaceAll("fugactivefug", PlayerAction.ACTIVE_ITEM.getKeyText());
		filteredText = filteredText.replaceAll("fugmessagefug", PlayerAction.MESSAGE_WINDOW.getKeyText());
		filteredText = filteredText.replaceAll("fugchatwheelfug", PlayerAction.CHAT_WHEEL.getKeyText());
		filteredText = filteredText.replaceAll("fugpingfug", PlayerAction.PING.getKeyText());
		filteredText = filteredText.replaceAll("fugscorewindowfug", PlayerAction.SCORE_WINDOW.getKeyText());

		return filteredText;
	}
}
