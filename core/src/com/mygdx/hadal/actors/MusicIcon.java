package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.MusicTrack;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.GameStateManager;

import static com.mygdx.hadal.constants.Constants.INTP_FASTSLOW;
import static com.mygdx.hadal.constants.Constants.TRANSITION_DURATION_SLOW;

/**
 * This icon is displayed at the start of levels to show the player the currently playing song
 * @author Boguana Blitherford
 */
public class MusicIcon extends AHadalActor {

	private static final float PAD_X = 40.0f;
	private static final float PAD_Y = 20.0f;
	private static final float MAX_WIDTH = 300.0f;

	private static final float FONT_SCALE = 0.3f;
	private static final float ANIMATION_SPEED = 0.04f;
	private static final float ICON_OFFSET_X = -85.0f;
	private static final float ICON_OFFSET_Y = -40.0f;
	private static final float ICON_WIDTH = 90.0f;
	private static final float ICON_HEIGHT = 90.0f;

	private static final float START_X = 1330.0f;
	private static final float END_X = 1280.0f;
	private static final float START_Y = 600.0f;

	private final String text;
	private final GlyphLayout layout;
	private final Animation<TextureRegion> musicIcon;

	public MusicIcon(MusicTrack track) {

		text = track.getMusicName();

		HadalGame.FONT_UI.getData().setScale(FONT_SCALE);
		layout = new GlyphLayout();
		layout.setText(HadalGame.FONT_UI, text);
		setWidth(layout.width);
		setHeight(layout.height);

		musicIcon = new Animation<>(ANIMATION_SPEED,
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

		GameStateManager.getSimplePatch().draw(batch, getX() - PAD_X / 2, getY() - PAD_Y / 2,
			getWidth() + PAD_X, getHeight() + PAD_Y);

		HadalGame.FONT_UI.getData().setScale(FONT_SCALE);
		HadalGame.FONT_UI.draw(batch, text, getX(), getY() + getHeight() / 2 + layout.height / 2, MAX_WIDTH, Align.left, true);

		batch.draw(musicIcon.getKeyFrame(animCdCount, true), getX() + ICON_OFFSET_X, getY() + ICON_OFFSET_Y,
				ICON_WIDTH, ICON_HEIGHT);
	}

	private static final float VISIBLE_DURATION = 6.0f;
	/**
	 * Run from PlayState Controller to animate music icon when a new track is played
	 */
	public void animateIcon() {
		addAction(Actions.sequence(
			Actions.moveTo(START_X, START_Y),
			Actions.moveTo(END_X - getWidth() - PAD_X, START_Y, TRANSITION_DURATION_SLOW, INTP_FASTSLOW),
			Actions.delay(VISIBLE_DURATION),
			Actions.moveTo(START_X, START_Y, TRANSITION_DURATION_SLOW, INTP_FASTSLOW),
			Actions.removeActor()));
	}
}
