package com.mygdx.hadal.actors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.UITag.uiType;
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
	private final static int x = 200;
	private final static int y = 10;
	
	//List of tags that are to be displayed
	private ArrayList<UITag> uiTags;
	
	//These variables are all fields that are displayed in the default tags for the ui
	private int score, lives, extraVar1, extraVar2;
	private float timer;
	
	public UIExtra(AssetManager assetManager, PlayState state) {
		super(assetManager);
		this.state = state;
		this.font = HadalGame.SYSTEM_FONT_UI;
		
		//Default tags include score and hi-score.
		uiTags = new ArrayList<UITag>();
		uiTags.add(new UITag(uiType.SCORE, "", 0.4f));
		uiTags.add(new UITag(uiType.HISCORE, "", 0.4f));
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		batch.setProjectionMatrix(state.hud.combined);

		font.getData().setScale(0.4f);
		
		String text = "";
		
		for (UITag tag : uiTags) {
			switch(tag.getType()) {
			case SCRAP:
				text = text.concat("SCRAP: " + state.getGsm().getRecord().getScrap() + "\n");
				break;
			case SCRIP:
				text = text.concat("SCRIP: " + state.getGsm().getRecord().getScrip() + "\n");
				break;
			case LIVES:
				text = text.concat("LIVES: " + lives + "\n");
				break;
			case SCORE:
				text = text.concat("SCORE: " + score + "\n");
				break;
			case TIMER:
				text = text.concat("TIMER: " + timer + " S\n");
				break;
			case HISCORE:
				text = text.concat("HI-SCORE: " + state.getGsm().getRecord().getHiScores().get(state.getLevel().name()) + "\n");
				break;
			case VAR1:
				text = text.concat(" " + extraVar1);
				break;
			case VAR2:
				text = text.concat(" " + extraVar2);
				break;
			case MISC:
				text = text.concat(tag.getMisc());
				break;
			case EMPTY:
				text = text.concat("\n");
				break;
			default:
				break;
			}	
		}
		font.draw(batch, text, HadalGame.CONFIG_WIDTH - x, HadalGame.CONFIG_HEIGHT - y);
	}
	
	/**
	 * This method is called when you want to change the tags in the ui.
	 * This method receives a varargs input of types. This is used for default tags.
	 * 
	 * Also, for all changeTypes methods changeType indicates the id of the change intended
	 * -1: remove
	 * 0: set
	 * 1: add
	 */
	public void changeTypes(int changeType, uiType... types) {
		
		ArrayList<UITag> tags = new ArrayList<UITag>();
		
		for (uiType type : types) {
			tags.add(new UITag(type));
		}
		
		changeTypes(changeType, tags);
	}

	/*
	 * This methid is like the one above except for varargs of tags, not types. Needed whe nadding MISc tags with extra fields.
	 */
	public void changeTypes(int changeType, UITag... tags) {
		changeTypes(changeType, Arrays.asList(tags));
	}
	
	/*
	 * This is where we actually, remove, set or add tags
	 */
	public void changeTypes(int changeType, List<UITag> tags) {
		
		if (changeType == 0) {
			uiTags.clear();
		}
		
		for (UITag tag : tags) {
			switch(changeType) {
			case -1:
				uiTags.remove(tag);
				break;
			case 0:
			case 1:
				uiTags.add(tag);
				break;
			}
		}
	}

	public int getScore() {
		return score;
	}
	
	public void incrementScore(int i) {
		score += i;
	}
	
	public int getLives() {
		return lives;
	}

	public void incrementLives(int lives) {
		this.lives += lives;
	}

	public float getTimer() {
		return timer;
	}

	public void incrementTimer(float timer) {
		this.timer += timer;
	}
	
	public float getVar1() {
		return extraVar1;
	}

	public void incrementVar1(int var1) {
		this.extraVar1 += var1;
	}
	
	public float getVar2() {
		return extraVar2;
	}

	public void incrementVar2(int var2) {
		this.extraVar2 += var2;
	}
}
