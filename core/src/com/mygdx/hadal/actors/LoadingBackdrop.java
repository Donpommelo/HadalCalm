package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.states.PlayState;

/**
 * Background actor for initializing screen.
 */
public class LoadingBackdrop extends AHadalActor{
	
	private TextureAtlas atlas;
	
	//This is the animation of this sprite
	private Animation<TextureRegion> loading;
	
	//precentage of assets loaded.
	private float progress;
	
	//width and height of image
	private float width, height;
	
	public LoadingBackdrop() {
		atlas = new TextureAtlas("ui/anchor_logo.atlas");
		loading = new Animation<TextureRegion>(PlayState.spriteAnimationSpeed, atlas.findRegions("anchor_logo"));
		
		//This image takes up the whole screen.
		this.width = loading.getKeyFrame(0).getRegionWidth();
		this.height = loading.getKeyFrame(0).getRegionHeight();
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		
		//Draw the animation at the percentage of progress
        progress = HadalGame.assetManager.getProgress();
        batch.draw((TextureRegion) loading.getKeyFrame(progress * loading.getAnimationDuration(), true), (HadalGame.CONFIG_WIDTH - width) / 2, (HadalGame.CONFIG_HEIGHT - height) / 2, width, height);
    }
}
