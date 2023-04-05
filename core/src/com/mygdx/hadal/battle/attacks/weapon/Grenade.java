package com.mygdx.hadal.battle.attacks.weapon;

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
import com.mygdx.hadal.strategies.hitbox.*;

public class Grenade extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(25, 25);
    public static final float LIFESPAN = 2.5f;
    public static final float BASE_DAMAGE = 15.0f;
    private static final float RECOIL = 2.5f;
    private static final float KNOCKBACK = 0.0f;

    public static final float EXPLOSION_DAMAGE = 45.0f;
    private static final int EXPLOSION_RADIUS = 150;
    private static final float EXPLOSION_KNOCKBACK = 25.0f;

    private static final Sprite PROJ_SPRITE = Sprite.GRENADE;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.LAUNCHER.playSourced(state, startPosition, 0.25f);
        user.recoil(startVelocity, RECOIL);

        Hitbox hboxBouncy = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                false, false, user, Sprite.NOTHING);
        hboxBouncy.setGravity(2.5f);
        hboxBouncy.setRestitution(0.5f);

        hboxBouncy.addStrategy(new ControllerDefault(state, hboxBouncy, user.getBodyData()));
        hboxBouncy.addStrategy(new DropThroughPassability(state, hboxBouncy, user.getBodyData()));

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                false, true, user, PROJ_SPRITE);
        hbox.setSyncDefault(false);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), hboxBouncy, new Vector2(), new Vector2()).setKillOnDeath(true));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.GRENADE_LAUNCHER, DamageTag.EXPLOSIVE, DamageTag.RANGED));
        hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), EXPLOSION_RADIUS, EXPLOSION_DAMAGE, EXPLOSION_KNOCKBACK,
                (short) 0, false, DamageSource.GRENADE_LAUNCHER));
        hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.BOMB, 0.4f).setSynced(false));
        hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.WALL_HIT1, 0.2f).setSynced(false));
        hbox.addStrategy(new FlashShaderNearDeath(state, hbox, user.getBodyData(), 1.0f));

        if (!state.isServer()) {
            ((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ClientState.ObjectLayer.HBOX);
        }
        return hboxBouncy;
    }
}