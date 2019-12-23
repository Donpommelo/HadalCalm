package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.Player;

/**
 * UIMomentum appears in the bottom right screen and displays information about the player's momentum freezing cd and stored momentums
 * @author Zachary Tu
 *
 */
public class UIActives extends AHadalActor {

	private Player player;
	private BitmapFont font;
	
	private TextureRegion base, ready, overlay;
	
	private static final float scale = 0.5f;

	public UIActives(Player player) {
		this.player = player;
		this.font = HadalGame.SYSTEM_FONT_UI;
		
		this.base = Sprite.UI_MO_BASE.getFrame();
		this.ready = Sprite.UI_MO_READY.getFrame();
		this.overlay = Sprite.UI_MO_OVERLAY.getFrame();
		setWidth(base.getRegionWidth() * scale);
		setHeight(base.getRegionHeight() * scale);
		setX(HadalGame.CONFIG_WIDTH - getWidth());
		setY(0);
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		batch.draw(base, getX(), getY(), getWidth(), getHeight());
		
		font.getData().setScale(0.2f);
		font.draw(batch, player.getPlayerData().getActiveItem().getName(), getX(), 100);
		
		float hpRatio = player.getPlayerData().getActiveItem().chargePercent();

		//Indicate cooldown
		if (hpRatio >= 1) {
			batch.draw(ready, getX(), getY(), getWidth(), getHeight());
		}
		
		batch.draw(overlay, getX(), getY(), getWidth(), getHeight());
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
