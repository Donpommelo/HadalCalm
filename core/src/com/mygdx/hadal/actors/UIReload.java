package com.mygdx.hadal.actors;

import static com.mygdx.hadal.utils.Constants.PPM;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * This ui element appears above the player's head when they are reloading to indicate reload progress
 * @author Zachary Tu
 *
 */
public class UIReload extends AHadalActor {

	private Player player;
	private PlayState state;
	
	private TextureRegion reload, reloadMeter, reloadBar;
	
	private float scale = 0.40f;
	
	public UIReload(AssetManager assetManager, PlayState state, Player player) {
		super(assetManager);
		this.player = player;
		this.state = state;
		
		this.reload = GameStateManager.uiAtlas.findRegion("UI_reload");
		this.reloadMeter = GameStateManager.uiAtlas.findRegion("UI_reload_meter");
		this.reloadBar = GameStateManager.uiAtlas.findRegion("UI_reload_bar");
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		batch.setProjectionMatrix(state.sprite.combined);

		if (player.getPlayerData().getCurrentTool().isReloading() && player.isAlive()) {
			
			float x = (player.getBody().getPosition().x * PPM) - reload.getRegionWidth() * scale / 2;
			float y = (player.getBody().getPosition().y * PPM) + reload.getRegionHeight() * scale + Player.hbHeight * Player.scale / 2;
			
			//Calculate reload progress
			float percent = player.getPlayerData().getCurrentTool().getReloadCd() / 
					(player.getPlayerData().getCurrentTool().getReloadTime());
			
			batch.draw(reloadBar, x + 10, y + 4, reloadBar.getRegionWidth() * scale * percent, reloadBar.getRegionHeight() * scale);
			batch.draw(reload, x, y, reload.getRegionWidth() * scale, reload.getRegionHeight() * scale);
			batch.draw(reloadMeter, x, y, reload.getRegionWidth() * scale, reload.getRegionHeight() * scale);
		}
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}

}
