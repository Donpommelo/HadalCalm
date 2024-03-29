package com.mygdx.hadal.map;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * This mode setting is used for modes where weapon spawns should be activated periodically
 * @author Jergarita Jisrael
 */
public class SpawnWeapons extends ModeSetting {

    private static final String weaponTimerId = "spawnWeapons";
    private static final float weaponSpawnTimer = 10.0f;

    @Override
    public String loadSettingStart(PlayState state, GameMode mode) {
        RectangleMapObject weaponTimer = new RectangleMapObject();
        weaponTimer.setName("Timer");
        weaponTimer.getProperties().put("interval", weaponSpawnTimer);
        weaponTimer.getProperties().put("triggeringId", weaponTimerId);

        TiledObjectUtil.parseAddTiledEvent(state, weaponTimer);

        return weaponTimerId;
    }
}
