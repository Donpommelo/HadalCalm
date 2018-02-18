package com.mygdx.hadal.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

public class UIPlay extends AHadalActor{

	private Player player;
	private PlayState state;
	private BitmapFont font;
	
	private TextureAtlas atlas;
	
	private TextureRegion main, reloading, hp, fuel, fuelCutoff;
	private Array<AtlasRegion> itemNull, itemSelect, itemUnselect;
	
	private float scale = 0.75f;
	private static final int x = 0;
	private static final int y = 0;
	
	public UIPlay(AssetManager assetManager, PlayState state, Player player) {
		super(assetManager);
		this.player = player;
		this.state = state;
		this.font = new BitmapFont();
		
		this.atlas = (TextureAtlas) HadalGame.assetManager.get(AssetList.UIATLAS.toString());
		this.main = atlas.findRegion("UI_main_overlay");
		this.reloading = atlas.findRegion("UI_main_reloading");
		this.reloading = atlas.findRegion("UI_main_reloading");
		this.hp = atlas.findRegion("UI_main_healthbar");
		this.fuel = atlas.findRegion("UI_main_fuelbar");
		this.fuelCutoff = atlas.findRegion("UI_main_fuel_cutoff");
		this.itemNull = atlas.findRegions("UI_main_null");
		this.itemSelect = atlas.findRegions("UI_main_selected");
		this.itemUnselect = atlas.findRegions("UI_main_unselected");
		
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		batch.setProjectionMatrix(state.hud.combined);

		float hpRatio = player.getPlayerData().currentHp / player.getPlayerData().getMaxHp();
		float fuelRatio = player.getPlayerData().currentFuel / player.getPlayerData().getMaxFuel();
		float fuelCutoffRatio = player.getPlayerData().airblastCost * 
				(1 + player.getPlayerData().getBonusAirblastCost()) / player.getPlayerData().getMaxFuel();
		
		batch.draw(hp, x + 233, y + 130, hp.getRegionWidth() * scale * hpRatio, hp.getRegionHeight() * scale);
		batch.draw(fuel, x + 233, y + 91, fuel.getRegionWidth() * scale * fuelRatio, fuel.getRegionHeight() * scale);
		
		batch.draw(main, x, y, main.getRegionWidth() * scale, main.getRegionHeight() * scale);
		
		batch.draw(fuelCutoff, x + 233 + fuelCutoffRatio * fuel.getRegionWidth() * scale, y + 86,
				fuelCutoff.getRegionWidth() * scale, fuelCutoff.getRegionHeight() * scale);

		
		if (player.getPlayerData().currentTool.reloading) {
			batch.draw(reloading, x, y, main.getRegionWidth() * scale, main.getRegionHeight() * scale);
		}

		font.draw(batch, player.getPlayerData().currentTool.getText(), x + 53, y + 98);
		
		for (int i = 0; i < 4; i++) {
			if (player.getPlayerData().multitools.length <= i) {
				batch.draw(itemNull.get(i), x, y, main.getRegionWidth() * scale, main.getRegionHeight() * scale);
			} else {
				if (i == player.getPlayerData().currentSlot) {
					batch.draw(itemSelect.get(i), x, y, main.getRegionWidth() * scale, main.getRegionHeight() * scale);
				} else {
					batch.draw(itemUnselect.get(i), x, y, main.getRegionWidth() * scale, main.getRegionHeight() * scale);
				}
			}
		}
	}

}
