package com.mygdx.hadal.managers.loaders;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.effects.Sprite;

import java.util.Objects;

public class SpriteLoader {

    public Array<? extends TextureRegion> getFrames(Sprite sprite) {

        if (sprite.equals(Sprite.NOTHING)) {
            return Objects.requireNonNull(Sprite.getAtlas(Sprite.SpriteType.EVENT)).findRegions("eggplant");
        }

        if (null == sprite.getFrames()) {

            //complex frames are made of several sprites, repeated, lined up back-to-back
            if (sprite.isComplex()) {
                sprite.setFrames(new Array<>());
                for (Sprite.SpriteRep frame : sprite.getComplexFrames()) {
                    if ("".equals(frame.spriteId())) {
                        sprite.getFrames().addAll(Objects.requireNonNull(Sprite.getAtlas(sprite.getType())).getRegions());
                    } else {
                        for (int i = 0; i < frame.repeat(); i++) {
                            sprite.getFrames().addAll(Objects.requireNonNull(Sprite.getAtlas(sprite.getType())).findRegions(frame.spriteId()));
                        }
                    }
                }
            } else {
                if ("".equals(sprite.getSpriteId())) {
                    sprite.setFrames(Objects.requireNonNull(Sprite.getAtlas(sprite.getType())).getRegions());
                } else {
                    sprite.setFrames(Objects.requireNonNull(Sprite.getAtlas(sprite.getType())).findRegions(sprite.getSpriteId()));
                }
            }
        }

        return sprite.getFrames();
    }

    public Animation<TextureRegion> getAnimation(Sprite sprite, float speed, Animation.PlayMode playMode) {
        Array<? extends TextureRegion> frames = getFrames(sprite);
        Animation<TextureRegion> animation = new Animation<>(speed, frames);
        animation.setPlayMode(playMode);
        return animation;
    }

    public TextureRegion getFrame(Sprite sprite, int frame) {
        Array<? extends TextureRegion> frames = getFrames(sprite);

        if (frame >= frames.size) {
            return null;
        }

        return frames.get(frame);
    }

    public Vector2 getDimensions(Sprite sprite) {
        TextureRegion frame = getFrame(sprite, 0);
        return new Vector2(frame.getRegionWidth(), frame.getRegionHeight());
    }
}