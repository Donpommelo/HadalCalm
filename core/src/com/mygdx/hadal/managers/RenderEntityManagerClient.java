package com.mygdx.hadal.managers;

import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.states.ClientState;

import java.util.UUID;

public class RenderEntityManagerClient extends RenderEntityManager {

    private final ClientState clientState;

    public RenderEntityManagerClient(ClientState state) {
        super(state);
        this.clientState = state;
    }

    @Override
    public void renderEntities() {
        for (ObjectMap<UUID, HadalEntity> m : clientState.getEntityListsClient()) {
            for (HadalEntity entity : m.values()) {
                renderEntity(entity);
            }
        }
        renderShadedEntities();
    }
}
