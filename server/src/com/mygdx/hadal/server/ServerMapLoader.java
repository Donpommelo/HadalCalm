package com.mygdx.hadal.server;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * The ServerMapLoader replaces the standard TmxMapLoader for headless playstates.
 * This is so we can override the load method to avoid any graphics
 */
public class ServerMapLoader extends TmxMapLoader {
    @Override
    public TiledMap load(String fileName, Parameters parameter) {
        FileHandle tmxFile = resolve(fileName);
        this.root = xml.parse(tmxFile);
        return loadTiledMap(tmxFile, parameter, name -> new TextureRegion());
    }
}
