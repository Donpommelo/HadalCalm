package com.mygdx.hadal.server.managers.loaders;

import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.managers.loaders.ShaderLoader;

public class ShaderLoaderHeadless extends ShaderLoader {

    @Override
    public void loadShader(Shader shader) { }

    @Override
    public void loadStaticShader(Shader shader) {}
}
