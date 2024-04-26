package com.mygdx.hadal.users;

import com.mygdx.hadal.equip.artifacts.DrownedPoetsInkwell;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.statuses.Invisibility;

public class EffectManager {

    private final User user;

    //should preemptive spawn particles appear for the next spawn? (reset after spawning. Only for invisible spawning)
    private boolean showSpawnParticles = true;

    //player's primary weapon in their last saved loadout. Only used for clients for the Ol' Faithful artifact
    private UnlockEquip lastEquippedPrimary = UnlockEquip.NOTHING;

    public EffectManager(User user) {
        this.user = user;
    }

    public void newLevelReset() {
        showSpawnParticles = true;
    }

    public void afterPlayerCreate(Player player) {
        if (!showSpawnParticles) {
            player.getPlayerData().addStatus(new Invisibility(player.getState(), DrownedPoetsInkwell.INVIS_DURATION,
                    player.getPlayerData(), player.getPlayerData()));
        }
        showSpawnParticles = true;
    }

    public boolean isShowSpawnParticles() { return showSpawnParticles; }

    public void setShowSpawnParticles(boolean showSpawnParticles) { this.showSpawnParticles = showSpawnParticles; }

    public UnlockEquip getLastEquippedPrimary() { return lastEquippedPrimary; }

    public void setLastEquippedPrimary(UnlockEquip lastEquippedPrimary) { this.lastEquippedPrimary = lastEquippedPrimary; }
}
