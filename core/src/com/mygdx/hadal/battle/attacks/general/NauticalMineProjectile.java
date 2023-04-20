package com.mygdx.hadal.battle.attacks.general;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class NauticalMineProjectile extends SyncedAttacker {

    public static final float NAUTICAL_MINE_LIFESPAN = 12.0f;
    public static final float NAUTICAL_MINE_EXPLOSION_DAMAGE = 75.0f;
    private static final float PRIME_DELAY = 1.0f;
    private static final float PROJ_DAMPEN = 1.0f;
    private static final Vector2 NAUTICAL_MINE_SIZE = new Vector2(120, 120);
    private static final int NAUTICAL_MINE_EXPLOSION_RADIUS = 400;
    private static final float NAUTICAL_MINE_EXPLOSION_KNOCKBACK = 40.0f;
    private static final float FOOTBALL_THRESHOLD = 200.0f;
    private static final float FOOTBALL_DEPRECIATION = 50.0f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.LAUNCHER.playSourced(state, user.getPixelPosition(), 1.0f);

        boolean event = false;
        float pushMultiplier = 1.0f;
        float lifespan = NAUTICAL_MINE_LIFESPAN;
        if (2 < extraFields.length) {
            event = extraFields[0] == 0.0f;
            pushMultiplier = extraFields[1];
            lifespan = extraFields[2];
        }

        Hitbox hbox = new RangedHitbox(state, startPosition, NAUTICAL_MINE_SIZE, lifespan, startVelocity,
                (short) 0, false, false, user, Sprite.NAVAL_MINE);
        hbox.setRestitution(0.5f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        if (event) {
            hbox.addStrategy(new ContactGoalScore(state, hbox, user.getBodyData()));
            hbox.addStrategy(new DamageThresholdDie(state, hbox, user.getBodyData(), FOOTBALL_THRESHOLD, FOOTBALL_DEPRECIATION));
        } else {
            hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()).setDelay(PRIME_DELAY));
        }

        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new Pushable(state, hbox, user.getBodyData(), pushMultiplier));
        hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), NAUTICAL_MINE_EXPLOSION_RADIUS, NAUTICAL_MINE_EXPLOSION_DAMAGE,
                NAUTICAL_MINE_EXPLOSION_KNOCKBACK, (short) 0, false, DamageSource.NAUTICAL_MINE));
        hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION_FUN, 0.4f).setSynced(false));
        hbox.addStrategy(new FlashShaderNearDeath(state, hbox, user.getBodyData(), 1.0f));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            @Override
            public void create() {
                super.create();
                hbox.getBody().setLinearDamping(PROJ_DAMPEN);
            }
        });

        return hbox;
    }
}