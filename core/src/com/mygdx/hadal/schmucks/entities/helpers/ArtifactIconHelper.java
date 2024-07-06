package com.mygdx.hadal.schmucks.entities.helpers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.schmucks.entities.Player;

public class ArtifactIconHelper {

    private static final float BASE_COOLDOWN = 0.25f;

    private final Player player;
    private final Array<ArtifactIconFlash> artifacts = new Array<>();
    private final Array<ArtifactIconFlash> toRemove = new Array<>();

    private float cooldown;

    public ArtifactIconHelper(Player player) {
        this.player = player;
    }


    public void controller(float delta) {

        if (cooldown > 0.0f) {
            cooldown -= delta;
        }

        for (ArtifactIconFlash artifact : artifacts) {
            if (artifact.isStarted()) {
                artifact.controller(delta);

                if (artifact.getLifespan() <= 0.0f) {
                    toRemove.add(artifact);
                }
            } else if (cooldown <= 0.0f) {
                artifact.setStarted(true);
                cooldown = BASE_COOLDOWN;
            }
        }

        for (ArtifactIconFlash artifact : toRemove) {
            artifacts.removeValue(artifact, true);
        }

        toRemove.clear();

    }

    public void render(SpriteBatch batch, Vector2 playerLocation) {
        for (ArtifactIconFlash artifact : artifacts) {
            if (artifact.isStarted()) {
                artifact.render(batch, playerLocation);
            }
        }
    }

    public void addArtifactFlash(UnlockArtifact artifact) {
        boolean copyFound = false;
        for (ArtifactIconFlash artifactOld : artifacts) {
            if (artifactOld.getArtifact().equals(artifact)) {
                copyFound = true;
                break;
            }
        }
        if (!copyFound) {
            artifacts.add(new ArtifactIconFlash(artifact));
        }
    }
}
