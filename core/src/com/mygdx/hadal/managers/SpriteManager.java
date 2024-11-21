package com.mygdx.hadal.managers;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.loaders.SpriteLoader;

/**
 * SpriteManager loads sprites.
 * Logic is delegated to Loader to make it easier for headless server to have different logic
 */
public class SpriteManager {

    private static SpriteLoader loader;

    public static void initLoader(SpriteLoader loader) {
        SpriteManager.loader = loader;
    }

    public static Array<? extends TextureRegion> getFrames(Sprite sprite) {
        return SpriteManager.loader.getFrames(sprite);
    }

    public static Animation<TextureRegion> getAnimation(Sprite sprite) {
        return getAnimation(sprite, sprite.getAnimationSpeed(), sprite.getPlayMode());
    }

    public static Animation<TextureRegion> getAnimation(Sprite sprite, float speed, Animation.PlayMode playMode) {
        return SpriteManager.loader.getAnimation(sprite, speed, playMode);
    }

    public static TextureRegion getFrame(Sprite sprite) {
        return getFrame(sprite, 0);
    }

    public static TextureRegion getFrame(Sprite sprite, int frame) {
        return SpriteManager.loader.getFrame(sprite, frame);
    }

    public static Vector2 getDimensions(Sprite sprite) {
        return SpriteManager.loader.getDimensions(sprite);
    }
}
