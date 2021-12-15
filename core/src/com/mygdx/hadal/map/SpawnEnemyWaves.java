package com.mygdx.hadal.map;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * This mode setting is used for modes where enemy waves spawners are created and periodically activate
 * @author Jodrach Jeroro
 */
public class SpawnEnemyWaves extends ModeSetting {

    private static final float waveSpawnTimer = 15.0f;

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        if (!state.isServer()) { return; }

        String waveTimerId = TiledObjectUtil.getPrefabTriggerId();
        String multiWaveId = TiledObjectUtil.getPrefabTriggerId();

        RectangleMapObject wave = new RectangleMapObject();
        wave.setName("Timer");
        wave.getProperties().put("interval", waveSpawnTimer);
        wave.getProperties().put("triggeredId", waveTimerId);
        wave.getProperties().put("triggeringId", multiWaveId);

        RectangleMapObject multiWave = new RectangleMapObject();
        multiWave.setName("Multitrigger");
        multiWave.getProperties().put("triggeredId", multiWaveId);
        multiWave.getProperties().put("triggeringId", "wave1,wave2,wave3,wave4,wave5,wave6");

        TiledObjectUtil.parseTiledEvent(state, wave);
        TiledObjectUtil.parseTiledEvent(state, multiWave);
    }
}
