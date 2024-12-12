package com.mygdx.hadal.schmucks.entities.helpers;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.entities.Schmuck;

/**
 * The SpecialHpHelper processes special effects that appear on the player's hp ui.
 * Conditional Hp that gradually decrements shows up as semi-transparent
 * Shield Hp does not currently have a visual effect, but is processed by an artifact effect
 */
public class SpecialHpHelper {

    public static final float BASE_DEGEN = 1.0f;
    public static final float DEGEN = 0.15f;
    private static final float PROC_CD = 0.02f;

    private final Schmuck schmuck;

    //keep track of last damage source to credit kills
    private DamageSource lastDamageSource = DamageSource.MISC;
    private float conditionalHp, shieldHp;
    private float procCdCount;

    public SpecialHpHelper(Schmuck schmuck) {
        this.schmuck = schmuck;
    }

    public void controller(float delta) {
        if (procCdCount >= PROC_CD) {
            procCdCount -= PROC_CD;
            if (conditionalHp > 0.0f) {

                //conditional hp gradually decrements over time
                float damage = PROC_CD * (BASE_DEGEN + DEGEN * conditionalHp);
                schmuck.getBodyData().receiveDamage(damage, new Vector2(), schmuck.getBodyData(), false,
                        null, lastDamageSource);
                conditionalHp -= damage;

                if (conditionalHp < 0.0f) {
                    conditionalHp = 0.0f;
                }
            }
        }
        procCdCount += delta;
    }

    public float receiveShieldDamage(float damage) {

        //shield hp is deducted first before actual damage is dealt
        if (damage > shieldHp) {
            float excessDamage = damage - shieldHp;
            shieldHp = 0.0f;
            return excessDamage;
        } else {
            shieldHp -= damage;
            return 0.0f;
        }
    }

    public void addConditionalHp(float conditionalHp, DamageSource damageSource) {
        this.conditionalHp += conditionalHp;
        this.lastDamageSource = damageSource;
    }

    public void addShield(float shield) {
        this.shieldHp += shield;
    }

    public float getConditionalHp() { return conditionalHp; }

    public float getShieldHp() { return shieldHp; }
}
