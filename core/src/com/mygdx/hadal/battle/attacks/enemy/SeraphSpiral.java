package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;

public class SeraphSpiral extends SyncedAttacker {

    private static final Vector2 PROJ_SIZE = new Vector2(165, 165);
    private static final float LIFESPAN = 12.0f;
    private static final float SPEED = 20.0f;
    private static final float BASE_DAMAGE = 6.0f;
    private static final float KNOCKBACK = 10.0f;

    private static final float GRID_DISTANCE = 224;

    private static final Sprite PROJ_SPRITE = Sprite.DIATOM_D;

    final Vector2 position = new Vector2();
    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {
        position.set(user.getPixelPosition());
        SoundEffect.MAGIC3_BURST.playSourced(state, position, 1.1f, 0.75f);

        boolean bonus = false;
        int clockwise = -1;
        if (extraFields.length > 1) {
            bonus = extraFields[0] == 0.0f;
            clockwise = (int) extraFields[1];
        }
        Hitbox[] hboxes;
        if (bonus) {
            hboxes = new Hitbox[4];
            hboxes[0] = spiralSingle(state, user, startPosition[0], 0, clockwise);
            hboxes[1] = spiralSingle(state, user, startPosition[0], 180, clockwise);
            hboxes[2] = spiralSingle(state, user, startPosition[0], 90, clockwise);
            hboxes[3] = spiralSingle(state, user, startPosition[0], 270, clockwise);
        } else {
            hboxes = new Hitbox[2];
            hboxes[0] = spiralSingle(state, user, startPosition[0], 0, clockwise);
            hboxes[1] = spiralSingle(state, user, startPosition[0], 180, clockwise);
        }

        return hboxes;
    }

    private Hitbox spiralSingle(PlayState state, Schmuck user, Vector2 startPosition, float angle, int clockwise) {
        Hitbox spiral = new RangedHitbox(state, startPosition, PROJ_SIZE, LIFESPAN,
                new Vector2(0, SPEED).setAngleDeg(angle), user.getHitboxFilter(),true, false,
                user, PROJ_SPRITE);
        spiral.makeUnreflectable();

        spiral.addStrategy(new ControllerDefault(state, spiral, user.getBodyData()));
        spiral.addStrategy(new HitboxStrategy(state, spiral, user.getBodyData()) {

            private final Vector2 startLocation = new Vector2();
            private float distance = GRID_DISTANCE * 2;
            private float pulseCount;
            private boolean firstLoop;
            private static final float pulseInterval = 0.06f;
            @Override
            public void create() { this.startLocation.set(hbox.getPixelPosition()); }

            @Override
            public void controller(float delta) {
                if (startLocation.dst2(hbox.getPixelPosition()) >= distance * distance) {

                    if (distance >= GRID_DISTANCE * 8) {
                        hbox.die();
                    } else {

                        if (firstLoop) {
                            distance += (GRID_DISTANCE * 2);
                        }
                        firstLoop = !firstLoop;
                        startLocation.set(hbox.getPixelPosition());
                        hbox.setLinearVelocity(hbox.getLinearVelocity().rotate90(clockwise));
                    }
                }
                pulseCount += delta;
                while (pulseCount >= pulseInterval) {
                    pulseCount -= pulseInterval;

                    Hitbox pulse = new Hitbox(state, hbox.getPixelPosition(), hbox.getSize(), pulseInterval,
                            new Vector2(), user.getHitboxFilter(), true, false, creator.getSchmuck(), Sprite.NOTHING);
                    pulse.makeUnreflectable();

                    pulse.addStrategy(new ControllerDefault(state, pulse, user.getBodyData()));
                    pulse.addStrategy(new DamageStandard(state, pulse, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                            DamageSource.ENEMY_ATTACK, DamageTag.RANGED).setStaticKnockback(true));
                    pulse.addStrategy(new FixedToEntity(state, pulse, user.getBodyData(), spiral, new Vector2(), new Vector2()).setRotate(true));
                    pulse.addStrategy(new ContactUnitSound(state, pulse, user.getBodyData(), SoundEffect.ZAP, 0.6f, true)
                            .setSynced(false));

                    if (!state.isServer()) {
                        ((ClientState) state).addEntity(pulse.getEntityID(), pulse, false, ClientState.ObjectLayer.HBOX);
                    }
                }
            }
        });

        return spiral;
    }
}