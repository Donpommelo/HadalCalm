package com.mygdx.hadal.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * UIMomentum appears in the bottom right screen and displays information about the player's momentum freezing cd and stored momentums
 * @author Zachary Tu
 *
 */
public class UIActives extends AHadalActor{

	private Player player;
	private PlayState state;
	private BitmapFont font;
	
	private TextureAtlas atlas;
	
	private TextureRegion base, ready, overlay;
	
	private float scale = 0.75f;

	private boolean mouseOver;

	public UIActives(AssetManager assetManager, PlayState state, Player player) {
		super(assetManager);
		this.player = player;
		this.state = state;
		this.font = HadalGame.SYSTEM_FONT_UI;
		
		this.atlas = (TextureAtlas) HadalGame.assetManager.get(AssetList.UIATLAS.toString());
		this.base = atlas.findRegion("UI_momentum_base");
		this.ready = atlas.findRegion("UI_momentum_ready");
		this.overlay = atlas.findRegion("UI_momentum_overlay");
		setWidth(base.getRegionWidth() * scale);
		setHeight(base.getRegionHeight() * scale);
		setX(HadalGame.CONFIG_WIDTH - getWidth());
		setY(0);
		
		mouseOver = false;
		
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
		batch.setProjectionMatrix(state.hud.combined);

		batch.draw(base, getX(), getY(), getWidth(), getHeight());
		
		font.getData().setScale(0.25f);
		font.draw(batch, player.getPlayerData().getActiveItem().getName(), getX() + 25, 130);
		
		float hpRatio = player.getPlayerData().getActiveItem().chargePercent();

		//Indicate cooldown
		if (hpRatio >= 1) {
			batch.draw(ready, getX(), getY(), getWidth(), getHeight());
		} else {
			font.getData().setScale(0.4f);
			font.draw(batch, Math.round(player.getPlayerData().getActiveItem().chargePercent() * 100) +" %", getX() + 34, 64);
		}
		
		batch.draw(overlay, getX(), getY(), getWidth(), getHeight());
		
		if (mouseOver) {
	       	 font.getData().setScale(0.20f);
	         font.draw(batch, player.getPlayerData().getActiveItem().getDescr(), getX(), getY() + 200, getWidth(), -1, true);
        }
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
