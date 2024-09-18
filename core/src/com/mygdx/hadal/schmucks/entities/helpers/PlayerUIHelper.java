package com.mygdx.hadal.schmucks.entities.helpers;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.SpriteConstants;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

import java.util.Objects;

import static com.mygdx.hadal.managers.SkinManager.FONT_SPRITE;

/**
 * PlayerUIHelper renders ui elements attached to the player.
 * This includes hp, fuel, name, reload/charge meter
 */
public class PlayerUIHelper {

    public static final float UI_SCALE = 0.4f;

    private static final int BAR_OFFSET_X = 10;
    private static final int BAR_OFFSET_Y = 4;
    private static final int TEXT_OFFSET_X = 12;

    private static final int BAR_X = 20;
    private static final int BAR_Y = 0;
    private static final int BAR_MIRROR_X_OFFSET = -5;
    private static final int HP_WIDTH = 5;
    private static final int HP_HEIGHT = 40;
    private static final int NAME_OFFSET_Y = 25;

    private static final int FLIP_RANGE = 80;
    private static final int CUTOFF_THICKNESS = 3;
    private static final float METER_DELAY = 0.25f;

    private static final int TYPE_OFFSET_X = -25;
    private static final int TYPE_OFFSET_Y = 20;
    private static final int TYPE_WIDTH = 50;
    private static final int TYPE_HEIGHT = 40;
    private static final float TYPING_BUBBLE_DURATION = 1.0f;

    private final PlayState state;
    private final Player player;
    private final Animation<TextureRegion> typingBubble;
    private final TextureRegion reloadMeter, reloadBar, hpBar, hpBarFade, fuelBar, fuelCutoff;

    //This is the percent of reload completed, if reloading. This is used to display the reload ui for all players.
    protected float reloadPercent, reloadDelayed;

    //This is the percent of charge completed, if charging. This is used to display the charge ui for all players.
    protected float chargePercent, chargeDelayed;

    //is the fuel/hp bar rendered on the right or left?
    private boolean barRight;

    //is the player currently typing in chat? (yes if this float is greater that 0.0f)
    private float typingCdCount;
    private boolean typing;

    public PlayerUIHelper(PlayState state, Player player) {
        this.state = state;
        this.player = player;

        this.reloadMeter = Sprite.UI_RELOAD_METER.getFrame();
        this.reloadBar = Sprite.UI_RELOAD_BAR.getFrame();
        this.hpBar = Sprite.UI_MAIN_HEALTHBAR.getFrame();
        this.hpBarFade = Sprite.UI_MAIN_HEALTH_MISSING.getFrame();
        this.fuelBar = Sprite.UI_MAIN_FUELBAR.getFrame();
        this.fuelCutoff = Sprite.UI_MAIN_FUEL_CUTOFF.getFrame();
        this.typingBubble =  new Animation<>(SpriteConstants.SPRITE_ANIMATION_SPEED_SLOW,
                Objects.requireNonNull(Sprite.NOTIFICATIONS_CHAT.getFrames()));
        typingBubble.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
    }

    public void controllerEquip(float delta) {
        Equippable currentTool = player.getEquipHelper().getCurrentTool();

        //keep track of reload/charge percent to properly sync those fields in the ui
        reloadPercent = currentTool.getReloadCd() / (currentTool.getReloadTime());
        chargePercent = currentTool.getChargeCd() / (currentTool.getChargeTime());

        typingCdCount -= delta;
        if (typingCdCount <= 0.0f) {
            typing = false;
        }
    }

    public void render(SpriteBatch batch, Vector2 playerLocation, boolean visible) {
        float textX = playerLocation.x - reloadMeter.getRegionWidth() * UI_SCALE / 2;
        float textY = playerLocation.y + reloadMeter.getRegionHeight() * UI_SCALE + player.getSize().y / 2;
        Equippable currentTool = player.getEquipHelper().getCurrentTool();

        //render player ui
        if (currentTool.isReloading()) {

            //Calculate reload progress
            reloadDelayed = Math.min(1.0f, reloadDelayed + (reloadPercent - reloadDelayed) * METER_DELAY);

            batch.draw(reloadBar, textX + BAR_OFFSET_X, textY + BAR_OFFSET_Y,
                    reloadBar.getRegionWidth() * UI_SCALE * reloadDelayed, reloadBar.getRegionHeight() * UI_SCALE);
            FONT_SPRITE.draw(batch, UIText.RELOADING.text(), textX + TEXT_OFFSET_X,
                    textY + reloadMeter.getRegionHeight() * UI_SCALE);
            batch.draw(reloadMeter, textX, textY, reloadMeter.getRegionWidth() * UI_SCALE, reloadMeter.getRegionHeight() * UI_SCALE);

            if (reloadDelayed > reloadPercent) {
                reloadDelayed = 0.0f;
            }
        } else {
            reloadDelayed = 0.0f;
        }

        if (currentTool.isCharging()) {

            //Calculate charge progress
            chargeDelayed = Math.min(1.0f, chargeDelayed + (chargePercent - chargeDelayed) * METER_DELAY);
            batch.draw(reloadBar, textX + BAR_OFFSET_X, textY + BAR_OFFSET_Y,
                    reloadBar.getRegionWidth() * UI_SCALE * chargeDelayed, reloadBar.getRegionHeight() * UI_SCALE);
            FONT_SPRITE.draw(batch, currentTool.getChargeText(), textX + TEXT_OFFSET_X,
                    textY + reloadMeter.getRegionHeight() * UI_SCALE);
            batch.draw(reloadMeter, textX, textY, reloadMeter.getRegionWidth() * UI_SCALE, reloadMeter.getRegionHeight() * UI_SCALE);
        } else {
            chargeDelayed = 0.0f;
        }

        //render "out of ammo"
        if (currentTool.isOutofAmmo()) {
            FONT_SPRITE.draw(batch, UIText.OUT_OF_AMMO.text(), textX + TEXT_OFFSET_X,
                    textY + reloadMeter.getRegionHeight() * UI_SCALE);
        }

        float hpX, hpRatio, fuelRatio, fuelCutoffRatio;
        if (visible) {
            if (barRight) {
                hpX = playerLocation.x + BAR_X;
                if (player.getMouseHelper().getAttackAngle() > 180 - FLIP_RANGE || player.getMouseHelper().getAttackAngle() < -180 + FLIP_RANGE) {
                    barRight = false;
                }
            } else {
                hpX = playerLocation.x - BAR_X - HP_WIDTH + BAR_MIRROR_X_OFFSET;
                if (player.getMouseHelper().getAttackAngle() < FLIP_RANGE && player.getMouseHelper().getAttackAngle() > -FLIP_RANGE) {
                    barRight = true;
                }
            }

            if (player.getUser().equals(HadalGame.usm.getOwnUser())) {
                if (JSONManager.setting.isDisplayHp()) {
                    hpRatio = state.getUIManager().getUiPlay().getHpRatio();
                    fuelRatio = state.getUIManager().getUiPlay().getFuelRatio();
                    fuelCutoffRatio = state.getUIManager().getUiPlay().getFuelCutoffRatio();
                    if (barRight) {
                        batch.draw(fuelBar, hpX, playerLocation.y + BAR_Y, HP_WIDTH, HP_HEIGHT * fuelRatio);
                        batch.draw(hpBarFade, hpX + HP_WIDTH, playerLocation.y + BAR_Y, HP_WIDTH, HP_HEIGHT);
                        batch.draw(hpBar, hpX + HP_WIDTH, playerLocation.y + BAR_Y, HP_WIDTH, HP_HEIGHT * hpRatio);
                        batch.draw(fuelCutoff, hpX, playerLocation.y + BAR_Y + fuelCutoffRatio * HP_HEIGHT, HP_WIDTH, CUTOFF_THICKNESS);
                    } else {
                        batch.draw(fuelBar, hpX - HP_WIDTH, playerLocation.y + BAR_Y, HP_WIDTH, HP_HEIGHT * fuelRatio);
                        batch.draw(hpBarFade, hpX, playerLocation.y + BAR_Y, HP_WIDTH, HP_HEIGHT);
                        batch.draw(hpBar, hpX, playerLocation.y + BAR_Y, HP_WIDTH, HP_HEIGHT * hpRatio);
                        batch.draw(fuelCutoff, hpX - HP_WIDTH, playerLocation.y + BAR_Y + fuelCutoffRatio * HP_HEIGHT, HP_WIDTH, CUTOFF_THICKNESS);
                    }
                }
            } else {
                hpRatio = player.getPlayerData().getCurrentHp() / player.getPlayerData().getStat(Stats.MAX_HP);
                batch.draw(hpBarFade, hpX, playerLocation.y + BAR_Y, HP_WIDTH, HP_HEIGHT);
                batch.draw(hpBar, hpX, playerLocation.y + BAR_Y, HP_WIDTH, HP_HEIGHT * hpRatio);
            }
        }

        if (JSONManager.setting.isDisplayNames()) {
            //draw player name
            FONT_SPRITE.draw(batch, player.getName(),
                    playerLocation.x - player.getSize().x / 2,
                    playerLocation.y + player.getSize().y / 2 + NAME_OFFSET_Y);
        }

        //display typing bubble if typing
        if (typing) {
            batch.draw(typingBubble.getKeyFrame(player.getAnimationTime(), true),
                    playerLocation.x + TYPE_OFFSET_X, playerLocation.y + player.getSize().y / 2 + TYPE_OFFSET_Y,
                    TYPE_WIDTH, TYPE_HEIGHT);
        }
    }

    public void startTyping() {
        typingCdCount = TYPING_BUBBLE_DURATION;
        typing = true;
    }

    public float getReloadPercent() { return reloadPercent; }

    public void setReloadPercent(float reloadPercent) { this.reloadPercent = reloadPercent; }

    public float getChargePercent() { return chargePercent; }

    public void setChargePercent(float chargePercent) { this.chargePercent = chargePercent; }

    public boolean isTyping() { return typing; }

    public void setTyping(boolean typing) { this.typing = typing; }
}
