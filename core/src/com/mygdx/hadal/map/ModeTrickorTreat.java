package com.mygdx.hadal.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.bots.RallyPoint;
import com.mygdx.hadal.event.modes.FlagSpawner;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.schmucks.entities.PlayerBot;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;

/**
 */
public class ModeTrickorTreat extends ModeSetting {

    private static final int STARTING_CANDY = 10;

    //this maps all flag spawners to their team so that bots can locate them
    private static final ObjectMap<AlignmentFilter, FlagSpawner> flagSpawners = new ObjectMap<>();
    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        flagSpawners.clear();
        mode.setTeamStartScore(STARTING_CANDY);
        state.addMapModifier(UnlockArtifact.TRICK_OR_TREAT);
    }

    @Override
    public void processAIPath(PlayState state, PlayerBot bot, Vector2 playerLocation,
                              Array<RallyPoint.RallyPointMultiplier> path) {

    }
}
