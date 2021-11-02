package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.UITag.uiType;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;

import java.util.ArrayList;

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
	private final ArrayList<UITag> uiTags;
	
	//Timer is used for timed scripted events. timerIncr is how much the timer should tick every update cycle (usually -1, 0 or 1)
	private float maxTimer, timer, timerIncr;

	//this is the displayed time
	private int currentTimer;
	private String displayedTimer;

	public UIExtra(PlayState state) {
		this.state = state;
		uiTags = new ArrayList<>();
	}
	
	private final StringBuilder text = new StringBuilder();
	@Override
    public void draw(Batch batch, float alpha) {

		if (state.getGsm().getSetting().isHideHUD()) { return; }

		HadalGame.FONT_UI.getData().setScale(fontScale);
		HadalGame.FONT_UI.draw(batch, text.toString(), x, HadalGame.CONFIG_HEIGHT - y, width, Align.left, true);
	}

	/**
	 * This is run whenever the contents of the ui change. It sets the text according to updated tags and info
	 */
	public void syncUIText(uiType changedType) {
		text.setLength(0);

		User user;
		if (state.isServer()) {
			user = HadalGame.server.getUsers().get(0);
		} else {
			user = HadalGame.client.getUsers().get(HadalGame.client.connID);
		}

		if (user != null) {
			for (UITag uiTag : uiTags) {
				text.append(uiTag.updateTagText(state, changedType, user));
			}
		}
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
			state.getKillFeed().addNotification("10 SECONDS REMAINING", false);
		}

		timer += (timerIncr * delta);

		if ((int) timer != currentTimer) {
			currentTimer = (int) timer;

			//convert the time to minutes:seconds
			int seconds = currentTimer % 60;
			if (seconds < 10) {
				displayedTimer = currentTimer / 60 + ": 0" + seconds;

			} else {
				displayedTimer = currentTimer / 60 + ": " + seconds;
			}
			syncUIText(uiType.TIMER);
		}

		//upon timer running out, a designated event activates
		if (timer <= 0 && timerIncr < 0) {
			if (state.getGlobalTimer() != null) {
				state.getGlobalTimer().getEventData().preActivate(null, null);
				timerIncr = 0;
			}
		}
	}

	private static final int maxScores = 5;
	/**
	 * For modes with a scoreboard ui tag, we add a sorted list of player scores.
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
	public void sortTeamScores(StringBuilder text) {
		int scoreNum = 0;
		for (int i = 0; i < AlignmentFilter.teamScores.length; i++) {
			rgb.set(AlignmentFilter.currentTeams[i].getColor1RGB());
			String hex = "#" + Integer.toHexString(Color.rgb888(rgb.x, rgb.y, rgb.z));
			text.append("[").append(hex).append("]").append(AlignmentFilter.currentTeams[i].toString())
				.append("[]").append(": ").append(AlignmentFilter.teamScores[i]).append("\n");
			scoreNum++;
			if (scoreNum > maxScores) {
				break;
			}
		}
	}

	public float getTimer() { return timer; }

	public void setTimer(float timer) { this.timer = timer; }

	public float getTimerIncr() { return timerIncr; }

	public void setTimerIncr(float timerIncr) { this.timerIncr = timerIncr; }

	public float getMaxTimer() { return maxTimer; }

	public String getDisplayedTimer() { return displayedTimer; }
}
