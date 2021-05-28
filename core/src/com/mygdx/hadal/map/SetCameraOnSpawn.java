package com.mygdx.hadal.map;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

public class SetCameraOnSpawn extends ModeSetting {

    private static final float pvpMatchZoom = 1.5f;

    @Override
    public String loadSettingSpawn(PlayState state) {
        String gameCameraId = TiledObjectUtil.getPrefabTriggerId();

        RectangleMapObject camera1 = new RectangleMapObject();
        camera1.setName("Camera");
        camera1.getProperties().put("zoom", pvpMatchZoom);
        camera1.getProperties().put("triggeredId", gameCameraId);

        TiledObjectUtil.parseTiledEvent(state, camera1);

        return "bounds1,bounds2," + gameCameraId;
    }
}
