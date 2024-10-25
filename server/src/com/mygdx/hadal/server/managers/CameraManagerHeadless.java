package com.mygdx.hadal.server.managers;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.managers.CameraManager;
import com.mygdx.hadal.states.PlayState;

public class CameraManagerHeadless extends CameraManager {

    public CameraManagerHeadless(PlayState state, TiledMap map) {
        super(state, map);
    }

    @Override
    public void controller(float delta) {}

    @Override
    public void cameraUpdate() {}

    @Override
    public void resize() {}

    @Override
    public void setSpectator() {}

    @Override
    public void setCameraPosition(Vector2 cameraPosition) {}
}
