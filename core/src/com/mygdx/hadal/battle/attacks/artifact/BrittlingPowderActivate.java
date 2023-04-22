package com.mygdx.hadal.battle.attacks.artifact;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class BrittlingPowderActivate extends SyncedAttacker {

    public static final float BASE_DAMAGE = 15.0f;
    private static final Vector2 PROJECTILE_SIZE = new Vector2(20, 20);
    private static final float LIFESPAN = 0.5f;
    private static final float KNOCKBACK = 5.0f;

    private static final Sprite[] PROJ_SPRITES = {Sprite.SCRAP_A, Sprite.SCRAP_B, Sprite.SCRAP_C, Sprite.SCRAP_D};

    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {
        SoundEffect.WALL_HIT1.playSourced(state, startPosition[0], 0.75f);

        Hitbox[] hboxes = new Hitbox[startPosition.length];
        for (int i = 0; i < startPosition.length; i++) {

            int randomIndex = MathUtils.random(PROJ_SPRITES.length - 1);
            Sprite projSprite = PROJ_SPRITES[randomIndex];

            Hitbox frag = new Hitbox(state, startPosition[i], PROJECTILE_SIZE, LIFESPAN, startVelocity[i], user.getHitboxFilter(),
                    true, false, user, projSprite);

            frag.addStrategy(new ControllerDefault(state, frag, user.getBodyData()));
            frag.addStrategy(new DamageStandard(state, frag, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.BRITTLING_POWDER, DamageTag.SHRAPNEL));

            hboxes[i] = frag;
        }
        return hboxes;
    }
}