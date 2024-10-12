package com.mygdx.hadal.managers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.states.PlayState;

public class RenderEntityManager {

    private final PlayState state;

    //this maps shaders to all current entities using them so they can be rendered in a batch
    private final ObjectMap<Shader, Array<HadalEntity>> dynamicShaderEntities = new ObjectMap<>();
    private final ObjectMap<Shader, Array<HadalEntity>> staticShaderEntities = new ObjectMap<>();

    public RenderEntityManager(PlayState state) {
        this.state = state;
    }

    public void renderEntities() {
        for (ObjectSet<HadalEntity> s : state.getEntityLists()) {
            for (HadalEntity entity : s) {
                renderEntity(entity);
            }
        }
        renderShadedEntities();
    }

    private final Vector2 entityLocation = new Vector2();
    /**
     * This method renders a single entity.
     * @param entity: the entity we are rendering
     */
    public void renderEntity(HadalEntity entity) {
        entityLocation.set(entity.getPixelPosition());
        if (entity.isVisible(entityLocation)) {

            //for shaded entities, add them to a map instead of rendering right away so we can render them at once
            if (entity.getShaderStatic() != null && entity.getShaderStatic() != Shader.NOTHING) {
                Array<HadalEntity> shadedEntities = staticShaderEntities.get(entity.getShaderStatic());
                if (null == shadedEntities) {
                    shadedEntities = new Array<>();
                    staticShaderEntities.put(entity.getShaderStatic(), shadedEntities);
                }
                shadedEntities.add(entity);
            } else if (entity.getShaderHelper().getShader() != null && entity.getShaderHelper().getShader() != Shader.NOTHING) {
                Array<HadalEntity> shadedEntities = dynamicShaderEntities.get(entity.getShaderHelper().getShader());
                if (null == shadedEntities) {
                    shadedEntities = new Array<>();
                    dynamicShaderEntities.put(entity.getShaderHelper().getShader(), shadedEntities);
                }
                shadedEntities.add(entity);
            } else {
                entity.render(state.getBatch(), entityLocation);
            }
        }
    }

    /**
     * This renders shaded entities so we can minimize shader switches
     */
    public void renderShadedEntities() {

        //do same thing for static shaders
        for (ObjectMap.Entry<Shader, Array<HadalEntity>> entry : staticShaderEntities) {

            //we sometimes set static shaders without loading them (overrided static shaders that are conditional)
            if (null == entry.key.getShaderProgram()) {
                entry.key.loadStaticShader();
            }
            state.getBatch().setShader(entry.key.getShaderProgram());
            for (HadalEntity entity : entry.value) {
                entityLocation.set(entity.getPixelPosition());
                entity.render(state.getBatch(), entityLocation);

                if (entity.getShaderHelper().getShaderStaticCount() <= 0.0f) {
                    entity.getShaderHelper().setStaticShader(Shader.NOTHING, 0.0f);
                }
            }
        }
        staticShaderEntities.clear();

        for (ObjectMap.Entry<Shader, Array<HadalEntity>> entry : dynamicShaderEntities) {
            //for each shader, render all entities using it at once so we only need to set it once
            state.getBatch().setShader(entry.key.getShaderProgram());
            for (HadalEntity entity : entry.value) {
                entityLocation.set(entity.getPixelPosition());

                //unlike static shaders, dynamic shaders need controller updated
                entity.getShaderHelper().processShaderController(state.getTimer());
                entity.render(state.getBatch(), entityLocation);

                if (entity.getShaderHelper().getShaderCount() <= 0.0f) {
                    entity.getShaderHelper().setShader(Shader.NOTHING, 0.0f);
                }
            }
        }
        dynamicShaderEntities.clear();

        state.getBatch().setShader(null);
    }
}
