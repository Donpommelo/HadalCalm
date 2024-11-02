package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class Fugu extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(36, 36);
    public static final float LIFESPAN = 1.2f;
    public static final float BASE_DAMAGE = 35.0f;
    private static final float RECOIL = 7.5f;
    private static final float KNOCKBACK = 12.5f;

    public static final int POISON_RADIUS = 250;
    public static final float POISON_DAMAGE = 0.75f;
    private static final float POISON_DURATION = 4.0f;

    private static final Sprite PROJ_SPRITE = Sprite.FUGU;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.LAUNCHER4)
                .setVolume(0.25f)
                .setPosition(startPosition));

        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.FUGUN,
                DamageTag.POISON, DamageTag.RANGED));
        hbox.addStrategy(new DieRagdoll(state, hbox, user.getBodyData(), false));
        hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.DEFLATE, 0.25f));

        hbox.addStrategy(new DiePoison(state, hbox, user.getBodyData(), POISON_RADIUS, POISON_DAMAGE, POISON_DURATION,
                (short) 0, DamageSource.FUGUN));

        return hbox;
    }
}