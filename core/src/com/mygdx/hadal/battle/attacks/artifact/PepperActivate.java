package com.mygdx.hadal.battle.attacks.artifact;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class PepperActivate extends SyncedAttacker {

    public static final float DAMAGE = 9.0f;
    private static final float RADIUS = 10.0f;
    private static final float EFFECT_DURATION = 1.0f;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        state.getWorld().QueryAABB(fixture -> {
                    if (fixture.getUserData() instanceof BodyData bodyData) {
                        if (bodyData.getSchmuck().getHitboxFilter() != user.getHitboxFilter()) {
                            bodyData.receiveDamage(DAMAGE, new Vector2(), user.getBodyData(), true, null, DamageSource.PEPPER);
                            bodyData.getSchmuck().setShader(Shader.STATIC, EFFECT_DURATION);
                        }
                    }
                    return true;
                },
                startPosition.x - RADIUS, startPosition.y - RADIUS,
                startPosition.x + RADIUS, startPosition.y + RADIUS);
    }
}