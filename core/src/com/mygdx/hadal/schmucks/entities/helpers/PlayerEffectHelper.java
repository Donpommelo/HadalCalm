package com.mygdx.hadal.schmucks.entities.helpers;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class PlayerEffectHelper {

    private final PlayState state;
    private final Player player;

    //particles and sounds used by the player
    private final ParticleEntity hoverBubbles, dustCloud;
    private final SoundEntity runSound, hoverSound, reloadSound;

    private boolean hovering, running, invisible, translucent, transparent;

    public PlayerEffectHelper(PlayState state, Player player) {
        this.state = state;
        this.player = player;

        dustCloud = new ParticleEntity(state, player, Particle.DUST, 1.0f, 0.0f, false,
                SyncType.NOSYNC);
        hoverBubbles = new ParticleEntity(state, player, Particle.BUBBLE_TRAIL, 1.0f, 0.0f, false,
                SyncType.NOSYNC);

        hoverSound = new SoundEntity(state, player, SoundEffect.HOVER, 0.0f, 0.2f, 1.0f,
                true, true, SyncType.NOSYNC);
        runSound = new SoundEntity(state, player, SoundEffect.RUN, 0.0f, 0.1f, 1.0f,
                true, true, SyncType.NOSYNC);
        reloadSound = new SoundEntity(state, player, SoundEffect.RELOAD, 0.0f, 0.2f, 1.0f,
                true, true, SyncType.NOSYNC);

        if (!state.isServer()) {
            ((ClientState) state).addEntity(dustCloud.getEntityID(), dustCloud, false, PlayState.ObjectLayer.EFFECT);
            ((ClientState) state).addEntity(hoverBubbles.getEntityID(), hoverBubbles, false, PlayState.ObjectLayer.EFFECT);
            ((ClientState) state).addEntity(hoverSound.getEntityID(), hoverSound, false, PlayState.ObjectLayer.EFFECT);
            ((ClientState) state).addEntity(runSound.getEntityID(), runSound, false, PlayState.ObjectLayer.EFFECT);
            ((ClientState) state).addEntity(reloadSound.getEntityID(), reloadSound, false, PlayState.ObjectLayer.EFFECT);
        }
    }

    public void setEffectOffset() {
        //for server, we adjust offset of particles to account for size changes
        dustCloud.setOffset(0, -player.getSize().y / 2);
        hoverBubbles.setOffset(0, -player.getSize().y / 2);
    }

    public void toggleRunningEffects(boolean running) {
        this.running = running;
        if (running && !invisible) {
            dustCloud.turnOn();
            runSound.turnOn();
        } else {
            dustCloud.turnOff();
            runSound.turnOff();
        }
    }

    public void toggleHoverEffects(boolean hovering) {
        this.hovering = hovering;
        if (hovering && !invisible) {
            hoverBubbles.turnOn();
            hoverSound.turnOn();
        } else {
            hoverBubbles.turnOff();
            hoverSound.turnOff();
        }
    }

    public void toggleReloadEffects(boolean reloading) {
        if (reloading && !invisible) {
            reloadSound.turnOn();
        } else {
            reloadSound.turnOff();
        }
    }

    public float processInvisibility() {
        //process player invisibility. Completely invisible players are partially transparent to allies
        float transparency = 1.0f;
        if (transparent) {
            return 0.0f;
        }

        if (invisible && state.getPlayer().getHitboxFilter() != player.getHitboxFilter()) {
                return 0.0f;
        }

        return transparency;
    }

    public void dispose() {
        //this is here to prevent the client from not updating the last, fatal instance of damage in the ui
        if (!state.isServer()) {
            player.getPlayerData().setCurrentHp(0);

            ((ClientState) state).removeEntity(hoverBubbles.getEntityID());
            ((ClientState) state).removeEntity(dustCloud.getEntityID());
        }
    }

    public boolean processTranslucentShader() {
        if (invisible && state.getPlayer().getHitboxFilter() == player.getHitboxFilter()) {
            return true;
        }

        if (translucent) {
            return state.getPlayer().getHitboxFilter() != player.getHitboxFilter();
        }
        return false;
    }

    public boolean isRunning() { return running; }

    public boolean isHovering() { return hovering; }

    public boolean isInvisible() { return invisible; }

    public void setInvisible(boolean invisible) { this.invisible = invisible; }

    public boolean isTranslucent() { return translucent; }

    public void setTranslucent(boolean translucent) { this.translucent = translucent; }

    public boolean isTransparent() { return transparent; }

    public void setTransparent(boolean transparent) { this.transparent = transparent; }
}
