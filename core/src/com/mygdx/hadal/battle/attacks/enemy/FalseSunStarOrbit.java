package com.mygdx.hadal.battle.attacks.enemy;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class FalseSunStarOrbit extends SyncedAttacker {

    private static final float LIFESPAN = 10.0f;
    private static final float BASE_DAMAGE = 16.0f;
    private static final float KNOCKBACK = 18.0f;

    private static final Sprite[] STAR_SPRITES = {Sprite.STAR_BLUE, Sprite.STAR_PURPLE, Sprite.STAR_RED, Sprite.STAR_YELLOW};
    private static final float LINGER = 1.0f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        int starNum = 0;
        float starSize = 0.0f;
        float starSpeed = 0.0f;
        float starDist = 0.0f;
        if (extraFields.length > 3) {
            starNum = (int) extraFields[0];
            starSize = extraFields[1];
            starSpeed = extraFields[2];
            starDist = extraFields[3];
        }

        RangedHitbox hbox = new RangedHitbox(state, new Vector2(), new Vector2(starSize, starSize), LIFESPAN,
                new Vector2(), user.getHitboxFilter(), true, true, user,
                STAR_SPRITES[MathUtils.random(STAR_SPRITES.length - 1)]);
        hbox.makeUnreflectable();
        hbox.setSpriteSize(new Vector2(starSize * 2.0f, starSize * 2.0f));

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.STAR, 0.0f, LINGER)
                .setParticleColor(HadalColor.RANDOM).setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true)
                .setSynced(false));
        hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));

        if (starNum % 8 == 0) {
            SoundEntity sound = new SoundEntity(state, hbox, SoundEffect.MAGIC25_SPELL, LIFESPAN, 0.8f, 0.5f,
                    true, true, SyncType.NOSYNC);

            if (!state.isServer()) {
                ((ClientState) state).addEntity(sound.getEntityID(), sound, false, ClientState.ObjectLayer.HBOX);
            }
        }

        if (starNum % 2 == 0) {
            hbox.addStrategy(new OrbitUser(state, hbox, user.getBodyData(), 90, starDist, starSpeed));
        } else {
            hbox.addStrategy(new OrbitUser(state, hbox, user.getBodyData(), 90, starDist, -starSpeed));
        }

        return hbox;
    }
}
