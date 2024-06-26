package com.mygdx.hadal.schmucks.entities.helpers;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.BodyData;

/**
 * HitsoundHelper is responsible for playing hitsounds when the player inflicts damage
 */
public class HitsoundHelper {

    private static final float HIT_SOUND_CD = 0.15f;

    private final Player player;

    //Large instances of damage play a pitched up hitsound and have a separate cooldown
    private float hitSoundCdCount, hitSoundLargeCdCount;

    public HitsoundHelper(Player player) {
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

        //no sounds for negated damage or self-damage
        if (damage <= 0.0f) { return; }
        if (HadalGame.usm.getOwnUser() != player.getUser()) { return; }

        float modifiedDamage = damage;
        if (player.getHitboxFilter() != vic.getSchmuck().getHitboxFilter()) {

            //lethal damage is always counted as "large instances of damage" for hitsound purposes
            if (vic.getCurrentHp() == 0) {
                modifiedDamage = 999;
            }

            if (modifiedDamage > MAX_DAMAGE_THRESHOLD) {
                if (hitSoundLargeCdCount < 0) {
                    hitSoundLargeCdCount = HIT_SOUND_CD;
                    hitSoundCdCount = HIT_SOUND_CD;
                    SoundEffect.playHitSound(true);
                }
            } else {
                if (hitSoundCdCount < 0) {
                    hitSoundCdCount = HIT_SOUND_CD;
                    SoundEffect.playHitSound(false);
                }
            }
        }
    }
}
