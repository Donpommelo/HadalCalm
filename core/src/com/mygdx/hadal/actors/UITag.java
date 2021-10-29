package com.mygdx.hadal.actors;

import com.mygdx.hadal.map.ModeGunGame;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;

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

		if (cachedText.isEmpty() || changedType.equals(type) || changedType.equals(ALL)) {
			text.setLength(0);

			switch (type) {
				case SCRAP:
					text.append("SCRAP: ").append(state.getGsm().getRecord().getScrap());
					break;
				case LIVES:
					text.append("LIVES: ").append(user.getScores().getLives());
					break;
				case SCORE:
					text.append("SCORE: ").append(user.getScores().getScore());
					break;
				case HISCORE:
					if (state.getGsm().getRecord().getHiScores().containsKey(state.getLevel().toString())) {
						text.append("HISCORE: ").append(state.getGsm().getRecord().getHiScores().get(state.getLevel().toString()));
					}
					break;
				case WINS:
					text.append("WINS: ").append(user.getScores().getWins());
					break;
				case TIMER:
					text.append("TIMER: ").append(uiExtra.getDisplayedTimer());
					break;
				case MISC:
					text.append(misc);
					break;
				case LEVEL:
					text.append(state.getMode().getInfo().getName()).append(" ").append(state.getLevel().getInfo().getName());
					break;
				case SCOREBOARD:
					uiExtra.sortIndividualScores(text);
					break;
				case TEAMSCORE:
					uiExtra.sortTeamScores(text);
					break;
				case GUNGAME:
					int score = user.getScores().getScore();

					text.append("SCORE: ").append(score).append("/").append(ModeGunGame.weaponOrder.length).append("\n")
						.append("NEXT WEAPON: ");

					//display next weapon in gun-game queue, unless we are on the last weapon
					if (score + 1 < ModeGunGame.weaponOrder.length) {
						text.append(ModeGunGame.weaponOrder[score + 1].getInfo().getName());
					} else {
						text.append("VICTORY");
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
		WINS,
		SCRAP,
		LIVES,
		TIMER,
		MISC,
		LEVEL,
		TEAMSCORE,
		GUNGAME,
		SCOREBOARD,
		EMPTY,
		ALL
	}
}
