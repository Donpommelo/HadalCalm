package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class RecombinantShot extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(40, 40);
    public static final float LIFESPAN = 1.5f;
    public static final float BASE_DAMAGE = 19.0f;
    private static final float RECOIL = 11.0f;
    private static final float KNOCKBACK = 2.2f;

    private static final float PITCH_SPREAD = 0.4f;
    private static final float PROJ_DAMPEN = 7.5f;
    public static final int DURABILITY = 3;

    private static final float HOME_DELAY = 0.5f;
    private static final float HOME_INTERVAL = 0.12f;
    private static final float HOME_SPEED = 55.0f;
    private static final float ROTATION_SPEED = 4.0f;

    private static final Sprite[] PROJ_SPRITES = {Sprite.BEACH_BALL_BLUE, Sprite.BEACH_BALL_GREEN, Sprite.BEACH_BALL_ORANGE,
            Sprite.BEACH_BALL_RED, Sprite.BEACH_BALL_YELLOW};
    private static final HadalColor[] PROJ_TRAILS = {HadalColor.BLUE, HadalColor.GREEN, HadalColor.ORANGE, HadalColor.RED, HadalColor.YELLOW};

    private static final IntArray SPRITES = new IntArray(new int[] {0, 1, 2, 3, 4});

    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {

        Hitbox[] hboxes = new Hitbox[startPosition.length];
        user.recoil(weaponVelocity, RECOIL);

        if (startPosition.length != 0) {
            SoundEffect.SPRING.playSourced(state, startPosition[0], 0.75f, 4.0f);
            SPRITES.shuffle();
            for (int i = 0; i < startPosition.length; i++) {
                Sprite projSprite = PROJ_SPRITES[SPRITES.get(i)];
                final int projNum = extraFields.length > i ? (int) extraFields[i] : 0;
                Hitbox hbox = new RangedHitbox(state, startPosition[i], PROJECTILE_SIZE,
                        LIFESPAN + HOME_INTERVAL * projNum, startVelocity[i], user.getHitboxFilter(),
                        false, true, user, projSprite);
                hbox.setDurability(DURABILITY);
                hbox.setRestitution(1.0f);

                hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
                hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS).setSyncType(SyncType.NOSYNC));
                hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
                hbox.addStrategy(new ContactWallLoseDurability(state, hbox, user.getBodyData()));
                hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.BULLET_BODY_HIT, 0.3f, true)
                        .setPitchSpread(PITCH_SPREAD).setSynced(false));
                hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.SPRING, 0.3f)
                        .setPitchSpread(PITCH_SPREAD).setSynced(false));
                hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.CR4P_CANNON,
                        DamageTag.SHRAPNEL, DamageTag.RANGED));
                hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.BEACH_BALL_TRAIL, 0.0f, 0.5f)
                        .setParticleColor(PROJ_TRAILS[SPRITES.get(i)]).setSyncType(SyncType.NOSYNC));
                hbox.addStrategy(new RotationConstant(state, hbox, user.getBodyData(), ROTATION_SPEED));
                hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

                    private float count;
                    @Override
                    public void create() {
                        super.create();
                        hbox.getBody().setLinearDamping(PROJ_DAMPEN);
                        count = HOME_DELAY + HOME_INTERVAL * projNum;
                    }

                    @Override
                    public void controller(float delta) {
                        if (count > 0.0f) {
                            count -= delta;
                            if (count <= 0.0f) {
                                if (user instanceof Player player) {
                                    float pitch = (MathUtils.random() - 0.5f) * PITCH_SPREAD;
                                    SoundEffect.SPRING.playSourced(state, startPosition[0], 0.75f, 2.0f + pitch);
                                    hbox.setLinearVelocity(player.getMouseHelper().getPosition().sub(hbox.getPosition()).nor().scl(HOME_SPEED));
                                    hbox.getBody().setLinearDamping(0);
                                }
                            }
                        }
                    }
                });
                hboxes[i] = hbox;
            }
        }
        return hboxes;
    }
}