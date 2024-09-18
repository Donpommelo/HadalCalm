package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayStateClient;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class Immolation extends SyncedAttacker {

    public static final float BASE_DAMAGE = 5.0f;
    public static final float LIFESPAN = 3.0f;
    public static final float BURN_INTERVAL = 1 / 60f;

    private static final Vector2 HITBOX_SIZE = new Vector2(120, 120);
    private static final float KNOCKBACK = 0.2f;
    private static final float RECOIL = 3.0f;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        SoundEffect.WOOSH.playSourced(state, user.getPixelPosition(), 1.0f);

        user.getBodyData().addStatus(new StatChangeStatus(state, 0.5f, Stats.AIR_DRAG, 6.0f, user.getBodyData(), user.getBodyData()));

        Hitbox hbox = new Hitbox(state, startPosition, HITBOX_SIZE, LIFESPAN, new Vector2(), user.getHitboxFilter(),
                true, true, user, Sprite.NOTHING);
        hbox.makeUnreflectable();

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), new Vector2(), new Vector2()));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.KICK1, 1.0f, true)
            .setSynced(false));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.FIRE, 0.0f, 1.0f).setParticleSize(40)
                .setParticleColor(HadalColor.FIERY_ROSE).setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            private float controllerCount;
            private final Vector2 pulseVelocity = new Vector2();
            private final Vector2 hoverDirection = new Vector2(0, RECOIL);
            @Override
            public void controller(float delta) {

                controllerCount += delta;
                while (controllerCount >= BURN_INTERVAL) {
                    controllerCount -= BURN_INTERVAL;

                    Hitbox pulse = new Hitbox(state, hbox.getPixelPosition(), HITBOX_SIZE, BURN_INTERVAL, pulseVelocity, user.getHitboxFilter(),
                            true, true, user, Sprite.NOTHING);
                    pulse.setSyncDefault(false);
                    pulse.setEffectsVisual(false);
                    pulse.makeUnreflectable();

                    pulse.addStrategy(new ControllerDefault(state, pulse, user.getBodyData()));
                    pulse.addStrategy(new DamageStandard(state, pulse, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                            DamageSource.IMMOLATION_AURA, DamageTag.MELEE).setStaticKnockback(true));

                    if (user instanceof Player player) {
                        hoverDirection.setAngleDeg(player.getMouseHelper().getAttackAngle() + 180);
                    }
                    user.pushMomentumMitigation(hoverDirection.x, hoverDirection.y);
                }
            }
        });

        if (!state.isServer()) {
            ((PlayStateClient) state).addEntity(hbox.getEntityID(), hbox, false, PlayStateClient.ObjectLayer.HBOX);
        }
    }
}