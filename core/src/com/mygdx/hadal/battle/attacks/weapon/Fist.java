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
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class Fist extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(60, 40);
    public static final float LIFESPAN = 0.15f;
    public static final float BASE_DAMAGE = 36.0f;
    private static final float KNOCKBACK = 25.0f;
    private static final int SPREAD = 30;

    private static final Sprite PROJ_SPRITE = Sprite.PUNCH;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.WOOSH)
                .setVolume(0.75f)
                .setPosition(startPosition));

        Hitbox hbox = new Hitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);
        hbox.makeUnreflectable();

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.FISTICUFFS, DamageTag.MELEE));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.SLAP, 0.8f, true));
        hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), SPREAD));

        return hbox;
    }
}