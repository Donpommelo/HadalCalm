package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.OrbitUser;

public class OrbitalStar extends SyncedAttacker {

    public static final float PROJ_LIFESPAN = 5.0f;
    public static final int PROJ_NUM = 4;
    public static final float PROJ_DAMAGE = 27.0f;

    private static final Vector2 PROJ_SIZE = new Vector2(25, 25);
    private static final Vector2 SPRITE_SIZE = new Vector2(40, 40);
    private static final float PROJ_KNOCKBACK = 25.0f;
    private static final float PROJ_SPEED = 180.0f;
    private static final float PROJ_RANGE = 5.0f;

    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {

        Hitbox[] hboxes = new Hitbox[PROJ_NUM];
        hboxes[0] = createOrbital(state, user, 0);
        hboxes[1] = createOrbital(state, user, 90);
        hboxes[2] = createOrbital(state, user, 180);
        hboxes[3] = createOrbital(state, user, 270);

        SoundEntity sound = new SoundEntity(state, hboxes[0], SoundEffect.MAGIC25_SPELL, PROJ_LIFESPAN, 1.0f, 1.0f,
                true, true, SyncType.NOSYNC);

        if (!state.isServer()) {
            ((ClientState) state).addEntity(sound.getEntityID(), sound, false, ObjectLayer.EFFECT);
        }

        return hboxes;
    }

    private static Hitbox createOrbital(PlayState state, Schmuck user, float startAngle) {
        Hitbox hbox = new RangedHitbox(state, user.getPixelPosition(), PROJ_SIZE, PROJ_LIFESPAN, new Vector2(),
                user.getHitboxFilter(), true, true, user, Sprite.STAR_WHITE);
        hbox.setSpriteSize(SPRITE_SIZE);
        hbox.makeUnreflectable();

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), PROJ_DAMAGE, PROJ_KNOCKBACK,
                DamageSource.ORBITAL_SHIELD, DamageTag.MAGIC).setStaticKnockback(true).setRepeatable(true));
        hbox.addStrategy(new OrbitUser(state, hbox, user.getBodyData(), startAngle, PROJ_RANGE, PROJ_SPEED));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.STAR_TRAIL, 0.0f, 1.0f)
                .setSyncType(SyncType.NOSYNC));

        return hbox;
    }
}