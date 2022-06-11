package com.mygdx.hadal.map;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.bots.BotManager;
import com.mygdx.hadal.bots.RallyPoint;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * This mode setting is used for modes where enemy waves spawners are created and periodically activate
 * @author Jodrach Jeroro
 */
public class SpawnEnemyWaves extends ModeSetting {

    @Override
    public void processGameEnd() {
        BotManager.terminatePathfindingThreads();
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {

        //load settings misc is run by both server and client, so this is needed to avoid creating events for client
        if (!state.isServer()) { return; }

        RectangleMapObject waveController = new RectangleMapObject();
        waveController.setName("WaveSpawnController");

        TiledObjectUtil.parseTiledEvent(state, waveController);

        //clear existing rally points to avoid memory leak
        for (RallyPoint point : BotManager.rallyPoints.values()) {
            point.getConnections().clear();
            point.getShortestPaths().clear();
        }
        BotManager.rallyPoints.clear();

        BotManager.initiateRallyPoints(state, state.getMap());
        BotManager.initiatePathfindingThreads();
    }
}
