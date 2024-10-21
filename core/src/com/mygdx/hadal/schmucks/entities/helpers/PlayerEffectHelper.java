package com.mygdx.hadal.schmucks.entities.helpers;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.SoundCreate;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

/**
 * PlayerEffectHelper is responsible for visual effects attached to the player.
 * This includes hovering bubbles/sound, running dust clouds/sound, reload sounds
 * This also covers flags that determine if the player is running, hovering, invisible
 */
public class PlayerEffectHelper {

    private final PlayState state;
    private final Player player;

    //particles and sounds used by the player
    private final ParticleEntity hoverBubbles, dustCloud;
    private final SoundEntity runSound, hoverSound, reloadSound;

    //these keep track of flags that decide the player's visual effects
    private boolean hovering, running, invisible, translucent, transparent;

    public PlayerEffectHelper(PlayState state, Player player) {
        this.state = state;
        this.player = player;

        dustCloud = EffectEntityManager.getParticle(state, new ParticleCreate(Particle.DUST, player));
        hoverBubbles = EffectEntityManager.getParticle(state, new ParticleCreate(Particle.BUBBLE_TRAIL, player));

        hoverSound = EffectEntityManager.getSound(state, new SoundCreate(SoundEffect.HOVER, player)
                .setVolume(0.2f));
        runSound = EffectEntityManager.getSound(state, new SoundCreate(SoundEffect.RUN, player)
                .setVolume(0.1f));
        reloadSound = EffectEntityManager.getSound(state, new SoundCreate(SoundEffect.RELOAD, player)
                .setVolume(0.2f));
    }

    public void setEffectOffset() {
        //for server, we adjust offset of particles to account for size changes
        if (dustCloud != null && hoverBubbles != null) {
            dustCloud.setOffset(0, -player.getSize().y / 2);
            hoverBubbles.setOffset(0, -player.getSize().y / 2);
        }
    }

    public void toggleRunningEffects(boolean running) {
        this.running = running;
        if (dustCloud != null && runSound != null) {
            if (running) {
                dustCloud.turnOn();
                runSound.turnOn();
            } else {
                dustCloud.turnOff();
                runSound.turnOff();
            }
        }
    }

    public void toggleHoverEffects(boolean hovering) {
        this.hovering = hovering;
        if (hoverBubbles != null && hoverSound != null) {
            if (hovering) {
                hoverBubbles.turnOn();
                hoverSound.turnOn();
            } else {
                hoverBubbles.turnOff();
                hoverSound.turnOff();
            }
        }
    }

    public void toggleReloadEffects(boolean reloading) {
        if (reloadSound != null) {
            if (reloading) {
                reloadSound.turnOn();
            } else {
                reloadSound.turnOff();
            }
        }
    }

    /**
     * This is run when the player is rendered and determines if they are visible.
     * @return transparency = 0 if the player should not be rendered
     */
    public float processInvisibility() {
        //process player invisibility. Completely invisible players are partially transparent to allies
        float transparency = 1.0f;
        if (transparent) {
            return 0.0f;
        }

        if (invisible && !HadalGame.usm.isOwnTeam(player.getUser())) {
            return 0.0f;
        }

        return transparency;
    }

    public void dispose() {
        //this is here to prevent the client from not updating the last, fatal instance of damage in the ui
        if (!state.isServer()) {
            player.getPlayerData().setCurrentHp(0);

            if (hoverBubbles != null) {
                ((ClientState) state).removeEntity(hoverBubbles.getEntityID());
            }
            if (dustCloud != null) {
                ((ClientState) state).removeEntity(dustCloud.getEntityID());
            }
        }
    }

    /**
     * This processes if the player is semi-transparent
     * @return whether the player is translucent or not
     */
    public boolean processTranslucentShader() {

        //player is translucent to allies and self if invisible
        if (invisible && HadalGame.usm.isOwnTeam(player.getUser())) {
            return true;
        }

        //if translucent, player is translucent to enemies
        if (translucent) {
            return !HadalGame.usm.isOwnTeam(player.getUser());
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
