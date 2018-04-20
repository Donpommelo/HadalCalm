package com.mygdx.hadal.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.misc.Nothing;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * This is the main ui element. It displays player loadout, hp, fuel.
 * @author Zachary Tu
 *
 */
public class UIPlay extends AHadalActor{

	private Player player;
	private PlayState state;
	private BitmapFont font;
	
	private TextureAtlas atlas;
	
	private TextureRegion main, reloading, hp, hpLow, hpMissing, fuel, fuelCutoff;
	private Array<AtlasRegion> itemNull, itemSelect, itemUnselect;
	
	private float scale = 0.50f;
	private static final int x = 0;
	private static final int y = 0;
	
	//This variable manages the delay of hp decreasing after receiving damage
	private static final float hpCatchup = 0.01f;
	
	private float hpDelayed = 1.0f;
	
	//These make the Hp bar blink red when at low Hp.
	private boolean blinking = false;
	private float blinkCdCount = 0.0f;
	
	//Rate of blinking whe nat low health
	private static final float blinkCd = 0.1f;
	
	//Percent of Hp for low heal indication to appear
	private static final float hpLowThreshold = 0.20f;
	
	private float hpRatio, fuelRatio, fuelCutoffRatio;
	
	public UIPlay(AssetManager assetManager, PlayState state, Player player) {
		super(assetManager);
		this.player = player;
		this.state = state;
		this.font = HadalGame.SYSTEM_FONT_UI;
		
		this.atlas = (TextureAtlas) HadalGame.assetManager.get(AssetList.UIATLAS.toString());
		this.main = atlas.findRegion("UI_main_overlay");
		this.reloading = atlas.findRegion("UI_main_reloading");
		this.reloading = atlas.findRegion("UI_main_reloading");
		this.hp = atlas.findRegion("UI_main_healthbar");
		this.hpLow = atlas.findRegion("UI_main_health_low");
		this.hpMissing = atlas.findRegion("UI_main_healthmissing");
		this.fuel = atlas.findRegion("UI_main_fuelbar");
		this.fuelCutoff = atlas.findRegion("UI_main_fuel_cutoff");
		this.itemNull = atlas.findRegions("UI_main_null");
		this.itemSelect = atlas.findRegions("UI_main_selected");
		this.itemUnselect = atlas.findRegions("UI_main_unselected");
		
		this.hpRatio = 1.0f;
		this.fuelRatio = 1.0f;
		this.fuelCutoffRatio = 1.0f;
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		batch.setProjectionMatrix(state.hud.combined);

		//Calc the ratios needed to draw the bars
		hpRatio = player.getPlayerData().getCurrentHp() / player.getPlayerData().getMaxHp();
		fuelRatio = player.getPlayerData().getCurrentFuel() / player.getPlayerData().getMaxFuel();
		fuelCutoffRatio = player.getPlayerData().getAirblastCost() / player.getPlayerData().getMaxFuel();
		
		//This code makes the hp bar delay work.
		if (hpDelayed > hpRatio) {
			hpDelayed -= hpCatchup;
		} else {
			hpDelayed = hpRatio;
		}
		
		batch.draw(hpMissing, x + 155, y + 50, hp.getRegionWidth() * scale * hpDelayed, hp.getRegionHeight() * scale);
		batch.draw(hp, x + 155, y + 50, hp.getRegionWidth() * scale * hpRatio, hp.getRegionHeight() * scale);
		batch.draw(fuel, x + 155, y + 22, fuel.getRegionWidth() * scale * fuelRatio, fuel.getRegionHeight() * scale);
		
		//This makes low Hp indicator blink at low health
		if (hpRatio <= hpLowThreshold) {
			
			blinkCdCount -= 0.01f;
			
			if (blinkCdCount < 0) {
				blinking = !blinking;
				blinkCdCount = blinkCd;
			}
		} else {
			blinking = false;
		}
		
		if (blinking) {
			batch.draw(hpLow, x, y, main.getRegionWidth() * scale, main.getRegionHeight() * scale);
		}
		
		batch.draw(main, x, y, main.getRegionWidth() * scale, main.getRegionHeight() * scale);
		
		batch.draw(fuelCutoff, x + 155 + fuelCutoffRatio * fuel.getRegionWidth() * scale, y + 22,
				fuelCutoff.getRegionWidth() * scale, fuelCutoff.getRegionHeight() * scale);

		
		if (player.getPlayerData().getCurrentTool().isReloading()) {
			batch.draw(reloading, x, y, main.getRegionWidth() * scale, main.getRegionHeight() * scale);
		}

		font.getData().setScale(0.25f);
		font.draw(batch, player.getPlayerData().getCurrentTool().getName(), x + 48, y + 90, 100, -1, true);
		font.getData().setScale(0.5f);
		font.draw(batch, player.getPlayerData().getCurrentTool().getText(), x + 48, y + 50);
		font.getData().setScale(0.25f);
		font.draw(batch, (int)player.getPlayerData().getCurrentHp() + "/" + (int)player.getPlayerData().getMaxHp(),
				x + 155, y + 66);
		
		for (int i = 0; i < 4; i++) {
			if (player.getPlayerData().getMultitools().length > i) {
				
				if (player.getPlayerData().getMultitools()[i] == null || player.getPlayerData().getMultitools()[i] instanceof Nothing) {
					batch.draw(itemNull.get(i), x, y, main.getRegionWidth() * scale, main.getRegionHeight() * scale);
				} else {
					if (i == player.getPlayerData().getCurrentSlot()) {
						batch.draw(itemSelect.get(i), x, y, main.getRegionWidth() * scale, main.getRegionHeight() * scale);
					} else {
						batch.draw(itemUnselect.get(i), x, y, main.getRegionWidth() * scale, main.getRegionHeight() * scale);
					}
				}	
			}
		}
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}

}
