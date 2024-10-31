package com.mygdx.hadal.server.managers.loaders;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.loaders.SpriteLoader;

public class SpriteLoaderHeadless extends SpriteLoader {

    @Override
    public Array<? extends TextureRegion> getFrames(Sprite sprite) { return null; }

    @Override
    public Animation<TextureRegion> getAnimation(Sprite sprite, float speed, Animation.PlayMode playMode) { return null; }

    @Override
    public TextureRegion getFrame(Sprite sprite, int frame) { return null; }

    @Override
    public Vector2 getDimensions(Sprite sprite) { return new Vector2(1, 1); }
}
