package com.mygdx.hadal.actors;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.ArrayList;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * This ui element appears above the player's head when they are reloading to indicate reload progress
 * @author Zachary Tu
 *
 */
public class UIPlayer extends AHadalActor {

	private ArrayList<Player> players;
	private PlayState state;
	
	private TextureRegion reload, reloadMeter, reloadBar;
	
	private float scale = 0.40f;
	
	public UIPlayer(AssetManager assetManager, PlayState state) {
		super(assetManager);
		this.players = new ArrayList<Player>();
		this.state = state;
		
		this.reload = GameStateManager.uiAtlas.findRegion("UI_reload");
		this.reloadMeter = GameStateManager.uiAtlas.findRegion("UI_reload_meter");
		this.reloadBar = GameStateManager.uiAtlas.findRegion("UI_reload_bar");
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		batch.setProjectionMatrix(state.sprite.combined);

		for (Player player : players) {
			if (player.getBody() == null)
				continue;
			
			if (player.getPlayerData().getCurrentTool().isReloading() && player.isAlive()) {
				
				float x = (player.getBody().getPosition().x * PPM) - reload.getRegionWidth() * scale / 2;
				float y = (player.getBody().getPosition().y * PPM) + reload.getRegionHeight() * scale + Player.hbHeight * Player.scale / 2;
				
				//Calculate reload progress
				float percent = player.getReloadPercent();
				
				batch.draw(reloadBar, x + 10, y + 4, reloadBar.getRegionWidth() * scale * percent, reloadBar.getRegionHeight() * scale);
				batch.draw(reload, x, y, reload.getRegionWidth() * scale, reload.getRegionHeight() * scale);
				batch.draw(reloadMeter, x, y, reload.getRegionWidth() * scale, reload.getRegionHeight() * scale);
			}
			
			HadalGame.SYSTEM_FONT_SPRITE.getData().setScale(1.0f);
			HadalGame.SYSTEM_FONT_SPRITE.draw(batch, player.getName(), 
					player.getBody().getPosition().x * PPM - Player.hbWidth * Player.scale / 2, 
					player.getBody().getPosition().y * PPM + Player.hbHeight * Player.scale / 2 + 15);
		}
	}
	
	public void addPlayer(Player player) {
		players.add(player);
	}
	
	public void removePlayer(Player player) {
		players.remove(player);
	}
}
