package com.mygdx.hadal.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

public class UIReload extends AHadalActor{

	private Player player;
	private PlayState state;
	
	private TextureAtlas atlas;
	
	private TextureRegion reload, reloadMeter, reloadBar;
	
	private float scale = 0.50f;
	
	public UIReload(AssetManager assetManager, PlayState state, Player player) {
		super(assetManager);
		this.player = player;
		this.state = state;
		
		this.atlas = (TextureAtlas) HadalGame.assetManager.get(AssetList.UIATLAS.toString());
		this.reload = atlas.findRegion("UI_reload");
		this.reloadMeter = atlas.findRegion("UI_reload_meter");
		this.reloadBar = atlas.findRegion("UI_reload_bar");
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		batch.setProjectionMatrix(state.hud.combined);

		if (player.getPlayerData().getCurrentTool().isReloading() && player.isAlive()) {
		
			Vector3 bodyScreenPosition = new Vector3(player.getBody().getPosition().x, player.getBody().getPosition().y, 0);
			state.camera.project(bodyScreenPosition);
			
			float x = bodyScreenPosition.x - reload.getRegionWidth() * scale / 2;
			float y = bodyScreenPosition.y + reload.getRegionHeight() * scale + Player.hbHeight * Player.scale / 2;
			
			float percent = player.getPlayerData().getCurrentTool().getReloadCd() / 
					(player.getPlayerData().getCurrentTool().getReloadTime());
			
			batch.draw(reloadBar, x + 12, y + 5, reloadBar.getRegionWidth() * scale * percent, reloadBar.getRegionHeight() * scale);
			batch.draw(reload, x, y, reload.getRegionWidth() * scale, reload.getRegionHeight() * scale);
			batch.draw(reloadMeter, x, y, reload.getRegionWidth() * scale, reload.getRegionHeight() * scale);
		}
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}

}
