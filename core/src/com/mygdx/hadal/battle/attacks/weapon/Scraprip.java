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
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

import static com.mygdx.hadal.constants.Constants.PPM;

public class Scraprip extends SyncedAttacker {

    public static final float BASE_DAMAGE = 60.0f;
    private static final Vector2 HITBOX_SIZE = new Vector2(200, 120);
    private static final Vector2 HITBOX_SPRITE_SIZE = new Vector2(300, 180);
    private static final float KNOCKBACK = 25.0f;
    private static final float LIFESPAN = 0.25f;

    @Override
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) {
        SoundManager.play(state, new SoundLoad(SoundEffect.WOOSH)
                .setPosition(startPosition));

        Hitbox hbox = new Hitbox(state, startPosition, HITBOX_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
                true, true, user, Sprite.IMPACT);
        hbox.setSpriteSize(HITBOX_SPRITE_SIZE);
        hbox.makeUnreflectable();

        hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
        hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.SCRAPRIPPER,
                DamageTag.MELEE, DamageTag.CUTTING).setConstantKnockback(true, startVelocity));
        hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), startVelocity, startVelocity.nor().scl(HITBOX_SIZE.x / 2 / PPM)));
        hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.SLASH, 0.8f, true));
        hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.EXPLOSION));

        return hbox;
    }
}