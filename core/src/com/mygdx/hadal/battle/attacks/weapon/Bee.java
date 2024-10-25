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
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class Bee extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(20, 18);
    public static final float LIFESPAN = 5.0f;
    public static final float BEE_BASE_DAMAGE = 6.0f;
    private static final float BEE_KNOCKBACK = 8.0f;
    private static final int BEE_DURABILITY = 5;
    private static final int BEE_SPREAD = 25;
    private static final float BEE_HOMING = 90;
    private static final int HOME_RADIUS = 30;

    private static final Sprite PROJ_SPRITE = Sprite.BEE;

    private final DamageSource damageSource;

    public Bee(DamageSource damageSource) {
        this.damageSource = damageSource;
    }

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                false, true, user, PROJ_SPRITE);
        hbox.setDensity(0.5f);
        hbox.setDurability(BEE_DURABILITY);

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BEE_BASE_DAMAGE, BEE_KNOCKBACK, damageSource,
                DamageTag.BEES, DamageTag.RANGED).setRepeatable(true));
        hbox.addStrategy(new HomingUnit(state, hbox, user.getBodyData(), BEE_HOMING, HOME_RADIUS)
                .setDisruptable(true).setSteering(false));
        hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), BEE_SPREAD));
        hbox.addStrategy(new CreateSound(state, hbox, user.getBodyData(), SoundEffect.BEE_BUZZ, 0.6f, true));

        return hbox;
    }
}