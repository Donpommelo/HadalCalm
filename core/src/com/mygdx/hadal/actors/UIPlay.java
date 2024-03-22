package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.misc.NothingWeapon;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;

import static com.mygdx.hadal.managers.SkinManager.*;

/**
 * This is the main ui element. It displays player loadout, hp, fuel.
 * @author Torbara Trorbmuffin
 */
public class UIPlay extends AHadalActor {

	private static final float MAIN_SCALE = 0.5f;
	private static final int MAIN_X = 0;
	private static final int MAIN_Y = 0;
	
	private static final int BAR_X = 155;
	private static final int HP_BAR_Y = 50;
	private static final int FUEL_BAR_Y = 22;
	
	private static final float BOSS_SCALE = 0.4f;
	private static final int BOSS_X = 10;
	private static final int BOSS_NAME_X = 110;
	private static final int BOSS_NAME_Y = 710;
	private static final int BOSS_BAR_Y = 700;
	private static final int BOSS_BAR_WIDTH = 1500;
	private static final int BOSS_BAR_HEIGHT = 30;
	
	private static final int ACTIVE_X = 380;
	private static final int ACTIVE_Y = 20;
	private static final int ACTIVE_WIDTH = 20;
	private static final int ACTIVE_HEIGHT = 120;
	private static final int ACTIVE_TEXT_Y = 30;

	private static final float FONT_SCALE_LARGE = 0.5f;
	private static final float FONT_SCALE_MEDIUM = 0.4f;
	private static final float FONT_SCALE_SMALL = 0.25f;

	//This variable manages the delay of hp decreasing after receiving damage
	private static final float HP_CATCHUP = 0.01f;
	private static final float BOSS_HP_CATCHUP = 0.002f;

	//Rate of blinking when at low health
	private static final float BLINK_CD = 0.1f;

	//Percent of Hp for low heal indication to appear
	private static final float HP_LOW_THRESHOLD = 0.2f;

	//This is the percentage of the boss hp bar that is effectively 0 (to prevent the 9patch from displaying weirdly)
	protected static final float BOSS_HP_FLOOR = 0.05f;

	protected final PlayState state;

	private final TextureRegion main, reloading, hp, hpLow, hpMissing, fuel, fuelCutoff;
	private final Array<? extends TextureRegion> itemNull, itemSelect, itemUnselect;


	//These make the Hp bar blink red when at low Hp.
	private boolean blinking;
	private float blinkCdCount;
	
	//fields displayed in this ui
	protected float hpRatio, hpMax, fuelRatio, fuelCutoffRatio;
	protected String weaponText, ammoText;
	protected float numWeaponSlots;
	protected float activePercent;
	
	//Are we currently fighting a boss. If so, who and what's its name.
	protected boolean bossFight;
	protected Schmuck boss;
	private String bossName;

	//this ration represents the visual delay in hp bar for the player and bosses
	private float bossHpDelayed = 1.0f;
	private float hpDelayed = 1.0f;

	//These stats manage the boss hp bar.
	protected float bossHpRatio;
	private final float hpWidthScaled, hpHeightScaled, fuelWidthScaled, fuelHeightScaled, fuelCutoffWidthScaled, fuelCutoffHeightScaled, activeWidthScaled, activeHeightScaled;
	
	public UIPlay(PlayState state) {
		this.state = state;

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
		
		setWidth(main.getRegionWidth() * MAIN_SCALE);
		setHeight(main.getRegionHeight() * MAIN_SCALE);
		
		hpWidthScaled = hp.getRegionWidth() * MAIN_SCALE;
		hpHeightScaled = hp.getRegionHeight() * MAIN_SCALE;
		fuelWidthScaled = fuel.getRegionWidth() * MAIN_SCALE;
		fuelHeightScaled = fuel.getRegionHeight() * MAIN_SCALE;
		fuelCutoffWidthScaled = fuelCutoff.getRegionWidth() * MAIN_SCALE;
		fuelCutoffHeightScaled = fuelCutoff.getRegionHeight() * MAIN_SCALE;
		activeWidthScaled = ACTIVE_WIDTH * MAIN_SCALE;
		activeHeightScaled = ACTIVE_HEIGHT * MAIN_SCALE;
	}
	
	/**
	 * This is run every update to keep track of the info displayed in this ui.
	 * This is in a separate method b/c so client's version (UIPlayClient) can use some overridden values
	 */
	public void calcVars() {
		Player player = HadalGame.usm.getOwnPlayer();

		//Calc the fields needed to draw the bars
		if (null != player) {
			if (null != player.getPlayerData()) {
				hpRatio = player.getPlayerData().getCurrentHp() / player.getPlayerData().getStat(Stats.MAX_HP);
				hpMax = player.getPlayerData().getStat(Stats.MAX_HP);
				fuelRatio = player.getPlayerData().getCurrentFuel() / player.getPlayerData().getStat(Stats.MAX_FUEL);
				fuelCutoffRatio = player.getAirblastHelper().getAirblastCost() / player.getPlayerData().getStat(Stats.MAX_FUEL);
				weaponText = player.getEquipHelper().getCurrentTool().getText();
				ammoText = player.getEquipHelper().getCurrentTool().getAmmoText();
				numWeaponSlots = player.getEquipHelper().getNumWeaponSlots();
				activePercent = player.getMagicHelper().getMagic().chargePercent();
			}
		}

		if (bossFight && null != boss.getBody()) {
			bossHpRatio = boss.getBodyData().getCurrentHp() / boss.getBodyData().getStat(Stats.MAX_HP);
			bossHpRatio = BOSS_HP_FLOOR + (bossHpRatio * (1 - BOSS_HP_FLOOR));
		}
	}

	//uiAccumulator used to make hp bar movement not scale to framerate
	private float uiAccumulator;
	private static final float UI_TIME = 1 / 60.0f;
	@Override
	public void act(float delta) {
		uiAccumulator += delta;
		while (UI_TIME <= uiAccumulator) {
			uiAccumulator -= UI_TIME;

			if (hpDelayed > hpRatio) {
				hpDelayed -= HP_CATCHUP;
			} else {
				hpDelayed = hpRatio;
			}
		}

		//This makes low Hp indicator blink at low health
		if (HP_LOW_THRESHOLD >= hpRatio) {
			blinkCdCount -= delta;
			if (0 > blinkCdCount) {
				blinking = !blinking;
				blinkCdCount = BLINK_CD;
			}
		} else {
			blinking = false;
		}
	}

	@Override
    public void draw(Batch batch, float alpha) {
		batch.setProjectionMatrix(state.getHud().combined);

		calcVars();

		//Draw boss hp bar, if existent. Do this before player check so spectators can see boss hp
		if (bossFight && null != boss.getBody()) {
			FONT_UI.getData().setScale(FONT_SCALE_SMALL);
			FONT_UI.draw(batch, bossName, BOSS_NAME_X, BOSS_NAME_Y);
			
			//This code makes the hp bar delay work.
			if (bossHpDelayed > bossHpRatio) {
				bossHpDelayed -= BOSS_HP_CATCHUP;
			} else {
				bossHpDelayed = bossHpRatio;
			}

			BOSS_GAUGE_GREY_PATCH.draw(batch, BOSS_X, BOSS_BAR_Y, 0, 0,
					BOSS_BAR_WIDTH, BOSS_BAR_HEIGHT, BOSS_SCALE, BOSS_SCALE, 0);
			BOSS_GAUGE_CATCHUP_PATCH.draw(batch, BOSS_X, BOSS_BAR_Y, 0, 0,
					BOSS_BAR_WIDTH * bossHpDelayed, BOSS_BAR_HEIGHT, BOSS_SCALE, BOSS_SCALE, 0);
			BOSS_GAUGE_RED_PATCH.draw(batch, BOSS_X, BOSS_BAR_Y, 0, 0,
					BOSS_BAR_WIDTH * bossHpRatio, BOSS_BAR_HEIGHT, BOSS_SCALE, BOSS_SCALE, 0);
			BOSS_GAUGE_PATCH.draw(batch, BOSS_X, BOSS_BAR_Y, 0, 0,
					BOSS_BAR_WIDTH, BOSS_BAR_HEIGHT, BOSS_SCALE, BOSS_SCALE, 0);
		}

		Player ownPlayer = HadalGame.usm.getOwnPlayer();

		if (null == ownPlayer) { return; }
		if (null == ownPlayer.getPlayerData()) { return; }
		if (!ownPlayer.isAlive()) { return; }

		//hide rest of ui if specified in settings. We don't want to hide boss ui.
		if (JSONManager.setting.isHideHUD()) { return; }
				
		//do not render in spectator mode
		if (state.isSpectatorMode()) { return; }

		batch.draw(hpMissing, MAIN_X + BAR_X, MAIN_Y + HP_BAR_Y, hpWidthScaled * hpDelayed, hpHeightScaled);
		batch.draw(hp, MAIN_X + BAR_X, MAIN_Y + HP_BAR_Y, hpWidthScaled * hpRatio, hpHeightScaled);
		batch.draw(fuel, MAIN_X + BAR_X, MAIN_Y + FUEL_BAR_Y, fuelWidthScaled * fuelRatio, fuelHeightScaled);
		
		if (blinking) {
			batch.draw(hpLow, MAIN_X, MAIN_Y, getWidth(), getHeight());
		}
		
		batch.draw(main, MAIN_X, MAIN_Y, getWidth(), getHeight());
		batch.draw(fuelCutoff, MAIN_X + BAR_X + fuelCutoffRatio * fuelWidthScaled,
			MAIN_Y + FUEL_BAR_Y, fuelCutoffWidthScaled, fuelCutoffHeightScaled);

		if (ownPlayer.getEquipHelper().getCurrentTool().isReloading()) {
			batch.draw(reloading, MAIN_X, MAIN_Y, getWidth(), getHeight());
		}

		FONT_UI.getData().setScale(FONT_SCALE_SMALL);
		FONT_UI.draw(batch, ownPlayer.getEquipHelper().getCurrentTool().getName(),
		MAIN_X + 48, MAIN_Y + 90, 100, -1, true);

		//we want to use a smaller font for high clip size weapons
		if (5 < weaponText.length()) {
			FONT_UI.getData().setScale(FONT_SCALE_MEDIUM);
		} else {
			FONT_UI.getData().setScale(FONT_SCALE_LARGE);
		}
		FONT_UI.draw(batch, weaponText, MAIN_X + 48, MAIN_Y + 40);
		FONT_UI.getData().setScale(FONT_SCALE_SMALL);
		FONT_UI.draw(batch, ammoText, MAIN_X + 48, MAIN_Y + 60);
		FONT_UI.draw(batch, (int) (hpRatio * hpMax) + "/" + (int) hpMax, MAIN_X + 155, MAIN_Y + 66);
		
		for (int i = 0; i < Loadout.MAX_WEAPON_SLOTS; i++) {
			if (i < numWeaponSlots) {
				if (ownPlayer.getEquipHelper().getMultitools()[i] == null ||
						ownPlayer.getEquipHelper().getMultitools()[i] instanceof NothingWeapon) {
					batch.draw(itemNull.get(i), MAIN_X, MAIN_Y, getWidth(), getHeight());
				} else {
					if (i == ownPlayer.getEquipHelper().getCurrentSlot()) {
						batch.draw(itemSelect.get(i), MAIN_X, MAIN_Y, getWidth(), getHeight());
					} else {
						batch.draw(itemUnselect.get(i), MAIN_X, MAIN_Y, getWidth(), getHeight());
					}
				}	
			}
		}

		//draw active item ui and charge indicator
		FONT_UI.draw(batch, ownPlayer.getMagicHelper().getMagic().getName(),
				ACTIVE_X, MAIN_Y + activeHeightScaled + ACTIVE_TEXT_Y);
		if (1.0f <= activePercent) {
			batch.draw(hp, ACTIVE_X, ACTIVE_Y, activeWidthScaled, activeHeightScaled * activePercent);
		} else {
			batch.draw(hpMissing, ACTIVE_X, ACTIVE_Y, activeWidthScaled, activeHeightScaled * activePercent);
		}
	}
	
	/**
	 * This sets an enemy as a boss
	 * @param boss: the boss
	 * @param name: the name to be displayed above the hp bar
	 */
	public void setBoss(Schmuck boss, String name) {
		this.boss = boss;
		bossFight = true;
		bossName = name;
	}
	
	/**
	 * This simply clears the boss hp bar from the ui
	 */
	public void clearBoss() { bossFight = false; }

	public float getHpRatio() { return hpRatio; }

	public float getFuelRatio() { return fuelRatio; }

	public float getFuelCutoffRatio() { return fuelCutoffRatio; }
}
