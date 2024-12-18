package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.managers.SoundManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateSound;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class Anchor extends SyncedAttacker {

    public static final float BASE_DAMAGE = 80.0f;
    private static final Vector2 PROJECTILE_SIZE = new Vector2(300, 259);
    private static final float LIFESPAN = 4.0f;
    private static final float PROJECTILE_SPEED = 60.0f;
    private static final float KNOCKBACK = 50.0f;

    private static final Sprite PROJ_SPRITE = Sprite.ANCHOR;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        Vector2 endPt = new Vector2();
        if (extraFields.length > 1) {
            endPt.set(extraFields[0], extraFields[1]);
        }

        Hitbox hbox = new Hitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, new Vector2(0, -PROJECTILE_SPEED),
                user.getHitboxFilter(), true, false, user, PROJ_SPRITE);
        hbox.setPassability((short) (BodyConstants.BIT_PROJECTILE | BodyConstants.BIT_WALL | BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY));
        hbox.makeUnreflectable();

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.ANCHOR_SMASH, DamageTag.WHACKING, DamageTag.MAGIC));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.SLASH, 0.8f, true));
        hbox.addStrategy(new CreateSound(state, hbox, user.getBodyData(), SoundEffect.FALLING, 0.5f, false)
                .setPitch(0.75f));

        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            private boolean landed;
            private final Vector2 hboxLocation = new Vector2();
            @Override
            public void controller(float delta) {
                hboxLocation.set(hbox.getPixelPosition());
                if (hboxLocation.y - hbox.getSize().y / 2 <= endPt.y) {
                    hbox.setLinearVelocity(0, 0);

                    if (!landed) {
                        landed = true;

                        SoundManager.play(state, new SoundLoad(SoundEffect.METAL_IMPACT_2).setPosition(hboxLocation));
                        EffectEntityManager.getParticle(state, new ParticleCreate(Particle.BOULDER_BREAK, hboxLocation)
                                .setLifespan(0.5f));
                    }
                }
            }
        });

        return hbox;
    }
}