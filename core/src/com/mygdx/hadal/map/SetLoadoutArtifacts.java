package com.mygdx.hadal.map;

import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This mode setting is used for modes with a set list of artifacts
 * @author Shibalante Shewort
 */
public class SetLoadoutArtifacts extends ModeSetting {

    private final ArrayList<UnlockArtifact> mapArtifacts = new ArrayList<>();

    public SetLoadoutArtifacts(UnlockArtifact... artifacts) {
        mapArtifacts.addAll(Arrays.asList(artifacts));
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        state.setMapArtifacts(mapArtifacts);
    }
}
