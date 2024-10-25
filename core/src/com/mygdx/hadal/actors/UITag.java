package com.mygdx.hadal.actors;

import com.mygdx.hadal.constants.UITagType;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.users.User;

import static com.mygdx.hadal.constants.UITagType.ALL;

/**
 * This is not technically an actor but I don't know where else to put it.
 * A UITag is anything that can show up in the UIExtra actor in the top corner of the screen. This can include information about 
 * score, lives, other info and can be dynamically changed within a level using the UIChanger event.
 * 
 * @author Fraptista Flebrooke
 */
public class UITag {

	private final UIExtra uiExtra;

	//The type of this tag. specifies what kind of information will be displayed.
	private final UITagType type;
	
	//If this tag is of the "MISC" type, this variable will hold the string to be displayed. Otherwise, this will be "".
	private final String misc;

	//this is the last String displayed by this tag.
	//If this tag is not modified, we do not need to recalc things like player score order
	private String cachedText = "";

	public UITag(UIExtra uiExtra, UITagType type, String misc) {
		this.uiExtra = uiExtra;
		this.type = type;
		this.misc = misc;
	}

	/**
	 * Constructor for standard non-MISC tags that consist of only a type. "Default Tags"
	 * @param type: type of the new tag.
	 */
	public UITag(UIExtra uiExtra, UITagType type) {
		this(uiExtra, type, "");
	}

	private final StringBuilder text = new StringBuilder();
	/**
	 * This updates a single tag and is called when that tag is modified
	 * @param changedType: the type of tag to be modified
	 * @param user: for tags concerning a player, this is the player's user
	 * @return the new string to appear in the ui
	 */
	public String updateTagText(PlayState state, UITagType changedType, User user) {

		//this tag needs updating if relevant fields have been changed or if no text is cached
		if (cachedText.isEmpty() || type.equals(changedType) || ALL.equals(changedType)) {
			text.setLength(0);

			switch (type) {
				case SCRAP:
					text.append(UIText.UI_SCRAP.text(Integer.toString(JSONManager.record.getScrap())));
					break;
				case LIVES:
					text.append(UIText.UI_LIVES.text(Integer.toString(user.getScoreManager().getLives())));
					break;
				case SCORE:
					text.append(UIText.SCORE.text(Integer.toString(user.getScoreManager().getScore())));
					break;
				case HISCORE:
					if (JSONManager.record.getHiScores().containsKey(state.getLevel().toString())) {
						text.append(UIText.UI_HISCORE.text(Integer.toString(JSONManager.record.getHiScores()
								.get(state.getLevel().toString()))));
					}
					break;
				case TIMER:
					text.append(UIText.UI_TIMER.text(state.getTimerManager().getDisplayedTimer()));
					break;
				case MISC:
					text.append(misc);
					break;
				case LEVEL:
					text.append(state.getMode().getName()).append(" ").append(state.getLevel().getName());
					break;
				case SCOREBOARD:
					uiExtra.sortIndividualScores(text);
					break;
				case WINBOARD:
					uiExtra.sortWins(text);
					break;
				case TEAMSCORE:
					uiExtra.sortTeamScores(text);
					break;
				case PLAYERS_ALIVE:
					uiExtra.sortTeamAlive(text);
					break;
				case GUNGAME:
					uiExtra.sortGunGame(text);
					break;
				case CURRENCY:
					text.append(UIText.UI_SCRAP.text(Integer.toString(user.getScoreManager().getCurrency())));
					break;
				case ARCADE_ROUND:
					uiExtra.processArcadeRound(text);
					break;
				case EMPTY:
				default:
					break;
			}
			text.append("\n");
			cachedText = text.toString();
		}
		return cachedText;
	}

	public UITagType getType() { return type; }

	public String getMisc() { return misc; }
}
