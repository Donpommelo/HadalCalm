package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.SyncType;
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

    public static final float VINE_DAMAGE = 16.0f;
    public static final Vector2 SEED_SIZE = new Vector2(45, 30);
    private static final Vector2 VINE_SIZE = new Vector2(40, 20);
    private static final Vector2 VINE_SPRITE_SIZE = new Vector2(60, 60);
    private static final float VINE_LIFESPAN = 1.25f;
    private static final float VINE_KB = 20.0f;

    private static final int BEND_LENGTH = 1;

    private static final Sprite[] VINE_SPRITES = {Sprite.VINE_A, Sprite.VINE_C, Sprite.VINE_D};

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.ATTACK1.playSourced(state, user.getPixelPosition(), 0.4f, 0.5f);

        final int vineNum = extraFields.length > 1 ? (int) extraFields[0] : 0;
        final int splitNum = extraFields.length > 1 ? (int) extraFields[1] : 0;
        final boolean synced = extraFields[2] == 0;

        //create an invisible hitbox that makes the vines as it moves
        RangedHitbox hbox = new RangedHitbox(state, startPosition, SEED_SIZE, VINE_LIFESPAN, startVelocity, user.getHitboxFilter(),
                false, false, user, Sprite.NOTHING);
        hbox.setPassability(Constants.BIT_WALL);
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
                if (displacement > VINE_SIZE.x) {
                    if (lastPositionTemp.isZero()) {
                        lastPosition.set(entityLocation);
                    } else {
                        lastPosition.add(new Vector2(lastPosition).sub(lastPositionTemp).nor().scl((displacement - VINE_SIZE.x) / PPM));
                    }
                    displacement = 0.0f;

                    int randomIndex = MathUtils.random(VINE_SPRITES.length - 1);
                    Sprite projSprite = VINE_SPRITES[randomIndex];

                    RangedHitbox vine = new RangedHitbox(state, lastPosition, VINE_SIZE, VINE_LIFESPAN, new Vector2(),
                            user.getHitboxFilter(), true, true, creator.getSchmuck(),
                            vineCountTotal == vineNum && splitNum == 0 ? Sprite.VINE_B : projSprite) {

                        private final Vector2 newPosition = new Vector2();
                        @Override
                        public void create() {
                            super.create();

                            //vines match hbox velocity but are drawn at an offset so they link together better
                            float newAngle = MathUtils.atan2(hbox.getLinearVelocity().y , hbox.getLinearVelocity().x);
                            newPosition.set(getPosition()).add(new Vector2(hbox.getLinearVelocity()).nor().scl(VINE_SIZE.x / 2 / PPM));
                            setTransform(newPosition.x, newPosition.y, newAngle);
                        }
                    };
                    vine.setSpriteSize(VINE_SPRITE_SIZE);
                    vine.setEffectsMovement(false);

                    vine.addStrategy(new ControllerDefault(state, vine, user.getBodyData()));
                    vine.addStrategy(new ContactUnitSound(state, vine, user.getBodyData(), SoundEffect.STAB, 0.6f, true).setSynced(false));
                    vine.addStrategy(new DamageStandard(state, vine, user.getBodyData(), VINE_DAMAGE, VINE_KB,
                            DamageSource.VINE_SOWER, DamageTag.RANGED).setStaticKnockback(true));
                    vine.addStrategy(new CreateParticles(state, vine, user.getBodyData(), Particle.DANGER_RED, 0.0f, 1.0f)
                            .setParticleSize(90.0f).setSyncType(SyncType.NOSYNC));
                    vine.addStrategy(new DieParticles(state, vine, user.getBodyData(), Particle.PLANT_FRAG).setSyncType(SyncType.NOSYNC));
                    vine.addStrategy(new Static(state, vine, user.getBodyData()));

                    if (!state.isServer()) {
                        ((ClientState) state).addEntity(vine.getEntityID(), vine, false, ClientState.ObjectLayer.HBOX);
                    }

                    vineCount++;
                    vineCountTotal++;
                    if (vineCount >= nextBend) {
                        if (extraFields.length > vineCountTotal + 2) {
                            //hbox's velocity changes randomly to make vine wobble
                            hbox.setLinearVelocity(hbox.getLinearVelocity().rotateDeg((bendRight ? -1 : 1) * extraFields[vineCountTotal + 2]));
                            bendRight = !bendRight;
                            vineCount = 0;
                            nextBend = BEND_LENGTH + (int) (extraFields[vineCountTotal + 2]) % 2 == 0 ? 0 : 1;
                        }
                    }

                    if (vineCountTotal > vineNum) {
                        hbox.die();
                    }
                }
            }

            @Override
            public void die() {

                if (splitNum > 0) {
                    //when vine dies, it creates 2 vines that branch in separate directions
                    float newDegrees = hbox.getLinearVelocity().angleDeg() + extraFields[5 + vineNum];
                    float[] extraFields1 = new float[3 + vineNum];
                    float[] extraFields2 = new float[3 + vineNum];
                    extraFields1[0] = vineNum;
                    extraFields2[0] = vineNum;
                    extraFields1[1] = 0;
                    extraFields2[1] = 0;
                    extraFields1[2] = 1;
                    extraFields2[2] = 1;
                    for (int i = 0; i < vineNum; i++) {
                        extraFields1[3 + i] = extraFields[vineNum + 5 + i];
                        extraFields2[3 + i] = extraFields[vineNum * 2 + 5 + i];
                    }
                    angle.set(hbox.getLinearVelocity()).setAngleDeg(newDegrees);
                    SyncedAttack.VINE.initiateSyncedAttackSingle(state, user, hbox.getPixelPosition(), new Vector2(angle), extraFields1);

                    newDegrees = hbox.getLinearVelocity().angleDeg() - extraFields[5 + vineNum];
                    angle.set(hbox.getLinearVelocity()).setAngleDeg(newDegrees);
                    SyncedAttack.VINE.initiateSyncedAttackSingle(state, user, hbox.getPixelPosition(), new Vector2(angle), extraFields2);
                }
            }
        });

        if (!state.isServer() && !synced) {
            ((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ClientState.ObjectLayer.HBOX);
        }

        return hbox;
    }
}