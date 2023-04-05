package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class Spear extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(50, 12);
    public static final float LIFESPAN = 1.0f;
    public static final float BASE_DAMAGE = 40.0f;
    public static final float NERFED_DAMAGE = 30.0f;
    private static final float RECOIL = 2.5f;
    private static final float KNOCKBACK = 15.0f;

    private static final Sprite PROJ_SPRITE = Sprite.HARPOON;

    private final boolean nerfed;

    public Spear(boolean nerfed) {
        this.nerfed = nerfed;
    }

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundEffect.SPIKE.playSourced(state, startPosition, 0.5f);
        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);
        hbox.setGravity(1.0f);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS).setSyncType(SyncType.NOSYNC));
        hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), nerfed ? NERFED_DAMAGE : BASE_DAMAGE, KNOCKBACK,
                DamageSource.SPEARGUN, DamageTag.POKING, DamageTag.RANGED));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.STAB, 0.8f, true).setSynced(false));
        hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.BULLET_DIRT_HIT, 0.6f).setSynced(false));

        return hbox;
    }
}