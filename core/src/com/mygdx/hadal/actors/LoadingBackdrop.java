package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.states.PlayState;

/**
 * Background actor for initializing screen.
 * @author Frulfpants Fulnpants
 */
public class LoadingBackdrop extends AHadalActor {
	
	//atlas for the displayed image. exposed with getter so it can be disposed of.
	private final TextureAtlas atlas;
	
	//This is the animation of this sprite
	private final Animation<TextureRegion> loading;
	
	//width and height of image
	private final float width, height;
	
	public LoadingBackdrop() {
		atlas = new TextureAtlas("ui/anchor_logo.atlas");
		loading = new Animation<>(PlayState.SPRITE_ANIMATION_SPEED_FAST, atlas.findRegions("anchor_logo"));
		
		//This image takes up the whole screen.
		this.width = loading.getKeyFrame(0).getRegionWidth();
		this.height = loading.getKeyFrame(0).getRegionHeight();
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		
		//Draw the animation at the percentage of progress
        batch.draw(loading.getKeyFrame(HadalGame.assetManager.getProgress() * loading.getAnimationDuration(), true),
			(HadalGame.CONFIG_WIDTH - width) / 2, (HadalGame.CONFIG_HEIGHT - height) / 2, width, height);
    }
	
	public TextureAtlas getAtlas() { return atlas; }
}
