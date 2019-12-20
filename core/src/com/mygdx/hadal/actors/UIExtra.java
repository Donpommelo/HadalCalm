package com.mygdx.hadal.actors;

import java.util.ArrayList;

import com.badlogic.gdx.assets.AssetManager;
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
	
	//Default starting location of window.
	private final static int x = 150;
	private final static int y = 10;
	
	private final static float scale = 0.25f;
	
	//List of tags that are to be displayed
	private ArrayList<UITag> uiTags;
	
	//These variables are all fields that are displayed in the default tags for the ui
	private int scrap, score, lives, wins, hiscore;
	private float timer, timerIncr;
	
	public UIExtra(AssetManager assetManager, PlayState state) {
		super(assetManager);
		this.state = state;
		this.font = HadalGame.SYSTEM_FONT_UI;
		
		//Default tags include score and hi-score.
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
				text = text.append("TIMER: " + timer + " S\n");
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

	/*
	 * This is where we actually, remove, set or add tags
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
	
	public void changeFields(Player p, int score, int lives, float timerSet, float timerIncrement) {
		
		SavedPlayerFields field = null;
		
		if (p == null) {
			for (SavedPlayerFields eachField: HadalGame.server.getScores().values()) {
				eachField.setScore(eachField.getScore() + score);
				eachField.setLives(eachField.getLives() + lives);
				
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
				
				if (field.getLives() <= 0) {
					p.getPlayerData().die(state.getWorldDummy().getBodyData(), null);
				}
			}
		}
		
		timer = timerSet;
		timerIncr = timerIncrement;
		
		state.getScoreWindow().syncTable();
	}
	
	public void incrementTimer(float delta) {
		timer += (timerIncr * delta);
	}
	
	public float getTimer() {
		return timer;
	}
}
