package com.mygdx.hadal.map;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;

/**
 * This mode setting is used for modes with a set list of artifacts
 * @author Shibalante Shewort
 */
public class SetLoadoutArtifacts extends ModeSetting {

    private final Array<UnlockArtifact> mapArtifacts = new Array<>();

    public SetLoadoutArtifacts(UnlockArtifact... artifacts) {
        mapArtifacts.addAll(artifacts);
    }

    @Override
    public void processNewPlayerLoadout(PlayState state, GameMode mode, Loadout newLoadout, int connID) {
        for (int i = 0; i < Loadout.MAX_ARTIFACT_SLOTS; i++) {
            if (mapArtifacts.size > i) {
                newLoadout.artifacts[i] = mapArtifacts.get(i);
            } else {
                newLoadout.artifacts[i] = UnlockArtifact.NOTHING;
            }
        }
    }
}
