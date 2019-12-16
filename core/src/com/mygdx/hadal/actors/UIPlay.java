package com.mygdx.hadal.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.misc.NothingWeapon;
import com.mygdx.hadal.equip.mods.WeaponMod;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.WeaponModifier;

/**
 * This is the main ui element. It displays player loadout, hp, fuel.
 * @author Zachary Tu
 *
 */
public class UIPlay extends AHadalActor {

	protected Player player;
	private PlayState state;
	private BitmapFont font;
	
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
	
	protected float hpRatio, hpMax, fuelRatio, fuelCutoffRatio;
	protected String weaponText, ammoText;
	
	//display extra info (weapon mods) when moused over
	private boolean mouseOver;

	public UIPlay(AssetManager assetManager, PlayState state, Player player) {
		super(assetManager);
		this.player = player;
		this.state = state;
		this.font = HadalGame.SYSTEM_FONT_UI;
		
		this.main = GameStateManager.uiAtlas.findRegion("UI_main_overlay");
		this.reloading = GameStateManager.uiAtlas.findRegion("UI_main_reloading");
		this.reloading = GameStateManager.uiAtlas.findRegion("UI_main_reloading");
		this.hp = GameStateManager.uiAtlas.findRegion("UI_main_healthbar");
		this.hpLow = GameStateManager.uiAtlas.findRegion("UI_main_health_low");
		this.hpMissing = GameStateManager.uiAtlas.findRegion("UI_main_healthmissing");
		this.fuel = GameStateManager.uiAtlas.findRegion("UI_main_fuelbar");
		this.fuelCutoff = GameStateManager.uiAtlas.findRegion("UI_main_fuel_cutoff");
		this.itemNull = GameStateManager.uiAtlas.findRegions("UI_main_null");
		this.itemSelect = GameStateManager.uiAtlas.findRegions("UI_main_selected");
		this.itemUnselect = GameStateManager.uiAtlas.findRegions("UI_main_unselected");
		
		this.hpRatio = 1.0f;
		this.fuelRatio = 1.0f;
		this.fuelCutoffRatio = 1.0f;
		
		setWidth(main.getRegionWidth() * scale);
		setHeight(main.getRegionHeight() * scale);
		
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
	
	public void calcVars() {
		//Calc the ratios needed to draw the bars
		hpRatio = player.getPlayerData().getCurrentHp() / player.getPlayerData().getMaxHp();
		hpMax = player.getPlayerData().getMaxHp();
		fuelRatio = player.getPlayerData().getCurrentFuel() / player.getPlayerData().getMaxFuel();
		fuelCutoffRatio = player.getPlayerData().getAirblastCost() / player.getPlayerData().getMaxFuel();
		weaponText = player.getPlayerData().getCurrentTool().getText();
		ammoText = player.getPlayerData().getCurrentTool().getAmmoText();
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		batch.setProjectionMatrix(state.hud.combined);

		calcVars();
		
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
			batch.draw(hpLow, x, y, getWidth(), getHeight());
		}
		
		batch.draw(main, x, y, getWidth(), getHeight());
		
		batch.draw(fuelCutoff, x + 155 + fuelCutoffRatio * fuel.getRegionWidth() * scale, y + 22,
				fuelCutoff.getRegionWidth() * scale, fuelCutoff.getRegionHeight() * scale);

		
		if (player.getPlayerData().getCurrentTool().isReloading()) {
			batch.draw(reloading, x, y, getWidth(), getHeight());
		}

		font.getData().setScale(0.25f);
		font.draw(batch, player.getPlayerData().getCurrentTool().getName(), x + 48, y + 90, 100, -1, true);
		font.getData().setScale(0.5f);
		font.draw(batch, weaponText, x + 48, y + 40);
		font.getData().setScale(0.25f);
		font.draw(batch, ammoText, x + 48, y + 60);
		font.draw(batch, (int)player.getPlayerData().getCurrentHp() + "/" + (int)hpMax,
				x + 155, y + 66);
		
		for (int i = 0; i < 4; i++) {
			if (player.getPlayerData().getMultitools().length > i) {
				
				if (player.getPlayerData().getMultitools()[i] == null || player.getPlayerData().getMultitools()[i] instanceof NothingWeapon) {
					batch.draw(itemNull.get(i), x, y, getWidth(), getHeight());
				} else {
					if (i == player.getPlayerData().getCurrentSlot()) {
						batch.draw(itemSelect.get(i), x, y, getWidth(), getHeight());
					} else {
						batch.draw(itemUnselect.get(i), x, y, getWidth(), getHeight());
					}
				}	
			}
		}
		
		if (mouseOver) {
			int yOffset = 0;
			if (state.isServer()) {
				for(WeaponModifier s : player.getPlayerData().getCurrentTool().getWeaponMods()) {
					font.getData().setScale(0.25f);
					font.draw(batch, s.getName(), x + 25, y + 150 + yOffset, 250, -1, true);
					yOffset += 25;
				}
			} else {
				for(WeaponMod s : player.getPlayerData().getOverrideWeaponMods()) {
					font.getData().setScale(0.25f);
					font.draw(batch, s.getName(), x + 25, y + 150 + yOffset, 250, -1, true);
					yOffset += 25;
				}
			}
		}
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}

}
