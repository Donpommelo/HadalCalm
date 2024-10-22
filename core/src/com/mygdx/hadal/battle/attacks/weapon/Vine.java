package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

import static com.mygdx.hadal.constants.Constants.PPM;

public class Vine extends SyncedAttacker {

    public static final Vector2 SEED_SIZE = new Vector2(45, 30);
    private static final Vector2 VINE_SIZE = new Vector2(40, 20);
    private static final Vector2 VINE_SPRITE_SIZE = new Vector2(60, 60);
    private static final float VINE_LIFESPAN = 1.25f;
    public static final float VINE_DAMAGE = 16.0f;
    private static final float VINE_KB = 20.0f;
    private static final int BEND_LENGTH = 1;
    private static final int BEND_VARIATION = 0;

    public static final int VINE_BEND_SPREAD_MIN = 15;
    public static final int VINE_BEND_SPREAD_MAX = 30;

    private static final Vector2 BOSS_VINE_SIZE = new Vector2(80, 40);
    private static final Vector2 BOSS_VINE_SPRITE_SIZE = new Vector2(120, 120);
    private static final float BOSS_VINE_LIFESPAN = 5.0f;
    public static final float BOSS_VINE_DAMAGE = 18.0f;
    private static final float BOSS_VINE_KB = 24.0f;
    private static final int BOSS_BEND_LENGTH = 2;
    private static final int BOSS_BEND_VARIATION = 0;

    private static final Sprite[] VINE_SPRITES = {Sprite.VINE_A, Sprite.VINE_C, Sprite.VINE_D};

    private final DamageSource damageSource;

    private final Vector2 vineSize, vineSpriteSize;
    private final float lifespan, damage, knockback;
    private final int bendLength, bendVariation;

    public Vine(DamageSource damageSource) {
        this.damageSource = damageSource;

        if (damageSource.equals(DamageSource.ENEMY_ATTACK)) {
            vineSize = BOSS_VINE_SIZE;
            vineSpriteSize = BOSS_VINE_SPRITE_SIZE;
            lifespan = BOSS_VINE_LIFESPAN;
            damage = BOSS_VINE_DAMAGE;
            knockback = BOSS_VINE_KB;
            bendLength = BOSS_BEND_LENGTH;
            bendVariation = BOSS_BEND_VARIATION;
        } else {
            vineSize = VINE_SIZE;
            vineSpriteSize = VINE_SPRITE_SIZE;
            lifespan = VINE_LIFESPAN;
            damage = VINE_DAMAGE;
            knockback = VINE_KB;
            bendLength = BEND_LENGTH;
            bendVariation = BEND_VARIATION;
        }
    }

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.ATTACK1.playSourced(state, user.getPixelPosition(), 0.4f, 0.5f);

        final int vineNum = extraFields.length > 1 ? (int) extraFields[0] : 0;
        final int splitNum = extraFields.length > 1 ? (int) extraFields[1] : 0;

        //create an invisible hitbox that makes the vines as it moves
        RangedHitbox hbox = new RangedHitbox(state, startPosition, SEED_SIZE, lifespan, startVelocity, user.getHitboxFilter(),
                false, false, user, Sprite.NOTHING);
        hbox.setPassability(BodyConstants.BIT_WALL);
        hbox.makeUnreflectable();
        hbox.setRestitution(1.0f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            private final Vector2 lastPosition = new Vector2();
            private final Vector2 lastPositionTemp = new Vector2();
            private final Vector2 entityLocation = new Vector2();
            private int vineCount, vineCountTotal, nextBend;
            private boolean bendRight;
            private float displacement;
            private final Vector2 angle = new Vector2();
            @Override
            public void controller(float delta) {
                entityLocation.set(hbox.getPixelPosition());

                displacement += lastPosition.dst(entityLocation);
                lastPositionTemp.set(lastPosition);
                lastPosition.set(entityLocation);

                //after moving distance equal to a vine, the hbox spawns a vine with random sprite
                if (displacement > vineSize.x) {
                    if (lastPositionTemp.isZero()) {
                        lastPosition.set(entityLocation);
                    } else {
                        lastPosition.add(new Vector2(lastPosition).sub(lastPositionTemp).nor().scl((displacement - vineSize.x) / PPM));
                    }
                    displacement = 0.0f;

                    int randomIndex = MathUtils.random(VINE_SPRITES.length - 1);
                    Sprite projSprite = VINE_SPRITES[randomIndex];

                    RangedHitbox vine = new RangedHitbox(state, lastPosition, vineSize, lifespan, new Vector2(),
                            user.getHitboxFilter(), true, true, creator.getSchmuck(),
                            vineCountTotal == vineNum && splitNum == 0 ? Sprite.VINE_B : projSprite) {

                        private final Vector2 newPosition = new Vector2();
                        @Override
                        public void create() {
                            super.create();

                            //vines match hbox velocity but are drawn at an offset so they link together better
                            float newAngle = MathUtils.atan2(hbox.getLinearVelocity().y , hbox.getLinearVelocity().x);
                            newPosition.set(getPosition()).add(new Vector2(hbox.getLinearVelocity()).nor().scl(vineSize.x / 2 / PPM));
                            setTransform(newPosition.x, newPosition.y, newAngle);
                        }
                    };
                    vine.setSpriteSize(vineSpriteSize);
                    vine.setEffectsMovement(false);

                    vine.addStrategy(new ControllerDefault(state, vine, user.getBodyData()));
                    vine.addStrategy(new ContactUnitSound(state, vine, user.getBodyData(), SoundEffect.STAB, 0.6f, true).setSynced(false));
                    vine.addStrategy(new DamageStandard(state, vine, user.getBodyData(), damage, knockback,
                            damageSource, DamageTag.RANGED).setStaticKnockback(true));
                    vine.addStrategy(new CreateParticles(state, vine, user.getBodyData(), Particle.DANGER_RED)
                            .setParticleSize(90.0f));
                    vine.addStrategy(new DieParticles(state, vine, user.getBodyData(), Particle.PLANT_FRAG));
                    vine.addStrategy(new Static(state, vine, user.getBodyData()));

                    if (!state.isServer()) {
                        ((ClientState) state).addEntity(vine.getEntityID(), vine, false, ObjectLayer.HBOX);
                    }

                    vineCount++;
                    vineCountTotal++;
                    if (vineCount >= nextBend) {
                        if (extraFields.length > vineCountTotal + 2) {
                            //hbox's velocity changes randomly to make vine wobble
                            float bendSpread = VINE_BEND_SPREAD_MIN + extraFields[vineCountTotal + 1]
                                    * (VINE_BEND_SPREAD_MAX - VINE_BEND_SPREAD_MIN);
                            hbox.setLinearVelocity(hbox.getLinearVelocity().rotateDeg((bendRight ? -1 : 1) * bendSpread));
                            bendRight = !bendRight;
                            vineCount = 0;

                            nextBend = (int) (bendLength - bendVariation + extraFields[vineCountTotal + 1] * 2 * bendVariation);
                        }
                    }

                    if (vineCountTotal > vineNum) {
                        hbox.die();
                    }
                }
            }

            @Override
            public void die() {
                if (!state.isServer()) { return;}
                if (splitNum > 0) {
                    //when vine dies, it creates 2 vines that branch in separate directions
                    float splitAngle = MathUtils.random(VINE_BEND_SPREAD_MIN, VINE_BEND_SPREAD_MAX);
                    float newDegrees = hbox.getLinearVelocity().angleDeg() + splitAngle;

                    angle.set(hbox.getLinearVelocity()).setAngleDeg(newDegrees);
                    WeaponUtils.createVine(state, user, hbox.getPixelPosition(), new Vector2(angle), vineNum, splitNum - 1, getSyncedAttack());

                    newDegrees = hbox.getLinearVelocity().angleDeg() - splitAngle;
                    angle.set(hbox.getLinearVelocity()).setAngleDeg(newDegrees);
                    WeaponUtils.createVine(state, user, hbox.getPixelPosition(), new Vector2(angle), vineNum, splitNum - 1, getSyncedAttack());
                }
            }
        });

        return hbox;
    }
}