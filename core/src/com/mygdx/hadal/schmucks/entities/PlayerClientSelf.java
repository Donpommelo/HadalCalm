package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.input.ClientController;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class PlayerClientSelf extends Player {

    public PlayerClientSelf(PlayState state, Vector2 startPos, String name, Loadout startLoadout, PlayerBodyData oldData,
                            int connID, User user, boolean reset, Event start) {
        super(state, startPos, name, startLoadout, oldData, connID, user, reset, start);
    }

    @Override
    public void loadParticlesAndSounds() {
        dustCloud = new ParticleEntity(state, this, Particle.DUST, 1.0f, 0.0f, false,
                SyncType.NOSYNC);
        hoverBubbles = new ParticleEntity(state, this, Particle.BUBBLE_TRAIL, 1.0f, 0.0f, false,
                SyncType.NOSYNC);

        hoverSound = new SoundEntity(state, this, SoundEffect.HOVER, 0.0f, 0.2f, 1.0f,
                true, true, SyncType.NOSYNC);
        runSound = new SoundEntity(state, this, SoundEffect.RUN, 0.0f, 0.1f, 1.0f,
                true, true, SyncType.NOSYNC);
        reloadSound = new SoundEntity(state, this, SoundEffect.RELOAD, 0.0f, 0.2f, 1.0f,
                true, true, SyncType.NOSYNC);

        ((ClientState) state).addEntity(dustCloud.getEntityID(), dustCloud, false, PlayState.ObjectLayer.EFFECT);
        ((ClientState) state).addEntity(hoverBubbles.getEntityID(), hoverBubbles, false, PlayState.ObjectLayer.EFFECT);
        ((ClientState) state).addEntity(hoverSound.getEntityID(), hoverSound, false, PlayState.ObjectLayer.EFFECT);
        ((ClientState) state).addEntity(runSound.getEntityID(), runSound, false, PlayState.ObjectLayer.EFFECT);
        ((ClientState) state).addEntity(reloadSound.getEntityID(), reloadSound, false, PlayState.ObjectLayer.EFFECT);
    }

    @Override
    public void clientController(float delta) {
        controller(delta);
    }

    //This contains the position of the client's mouse, to be sent to the server
    private final Vector3 mousePosition = new Vector3();
    private final Vector2 playerPosition = new Vector2();
    @Override
    public void onReceiveSync(Object o, float timestamp) {
        if (o instanceof PacketsSync.SyncClientPlayer p) {
            ((ClientState) state).setLatency(p.ping);
            state.setTimer(p.timestamp - 2 * PlayState.SYNC_TIME);

            mousePosition.set(getMouse().getPixelPosition().x, getMouse().getPixelPosition().y, 0);
            playerPosition.set(getPixelPosition());

            HadalGame.client.sendUDP(new Packets.SyncServerPlayer(mousePosition.x, mousePosition.y, getPosition(), getLinearVelocity(),
                    ((ClientController) state.getController()).getButtonsHeld().toArray(new PlayerAction[0]), state.getTimer(), p.timestamp));
            ((ClientController) state.getController()).postKeystrokeSync();
        }
    }

    @Override
    public void clientInterpolation() {}
}
