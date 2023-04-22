package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.DestructableBlock;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.EventUtils;
import com.mygdx.hadal.schmucks.entities.ClientIllusion;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;

public class Terraform extends SyncedAttacker {

    public static final int BLOCK_HP = 250;
    private static final Vector2 BLOCK_SIZE = new Vector2(64, 192);
    private static final float BLOCK_SPEED = 8.0f;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {

        Vector2 weaponVelo = new Vector2();
        if (extraFields.length > 1) {
            weaponVelo.set(extraFields[0], extraFields[1]);
        }

        SoundEffect.MAGIC1_ACTIVE.playSourced(state, startPosition, 1.0f);
        Event block = new DestructableBlock(state, user.getProjectileOrigin(weaponVelo, BLOCK_SIZE.x), BLOCK_SIZE, BLOCK_HP, false) {

            @Override
            public void create() {
                super.create();
                body.setLinearVelocity(new Vector2(weaponVelo).nor().scl(BLOCK_SPEED));
                EventUtils.addFeetFixture(this);
            }
        };
        block.setEventSprite(Sprite.UI_MAIN_HEALTH_MISSING);
        block.setScaleAlign(ClientIllusion.alignType.CENTER_STRETCH);
        block.setStandardParticle(Particle.IMPACT);
        block.setGravity(1.0f);
        block.setSynced(true);
    }
}