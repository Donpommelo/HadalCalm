package com.mygdx.hadal.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Blinded;

/**
 * RenderManager encapsulates several other managers that process graphics in the playstate.
 * This is organized like this so headless server can just not have a render manager
 */
public class RenderManager {

    private final PlayState state;

    private final RenderEntityManager entityManager;
    private final RenderShaderManager shaderManager;
    private final RenderWorldManager worldManager;

    //white screen used for blind effect
    private final TextureRegion white;

    public RenderManager(PlayState state, TiledMap map) {
        this.state = state;

        if (state instanceof ClientState clientState) {
            this.entityManager = new RenderEntityManagerClient(clientState);
        } else {
            this.entityManager = new RenderEntityManager(state);
        }
        this.shaderManager = new RenderShaderManager(state, map);
        this.worldManager = new RenderWorldManager(state, map);

        this.white = new TextureRegion((Texture) HadalGame.assetManager.get(AssetList.WHITE.toString()));
    }

    public void render(SpriteBatch batch, float delta) {
        shaderManager.renderBefore(batch);

        worldManager.render(delta);

        //Iterate through entities in the world to render visible entities
        batch.setProjectionMatrix(state.getCamera().combined);
        batch.begin();

        Particle.drawParticlesBelow(batch, delta);
        entityManager.renderEntities();
        Particle.drawParticlesAbove(batch, delta);

        shaderManager.renderAfter(batch);

        //add white filter if the player is blinded
        if (null != HadalGame.usm.getOwnPlayer()) {
            if (HadalGame.usm.getOwnPlayer().getBlinded() > 0.0f) {
                batch.setProjectionMatrix(state.getHud().combined);
                batch.begin();

                batch.setColor(1.0f, 1.0f, 1.0f, Blinded.getBlindAmount(HadalGame.usm.getOwnPlayer().getBlinded()));
                batch.draw(white, 0, 0, HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);
                batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);

                batch.end();
            }
        }
    }

    public void resize() {
        getShaderManager().resize();
    }

    public RenderShaderManager getShaderManager() { return shaderManager; }

    public RenderWorldManager getWorldManager() { return worldManager; }
}
