package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class VengefulSpirit extends SyncedAttacker {

    public static final float SPIRIT_DEFAULT_DAMAGE = 45.0f;
    private static final int SPIRIT_SIZE = 50;
    private static final float SPIRIT_HOMING = 120;
    private static final int SPIRIT_HOMING_RADIUS = 40;
    private static final float SPIRIT_KNOCKBACK = 25.0f;
    private static final float SPIRIT_LIFESPAN = 7.5f;

    private final DamageSource damageSource;

    public VengefulSpirit(DamageSource damageSource) {
        this.damageSource = damageSource;
    }

    @Override
    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {
        Hitbox[] hboxes = new Hitbox[startPosition.length];

        boolean attached = true;
        float spiritDamage = SPIRIT_DEFAULT_DAMAGE;
        if (3 < extraFields.length) {
            attached = extraFields[0] == 1.0f;
            spiritDamage = extraFields[2];
        }

        if (0 != startPosition.length) {
            SoundEffect.DARKNESS2.playSourced(state, user.getPixelPosition(), 0.2f);

            for (int i = 0; i < startPosition.length; i++) {
                Hitbox hbox = new RangedHitbox(state, startPosition[i], new Vector2(SPIRIT_SIZE, SPIRIT_SIZE), SPIRIT_LIFESPAN,
                        new Vector2(), user.getHitboxFilter(), true, true, user, Sprite.SKULL) {

                    private final Vector2 entityVelocity = new Vector2();
                    @Override
                    public void render(SpriteBatch batch, Vector2 entityLocation) {

                        if (!alive) { return; }
                        entityVelocity.set(getLinearVelocity());
                        if (entityVelocity.isZero() && user instanceof Player player) {
                            entityVelocity.set(0, 1).setAngleDeg(player.getMouseHelper().getAttackAngle());
                        }

                        boolean flip = true;
                        float realAngle = entityVelocity.angleRad();
                        if ((realAngle > MathUtils.PI / 2 && realAngle < 3 * MathUtils.PI / 2) || (realAngle < -MathUtils.PI / 2 && realAngle > -3 * MathUtils.PI / 2)) {
                            flip = false;
                        }

                        batch.draw(projectileSprite.getKeyFrame(animationTime, true),
                                (flip ? spriteSize.x : 0) + entityLocation.x - spriteSize.x / 2,
                                entityLocation.y - spriteSize.y / 2,
                                (flip ? -1 : 1) * spriteSize.x / 2,
                                spriteSize.y / 2,
                                (flip ? -1 : 1) * spriteSize.x, spriteSize.y, 1, 1,
                                (flip ? 0 : 180) + entityVelocity.angleDeg());
                    }

                };

                hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
                hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
                hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), spiritDamage, SPIRIT_KNOCKBACK,
                        damageSource, DamageTag.MAGIC, DamageTag.RANGED));

                //attached hboxes will follow the player until they have a target to home in on
                if (attached) {
                    hbox.addStrategy(new HomingUnit(state, hbox, user.getBodyData(), SPIRIT_HOMING, SPIRIT_HOMING_RADIUS)
                            .setFixedUntilHome(true).setTarget(user));
                } else {
                    hbox.addStrategy(new HomingUnit(state, hbox, user.getBodyData(), SPIRIT_HOMING, SPIRIT_HOMING_RADIUS));
                }
                hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.GHOST_LIGHT, 0.0f, 1.0f)
                        .setSyncType(SyncType.NOSYNC));
                hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.DARKNESS1, 0.25f).setSynced(false));

                hboxes[i] = hbox;
            }
        }
        return hboxes;
    }
}