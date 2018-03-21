package com.mygdx.hadal.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.states.PlayState;

public class UILevel extends AHadalActor{

	private PlayState state;
	private BitmapFont font;
	
	private final static int x = 200;
	private final static int y = 10;
	
	private uiType type;
	
	private int score, extraVar;
	
	public UILevel(AssetManager assetManager, PlayState state) {
		super(assetManager);
		this.state = state;
		this.font = HadalGame.SYSTEM_FONT_UI;
		
		type = uiType.SCORES;
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		batch.setProjectionMatrix(state.hud.combined);

		font.getData().setScale(0.5f);
		
		switch(type) {
		case HUB:
			font.draw(batch, "SCRAP: " + state.getGsm().getRecord().getScrap(), 
					HadalGame.CONFIG_WIDTH - x, HadalGame.CONFIG_HEIGHT - y);
			font.draw(batch, "SCRIP: " + state.getGsm().getRecord().getScrip(), 
					HadalGame.CONFIG_WIDTH - x, HadalGame.CONFIG_HEIGHT - y - 30);
			break;
		case LIVES:
			font.draw(batch, "LIVES: " + extraVar, HadalGame.CONFIG_WIDTH - x, HadalGame.CONFIG_HEIGHT - y - 60);
		case SCORES:
			font.draw(batch, "SCORE: " + score, HadalGame.CONFIG_WIDTH - x, HadalGame.CONFIG_HEIGHT - y);
			font.draw(batch, "HI-SCORE: " + state.getGsm().getRecord().getHiScores().get(state.getLevel().name()),
					HadalGame.CONFIG_WIDTH - x , HadalGame.CONFIG_HEIGHT - y - 30);
			break;
		default:
			break;
		}		
	}
	
	public uiType getType() {
		return type;
	}

	public void setType(uiType type) {
		this.type = type;
	}

	public int getScore() {
		return score;
	}
	
	public void incrementScore(int i) {
		score += i;
	}
	
	public int getExtraVar() {
		return extraVar;
	}

	public void incrementExtraVar(int extraVar) {
		this.extraVar += extraVar;
	}

	public enum uiType {
		SCORES,
		HUB,
		LIVES,
	}
}
