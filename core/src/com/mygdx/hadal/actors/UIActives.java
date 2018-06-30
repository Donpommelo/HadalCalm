package com.mygdx.hadal.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
	private int x = 0;
	private int y = 0;
	
	public UIActives(AssetManager assetManager, PlayState state, Player player) {
		super(assetManager);
		this.player = player;
		this.state = state;
		this.font = HadalGame.SYSTEM_FONT_UI;
		
		this.atlas = (TextureAtlas) HadalGame.assetManager.get(AssetList.UIATLAS.toString());
		this.base = atlas.findRegion("UI_momentum_base");
		this.ready = atlas.findRegion("UI_momentum_ready");
		this.overlay = atlas.findRegion("UI_momentum_overlay");
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		batch.setProjectionMatrix(state.hud.combined);

		batch.draw(base, x + HadalGame.CONFIG_WIDTH - base.getRegionWidth() * scale, y, base.getRegionWidth() * scale, base.getRegionHeight() * scale);
		
		font.getData().setScale(0.4f);
		font.draw(batch, player.getPlayerData().getActiveItem().getName(), 
				x + HadalGame.CONFIG_WIDTH - base.getRegionWidth() * scale + 25, 130);
		
		float hpRatio = player.getPlayerData().getActiveItem().chargePercent();

		//Indicate cooldown
		if (hpRatio >= 1) {
			batch.draw(ready, x + HadalGame.CONFIG_WIDTH - base.getRegionWidth() * scale, y, base.getRegionWidth() * scale, base.getRegionHeight() * scale);
		} else {
			font.getData().setScale(0.4f);
			font.draw(batch, Math.round(player.getPlayerData().getActiveItem().chargePercent() * 100) +" %", 
					x + HadalGame.CONFIG_WIDTH - base.getRegionWidth() * scale + 34, 64);
		}
		
		batch.draw(overlay, x + HadalGame.CONFIG_WIDTH - base.getRegionWidth() * scale, y, base.getRegionWidth() * scale, base.getRegionHeight() * scale);

	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
