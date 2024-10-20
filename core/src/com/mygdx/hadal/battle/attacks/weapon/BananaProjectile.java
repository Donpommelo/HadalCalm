package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class BananaProjectile extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(43, 30);
    public static final float LIFESPAN = 4.0f;
    public static final float BASE_DAMAGE = 15.0f;
    private static final float RECOIL = 5.0f;
    private static final float KNOCKBACK = 0.0f;

    public static final float EXPLOSION_DAMAGE = 40.0f;
    private static final int EXPLOSION_RADIUS = 200;
    private static final float EXPLOSION_KNOCKBACK = 45.0f;

    private static final Sprite PROJ_SPRITE = Sprite.BANANA;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.SPRING.playSourced(state, startPosition, 0.5f);
        user.recoil(startVelocity, RECOIL);

        //bouncy hbox is separate so it can pass through drop-through platforms
        Hitbox hboxBouncy = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                false, false, user, Sprite.NOTHING);
        hboxBouncy.setRestitution(0.8f);
        hboxBouncy.setGravity(3.5f);

        hboxBouncy.addStrategy(new ControllerDefault(state, hboxBouncy, user.getBodyData()));
        hboxBouncy.addStrategy(new DropThroughPassability(state, hboxBouncy, user.getBodyData()));

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                false, true, user, PROJ_SPRITE);
        hbox.setSyncDefault(false);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), hboxBouncy, new Vector2(), new Vector2()).setKillOnDeath(true));
        hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.BANANA, DamageTag.RANGED));
        hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), EXPLOSION_RADIUS, EXPLOSION_DAMAGE, EXPLOSION_KNOCKBACK,
                (short) 0, false, DamageSource.BANANA));
        hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.SPRING, 0.1f).setSynced(false));
        hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION1, 0.6f).setSynced(false));
        hbox.addStrategy(new FlashShaderNearDeath(state, hbox, user.getBodyData(), 1.0f));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            @Override
            public void create() {
                //Set banana to have constant angular velocity for visual effect.
                if (startVelocity.x > 0) {
                    hbox.setAngularVelocity(-10);
                } else {
                    hbox.setAngularVelocity(10);
                }
            }


        });

        if (!state.isServer()) {
            ((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ObjectLayer.HBOX);
        }
        return hboxBouncy;
    }
}
