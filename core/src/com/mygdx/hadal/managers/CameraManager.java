package com.mygdx.hadal.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.CameraUtil;

/**
 * CameraManager manages the playstate camera. Logic controlling the camera was moved here for organizational purposes
 */
public class CameraManager {

    private final PlayState state;

    private final TiledMap map;

    //This is the coordinate that the camera tries to focus on when set to aim at an entity. When null, the camera focuses on the player.
    private Vector2 cameraTarget;

    //the camera offset from the target by this vector. (this is pretty much only used when focusing on the player)
    private final Vector2 cameraOffset = new Vector2();

    //coordinate the camera is looking at in spectator mode.
    private final Vector2 spectatorTarget = new Vector2();

    //These are the bounds of the camera movement. We make the numbers really big so that the default is no bounds.
    private final float[] cameraBounds = {100000.0f, -100000.0f, 100000.0f, -100000.0f};
    private final float[] spectatorBounds = {100000.0f, -100000.0f, 100000.0f, -100000.0f};

    //are the spectator bounds distinct from the camera bounds? (relevant mostly for 1-p maps with multiple sets of bounds)
    private boolean spectatorBounded;

    //The current zoom of the camera and the zoom that the camera will lerp towards
    private float zoom;
    protected float zoomDesired;

    //modifier that affects the camera zoom
    private float zoomModifier = 0.0f;

    public CameraManager(PlayState state, TiledMap map) {
        this.state = state;
        this.map = map;

        //map null in case of headless server
        if (map != null) {
            zoom = map.getProperties().get("zoom", 1.0f, float.class);
            zoomDesired = zoom;
        }
    }

    private static final float CAMERA_TIME = 1 / 120f;
    private float cameraAccumulator;
    public void controller(float delta) {
        //Update the game camera.
        cameraAccumulator += delta;
        while (cameraAccumulator >= CAMERA_TIME) {
            cameraAccumulator -= CAMERA_TIME;
            cameraUpdate();
        }
    }

    /**
     * This is called every update. This resets the camera zoom and makes it move towards the player (or other designated target).
     */
    private static final float MOUSE_CAMERA_TRACK = 0.5f;
    private static final float CAMERA_INTERPOLATION = 0.08f;
    private static final float CAMERA_AIM_INTERPOLATION = 0.025f;
    final Vector2 aimFocusVector = new Vector2();
    final Vector3 mousePosition = new Vector3();
    final Vector2 cameraFocusAimVector = new Vector2();
    final Vector2 cameraFocusAimPoint = new Vector2();
    public void cameraUpdate() {
        zoom = zoom + (zoomDesired * (1.0f + zoomModifier) - zoom) * 0.1f;

        state.getCamera().zoom = zoom;

        if (cameraTarget == null) {
            //the camera should be draggable as a spectator or during respawn time
            if (state.getSpectatorManager().isSpectatorMode() || state.getUIManager().getKillFeed().isRespawnSpectator()) {
                //in spectator mode, the camera moves when dragging the mouse
                state.getUIManager().getUiSpectator().spectatorDragCamera(spectatorTarget);
                aimFocusVector.set(spectatorTarget);
            } else if (null != HadalGame.usm.getOwnPlayer()) {
                if (null != HadalGame.usm.getOwnPlayer().getPlayerData()) {
                    //we check for body being null so client does not try to lerp camera towards starting position of (0, 0)
                    aimFocusVector.set(HadalGame.usm.getOwnPlayer().getPixelPosition());

                    //if enabled, camera tracks mouse position
                    if (JSONManager.setting.isMouseCameraTrack()) {
                        mousePosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                        HadalGame.viewportCamera.unproject(mousePosition);
                        mousePosition.sub(aimFocusVector.x, aimFocusVector.y, 0);
                        cameraFocusAimVector.x = (int) (cameraFocusAimVector.x + (mousePosition.x - cameraFocusAimVector.x) * CAMERA_AIM_INTERPOLATION);
                        cameraFocusAimVector.y = (int) (cameraFocusAimVector.y + (mousePosition.y - cameraFocusAimVector.y) * CAMERA_AIM_INTERPOLATION);
                        cameraFocusAimPoint.set(aimFocusVector).add(cameraFocusAimVector);
                        aimFocusVector.mulAdd(cameraFocusAimPoint, MOUSE_CAMERA_TRACK).scl(1.0f / (1.0f + MOUSE_CAMERA_TRACK));
                    }
                }
            }
        } else {
            aimFocusVector.set(cameraTarget);
        }
        CameraUtil.obeyCameraBounds(aimFocusVector, state.getCamera(), cameraBounds);
        aimFocusVector.add(cameraOffset);

        //process camera shaking
        CameraUtil.shake(state.getCamera(), aimFocusVector, CAMERA_TIME);
        spectatorTarget.set(aimFocusVector);
        CameraUtil.lerpToTarget(state.getCamera(), aimFocusVector, CAMERA_INTERPOLATION);
    }

    final Vector2 tmpVector2 = new Vector2();
    /**
     * Run when the camera resizes. Tries to keep camera position constant
     */
    public void resize() {
        //This refocuses the camera to avoid camera moving after resizing
        if (JSONManager.setting.isMouseCameraTrack()) {
            tmpVector2.set(aimFocusVector.x, aimFocusVector.y);
        } else if (null == cameraTarget && null != HadalGame.usm.getOwnPlayer()) {
            if (HadalGame.usm.getOwnPlayer().getBody() != null && HadalGame.usm.getOwnPlayer().isAlive()) {
                tmpVector2.set(HadalGame.usm.getOwnPlayer().getPixelPosition().x, HadalGame.usm.getOwnPlayer().getPixelPosition().y);
            }
        } else {
            tmpVector2.set(cameraTarget.x, cameraTarget.y);
        }
        CameraUtil.obeyCameraBounds(tmpVector2, state.getCamera(), cameraBounds);

        state.getCamera().position.set(new Vector3(tmpVector2.x, tmpVector2.y, 0));
    }

    private static final float SPECTATOR_DEFAULT_ZOOM = 1.5f;

    /**
     * This handles how the camera should behave when transitioning to spectator
     * Uses different zoom/bounds
     */
    public void setSpectator() {
        spectatorTarget.set(state.getCamera().position.x, state.getCamera().position.y);

        this.zoomDesired = map.getProperties().get("zoom", SPECTATOR_DEFAULT_ZOOM, float.class);
        this.cameraTarget = null;
        this.cameraOffset.set(0, 0);
        if (spectatorBounded) {
            System.arraycopy(spectatorBounds, 0, cameraBounds, 0, 4);
        }
    }

    /**
     * This sets the cameras position directly without interpolating to the point.
     * Used to things like spawning in
     */
    public void setCameraPosition(Vector2 cameraPosition) {
        tmpVector2.set(cameraPosition);
        CameraUtil.obeyCameraBounds(tmpVector2, state.getCamera(), cameraBounds);
        state.getCamera().position.set(new Vector3(tmpVector2.x, tmpVector2.y, 0));
    }

    public void setCameraTarget(Vector2 cameraTarget) {	this.cameraTarget = cameraTarget; }

    public void setCameraOffset(float offsetX, float offsetY) {	cameraOffset.set(offsetX, offsetY); }

    public Vector2 getCameraTarget() {	return cameraTarget; }

    public Vector2 getCameraFocusAimVector() { return cameraFocusAimVector; }

    public float[] getCameraBounds() { return cameraBounds; }

    public float[] getSpectatorBounds() { return spectatorBounds; }

    public void setSpectatorBounded(boolean spectatorBounded) { this.spectatorBounded = spectatorBounded; }

    public boolean isSpectatorBounded() { return spectatorBounded; }

    public void setZoom(float zoom) { this.zoomDesired = zoom; }

    public void setZoomModifier(float zoomModifier) { this.zoomModifier = zoomModifier; }
}
