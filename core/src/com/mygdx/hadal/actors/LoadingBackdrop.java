package com.mygdx.hadal.actors;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.hadal.HadalGame;

/**
 * Background actor for initializing screen.
 */
public class LoadingBackdrop extends AHadalActor{
	
	private TextureAtlas atlas;
	
	//This is the animation of this sprite
	private Animation<TextureRegion> loading;
	
	private float progress, width, height;
	
	public LoadingBackdrop(AssetManager assetManager) {
		super(assetManager);
		atlas = new TextureAtlas("ui/anchor_logo.atlas");
		loading = new Animation<TextureRegion>(0.08f, atlas.findRegions("anchor_logo"));
		this.width = loading.getKeyFrame(0).getRegionWidth();
		this.height = loading.getKeyFrame(0).getRegionHeight();
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
        progress = assetManager.getProgress();
        batch.draw((TextureRegion) loading.getKeyFrame(progress * loading.getAnimationDuration(), true), 
        		(HadalGame.CONFIG_WIDTH - width) / 2, (HadalGame.CONFIG_HEIGHT - height) / 2, width, height);
    }
}
