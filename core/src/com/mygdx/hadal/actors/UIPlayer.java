package com.mygdx.hadal.actors;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.ArrayList;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * This ui element appears above the player's head when they are reloading to indicate reload progress
 * It also contains other player information like player hp and name
 * @author Zachary Tu
 *
 */
public class UIPlayer extends AHadalActor {

	private ArrayList<Player> players;
	private PlayState state;
	
	private TextureRegion reload, reloadMeter, reloadBar;
	private Texture empty, full;
	
	private float scale = 0.40f;
	private float hpScale = 0.40f;
	
	private boolean server;
	
	public UIPlayer(AssetManager assetManager, PlayState state) {
		super(assetManager);
		this.players = new ArrayList<Player>();
		this.state = state;
		
		this.reload = GameStateManager.uiAtlas.findRegion("UI_reload");
		this.reloadMeter = GameStateManager.uiAtlas.findRegion("UI_reload_meter");
		this.reloadBar = GameStateManager.uiAtlas.findRegion("UI_reload_bar");
		
		this.empty = new Texture(AssetList.HEART_EMPTY.toString());
		this.full = new Texture(AssetList.HEART_FULL.toString());
		
		server = state.isServer();
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		batch.setProjectionMatrix(state.sprite.combined);

		for (Player player : players) {
			if (player.getBody() == null)
				continue;
			
			if (player.getPlayerData().getCurrentTool().isReloading() && player.isAlive()) {
				
				float x = (player.getPosition().x * PPM) - reload.getRegionWidth() * scale / 2;
				float y = (player.getPosition().y * PPM) + reload.getRegionHeight() * scale + Player.hbHeight * Player.scale / 2;
				
				//Calculate reload progress
				float percent = player.getReloadPercent();
				
				batch.draw(reloadBar, x + 10, y + 4, reloadBar.getRegionWidth() * scale * percent, reloadBar.getRegionHeight() * scale);
				batch.draw(reload, x, y, reload.getRegionWidth() * scale, reload.getRegionHeight() * scale);
				batch.draw(reloadMeter, x, y, reload.getRegionWidth() * scale, reload.getRegionHeight() * scale);
			}
			
			if (player.getPlayerData().getCurrentTool().isCharging() && player.isAlive()) {
				
				float x = (player.getPosition().x * PPM) - reload.getRegionWidth() * scale / 2;
				float y = (player.getPosition().y * PPM) + reload.getRegionHeight() * scale + Player.hbHeight * Player.scale / 2;
				
				//Calculate charge progress
				float percent = player.getChargePercent();
				
				batch.draw(reloadBar, x + 10, y + 4, reloadBar.getRegionWidth() * scale * percent, reloadBar.getRegionHeight() * scale);
				batch.draw(reloadMeter, x, y, reload.getRegionWidth() * scale, reload.getRegionHeight() * scale);
			}
			
			//This draws a heart by the player's sprite to indicate hp remaining
			if (player.isAlive()) {
				
				float x = (player.getPosition().x * PPM) - Player.hbWidth * Player.scale - empty.getWidth() * hpScale + 10;
				float y = (player.getPosition().y * PPM) + Player.hbHeight * Player.scale / 2 - 5;
				
				float hpRatio = 0.0f;
				
				if (server) {
					hpRatio = player.getPlayerData().getCurrentHp() / player.getPlayerData().getMaxHp();
				} else {
					hpRatio = player.getPlayerData().getCurrentHp() / player.getPlayerData().getOverrideMaxHp();
				}
				
				
				batch.draw(empty, x - empty.getWidth() / 2 * hpScale, y - empty.getHeight() / 2 * hpScale,
		                empty.getWidth() / 2, empty.getHeight() / 2,
		                empty.getWidth(), empty.getHeight(),
		                hpScale, hpScale, 0, 0, 0, empty.getWidth(), empty.getHeight(), false, false);

		        batch.draw(full, x - full.getWidth() / 2 * hpScale, y - full.getHeight() / 2 * hpScale - (int)(full.getHeight() * (1 - hpRatio) * hpScale),
		                full.getWidth() / 2, full.getHeight() / 2,
		                full.getWidth(), full.getHeight(),
		                hpScale, hpScale, 0, 0, (int) (full.getHeight() * (1 - hpRatio)),
		                full.getWidth(), full.getHeight(), false, false);
			}
			
			HadalGame.SYSTEM_FONT_SPRITE.getData().setScale(1.0f);
			HadalGame.SYSTEM_FONT_SPRITE.draw(batch, player.getName(), 
					player.getPosition().x * PPM - Player.hbWidth * Player.scale / 2, 
					player.getPosition().y * PPM + Player.hbHeight * Player.scale / 2 + 15);
			
			batch.setProjectionMatrix(state.hud.combined);
		}
	}
	
	public void addPlayer(Player player) {
		players.add(player);
	}
	
	public void removePlayer(Player player) {
		players.remove(player);
	}
}
