package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockArtifact;

/**
 * An Artifact icon is a ui element that corresponds to a single artifact in the reliquary hub event.
 * These are also used in the current artifacts ui and results screen
 * @author Ghargarine Ghoatmeal
 */
public class ArtifactIcon extends AHadalActor {

	private static final float pad = 20.0f;
	
	//This is the artifact that this tag represents
	private final UnlockArtifact artifact;

	private static final float fontScale = 0.25f;
	private static final Color color = Color.WHITE;
	protected final GlyphLayout layout;
	
	private final TextureRegion icon;
	private final String text;
	
	private final float textOffsetX, textOffsetY, targetWidth;
	
	//is this ui element moused over? display extra info if it is.
	private boolean mouseOver;
	
	public ArtifactIcon(UnlockArtifact artifact, String text, float textOffsetX, float textOffsetY, float targetWidth) {
		this.artifact = artifact;
		this.text = text;
		this.textOffsetX = textOffsetX;
		this.textOffsetY = textOffsetY;
		this.targetWidth = targetWidth;
		
		this.icon = artifact.getFrame();

		HadalGame.FONT_UI.getData().setScale(fontScale);
		layout = new GlyphLayout();
		layout.setText(HadalGame.FONT_UI, text, color, targetWidth, Align.left, true);

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
		batch.draw(icon, getX(), getY(), getWidth(), getHeight());

		//this displays artifact short description when this actor is moused over
         if (mouseOver) {
        	 GameStateManager.getSimplePatch().draw(batch, getX() - pad / 2 + textOffsetX,
					 getY() - pad / 2 + textOffsetY, layout.width + pad, layout.height + pad);
			 HadalGame.FONT_UI.setColor(color);
			 HadalGame.FONT_UI.getData().setScale(fontScale);
			 HadalGame.FONT_UI.draw(batch, text, getX() + textOffsetX, getY() + textOffsetY + layout.height,
					 targetWidth, Align.left, true);
         }
    }

	public UnlockArtifact getArtifact() { return artifact; }
}
