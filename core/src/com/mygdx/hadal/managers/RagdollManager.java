package com.mygdx.hadal.managers;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.mygdx.hadal.managers.loaders.RagdollLoader;
import com.mygdx.hadal.requests.RagdollCreate;
import com.mygdx.hadal.schmucks.entities.Ragdoll;
import com.mygdx.hadal.states.PlayState;

public class RagdollManager {

    private static RagdollLoader loader;

    public static void initLoader(RagdollLoader loader) {
        RagdollManager.loader = loader;
    }


    public static Ragdoll getRagdoll(PlayState state, RagdollCreate ragdollCreate) {
        return RagdollManager.loader.getRagdoll(state, ragdollCreate);
    }

    public static Ragdoll getRagdollFBO(PlayState state, RagdollCreate ragdollCreate, FrameBuffer frameBuffer) {
        return RagdollManager.loader.getRagdollFBO(state, ragdollCreate, frameBuffer);
    }
}