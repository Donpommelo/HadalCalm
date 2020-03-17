package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;

/**
 * Static background actor for title screen.
 */
public class ResultsBackdrop extends AHadalActor {
	
	private Texture backgroundTexture;
	
	public ResultsBackdrop() {
		backgroundTexture = HadalGame.assetManager.get(AssetList.RESULTS_CARD.toString());
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
        batch.draw(backgroundTexture, 0, 0, HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);
    }
}
