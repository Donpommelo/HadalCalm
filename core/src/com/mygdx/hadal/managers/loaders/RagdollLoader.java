package com.mygdx.hadal.managers.loaders;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.requests.RagdollCreate;
import com.mygdx.hadal.schmucks.entities.Ragdoll;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

/**
 * RagdollLoader centralizes the creation of ragdoll entities.
 * This makes it easier for headless servers to handle them (by skipping their creation)
 */
public class RagdollLoader {

    public Ragdoll getRagdoll(PlayState state, RagdollCreate ragdollCreate) {
        Ragdoll ragdoll = new Ragdoll(state, ragdollCreate);
        if (!state.isServer()) {
            ((ClientState) state).addEntity(ragdoll.getEntityID(), ragdoll, false, ObjectLayer.STANDARD);
        }
        return ragdoll;
    }

    /**
     * This is a special version of the ragdoll that contains a framebuffer that must be disposed
     * atm, this is only used for player death ragdolls
     */
    public Ragdoll getRagdollFBO(PlayState state, RagdollCreate ragdollCreate, FrameBuffer frameBuffer) {
        Ragdoll ragdoll = new Ragdoll(state, ragdollCreate) {

            //we need to dispose of the fbo when the ragdolls are done
            @Override
            public void dispose() {
                super.dispose();
                frameBuffer.dispose();
            }
        };
        if (!state.isServer()) {
            ((ClientState) state).addEntity(ragdoll.getEntityID(), ragdoll, false, ObjectLayer.STANDARD);
        }
        return ragdoll;
    }
}

