package com.mygdx.hadal.schmucks.entities.helpers;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.managers.StateManager;
import com.mygdx.hadal.map.SettingArcade;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.packets.PacketsLoadout;

import java.util.Arrays;

/**
 * LoadoutArtifactHelper manages the player's equipped artifacts.
 * This mostly contains utility functions for managing active artifacts.
 * This does not contain a list of artifacts since, unlike weapons and magic, artifacts don't contain their own data;
 * they are really just lists of statuses that are added/removed.
 */
public class LoadoutArtifactHelper {

    private final Player player;

    public LoadoutArtifactHelper(Player player) {
        this.player = player;
    }

    /**
     * This syncs a player's artifacts
     * @param artifact: the player's new set of artifacts
     * @param override: should this override artifact slot limitations?
     * @param save: should this sync save the result into the saved loadout file?
     */
    public void syncArtifact(UnlockArtifact[] artifact, boolean override, boolean save) {

        Loadout loadout = player.getUser().getLoadoutManager().getActiveLoadout();

        //first, removes statuses of existing artifacts and reset list
        for (int i = 0; i < Loadout.MAX_ARTIFACT_SLOTS; i++) {
            player.getPlayerData().removeArtifactStatus(loadout.artifacts[i]);
        }
        loadout.artifacts = new UnlockArtifact[Loadout.MAX_ARTIFACT_SLOTS];

        UnlockArtifact[] artifactsTemp = new UnlockArtifact[Loadout.MAX_ARTIFACT_SLOTS];

        if (SettingArcade.arcade && player.getState().isServer()) {
            System.arraycopy(player.getUser().getLoadoutManager().getArcadeLoadout().artifacts, 0, artifactsTemp, 0, Loadout.MAX_ARTIFACT_SLOTS);
        } else {
            System.arraycopy(artifact, 0, artifactsTemp, 0, Loadout.MAX_ARTIFACT_SLOTS);
        }

        Arrays.fill(loadout.artifacts, UnlockArtifact.NOTHING);
        for (int i = 0; i < Loadout.MAX_ARTIFACT_SLOTS; i++) {
            if (SettingArcade.arcade && player.getState().isServer()) {
                addArtifact(artifactsTemp[i], true, save);
            } else {
                addArtifact(artifactsTemp[i], override, save);
            }
        }

        //sync arcade loadout here. Normally, this is synced when adding/removing artifact, but it is skipped if loadout is empty
        if (SettingArcade.arcade) {
            saveArcadeArtifacts();
        }

        //add map modifiers as 0-cost, overriding, invisible artifacts
        for (UnlockArtifact modifier : player.getState().getMapModifiers()) {
            addArtifact(modifier, false, false);
        }

        //must save artifacts in case of removing last artifact (since that won't save in syncArtifact)
        if (save) {
            saveArtifacts();
        }

        //If this is the player being controlled by the user, update artifact ui
        if (player.getUser().equals(HadalGame.usm.getOwnUser())) {
            player.getState().getUIManager().getUiArtifact().syncArtifact();
        }

        //set this boolean so score window is updated
        player.getUser().setScoreUpdated(true);
    }

    /**
     * Add a new artifact.
     * @param override whether this change should override artifact limits (like admin's card)
     * @param save whether this change should be saved into loadout file (like special mode modifiers shouldn't)
     * @return whether the artifact adding was successful
     */
    public boolean addArtifact(UnlockArtifact artifactUnlock, boolean override, boolean save) {

        if (UnlockArtifact.NOTHING.equals(artifactUnlock)) { return false; }

        Artifact newArtifact = artifactUnlock.getArtifact();
        int slotsUsed = 0;

        //iterate through all artifacts and count the number of slots used
        for (int i = 0; i < Loadout.MAX_ARTIFACT_SLOTS; i++) {

            //new artifact fails to add if slot cost is too high
            slotsUsed += getActiveLoadout().artifacts[i].getArtifact().getSlotCost();

            if (slotsUsed + newArtifact.getSlotCost() > getNumArtifactSlots() && !override) {
                return false;
            }

            if (!(UnlockArtifact.NOTHING.equals(getActiveLoadout().artifacts[i]))) {

                //new artifact fails to add if a repeat
                if (artifactUnlock.equals(getActiveLoadout().artifacts[i])) {
                    return false;
                }

            } else {

                //when we reach a NOTHING (empty slot), we add the artifact
                newArtifact.loadEnchantments(player.getState(), player.getPlayerData());
                if (newArtifact.getEnchantment() != null) {
                    player.getPlayerData().addStatus(newArtifact.getEnchantment());
                    newArtifact.getEnchantment().setArtifact(artifactUnlock);
                }
                getActiveLoadout().artifacts[i] = artifactUnlock;

                syncArtifacts(override, save);

                return true;
            }
        }
        return false;
    }

    /**
     * Remove a designated artifact.
     * use override to not abide by artifact slot costs
     */
    public void removeArtifact(UnlockArtifact artifact, boolean override) {

        if (UnlockArtifact.NOTHING.equals(artifact)) { return; }

        int indexRemoved = -1;

        //iterate through artifacts until we find the one we're trying to remove
        for (int i = 0; i < Loadout.MAX_ARTIFACT_SLOTS; i++) {
            if (getActiveLoadout().artifacts[i].equals(artifact)) {
                indexRemoved = i;
                break;
            }
        }

        //if found, remove all of the artifact's statuses and move other artifacts up in the list
        if (indexRemoved != -1) {
            if (getActiveLoadout().artifacts[indexRemoved] != null) {
                player.getPlayerData().removeArtifactStatus(artifact);
            }

            System.arraycopy(getActiveLoadout().artifacts, indexRemoved + 1, getActiveLoadout().artifacts, indexRemoved,
                    Loadout.MAX_ARTIFACT_SLOTS - 1 - indexRemoved);
            getActiveLoadout().artifacts[Loadout.MAX_ARTIFACT_SLOTS - 1] = UnlockArtifact.NOTHING;
        }

        syncArtifacts(override, true);
    }

    /**
     * This checks if the player has too many artifacts and removes all of the ones over carrying capacity
     */
    public void checkArtifactSlotCosts() {
        int slotsUsed = 0;
        for (int i = 0; i < Loadout.MAX_ARTIFACT_SLOTS; i++) {
            slotsUsed += getActiveLoadout().artifacts[i].getArtifact().getSlotCost();
            if (slotsUsed > getNumArtifactSlots()) {
                removeArtifact(getActiveLoadout().artifacts[i], true);
            }
        }
    }

    /**
     * This is called when a player's artifacts may change to sync ui and clients
     * @param override whether this change should override artifact limits
     * @param save whether this change should be saved into loadout file
     */
    public void syncArtifacts(boolean override, boolean save) {
        if (null == player.getPlayerData()) { return; }

        if (!override) {
            checkArtifactSlotCosts();
        }

        if (save) {
            saveArtifacts();
        }

        if (SettingArcade.arcade) {
            saveArcadeArtifacts();
        }

        if (player.getUser().equals(HadalGame.usm.getOwnUser())) {
            player.getState().getUIManager().getUiArtifact().syncArtifact();
        }

        //set this boolean so score window is updated
        player.getUser().setScoreUpdated(true);

        syncServerArtifactChange(getActiveLoadout().artifacts, save);
        player.getPlayerData().calcStats();
    }

    /**
     * This method saves the player's current artifacts into records
     */
    public void saveArtifacts() {
        if (player.getUser().equals(HadalGame.usm.getOwnUser())) {
            for (int i = 0; i < Loadout.MAX_ARTIFACT_SLOTS; i++) {
                JSONManager.loadout.setArtifact(HadalGame.usm.getOwnUser(), i, getActiveLoadout().artifacts[i].toString());
            }
        }
    }

    public void saveArcadeArtifacts() {
        for (int i = 0; i < Loadout.MAX_ARTIFACT_SLOTS; i++) {
            player.getUser().getLoadoutManager().getArcadeLoadout().artifacts[i] = getActiveLoadout().artifacts[i];
        }
    }

    /**
     * This returns the number of artifact slots after modifications
     * The extra if/else is there b/c artifact slots are checked by the client when they use the reliquary hub event.
     */
    public int getNumArtifactSlots() {
        if (StateManager.currentMode == StateManager.Mode.SINGLE) {
            return Math.min((int) (JSONManager.record.getSlotsUnlocked() + player.getPlayerData().getStat(Stats.ARTIFACT_SLOTS)), Loadout.MAX_ARTIFACT_SLOTS);
        } else {
            if (player.getState().isServer()) {
                return Math.min((int) (JSONManager.setting.getArtifactSlots() + player.getPlayerData().getStat(Stats.ARTIFACT_SLOTS)), Loadout.MAX_ARTIFACT_SLOTS);
            } else {
                return Math.min((int) (JSONManager.hostSetting.getArtifactSlots() + player.getPlayerData().getStat(Stats.ARTIFACT_SLOTS)), Loadout.MAX_ARTIFACT_SLOTS);
            }
        }
    }

    /**
     * This returns the number of unused artifact slots for display in reliquary hub event
     */
    public int getArtifactSlotsRemaining() { return getNumArtifactSlots() - getActiveLoadout().getArtifactSlotsUsed(); }

    public void syncServerArtifactChange(UnlockArtifact[] artifact, boolean save) {
        HadalGame.server.sendToAllTCP(new PacketsLoadout.SyncArtifactServer(player.getUser().getConnID(), artifact, save));
    }

    private Loadout getActiveLoadout() {
        return player.getUser().getLoadoutManager().getActiveLoadout();
    }
}
