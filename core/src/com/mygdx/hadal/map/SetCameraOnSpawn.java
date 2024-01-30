package com.mygdx.hadal.map;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * This mode setting sets up the camera zoom and bound properties.
 * In retrospect, there isn't that much reason to include this as a mode setting outside of the fact that campaign modes
 * don't need it due to often having specific camera needs
 * @author Lotchocolate Leldutticini
 */
public class SetCameraOnSpawn extends ModeSetting {

    //for pvp matches, this is thte default camera zoom
    private static final float pvpMatchZoom = 1.5f;

    @Override
    public String loadSettingSpawn(PlayState state) {
        state.getCameraManager().setZoom(pvpMatchZoom);

        String gameCameraId = TiledObjectUtil.getPrefabTriggerId();

        RectangleMapObject camera1 = new RectangleMapObject();
        camera1.setName("Camera");
        camera1.getProperties().put("zoom", pvpMatchZoom);
        camera1.getProperties().put("triggeredId", gameCameraId);

        TiledObjectUtil.parseTiledEvent(state, camera1);

        return "bounds1,bounds2," + gameCameraId;
    }
}
