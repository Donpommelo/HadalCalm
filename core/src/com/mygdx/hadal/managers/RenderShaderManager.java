package com.mygdx.hadal.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.event.hub.Wallpaper;
import com.mygdx.hadal.states.PlayState;

public class RenderShaderManager {

    private final PlayState state;

    private final TextureRegion bg;
    private Shader shaderBase = Shader.NOTHING;

    public RenderShaderManager(PlayState state, TiledMap map) {
        this.state = state;

        if (map.getProperties().get("customShader", false, Boolean.class)) {
            shaderBase = Wallpaper.SHADERS[JSONManager.setting.getCustomShader()];
            ShaderManager.loadShader(shaderBase);
        } else if (map.getProperties().get("shader", String.class) != null) {
            shaderBase = Shader.valueOf(map.getProperties().get("shader", String.class));
            ShaderManager.loadShader(shaderBase);
        }

        //Init background image
        this.bg = new TextureRegion((Texture) HadalGame.assetManager.get(AssetList.BACKGROUND2.toString()));
    }

    public void renderBefore(SpriteBatch batch) {
        //Render Background
        batch.setProjectionMatrix(state.getHud().combined);
        batch.disableBlending();
        batch.begin();

        //render shader
        if (shaderBase.getShaderProgram() != null) {
            batch.setShader(shaderBase.getShaderProgram());
            shaderBase.shaderPlayUpdate(state, state.getTimer());
            shaderBase.shaderDefaultUpdate(state.getTimer());
        }

        batch.draw(bg, 0, 0, HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);

        if (shaderBase.getShaderProgram() != null) {
            if (shaderBase.isBackground()) {
                batch.setShader(null);
            }
        }

        batch.end();
        batch.enableBlending();
    }

    public void renderAfter(SpriteBatch batch) {
        if (shaderBase.getShaderProgram() != null) {
            if (!shaderBase.isBackground()) {
                batch.setShader(null);
            }
        }

        batch.end();
    }

    public void resize() {
        if (shaderBase.getShaderProgram() != null) {
            shaderBase.getShaderProgram().bind();
            shaderBase.shaderResize();
        }
    }

    /**
     * This sets a shader to be used as a "base-shader" for things like the background
     */
    public void setShaderBase(Shader shader) {
        shaderBase = shader;
        ShaderManager.loadShader(shaderBase);
    }
}
