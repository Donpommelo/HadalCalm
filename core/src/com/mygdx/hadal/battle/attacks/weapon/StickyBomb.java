package com.mygdx.hadal.battle.attacks.weapon;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.loaders.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class StickyBomb extends SyncedAttacker {

    public static final Vector2 PROJECTILE_SIZE = new Vector2(50, 50);
    public static final float LIFESPAN = 5.0f;
    private static final float RECOIL = 2.0f;

    public static final float EXPLOSION_DAMAGE = 55.0f;
    private static final int EXPLOSION_RADIUS = 200;
    private static final float EXPLOSION_KNOCKBACK = 25.0f;

    private static final Vector2 STICKY_SIZE = new Vector2(20, 20);

    private static final Sprite PROJ_SPRITE = Sprite.STICKYBOMB;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.LAUNCHER)
                .setVolume(0.25f)
                .setPosition(startPosition));

        user.recoil(startVelocity, RECOIL);

        Hitbox hbox = new RangedHitbox(state, startPosition, STICKY_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, PROJ_SPRITE);
        hbox.setSpriteSize(PROJECTILE_SIZE);
        hbox.setSyncedDeleteNoDelay(true);
        hbox.setSyncedDelete(true);
        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), EXPLOSION_RADIUS, EXPLOSION_DAMAGE, EXPLOSION_KNOCKBACK,
                (short) 0, false, DamageSource.STICKYBOMB_LAUNCHER));
        hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.BOMB, 0.25f));
        hbox.addStrategy(new ContactStick(state, hbox, user.getBodyData(), true, true));
        hbox.addStrategy(new FlashShaderNearDeath(state, hbox, user.getBodyData(), 1.0f));

        if (user instanceof Player player) {
            player.getSpecialWeaponHelper().getStickyBombs().addLast(hbox);
        }

        return hbox;
    }
}