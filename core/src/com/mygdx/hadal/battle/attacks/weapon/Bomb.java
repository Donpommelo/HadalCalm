package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class Bomb extends SyncedAttacker {

    private static final Vector2 BOMB_SPRITE_SIZE = new Vector2(60, 141);
    private static final Vector2 BOMB_SIZE = new Vector2(60, 60);
    private static final float BOMB_LIFESPAN = 3.0f;

    public static final float BOMB_EXPLOSION_DAMAGE = 40.0f;
    private static final int BOMB_EXPLOSION_RADIUS = 150;
    private static final float BOMB_EXPLOSION_KNOCKBACK = 25.0f;

    private static final Sprite BOMB_SPRITE = Sprite.BOMB;
    private static final Sprite SPARK_SPRITE = Sprite.SPARKS;

    private final DamageSource damageSource;

    public Bomb(DamageSource damageSource) {
        this.damageSource = damageSource;
    }

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.LAUNCHER)
                .setVolume(0.2f)
                .setPosition(startPosition));

        Hitbox hbox = new RangedHitbox(state, startPosition, BOMB_SIZE, BOMB_LIFESPAN, startVelocity, user.getHitboxFilter(),
                false, true, user, BOMB_SPRITE);
        hbox.setSpriteSize(BOMB_SPRITE_SIZE);
        hbox.setGravity(2.5f);
        hbox.setRestitution(0.5f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DropThroughPassability(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), 0, 0, damageSource,
                DamageTag.EXPLOSIVE, DamageTag.RANGED));
        hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), BOMB_EXPLOSION_RADIUS, BOMB_EXPLOSION_DAMAGE, BOMB_EXPLOSION_KNOCKBACK,
                (short) 0, false, damageSource));
        hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.BOMB, 0.4f));
        hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.WALL_HIT1, 0.2f));
        hbox.addStrategy(new FlashShaderNearDeath(state, hbox, user.getBodyData(), 1.0f));

        Hitbox sparks = new RangedHitbox(state, startPosition, BOMB_SIZE, BOMB_LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, false, user, SPARK_SPRITE);
        sparks.setSpriteSize(BOMB_SPRITE_SIZE);
        sparks.setSyncDefault(false);

        sparks.addStrategy(new ControllerDefault(state, sparks, user.getBodyData()));
        sparks.addStrategy(new FixedToEntity(state, sparks, user.getBodyData(), hbox, new Vector2(), new Vector2()));

        if (!state.isServer()) {
            ((ClientState) state).addEntity(sparks.getEntityID(), sparks, false, ObjectLayer.HBOX);
        }

        return hbox;
    }
}