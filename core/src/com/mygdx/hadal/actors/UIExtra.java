package com.mygdx.hadal.actors;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.UITag.uiType;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.states.PlayState;

/**
 * The UIExtra is an extra ui actor displayed in the upper right hand side.
 * It displays list of strings decided by the uiTags list which can be modified in level with events.
 * @author Zachary Tu
 *
 */
public class UIExtra extends AHadalActor{

	private PlayState state;
	private BitmapFont font;
	
	private final static int x = 150;
	private final static int y = 10;
	private final static float scale = 0.25f;
	
	//List of tags that are to be displayed
	private ArrayList<UITag> uiTags;
	
	//These variables are all fields that are displayed in the default tags for the ui
	private int scrap, score, lives, wins, hiscore;
	
	//Timer is used for timed scripted events. timerIncr is how much the timer should tick every update cycle (usually -1, 0 or 1)
	private float timer, timerIncr;
	
	public UIExtra(PlayState state) {
		this.state = state;
		this.font = HadalGame.SYSTEM_FONT_UI;
		
		uiTags = new ArrayList<UITag>();
	}
	
	private StringBuilder text = new StringBuilder();
	@Override
    public void draw(Batch batch, float alpha) {
		
		text.setLength(0);
		
		for (UITag tag : uiTags) {
			switch(tag.getType()) {
			case SCRAP:
				text = text.append("SCRAP: " + scrap + "\n");
				break;
			case LIVES:
				text = text.append("LIVES: " + lives + "\n");
				break;
			case SCORE:
				text = text.append("SCORE: " + score + "\n");
				break;
			case WINS:
				text = text.append("WINS: " + wins + "\n");
				break;
			case TIMER:
				text = text.append("TIMER: " + (int)timer + " S\n");
				break;
			case HISCORE:
				text = text.append("HI-SCORE: " + hiscore + "\n");
				break;
			case MISC:
				text = text.append(tag.getMisc() + "\n");
				break;
			case EMPTY:
				text = text.append("\n");
				break;
			default:
				break;
			}	
		}
		font.getData().setScale(scale);
		font.draw(batch, text.toString(), HadalGame.CONFIG_WIDTH - x, HadalGame.CONFIG_HEIGHT - y, x, -1, true);
	}

	/**
	 * This adds tags to the ui
	 * @param tags: These are the tags that should be added. This is a comma-separated string of each uiType. If a string matches no tag types, we just display the text as is.
	 * @param clear: Do we clear all the existing ui tags first?
	 */
	public void changeTypes(String tags, boolean clear) {
		
		if (clear) {
			uiTags.clear();
		}
		
		for (String type : tags.split(",")) {
			
			boolean found = false;
			
			for (UITag.uiType tag: UITag.uiType.values()) {
				if (tag.name().equals(type)) {
					found = true;
					uiTags.add(new UITag(tag));
				}
			}
			
			if (!found) {
				uiTags.add(new UITag(uiType.MISC, type));
			}
		}
	}
	
	private StringBuilder tags = new StringBuilder();
	/**
	 * This returns a comma-separated string of the current tags in order.
	 * When a new client connects, the server uses this to send them the current ui status to sync with.
	 */
	public String getCurrentTags() {
		tags.setLength(0);
		
		for (UITag tag : uiTags) {
			if (tag.getType().equals(uiType.MISC)) {
				tags.append(tag.getMisc() + ",");
			} else {
				tags.append(tag.getType().toString() + ",");
			}
		}
		return tags.toString();
	}

	/**
	 * This syncs the info that this ui displays.
	 * The ui contains values for each displayed field and must update them when these fields change.
	 * This occurs when any player field is changed by changeFields() or when the score window is synced.
	 * Also when certain record values are changed (atm this is just for currency changing) 
	 */
	public void syncData() {
		scrap = state.getGsm().getRecord().getScrap();
		hiscore = state.getGsm().getRecord().getHiScores().get(state.getLevel().name());
		
		SavedPlayerFields field = null;
		
		if (state.isServer()) {
			field = HadalGame.server.getScores().get(0);
		} else {
			field = HadalGame.client.getScores().get(HadalGame.client.connID);
		}
		
		if (field != null) {
			score = field.getScore();
			wins = field.getWins();
			lives = field.getLives();
		}
	}
	
	/**
	 * This is run when any info displayed by this ui is changed. atm, this is just run when a playerChanger event activates for a specific player
	 * @param p: The player whose field has changed. If null, this change applies to all players.
	 * @param score, lives, timerSet, timerIncrement: Amount to change. default 0.
	 */
	public void changeFields(Player p, int score, int lives, float timerSet, float timerIncrement) {
		
		SavedPlayerFields field = null;
		
		if (p == null) {
			for (SavedPlayerFields eachField: HadalGame.server.getScores().values()) {
				eachField.setScore(eachField.getScore() + score);
				eachField.setLives(eachField.getLives() + lives);
				
				//If all players are losing lives at once and they have 0 lives, they get a game over.
				if (eachField.getLives() <= 0) {
					state.levelEnd("GAME OVER");
					break;
				}
			}
		} else {
			field = HadalGame.server.getScores().get(p.getConnID());	
			if (field != null) {
				field.setScore(field.getScore() + score);
				field.setLives(field.getLives() + lives);
				
				//If a single player runs out of lives, they die
				if (field.getLives() <= 0) {
					p.getPlayerData().die(state.getWorldDummy().getBodyData());
				}
			}
		}
		
		timer = timerSet;
		timerIncr = timerIncrement;
		
		state.getScoreWindow().syncTable();
	}
	
	/**
	 * This increments the timer for timed levels. When time runs out, we want to run an event designated in the map (if it exists)
	 * @param delta: amount of time that has passed since last update
	 */
	public void incrementTimer(float delta) {
		timer += (timerIncr * delta);
		
		if (timer <= 0 && timerIncr < 0) {
			if (state.getGlobalTimer() != null) {
				state.getGlobalTimer().getEventData().preActivate(null, null);
			}
		}
	}

	public float getTimer() { return timer; }

	public void setTimer(float timer) { this.timer = timer; }

	public float getTimerIncr() { return timerIncr; }

	public void setTimerIncr(float timerIncr) { this.timerIncr = timerIncr; }
}
