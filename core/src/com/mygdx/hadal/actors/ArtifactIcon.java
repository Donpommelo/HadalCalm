package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockArtifact;

/**
 * An Artifact icon is a ui element that corresponds to a single artifact in the reliquary hub event.
 * @author Zachary Tu
 */
public class ArtifactIcon extends AHadalActor {

	private static float padding = 20.0f;
	
	//This is the artifact that this tag represents
	private UnlockArtifact artifact;
	
	private BitmapFont font;
	
	private float scale = 0.25f;
	private Color color;
	protected GlyphLayout layout;
	
	private TextureRegion ready;
	private String text;
	
	private float textOffsetX, textOffsetY, targetWidth;
	
	//is this ui element moused over? display extra info if it is.
	private boolean mouseOver;
	
	public ArtifactIcon(UnlockArtifact artifact, String text, float textOffsetX, float textOffsetY, float targetWidth) {
		this.artifact = artifact;
		this.text = text;
		this.textOffsetX = textOffsetX;
		this.textOffsetY = textOffsetY;
		this.targetWidth = targetWidth;
		
		font = HadalGame.SYSTEM_FONT_UI;
		font.getData().setScale(scale);
		color = Color.WHITE;
		
		this.ready = Sprite.UI_MO_READY.getFrame();
		
		layout = new GlyphLayout();
		layout.setText(font, text, color, targetWidth, Align.left, true);
		
		addListener(new ClickListener() {
			
			@Override
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				super.enter(event, x, y, pointer, fromActor);
				mouseOver = true;
			}

			@Override
			public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
				super.enter(event, x, y, pointer, toActor);
				mouseOver = false;
			}
		});
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		batch.draw(ready, getX(), getY(), getWidth(), getHeight());
         
         if (mouseOver) {
        	 GameStateManager.getSimplePatch().draw(batch, getX() - padding / 2 + textOffsetX, getY() - padding / 2 + textOffsetY, layout.width + padding, layout.height + padding);
        	 font.setColor(color);
        	 font.getData().setScale(scale);
        	 font.draw(batch, text, getX() + textOffsetX, getY() + textOffsetY + layout.height, targetWidth, Align.left, true);
         }
    }

	public UnlockArtifact getArtifact() { return artifact; }
}
