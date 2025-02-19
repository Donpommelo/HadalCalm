package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.UITagType;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.managers.SpriteManager;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.map.ModeGunGame;
import com.mygdx.hadal.map.SettingArcade;
import com.mygdx.hadal.map.SettingTeamMode;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.utils.TextUtil;

import static com.mygdx.hadal.constants.Constants.MAX_NAME_LENGTH_SHORT;
import static com.mygdx.hadal.constants.Constants.MAX_NAME_LENGTH_SUPER_SHORT;
import static com.mygdx.hadal.managers.SkinManager.FONT_UI;

/**
 * The UIExtra is an extra ui actor displayed in the upper left hand side.
 * It displays list of strings decided by the uiTags list which can be modified in level with events.
 * @author Yacardo Yarabba
 */
public class UIExtra extends AHadalActor {

	private static final int X = 10;
	private static final int Y = 10;
	private static final int WIDTH = 240;
	private static final float FONT_SCALE = 0.25f;

	private final PlayState state;
	
	//List of tags that are to be displayed
	private final Array<UITag> uiTags = new Array<>();

	private short viewingUserTeam;

	private final TextureRegion hpBar, hpBarFade;

	public UIExtra(PlayState state) {
		this.state = state;
		this.hpBar = SpriteManager.getFrame(Sprite.UI_MAIN_HEALTHBAR);
		this.hpBarFade = SpriteManager.getFrame(Sprite.UI_MAIN_HEALTH_MISSING);
	}
	
	private final StringBuilder text = new StringBuilder();
	@Override
    public void draw(Batch batch, float alpha) {

		if (JSONManager.setting.isHideHUD()) { return; }

		FONT_UI.getData().setScale(FONT_SCALE);
		FONT_UI.draw(batch, text.toString(), X, HadalGame.CONFIG_HEIGHT - Y, WIDTH, Align.left, true);

		renderTeamHp(batch, viewingUserTeam);
	}

	private static final int HP_WIDTH = 45;
	private static final int HP_HEIGHT = 8;
	private static final int HP_BAR_OFFSET_Y = -9;
	private static final int NAME_MAX_LENGTH = 225;
	private static final int ROW_HEIGHT = 14;
	private static final int START_Y_EXTRA = 200;
	private static final int START_X_EXTRA = 10;
	private static final int MAX_ALLIES = 6;
	/**
	 * In team modes, this renders ally hp bars in the upper right hand side of the screen
	 */
	private void renderTeamHp(Batch batch, short viewingUserTeam) {
		if (state.getUIManager().getScoreWindow() == null) { return; }
		if (SettingTeamMode.TeamMode.FFA == state.getMode().getTeamMode()) { return; }

		float currentY = HadalGame.CONFIG_HEIGHT - START_Y_EXTRA;
		int allyNumber = 0;

		//iterate through each non-spectator on the same team
		for (User user : state.getUIManager().getScoreWindow().getOrderedUsers()) {
			if (!user.isSpectator() && user.getPlayer() != null) {
				if (user.getPlayer().getPlayerData() != null &&
						(!user.equals(HadalGame.usm.getOwnUser()) || SettingArcade.arcade)) {
					if (user.getPlayer().getHitboxFilter() == viewingUserTeam) {
						if (SettingArcade.arcade && state.getMode().equals(GameMode.ARCADE)) {
							if (user.getScoreManager().isReady()) {
								FONT_UI.draw(batch, UIText.UI_READY.text(TextUtil.getPlayerColorName(user.getPlayer(), MAX_NAME_LENGTH_SUPER_SHORT)),
										HadalGame.CONFIG_WIDTH - NAME_MAX_LENGTH - HP_WIDTH - START_X_EXTRA, currentY, NAME_MAX_LENGTH,
										Align.left, true);
							} else {
								FONT_UI.draw(batch, UIText.UI_NOT_READY.text(TextUtil.getPlayerColorName(user.getPlayer(), MAX_NAME_LENGTH_SUPER_SHORT)),
										HadalGame.CONFIG_WIDTH - NAME_MAX_LENGTH - HP_WIDTH - START_X_EXTRA, currentY, NAME_MAX_LENGTH,
										Align.left, true);
							}
						} else {
							FONT_UI.draw(batch, TextUtil.getPlayerColorName(user.getPlayer(), MAX_NAME_LENGTH_SHORT),
									HadalGame.CONFIG_WIDTH - NAME_MAX_LENGTH - HP_WIDTH - START_X_EXTRA, currentY, NAME_MAX_LENGTH,
									Align.left, true);
						}

						//draw bar corresponding to hp ratio (dead players are set to 0%)
						float hpRatio = user.getPlayer().getPlayerData().getCurrentHp() /
								user.getPlayer().getPlayerData().getStat(Stats.MAX_HP);
						if (!user.getPlayer().isAlive()) {
							hpRatio = 0.0f;
						}
						batch.draw(hpBarFade, HadalGame.CONFIG_WIDTH - HP_WIDTH - START_X_EXTRA, currentY + HP_BAR_OFFSET_Y,
								HP_WIDTH, HP_HEIGHT);
						batch.draw(hpBar, HadalGame.CONFIG_WIDTH - HP_WIDTH - START_X_EXTRA, currentY + HP_BAR_OFFSET_Y,
								HP_WIDTH * hpRatio, HP_HEIGHT);
						currentY -= ROW_HEIGHT;
						allyNumber++;
						if (allyNumber > MAX_ALLIES) {
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * This is run whenever the contents of the ui change. It sets the text according to updated tags and info
	 */
	public void syncUIText(UITagType changedType) {

		//clear existing text
		text.setLength(0);

		User user = null;

		//if we are spectating another player, we want to ui to match the spectate target instead of ourselves
		boolean spectatorFound = false;
		if (state.getSpectatorManager().isSpectatorMode()) {
			if (state.getUIManager().getUiSpectator().getSpectatorTarget() != null) {
				user = state.getUIManager().getUiSpectator().getSpectatorTarget().getUser();
				spectatorFound = true;
			}
		}
		if (!spectatorFound) {
			user = HadalGame.usm.getOwnUser();
		}

		//check if user is null b/c several ui tags require checking user information
		if (user != null) {
			if (user.getPlayer() != null) {
				viewingUserTeam = user.getPlayer().getHitboxFilter();
			}
			for (UITag uiTag : uiTags) {
				text.append(uiTag.updateTagText(state, changedType, user));
			}
		}

		FONT_UI.getData().setScale(FONT_SCALE);
	}

	/**
	 * This adds tags to the ui
	 * @param tags: These are the tags that should be added. This is a comma-separated string of each uiType.
	 * @param clear: Do we clear all the existing ui tags first?
	 */
	public void changeTypes(String tags, boolean clear) {

		if (clear) {
			uiTags.clear();
		}

		for (String type : tags.split(",")) {
			boolean found = false;
			for (UITagType tag : UITagType.values()) {
				if (tag.toString().equals(type)) {
					found = true;
					uiTags.add(new UITag(this, tag));
					break;
				}
			}

			//If a string matches no tag types, we just display the text as is.
			if (!found) {
				uiTags.add(new UITag(this, UITagType.MISC, type));
			}
		}
		syncUIText(UITagType.ALL);
	}
	
	private final StringBuilder tags = new StringBuilder();
	/**
	 * This returns a comma-separated string of the current tags in order.
	 * When a new client connects, the server uses this to send them the current ui status to sync with.
	 */
	public String getCurrentTags() {
		tags.setLength(0);

		//misc tags return their exact text. Other tags return their type name
		for (UITag tag : uiTags) {
			if (UITagType.MISC.equals(tag.getType())) {
				tags.append(tag.getMisc()).append(",");
			} else {
				tags.append(tag.getType().toString()).append(",");
			}
		}
		return tags.toString();
	}

	private static final int MAX_SCORES = 5;
	private static final int MAX_CHARACTERS = 26;
	/**
	 * For modes with a scoreboard ui tag, we add a sorted list of player scores. List is, at most, 5 names long
	 * @param text: the stringbuilder we will be appending the scoreboard text to
	 */
	public void sortIndividualScores(StringBuilder text) {
		if (state.getUIManager().getScoreWindow() != null) {
			int scoreNum = 0;
			for (User user : state.getUIManager().getScoreWindow().getOrderedUsers()) {
				if (!user.isSpectator()) {
					text.append(user.getStringManager().getNameAbridgedColored(MAX_NAME_LENGTH_SHORT)).append(": ")
							.append(alignScoreText(user.getStringManager().getNameShort(), String.valueOf(user.getScoreManager().getScore()),
									MAX_NAME_LENGTH_SHORT, MAX_CHARACTERS))
							.append(user.getScoreManager().getScore()).append("\n");
					scoreNum++;
					if (MAX_SCORES < scoreNum) {
						break;
					}
				}
			}
		}
	}

	private static final Vector3 rgb = new Vector3();
	/**
	 * Works similarly to sortIndividualScores except using the team's color instead
	 */
	public void sortTeamScores(StringBuilder text) {
		int scoreNum = 0;
		for (int i = 0; i < AlignmentFilter.teamScores.length; i++) {
			rgb.set(AlignmentFilter.currentTeams[i].getPalette().getIcon().getRGB());
			String hex = "#" + Integer.toHexString(Color.rgb888(rgb.x, rgb.y, rgb.z));
			text.append("[").append(hex).append("]").append(AlignmentFilter.currentTeams[i].getTeamName())
				.append(alignScoreText(AlignmentFilter.currentTeams[i].getTeamName(), String.valueOf(AlignmentFilter.teamScores[i]),
							MAX_NAME_LENGTH_SHORT, MAX_CHARACTERS))
				.append("[]").append(": ").append(AlignmentFilter.teamScores[i]).append("\n");
			scoreNum++;
			if (MAX_SCORES < scoreNum) {
				break;
			}
		}
	}

	public void sortWins(StringBuilder text) {
		if (state.getUIManager().getScoreWindow() != null) {
			for (User user : state.getUIManager().getScoreWindow().getOrderedUsers()) {
				if (!user.isSpectator()) {
					text.append(user.getStringManager().getNameAbridgedColored(MAX_NAME_LENGTH_SHORT)).append(": ")
							.append(alignScoreText(user.getStringManager().getNameShort(), String.valueOf(user.getScoreManager().getWins()),
									MAX_NAME_LENGTH_SHORT, MAX_CHARACTERS))
							.append(user.getScoreManager().getWins()).append("\n");
				}
			}
		}
	}

	/**
	 * This iterates through each team and gets the number of players on that team that are currently alive
	 */
	public void sortTeamAlive(StringBuilder text) {
		if (state.getUIManager().getScoreWindow() != null) {
			int scoreNum = 0;
			for (int i = 0; i < AlignmentFilter.teamScores.length; i++) {
				int numAlive = 0;
				for (User user : state.getUIManager().getScoreWindow().getOrderedUsers()) {
					if (!user.isSpectator()) {
						if (user.getPlayer() != null) {
							if (user.getPlayer().isAlive()) {
								if (user.getLoadoutManager().getActiveLoadout().team == AlignmentFilter.currentTeams[i]) {
									numAlive++;
								}
							}
						}
					}
				}
				rgb.set(AlignmentFilter.currentTeams[i].getPalette().getIcon().getRGB());
				String hex = "#" + Integer.toHexString(Color.rgb888(rgb.x, rgb.y, rgb.z));
				text.append("[").append(hex).append("]").append(AlignmentFilter.currentTeams[i].getTeamName())
						.append(alignScoreText(AlignmentFilter.currentTeams[i].getTeamName(), String.valueOf(numAlive),
								MAX_NAME_LENGTH_SUPER_SHORT, MAX_CHARACTERS - 1 - UIText.PLAYERS_ALIVE.text().length()))
						.append("[]").append(": ").append(numAlive).append(" ").append(UIText.PLAYERS_ALIVE.text()).append("\n");
				scoreNum++;
				if (MAX_SCORES < scoreNum) {
					break;
				}
			}
		}
	}

	/**
	 * Sort gun game ui text to display top player progress
	 */
	public void sortGunGame(StringBuilder text) {
		if (state.getUIManager().getScoreWindow() != null) {
			int scoreNum = 0;
			for (User user : state.getUIManager().getScoreWindow().getOrderedUsers()) {
				if (!user.isSpectator()) {
					text.append(user.getStringManager().getNameAbridgedColored(MAX_NAME_LENGTH_SUPER_SHORT)).append(": ")
						.append(alignScoreText(user.getStringManager().getNameShort(), UIText.UI_GUNGAME.text(Integer.toString(user.getScoreManager().getScore()), Integer.toString(ModeGunGame.weaponOrder.length)),
								MAX_NAME_LENGTH_SUPER_SHORT, MAX_CHARACTERS))
						.append(UIText.UI_GUNGAME.text(Integer.toString(user.getScoreManager().getScore()), Integer.toString(ModeGunGame.weaponOrder.length))).append("\n");
					scoreNum++;
					if (MAX_SCORES < scoreNum) {
						break;
					}
				}
			}
		}
	}

	public void processArcadeRound(StringBuilder text) {
		if (state.getUIManager().getScoreWindow() != null) {
			if (SettingArcade.overtime) {
				text.append(UIText.UI_ARCADE_ROUND.text(String.valueOf(SettingArcade.currentRound))).append("\n");
			} else {
				if (SettingArcade.roundNum == 0) {
					text.append(UIText.UI_ARCADE_ROUND.text(String.valueOf(SettingArcade.currentRound))).append("\n");
				} else {
					text.append(UIText.UI_ARCADE_ROUND_LIMIT.text(String.valueOf(SettingArcade.currentRound),
							String.valueOf(SettingArcade.roundNum))).append("\n");
				}

				if (SettingArcade.winCap != 0) {
					text.append(UIText.UI_ARCADE_WIN_CAP.text(String.valueOf(SettingArcade.winCap))).append("\n");
				}
			}
		}
	}

	/**
	 * This creates a string of empty spaces between 2 strings to ensure the total length is a certain number of characters long
	 */
	private String alignScoreText(String name, String score, int maxNameLength, int maxTotalLength) {
		int spaces = maxTotalLength - score.length();
		spaces -= (name.length() > maxNameLength ? maxNameLength + 1 : name.length());
		return " ".repeat(Math.max(0, spaces));
	}

	private static String lastTags = "";
	/**
	 * Upon setting a boss, this ui is cleared to ovoid overlapping with boss ui.
	 * Save last tags so we can redisplay them when boss is cleared
	 */
	public void setBoss() {
		lastTags = getCurrentTags();
		changeTypes("", true);
	}

	public void clearBoss() {
		changeTypes(lastTags, true);
	}
}
