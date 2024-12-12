package com.mygdx.hadal.managers;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.mygdx.hadal.states.PlayState;

import static com.mygdx.hadal.constants.Constants.PPM;

/**
 * RenderWorldManager is used by the PlayState to draw the world
 */
public class RenderWorldManager {

    private final PlayState state;

    //b2dr renders debug lines for testing
    private final Box2DDebugRenderer b2dr;
    private final OrthogonalTiledMapRenderer tmr;

    //do we draw the hitbox lines?
    private boolean debugHitbox;

    public RenderWorldManager(PlayState state, TiledMap map) {
        this.state = state;

        this.b2dr = new Box2DDebugRenderer();
        this.tmr = new OrthogonalTiledMapRenderer(map, state.getBatch());

        //set whether to draw hitbox debug lines or not
        this.debugHitbox = JSONManager.setting.isDebugHitbox();
    }

    public void render(float delta) {
        //Render Tiled Map + world
        tmr.setView(state.getCamera());
        tmr.render();

        //Render debug lines for box2d objects. THe 0 check prevents debug outlines from appearing in the freeze-frame
        if (debugHitbox && 0.0f != delta) {
            b2dr.render(state.getWorld(), state.getCamera().combined.scl(PPM));
            state.getCamera().combined.scl(1.0f / PPM);
        }
    }

    public void dispose() {
        b2dr.dispose();
        tmr.dispose();
    }

    public void toggleVisibleHitboxes(boolean debugHitbox) { this.debugHitbox = debugHitbox; }
}
