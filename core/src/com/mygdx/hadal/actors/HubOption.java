package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;

/**
 */
public class HubOption extends Text {

	private static final int ICON_TEXT_WIDTH = 150;
	private static final int TEXT_OFFSET_Y = 45;

	private static final int DEFAULT_OPTION_WIDTH = 160;
	private static final int DEFAULT_OPTION_HEIGHT = 150;
	private static final int DEFAULT_ICON_WIDTH = 80;
	private static final int DEFAULT_ICON_HEIGHT = 80;

	private final Animation<TextureRegion> frame;
	private int optionWidth = DEFAULT_OPTION_WIDTH;
	private int optionHeight = DEFAULT_OPTION_HEIGHT;
	private int iconWidth = DEFAULT_ICON_WIDTH;
	private int iconHeight = DEFAULT_ICON_HEIGHT;
	private int iconOffsetX, iconOffsetY;
	private float animationTime;

	public HubOption(String text, Animation<TextureRegion> frame) {
		super(text);
		this.frame = frame;

		this.iconOffsetX = (optionWidth - iconWidth) / 2;

		setWrap(ICON_TEXT_WIDTH);
		setAlign(Align.center);
		setYOffset(TEXT_OFFSET_Y);
		setButton(true);
		setScale(UIHub.OPTIONS_SCALE);
	}

	@Override
    public void draw(Batch batch, float alpha) {
		if (frame != null) {
			batch.draw(frame.getKeyFrame(animationTime), getX() + iconOffsetX, getY() + iconOffsetY, iconWidth, iconHeight);
		}
		super.draw(batch, alpha);
	}

    @Override
	public void act(float delta) {
		super.act(delta);
		animationTime += delta;
	}

	@Override
	public void updateHitBox() {
		super.updateHitBox();
		setHeight(optionHeight);
		setWidth(optionWidth);
		this.iconOffsetX = (optionWidth - iconWidth) / 2;
	}

	public HubOption setOptionWidth(int optionWidth) {
		this.optionWidth = optionWidth;
		updateHitBox();
		return this;
	}

	public HubOption setOptionHeight(int optionHeight) {
		this.optionHeight = optionHeight;
		updateHitBox();
		return this;
	}

	public HubOption setIconWidth(int iconWidth) {
		this.iconWidth = iconWidth;
		return this;
	}

	public HubOption setIconHeight(int iconHeight) {
		this.iconHeight = iconHeight;
		return this;
	}

	public HubOption setIconOffsetX(int iconOffsetX) {
		this.iconOffsetX = iconOffsetX;
		return this;
	}

	public HubOption setIconOffsetY(int iconOffsetY) {
		this.iconOffsetY = iconOffsetY;
		return this;
	}
}
