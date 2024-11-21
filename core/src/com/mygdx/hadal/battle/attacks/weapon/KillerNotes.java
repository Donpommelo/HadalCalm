package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class KillerNotes extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(40, 40);
    public static final float LIFESPAN = 0.8f;
    public static final float BASE_DAMAGE = 25.0f;
    private static final float RECOIL = 7.0f;
    private static final float KNOCKBACK = 18.0f;

    private static final Sprite PROJ_SPRITE = Sprite.DIATOM_D;

    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) {

        Hitbox[] hboxes = new Hitbox[startPosition.length];
        user.recoil(weaponVelocity, RECOIL);

        if (startPosition.length != 0) {
            for (int i = 0; i < startPosition.length; i++) {
                int note = extraFields.length <= i ? 0 : (int) extraFields[i];
                SoundEffect soundEffect = SoundEffect.PIANO_C;
                switch (note) {
                    case 1 -> soundEffect = SoundEffect.PIANO_D;
                    case 2 -> soundEffect = SoundEffect.PIANO_F;
                    case 3 -> soundEffect = SoundEffect.PIANO_G;
                    case 4 -> soundEffect = SoundEffect.PIANO_A;
                    case 5 -> soundEffect = SoundEffect.PIANO_B;
                    case 6 -> soundEffect = SoundEffect.PIANO_C2;
                }
                SoundManager.play(state, new SoundLoad(soundEffect)
                        .setVolume(0.5f)
                        .setPosition(startPosition[i]));

                Hitbox hbox = new RangedHitbox(state, startPosition[i], PROJECTILE_SIZE, LIFESPAN, startVelocity[i], user.getHitboxFilter(),
                        true, true, user, PROJ_SPRITE);
                hbox.setDurability(2);

                hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
                hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
                hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
                hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
                        DamageSource.KILLER_BEAT, DamageTag.ENERGY, DamageTag.RANGED, DamageTag.SOUND));
                hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.RING_TRAIL));
                hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.NOTE_IMPACT));
                hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.NOTE_IMPACT));
                hboxes[i] = hbox;
            }
        }
        return hboxes;
    }
}