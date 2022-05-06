package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.UITag.uiType;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.map.SettingTeamMode;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.HText;
import com.mygdx.hadal.utils.Stats;

import static com.mygdx.hadal.utils.Constants.MAX_NAME_LENGTH_SHORT;

/**
 * The UIExtra is an extra ui actor displayed in the upper left hand side.
 * It displays list of strings decided by the uiTags list which can be modified in level with events.
 * @author Yacardo Yarabba
 */
public class UIExtra extends AHadalActor {

	private final PlayState state;

	private static final int x = 10;
	private static final int width = 240;
	private static final int y = 10;
	private static final float fontScale = 0.25f;
	
	//List of tags that are to be displayed
	private final Array<UITag> uiTags = new Array<>();
	
	//Timer is used for timed scripted events. timerIncr is how much the timer should tick every update cycle (usually -1, 0 or 1)
	private float maxTimer, timer, timerIncr;

	//this is the displayed time
	private int currentTimer;
	private String displayedTimer;
	private short viewingUserTeam;

	private final TextureRegion hpBar, hpBarFade;

	public UIExtra(PlayState state) {
		this.state = state;
		this.hpBar = Sprite.UI_MAIN_HEALTHBAR.getFrame();
		this.hpBarFade = Sprite.UI_MAIN_HEALTH_MISSING.getFrame();
	}
	
	private final StringBuilder text = new StringBuilder();
	@Override
    public void draw(Batch batch, float alpha) {

		if (state.getGsm().getSetting().isHideHUD()) { return; }

		HadalGame.FONT_UI.getData().setScale(fontScale);
		HadalGame.FONT_UI.draw(batch, text.toString(), x, HadalGame.CONFIG_HEIGHT - y, width, Align.left, true);

		renderTeamHp(batch, viewingUserTeam);
	}

	private static final int hpWidth = 60;
	private static final int hpHeight = 8;
	private static final int hpBarOffsetY = -9;
	private static final int nameMaxLength = 205;
	private static final int rowHeight = 14;
	private static final int startYExtra = 200;
	private static final int startXExtra = 10;
	private void renderTeamHp(Batch batch, short viewingUserTeam) {
		if (state.getScoreWindow() == null) { return; }
		if (state.getMode().getTeamMode() == SettingTeamMode.TeamMode.FFA) { return; }

		float currentY = HadalGame.CONFIG_HEIGHT - startYExtra;
		for (User user: state.getScoreWindow().getOrderedUsers()) {
			if (!user.isSpectator() && user.getPlayer() != null) {
				if (user.getPlayer().getPlayerData() != null && !user.getPlayer().equals(state.getPlayer())) {
					if (user.getPlayer().getHitboxfilter() == viewingUserTeam) {
						HadalGame.FONT_UI.draw(batch, WeaponUtils.getPlayerColorName(user.getPlayer(), MAX_NAME_LENGTH_SHORT),
								HadalGame.CONFIG_WIDTH - nameMaxLength - hpWidth - startXExtra, currentY, nameMaxLength, Align.left, true);

						float hpRatio = user.getPlayer().getPlayerData().getCurrentHp() /
								user.getPlayer().getPlayerData().getStat(Stats.MAX_HP);
						if (!user.getPlayer().isAlive()) {
							hpRatio = 0.0f;
						}
						batch.draw(hpBarFade, HadalGame.CONFIG_WIDTH - hpWidth - startXExtra, currentY + hpBarOffsetY, hpWidth, hpHeight);
						batch.draw(hpBar, HadalGame.CONFIG_WIDTH - hpWidth - startXExtra, currentY + hpBarOffsetY, hpWidth * hpRatio, hpHeight);
						currentY -= rowHeight;
					}
				}
			}
		}
	}

	/**
	 * This is run whenever the contents of the ui change. It sets the text according to updated tags and info
	 */
	public void syncUIText(uiType changedType) {

		//clear existing text
		text.setLength(0);

		User user = null;

		//if we are spectating another player, we want to ui to match the spectate target instead of ourselves
		boolean spectatorFound = false;
		if (state.isSpectatorMode()) {
			if (state.getUiSpectator().getSpectatorTarget() != null) {
				user = state.getUiSpectator().getSpectatorTarget().getUser();
				spectatorFound = true;
			}
		}
		if (!spectatorFound) {
			if (state.isServer()) {
				user = HadalGame.server.getUsers().get(0);
			} else {
				user = HadalGame.client.getUsers().get(HadalGame.client.connID);
			}
		}

		//check if user is null b/c several ui tags require checking user information
		if (user != null) {
			if (user.getPlayer() != null) {
				viewingUserTeam = user.getPlayer().getHitboxfilter();
			}
			for (UITag uiTag : uiTags) {
				text.append(uiTag.updateTagText(state, changedType, user));
			}
		}

		HadalGame.FONT_UI.getData().setScale(fontScale);
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
			for (UITag.uiType tag: UITag.uiType.values()) {
				if (tag.toString().equals(type)) {
					found = true;
					uiTags.add(new UITag(this, tag));
					break;
				}
			}

			//If a string matches no tag types, we just display the text as is.
			if (!found) {
				uiTags.add(new UITag(this, uiType.MISC, type));
			}
		}
		syncUIText(uiType.ALL);
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
			if (tag.getType().equals(uiType.MISC)) {
				tags.append(tag.getMisc()).append(",");
			} else {
				tags.append(tag.getType().toString()).append(",");
			}
		}
		return tags.toString();
	}

	/**
	 * Change the game timer settings
	 * @param timerSet: This sets the time to a designated amount
	 * @param timerIncrement: This sets the amount of time that changes each second (usually -1, 0 or 1)
	 */
	public void changeTimer(float timerSet, float timerIncrement) {
		maxTimer = timerSet;
		timer = timerSet;
		timerIncr = timerIncrement;

		syncUIText(uiType.TIMER);
	}

	//display a time warning when the time is low
	private final static float notificationThreshold = 10.0f;
	/**
	 * This increments the timer for timed levels. When time runs out, we want to run an event designated in the map (if it exists)
	 * @param delta: amount of time that has passed since last update
	 */
	public void incrementTimer(float delta) {

		if (timer > notificationThreshold && timer + (timerIncr * delta) < notificationThreshold) {
			state.getKillFeed().addNotification(HText.TIMER_REMAINING.text(), false);
		}

		timer += (timerIncr * delta);

		//timer text changes after a whole second passes
		if ((int) timer != currentTimer) {
			currentTimer = (int) timer;

			//convert the time to minutes:seconds
			int seconds = currentTimer % 60;

			//this makes the timer have the same number of characters whether the seconds amount is 1 or 2 digits
			if (seconds < 10) {
				displayedTimer = currentTimer / 60 + ": 0" + seconds;
			} else {
				displayedTimer = currentTimer / 60 + ": " + seconds;
			}
			syncUIText(uiType.TIMER);
		}

		//upon timer running out, a designated event activates.
		if (timer <= 0 && timerIncr < 0) {
			if (state.getGlobalTimer() != null) {
				state.getGlobalTimer().getEventData().preActivate(null, null);
				timerIncr = 0;
			}
		}
	}

	private static final int maxScores = 5;
	/**
	 * For modes with a scoreboard ui tag, we add a sorted list of player scores. List is, at most, 5 names long
	 * @param text: the stringbuilder we will be appending the scoreboard text to
	 */
	public void sortIndividualScores(StringBuilder text) {
		if (state.getScoreWindow() != null) {
			int scoreNum = 0;
			for (User user: state.getScoreWindow().getOrderedUsers()) {
				if (!user.isSpectator()) {
					text.append(user.getNameAbridgedColored(MAX_NAME_LENGTH_SHORT)).append(": ").append(user.getScores().getScore()).append("\n");
					scoreNum++;
					if (scoreNum > maxScores) {
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
			rgb.set(AlignmentFilter.currentTeams[i].getColor1().getRGB());
			String hex = "#" + Integer.toHexString(Color.rgb888(rgb.x, rgb.y, rgb.z));
			text.append("[").append(hex).append("]").append(AlignmentFilter.currentTeams[i].getTeamName())
				.append("[]").append(": ").append(AlignmentFilter.teamScores[i]).append("\n");
			scoreNum++;
			if (scoreNum > maxScores) {
				break;
			}
		}
	}

	public void sortTeamAlive(StringBuilder text) {
		if (state.getScoreWindow() != null) {
			int scoreNum = 0;
			for (int i = 0; i < AlignmentFilter.teamScores.length; i++) {
				int numAlive = 0;
				for (User user: state.getScoreWindow().getOrderedUsers()) {
					if (!user.isSpectator()) {
						if (user.getPlayer() != null) {
							if (user.getPlayer().isAlive()) {
								if (user.getPlayer().getStartLoadout().team == AlignmentFilter.currentTeams[i]) {
									numAlive++;
								}
							}
						}
					}
				}
				rgb.set(AlignmentFilter.currentTeams[i].getColor1().getRGB());
				String hex = "#" + Integer.toHexString(Color.rgb888(rgb.x, rgb.y, rgb.z));
				text.append("[").append(hex).append("]").append(AlignmentFilter.currentTeams[i].getTeamName())
						.append("[]").append(": ").append(numAlive).append(" ").append(HText.PLAYERS_ALIVE.text()).append("\n");
				scoreNum++;
				if (scoreNum > maxScores) {
					break;
				}
			}
		}
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

	public float getTimer() { return timer; }

	public void setTimer(float timer) { this.timer = timer; }

	public float getTimerIncr() { return timerIncr; }

	public void setTimerIncr(float timerIncr) { this.timerIncr = timerIncr; }

	public float getMaxTimer() { return maxTimer; }

	public String getDisplayedTimer() { return displayedTimer; }
}
