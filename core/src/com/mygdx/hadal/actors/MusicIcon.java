package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.MusicTrack;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.GameStateManager;

import static com.mygdx.hadal.utils.Constants.INTP_FASTSLOW;
import static com.mygdx.hadal.utils.Constants.TRANSITION_DURATION_SLOW;

/**
 * This icon is displayed at the start of levels to show the player the currently playing song
 * @author Boguana Blitherford
 */
public class MusicIcon extends AHadalActor {

	private final String text;
	private final GlyphLayout layout;
	private final Animation<TextureRegion> musicIcon;

	private static final float padX = 40.0f;
	private static final float padY = 20.0f;
	private static final float maxWidth = 300.0f;

	private static final float fontScale = 0.3f;
	private static final float animspeed = 0.04f;
	private static final float iconOffsetX = -85.0f;
	private static final float iconOffsetY = -40.0f;
	private static final float iconWidth = 90.0f;
	private static final float iconHeight = 90.0f;

	private static final float startX = 1330.0f;
	private static final float endX = 1280.0f;
	private static final float startY = 600.0f;

	public MusicIcon(MusicTrack track) {

		text = track.getMusicName();

		HadalGame.FONT_UI.getData().setScale(fontScale);
		layout = new GlyphLayout();
		layout.setText(HadalGame.FONT_UI, text);
		setWidth(layout.width);
		setHeight(layout.height);

		musicIcon = new Animation<>(animspeed,
			((TextureAtlas) HadalGame.assetManager.get(AssetList.MUSIC_ATL.toString())).findRegions("music"));
	}

	private float animCdCount;
	@Override
	public void act(float delta) {
		super.act(delta);
		animCdCount += delta;
	}

	@Override
    public void draw(Batch batch, float alpha) {

		GameStateManager.getSimplePatch().draw(batch, getX() - padX / 2, getY() - padY / 2,
			getWidth() + padX, getHeight() + padY);

		HadalGame.FONT_UI.getData().setScale(fontScale);
		HadalGame.FONT_UI.draw(batch, text, getX(), getY() + getHeight() / 2 + layout.height / 2, maxWidth, Align.left, true);

		batch.draw(musicIcon.getKeyFrame(animCdCount, true), getX() + iconOffsetX, getY() + iconOffsetY,
				iconWidth, iconHeight);
	}

	private static final float visibleDuration = 6.0f;
	public void animateIcon() {
		addAction(Actions.sequence(
			Actions.moveTo(startX, startY),
			Actions.moveTo(endX - getWidth() - padX, startY, TRANSITION_DURATION_SLOW, INTP_FASTSLOW),
			Actions.delay(visibleDuration),
			Actions.moveTo(startX, startY, TRANSITION_DURATION_SLOW, INTP_FASTSLOW),
			Actions.removeActor()));
	}
}
