package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;

/**
 */
public class HubOption extends Text {

	private static final int IconTextWidth = 150;
	private static final int TextOffsetY = 45;

	private static final int DefaultOptionWidth = 160;
	private static final int DefaultOptionHeight = 150;
	private static final int DefaultIconWidth = 80;
	private static final int DefaultIconHeight = 80;

	private final TextureRegion frame;
	private int optionWidth = DefaultOptionWidth;
	private int optionHeight = DefaultOptionHeight;
	private int iconWidth = DefaultIconWidth;
	private int iconHeight = DefaultIconHeight;
	private int iconOffsetX, iconOffsetY;

	public HubOption(String text, TextureRegion frame) {
		super(text);
		this.frame = frame;

		this.iconOffsetX = (optionWidth - iconWidth) / 2;

		setWrap(IconTextWidth);
		setAlign(Align.center);
		setYOffset(TextOffsetY);
		setButton(true);
		setScale(UIHub.OptionsScale);
	}

	@Override
    public void draw(Batch batch, float alpha) {
		super.draw(batch, alpha);
		if (frame != null) {
			batch.draw(frame, getX() + iconOffsetX, getY() + iconOffsetY, iconWidth, iconHeight);
		}
    }

	@Override
	public void updateHitBox() {
		super.updateHitBox();
		setHeight(optionHeight);
		setWidth(optionWidth);
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
