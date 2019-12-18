package com.mygdx.hadal.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.misc.NothingWeapon;
import com.mygdx.hadal.equip.mods.WeaponMod;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
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
	private Array<? extends TextureRegion> itemNull, itemSelect, itemUnselect;
	
	private float mainScale = 0.50f;
	private static final int mainX = 0;
	private static final int mainY = 0;
	
	private static final int barX = 155;
	private static final int hpBarY = 50;
	private static final int fuelBarY = 22;
	
	private static final int bossX = 50;
	private static final int bossNameY = 700;
	private static final int bossBarY = 660;
	private static final int bossBarWidth = 1500;
	private static final int bossBarHeight = 30;
	
	//This variable manages the delay of hp decreasing after receiving damage
	private static final float hpCatchup = 0.01f;
	private static final float bossHpCatchup = 0.002f;
	
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
	
	private boolean bossFight = false;
	private Schmuck boss;
	private String bossName;
	private float bossHpDelayed = 1.0f;
	private float bossHpRatio;
	
	//display extra info (weapon mods) when moused over
	private boolean mouseOver;

	public UIPlay(AssetManager assetManager, PlayState state, Player player) {
		super(assetManager);
		this.player = player;
		this.state = state;
		this.font = HadalGame.SYSTEM_FONT_UI;
		
		this.main = Sprite.UI_MAIN_OVERLAY.getFrame();
		this.reloading = Sprite.UI_MAIN_RELOAD.getFrame();
		this.hp = Sprite.UI_MAIN_HEALTHBAR.getFrame();
		this.hpLow = Sprite.UI_MAIN_HEALTH_LOW.getFrame();
		this.hpMissing = Sprite.UI_MAIN_HEALTH_MISSING.getFrame();
		this.fuel = Sprite.UI_MAIN_FUELBAR.getFrame();
		this.fuelCutoff = Sprite.UI_MAIN_FUEL_CUTOFF.getFrame();
		
		this.itemNull = Sprite.UI_MAIN_NULL.getFrames();
		this.itemSelect = Sprite.UI_MAIN_SELECTED.getFrames();
		this.itemUnselect = Sprite.UI_MAIN_UNSELECTED.getFrames();
		
		this.hpRatio = 1.0f;
		this.fuelRatio = 1.0f;
		this.fuelCutoffRatio = 1.0f;
		
		setWidth(main.getRegionWidth() * mainScale);
		setHeight(main.getRegionHeight() * mainScale);
		
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
	
	/**
	 * This is run every update to keep track of the info displayed in this ui.
	 * This is in a separate method b/c so client's version (UIPlayClient) can use some overridden values
	 */
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
		
		batch.draw(hpMissing, mainX + barX, mainY + hpBarY, hp.getRegionWidth() * mainScale * hpDelayed, hp.getRegionHeight() * mainScale);
		batch.draw(hp, mainX + barX, mainY + hpBarY, hp.getRegionWidth() * mainScale * hpRatio, hp.getRegionHeight() * mainScale);
		batch.draw(fuel, mainX + barX, mainY + fuelBarY, fuel.getRegionWidth() * mainScale * fuelRatio, fuel.getRegionHeight() * mainScale);
		
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
			batch.draw(hpLow, mainX, mainY, getWidth(), getHeight());
		}
		
		batch.draw(main, mainX, mainY, getWidth(), getHeight());
		
		batch.draw(fuelCutoff, mainX + barX + fuelCutoffRatio * fuel.getRegionWidth() * mainScale, mainY + fuelBarY,
				fuelCutoff.getRegionWidth() * mainScale, fuelCutoff.getRegionHeight() * mainScale);

		
		if (player.getPlayerData().getCurrentTool().isReloading()) {
			batch.draw(reloading, mainX, mainY, getWidth(), getHeight());
		}

		font.getData().setScale(0.25f);
		font.draw(batch, player.getPlayerData().getCurrentTool().getName(), mainX + 48, mainY + 90, 100, -1, true);
		font.getData().setScale(0.5f);
		font.draw(batch, weaponText, mainX + 48, mainY + 40);
		font.getData().setScale(0.25f);
		font.draw(batch, ammoText, mainX + 48, mainY + 60);
		font.draw(batch, (int)player.getPlayerData().getCurrentHp() + "/" + (int)hpMax,
				mainX + 155, mainY + 66);
		
		for (int i = 0; i < 4; i++) {
			if (player.getPlayerData().getMultitools().length > i) {
				
				if (player.getPlayerData().getMultitools()[i] == null || player.getPlayerData().getMultitools()[i] instanceof NothingWeapon) {
					batch.draw(itemNull.get(i), mainX, mainY, getWidth(), getHeight());
				} else {
					if (i == player.getPlayerData().getCurrentSlot()) {
						batch.draw(itemSelect.get(i), mainX, mainY, getWidth(), getHeight());
					} else {
						batch.draw(itemUnselect.get(i), mainX, mainY, getWidth(), getHeight());
					}
				}	
			}
		}
		
		//Draw boss hp bar, if existant
		if (bossFight && boss.getBody() != null) {
			font.draw(batch, bossName, bossX, bossNameY);
			//This code makes the hp bar delay work.
			if (bossHpDelayed > bossHpRatio) {
				bossHpDelayed -= bossHpCatchup;
			} else {
				bossHpDelayed = bossHpRatio;
			}
			
			bossHpRatio = boss.getBodyData().getCurrentHp() / boss.getBodyData().getMaxHp();
			
			batch.draw(hpMissing, bossX, bossBarY, bossBarWidth * mainScale * bossHpDelayed, bossBarHeight * mainScale);
			batch.draw(hp, bossX, bossBarY, bossBarWidth * mainScale * bossHpRatio, bossBarHeight * mainScale);
		}
		
		//Draw weapon mod info if moused over
		if (mouseOver) {
			int yOffset = 0;
			if (state.isServer()) {
				for(WeaponModifier s : player.getPlayerData().getCurrentTool().getWeaponMods()) {
					font.getData().setScale(0.25f);
					font.draw(batch, s.getName(), mainX + 25, mainY + 150 + yOffset, 250, -1, true);
					yOffset += 25;
				}
			} else {
				for(WeaponMod s : player.getPlayerData().getOverrideWeaponMods()) {
					font.getData().setScale(0.25f);
					font.draw(batch, s.getName(), mainX + 25, mainY + 150 + yOffset, 250, -1, true);
					yOffset += 25;
				}
			}
		}
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void setBoss(Schmuck boss, String name) {
		this.boss = boss;
		bossFight = true;
		bossName = name;
	}
	
	public void clearBoss() {
		bossFight = false;
	}
}
