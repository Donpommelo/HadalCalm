package com.mygdx.hadal.statuses;

import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.enemies.Krill;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayStateClient;
import com.mygdx.hadal.states.PlayState;

public class MarkedKrill extends Status {

    private static final int NUM_KRILL = 4;
    private static final float KRILL_DURATION = 25.0f;
    private static final float LINGER = 1.0f;

    public MarkedKrill(PlayState state, float i, BodyData p, BodyData v) {
        super(state, i, false, p, v);
    }

    @Override
    public void onInflict() {
        ParticleEntity particleEntity = new ParticleEntity(state, inflicted.getSchmuck(), Particle.KRILL_ALERT, LINGER, duration + LINGER,
                true, SyncType.NOSYNC)
                .setPrematureOff(LINGER)
                .setShowOnInvis(true);
        if (!state.isServer()) {
            ((PlayStateClient) state).addEntity(particleEntity.getEntityID(), particleEntity, false, PlayStateClient.ObjectLayer.EFFECT);
        }
    }

    private static final float PROC_CD = 1.5f;
    private float procCdCount;
    boolean krillCreated;
    @Override
    public void timePassing(float delta) {
        super.timePassing(delta);
        if (procCdCount >= PROC_CD && !krillCreated) {
            krillCreated = true;
            if (state.isServer() && inflicter.getSchmuck() instanceof Player player && inflicter.getSchmuck().isAlive()) {
                Krill krillMain = createKrill(player, null);
                for (int i = 0; i < NUM_KRILL; i++) {
                    createKrill(player, krillMain);
                }
            }
        }
        procCdCount += delta;
    }

    private Krill createKrill(Player player, Krill main) {
        return new Krill(state, player.getPixelPosition(), 0.0f, player.getHitboxFilter()) {

            @Override
            public void create() {
                super.create();
                getBodyData().addStatus(new Temporary(state, KRILL_DURATION, getBodyData(), getBodyData(), KRILL_DURATION));
                getBodyData().addStatus(new Summoned(state, getBodyData(), player));
            }

            @Override
            public void controller(float delta) {
                super.controller(delta);
                if (null != main) {
                    setAttackTarget(main.getAttackTarget());
                }
            }
        };
    }
}
