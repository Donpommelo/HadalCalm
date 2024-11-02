package com.mygdx.hadal.managers;

import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.managers.loaders.ShaderLoader;

public class ShaderManager {

    private static ShaderLoader loader;

    public static void initLoader(ShaderLoader loader) {
        ShaderManager.loader = loader;
    }

    public static void loadShader(Shader shader) {
        ShaderManager.loader.loadShader(shader);
    }

    public static void loadStaticShader(Shader shader) {
        ShaderManager.loader.loadStaticShader(shader);
    }
}
