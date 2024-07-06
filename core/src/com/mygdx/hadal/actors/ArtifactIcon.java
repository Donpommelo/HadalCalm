package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.mygdx.hadal.save.UnlockArtifact;

import static com.mygdx.hadal.managers.SkinManager.FONT_UI;
import static com.mygdx.hadal.managers.SkinManager.SIMPLE_PATCH;

/**
 * An Artifact icon is a ui element that corresponds to a single artifact in the reliquary hub event.
 * These are also used in the current artifacts ui and results screen
 * @author Ghargarine Ghoatmeal
 */
public class ArtifactIcon extends AHadalActor {

	private static final float ICON_PAD = 20.0f;

	private static final float FONT_SCALE = 0.25f;
	private static final Color COLOR = Color.WHITE;

	//This is the artifact that this tag represents
	private final UnlockArtifact artifact;

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
		
		this.icon = artifact.getFrameSmall();

		FONT_UI.getData().setScale(FONT_SCALE);
		layout = new GlyphLayout();
		layout.setText(FONT_UI, text, COLOR, targetWidth, Align.left, true);

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
		 SIMPLE_PATCH.draw(batch, getX() - ICON_PAD / 2 + textOffsetX,
				 getY() - ICON_PAD / 2 + textOffsetY, layout.width + ICON_PAD, layout.height + ICON_PAD);
		 FONT_UI.setColor(COLOR);
		 FONT_UI.getData().setScale(FONT_SCALE);
		 FONT_UI.draw(batch, text, getX() + textOffsetX, getY() + textOffsetY + layout.height,
				 targetWidth, Align.left, true);
		}
    }

	public UnlockArtifact getArtifact() { return artifact; }
}
