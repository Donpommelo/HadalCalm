package com.mygdx.hadal.map;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

public class SpawnWeapons extends ModeSetting {

    private static final String weaponTimerId = "spawnWeapons";
    private static final float weaponSpawnTimer = 15.0f;

    @Override
    public String loadSettingStart(PlayState state, GameMode mode) {
        RectangleMapObject weaponTimer = new RectangleMapObject();
        weaponTimer.setName("Timer");
        weaponTimer.getProperties().put("interval", weaponSpawnTimer);
        weaponTimer.getProperties().put("triggeringId", weaponTimerId);

        TiledObjectUtil.parseTiledEvent(state, weaponTimer);

        return weaponTimerId;
    }
}
