package com.mygdx.hadal.schmucks.entities.helpers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.save.UnlockArtifact;

/**
 * An ArtifactIconFlash is a single instance of an artifact icon flashing upon activating.
 * Kept track of in ArtifactIconHelper
 */
public class ArtifactIconFlash {

    private static final float BASE_LIFESPAN = 1.5f;
    private static final float DISTANCE_THRESHOLD = 1.25f;
    private static final float MIN_SIZE = 20;
    private static final float MAX_SIZE = 60;
    private static final float DISTANCE_Y_START = 50;
    private static final float DISTANCE_Y = 75;
    private static final float DISTANCE_Y_FADE = 100;

    private final UnlockArtifact artifact;
    private boolean started;
    private float lifespan;

    public ArtifactIconFlash(UnlockArtifact artifact) {
         this.artifact = artifact;
         this.lifespan = BASE_LIFESPAN;
    }

    public void controller(float delta) {
        lifespan -= delta;
    }

    public void render(SpriteBatch batch, Vector2 playerLocation) {
        float size = MIN_SIZE;
        float distance = DISTANCE_Y_START;

        //icon grows to a max size and moves upward to a max distance from the player before slowing down
        if (lifespan > DISTANCE_THRESHOLD) {
            size += (BASE_LIFESPAN - lifespan) / (BASE_LIFESPAN - DISTANCE_THRESHOLD) * (MAX_SIZE - MIN_SIZE);
            distance += (BASE_LIFESPAN - lifespan) / (BASE_LIFESPAN - DISTANCE_THRESHOLD) * (DISTANCE_Y - DISTANCE_Y_START);
        } else {
            size = MAX_SIZE;
            distance = DISTANCE_Y;
            distance += (DISTANCE_THRESHOLD - lifespan) / DISTANCE_THRESHOLD * (DISTANCE_Y_FADE - DISTANCE_Y);
        }

        batch.draw(artifact.getFrameLines(), playerLocation.x - size / 2, playerLocation.y + distance,
                size, size);
    }


    public UnlockArtifact getArtifact() { return artifact; }

    public boolean isStarted() { return started; }

    public void setStarted(boolean started) { this.started = started; }

    public float getLifespan() { return lifespan; }
}
