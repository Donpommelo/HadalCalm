package com.mygdx.hadal.server.managers.loaders;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.mygdx.hadal.managers.loaders.RagdollLoader;
import com.mygdx.hadal.requests.RagdollCreate;
import com.mygdx.hadal.schmucks.entities.Ragdoll;
import com.mygdx.hadal.states.PlayState;

public class RagdollLoaderHeadless extends RagdollLoader {

    @Override
    public Ragdoll getRagdoll(PlayState state, RagdollCreate ragdollCreate) { return null; }

    @Override
    public Ragdoll getRagdollFBO(PlayState state, RagdollCreate ragdollCreate, FrameBuffer frameBuffer) { return null; }
}