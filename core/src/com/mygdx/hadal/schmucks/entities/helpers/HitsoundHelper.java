package com.mygdx.hadal.schmucks.entities.helpers;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class HitsoundHelper {

    private static final float HIT_SOUND_CD = 0.15f;

    private final PlayState state;
    private final Player player;
    private float hitSoundCdCount, hitSoundLargeCdCount;

    public HitsoundHelper(PlayState state, Player player) {
        this.state = state;
        this.player = player;
    }

    public void controller(float delta) {
        hitSoundCdCount -= delta;
        hitSoundLargeCdCount -= delta;
    }

    /**
     * When the player deals damage, we play this hitsound depending on the amount of damage dealt
     */
    private static final float MAX_DAMAGE_THRESHOLD = 60.0f;
    public void playHitSound(BodyData vic, float damage) {

        if (damage <= 0.0f) { return; }

        if (HadalGame.usm.getOwnUser() != player.getUser()) { return; }

        float modifiedDamage = damage;
        if (player.getHitboxFilter() != vic.getSchmuck().getHitboxFilter()) {
            if (vic.getCurrentHp() == 0) {
                modifiedDamage = 999;
            }

            if (modifiedDamage > MAX_DAMAGE_THRESHOLD) {
                if (hitSoundLargeCdCount < 0) {
                    hitSoundLargeCdCount = HIT_SOUND_CD;
                    hitSoundCdCount = HIT_SOUND_CD;
                    SoundEffect.playHitSound(state.getGsm(), true);
                }
            } else {
                if (hitSoundCdCount < 0) {
                    hitSoundCdCount = HIT_SOUND_CD;
                    SoundEffect.playHitSound(state.getGsm(), false);
                }
            }
        }
    }
}
