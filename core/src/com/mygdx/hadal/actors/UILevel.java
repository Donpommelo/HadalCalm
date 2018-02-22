package com.mygdx.hadal.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.states.PlayState;

public class UILevel extends AHadalActor{

	private PlayState state;
	private BitmapFont font;
	
	private final static int x = 300;
	private final static int y = 10;
	
	
	public UILevel(AssetManager assetManager, PlayState state) {
		super(assetManager);
		this.state = state;
		this.font = HadalGame.SYSTEM_FONT_UI;
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		batch.setProjectionMatrix(state.hud.combined);

		font.getData().setScale(1.0f);
		font.draw(batch, "SCORE: " + state.getScore(), HadalGame.CONFIG_WIDTH - x, HadalGame.CONFIG_HEIGHT - y);
//		font.draw(batch, "LEVEL: " + state.getLevel(), HadalGame.CONFIG_WIDTH - x , HadalGame.CONFIG_HEIGHT - y - 100);
	}

}
