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
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class ProliferatorProjectile extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(60, 60);
    public static final float LIFESPAN = 4.0f;
    public static final float BASE_DAMAGE = 46.0f;
    private static final float RECOIL = 9.0f;
    private static final float KNOCKBACK = 21.0f;

    private static final Sprite PROJ_SPRITE = Sprite.ORB_BLUE;

    private static final float PROJ_DAMPEN = 6.0f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.SPRING)
                .setVolume(0.6f)
                .setPosition(startPosition));

        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.EXPLOSION));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.PEARL_REVOLVER,
                DamageTag.ENERGY, DamageTag.RANGED));
        hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

            @Override
            public void create() {
                super.create();
                hbox.getBody().setLinearDamping(PROJ_DAMPEN);
            }

            @Override
            public void die() {
                //remove dead coils from list
                if (user instanceof Player player) {
                    player.getSpecialWeaponHelper().getLeapFrogs().removeValue(hbox, false);
                }
            }
        });

        if (user instanceof Player player) {
            player.getSpecialWeaponHelper().getLeapFrogs().add(hbox);
        }

        return hbox;
    }
}