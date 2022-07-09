package com.mygdx.hadal.actors;

import com.mygdx.hadal.map.ModeGunGame;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

import static com.mygdx.hadal.actors.UITag.uiType.ALL;

/**
 * This is not technically an actor but I don't know where else to put it.
 * 
 * A UITag is anything that can show up in the UIExtra actor in the top corner of the screen. This can include information about 
 * score, lives, other info and can be dynamically changed within a level using the UIChanger event.
 * 
 * @author Fraptista Flebrooke
 */
public class UITag {

	private final UIExtra uiExtra;

	//The type of this tag. specifies what kind of information will be displayed.
	private final uiType type;
	
	//If this tag is of the "MISC" type, this variable will hold the string to be displayed. Otherwise, this will be "".
	private final String misc;

	//this is the last String displayed by this tag.
	//If this tag is not modified, we do not need to recalc things like player score order
	private String cachedText = "";

	public UITag(UIExtra uiExtra, uiType type, String misc) {
		this.uiExtra = uiExtra;
		this.type = type;
		this.misc = misc;
	}

	/**
	 * Constructor for standard non-MISC tags that consist of only a type. "Default Tags"
	 * @param type: type of the new tag.
	 */
	public UITag(UIExtra uiExtra, uiType type) {
		this(uiExtra, type, "");
	}

	private final StringBuilder text = new StringBuilder();
	/**
	 * This updates a single tag and is called when that tag is modified
	 * @param changedType: the type of tag to be modified
	 * @param user: for tags concerning a player, this is the player's user
	 * @return the new string to appear in the ui
	 */
	public String updateTagText(PlayState state, uiType changedType, User user) {

		//this tag needs updating if relevant fields have been changed or if no text is cached
		if (cachedText.isEmpty() || type.equals(changedType) || ALL.equals(changedType)) {
			text.setLength(0);

			switch (type) {
				case SCRAP:
					text.append(UIText.UI_SCRAP.text(Integer.toString(state.getGsm().getRecord().getScrap())));
					break;
				case LIVES:
					text.append(UIText.UI_LIVES.text(Integer.toString(user.getScores().getLives())));
					break;
				case SCORE:
					text.append(UIText.SCORE.text(Integer.toString(user.getScores().getScore())));
					break;
				case HISCORE:
					if (state.getGsm().getRecord().getHiScores().containsKey(state.getLevel().toString())) {
						text.append(UIText.UI_HISCORE.text(Integer.toString(state.getGsm().getRecord().getHiScores()
								.get(state.getLevel().toString()))));
					}
					break;
				case TIMER:
					text.append(UIText.UI_TIMER.text(uiExtra.getDisplayedTimer()));
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
				case TEAMSCORE:
					uiExtra.sortTeamScores(text);
					break;
				case PLAYERS_ALIVE:
					uiExtra.sortTeamAlive(text);
					break;
				case GUNGAME:
					int score = user.getScores().getScore();
					if (score + 1 < ModeGunGame.weaponOrder.length) {
						text.append(UIText.UI_GUNGAME.text(Integer.toString(score), Integer.toString(ModeGunGame.weaponOrder.length),
								ModeGunGame.weaponOrder[score + 1].getName()));
					} else {
						text.append(UIText.UI_GUNGAME.text(Integer.toString(score), Integer.toString(ModeGunGame.weaponOrder.length),
								UIText.UI_VICTORY.text()));
					}
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

	public uiType getType() { return type; }

	public String getMisc() { return misc; }

	/**
	 * These are the various types of tags that can be added/removed from the UI.
	 * Feel free to add more
	 *
	 */
	public enum uiType {
		SCORE,
		HISCORE,
		SCRAP,
		LIVES,
		TIMER,
		MISC,
		LEVEL,
		TEAMSCORE,
		GUNGAME,
		PLAYERS_ALIVE,
		ALLY_HEALTH,
		SCOREBOARD,
		EMPTY,
		ALL
	}
}
