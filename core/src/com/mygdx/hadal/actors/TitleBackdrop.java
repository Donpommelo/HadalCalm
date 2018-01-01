package com.mygdx.hadal.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;

/**
 * Static background actor for title screen.
 */
public class TitleBackdrop extends AHadalActor{
	private Texture backgroundTexture;
	
	public TitleBackdrop(AssetManager assetManager) {
		super(assetManager);
		backgroundTexture = getAssetManager().get(AssetList.TITLE_CARD.toString());
		backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
        batch.draw(backgroundTexture, 0, 0, HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);
    }
}
