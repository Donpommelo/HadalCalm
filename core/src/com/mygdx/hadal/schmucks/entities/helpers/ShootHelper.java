package com.mygdx.hadal.schmucks.entities.helpers;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ProcTime;

/**
 * ShootHelper processes the player's shooting
 */
public class ShootHelper {

    private final PlayState state;
    private final Player player;

    //Is the player currently shooting?
    private boolean shooting;

    //does the player have a shoot action buffered? (i.e used when still on cd)
    private boolean shootBuffered;

    //Counter that keeps track of delay between action execution + next action
    private float shootCdCount;

    public ShootHelper(PlayState state, Player player) {
        this.state = state;
        this.player = player;
    }

    public void controller(float delta) {
        if (shooting) {
            shoot(delta, player.getEquipHelper().getCurrentTool(), true);
        }

        //If player is reloading, run the reload method of the current equipment.
        boolean reloading = player.getEquipHelper().getCurrentTool().isReloading();

        player.getEffectHelper().toggleReloadEffects(reloading);
        if (reloading) {
            player.getEquipHelper().getCurrentTool().reload(delta);
        }

        player.getUiHelper().controllerEquip(delta);

        //process weapon update (this is for weapons that have an effect that activates over time which is pretty rare)
        player.getEquipHelper().getCurrentTool().update(state, delta);

        if (shootBuffered && shootCdCount < 0) {
            shootBuffered = false;
            shoot(delta, player.getEquipHelper().getCurrentTool(), true);
        }

        //process cooldowns on firing
        shootCdCount -= delta;
    }


    /**
     * All players process equipment effects. Important for things like weapons with toggle sounds
     */
    public void controllerUniversal(float delta, Vector2 playerPosition) {
        for (Equippable equippable : player.getEquipHelper().getMultitools()) {
            equippable.processEffects(state, delta, playerPosition);
        }
    }

    /**
     * This method is called when a schmuck wants to use a tool.
     * @param delta: Time passed since last usage. This is used for Charge tools that keep track of time charged.
     * @param tool: Equipment that the schmuck wants to use
     * @param wait: Should this tool wait for base cooldowns. No for special tools like built-in airblast
     */
    public void shoot(float delta, Equippable tool, boolean wait) {
        if (player.isAlive()) {
            player.getBodyData().statusProcTime(new ProcTime.WhileAttack(delta, tool));

            //Only register the attempt if the user is not waiting on a tool's delay or cooldown. (or if tool ignores wait)
            if ((shootCdCount < 0) || !wait) {

                //Register the tool targeting the input coordinates.
                tool.mouseClicked(delta, state, player.getPlayerData(), player.getHitboxFilter(), player.getMouseHelper().getPixelPosition());

                //the schmuck will not register another tool usage for the tool's cd
                shootCdCount = tool.getUseCd() * (1 - player.getBodyData().getStat(Stats.TOOL_SPD));

                //execute the tool.
                tool.execute(state, player.getPlayerData());
            }
        }
    }

    /**
     * This is called when the player clicks the fire button. It is used to buffer fire inputs during weapon cooldowns
     */
    public void startShooting() {
        shooting = true;
        if (shootCdCount >= 0) {
            shootBuffered = true;
        }
    }

    /**
     * Player releases mouse. This is used to fire charge weapons.
     */
    public void release() {
        if (player.isAlive() && shooting) {
            useToolRelease(player.getEquipHelper().getCurrentTool());
        }
    }

    /**
     * This method is called after the user releases the button for a tool. Mostly used by charge weapons that execute when releasing
     * instead of after pressing.
     * @param tool: tool to release
     */
    public void useToolRelease(Equippable tool) {
        tool.release(state, player.getPlayerData());
    }

    public float getShootCdCount() { return shootCdCount; }

    public void setShootCdCount(float shootCdCount) { this.shootCdCount = shootCdCount; }

    public boolean isShooting() { return shooting; }

    public void setShooting(boolean shooting) { this.shooting = shooting; }
}
