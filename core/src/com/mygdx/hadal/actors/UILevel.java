package com.mygdx.hadal.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.states.HubState;
import com.mygdx.hadal.states.PlayState;

public class UILevel extends AHadalActor{

	private PlayState state;
	private BitmapFont font;
	
	private final static int x = 200;
	private final static int y = 10;
	
	private boolean hub = false;
	
	public UILevel(AssetManager assetManager, PlayState state) {
		super(assetManager);
		this.state = state;
		this.font = HadalGame.SYSTEM_FONT_UI;
		
		if (state instanceof HubState) {
			hub = true;
		}
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		batch.setProjectionMatrix(state.hud.combined);

		font.getData().setScale(0.5f);
		if (hub) {
			font.draw(batch, "SCRAP: " + state.getGsm().getRecord().getScrap(), 
					HadalGame.CONFIG_WIDTH - x, HadalGame.CONFIG_HEIGHT - y);
			font.draw(batch, "SCRIP: " + state.getGsm().getRecord().getScrip(), 
					HadalGame.CONFIG_WIDTH - x, HadalGame.CONFIG_HEIGHT - y - 30);
		} else {
			font.draw(batch, "SCORE: " + state.getScore(), HadalGame.CONFIG_WIDTH - x, HadalGame.CONFIG_HEIGHT - y);
			font.draw(batch, "HI-SCORE: " + state.getGsm().getRecord().getHiScores().get(state.getLevel().name()),
					HadalGame.CONFIG_WIDTH - x , HadalGame.CONFIG_HEIGHT - y - 30);
		}
		
	}
}
