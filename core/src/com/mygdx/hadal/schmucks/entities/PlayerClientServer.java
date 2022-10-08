package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.client.ClientPredictionFrame;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.WorldUtil;

public class PlayerClientServer extends Player {

    //this represents how precisely we lerp towards the server position
    private static final float CONVERGE_MULTIPLIER = 0.02f;

    //this is an ordered list of client frames that keep track of the client's velocity and displacement over time
    private final Array<ClientPredictionFrame> frames = new Array<>();

    //the client's most recent position
    private final Vector2 lastPosition = new Vector2();

    //where we predict the client would be on the server
    private final Vector2 predictedPosition = new Vector2();
    private final Vector2 rubberbandPosition = new Vector2();

    //this is the amount of positional history that we keep track of
    private float historyDuration;

    //the game time when we last received a timestamp from the server
    private float lastTimestamp;

    private float latency;

    public PlayerClientServer(PlayState state, Vector2 startPos, String name, Loadout startLoadout, PlayerBodyData oldData,
                              int connID, User user, boolean reset, Event start) {
        super(state, startPos, name, startLoadout, oldData, connID, user, reset, start);
    }

    @Override
    public void onServerSync() {
        float lastServerTime = state.getTimer();
        int ping = 0;
        if (getUser() != null) {
            ping = getUser().getScores().getPing();
        }

        HadalGame.server.sendToAllExceptUDP(getConnID(), new PacketsSync.SyncPlayer(entityID, getPosition(), getLinearVelocity(),
                entityAge, lastServerTime, moveState, getBodyData().getCurrentHp(),
                mouseAngle, grounded, playerData.getCurrentSlot(),
                playerData.getCurrentTool().isReloading() ? reloadPercent : -1.0f,
                playerData.getCurrentTool().isCharging() ? chargePercent : -1.0f,
                playerData.getCurrentFuel(),
                playerData.getCurrentTool().getClipLeft(), playerData.getCurrentTool().getAmmoLeft(),
                playerData.getActiveItem().chargePercent(),
                getMainFixture().getFilterData().maskBits, invisible, blinded));

        HadalGame.server.sendToUDP(getConnID(), new PacketsSync.SyncClientPlayer(entityID, getPosition(), getLinearVelocity(),
                entityAge, lastServerTime, ping));
    }

    private final Vector2 playerVelocity = new Vector2();
    private final Vector2 playerWorldLocation = new Vector2();
    private final Vector2 newPredictedPosition = new Vector2();
    private final Vector2 newPosition = new Vector2();
    private float predictionCount;
    private float shortestFraction;
    @Override
    public void controller(float delta) {
        super.controller(delta);

        //for the server's own player, the sprite's arm should exactly match their mouse
        playerVelocity.set(getLinearVelocity());
        playerWorldLocation.set(getPosition());

        if (body != null && alive) {
            //we add a new prediction frame to our list with our current displacement/velocity
            ClientPredictionFrame frame = new ClientPredictionFrame(delta);
            frame.positionChange.set(playerWorldLocation).sub(lastPosition);
            frame.velocity.set(playerVelocity);
            frames.add(frame);
            historyDuration += delta;

            //we adjust predicted position to ensure it is up-to-date
            predictedPosition.add(frame.positionChange);

            predictionCount += delta;

            while (predictionCount >= Constants.INTERVAL) {
                predictionCount -= Constants.INTERVAL;

                float time = CONVERGE_MULTIPLIER * latency;
                float t = Constants.INTERVAL / (latency * (1 + CONVERGE_MULTIPLIER));

                newPredictedPosition.set(playerWorldLocation).add(predictedPosition.sub(playerWorldLocation).scl(t));

                shortestFraction = 1.0f;

                //when predicting, we extrapolate our position based on our prediction plus our current velocity given the current latency.
                if (WorldUtil.preRaycastCheck(playerWorldLocation, newPredictedPosition)) {
                    state.getWorld().rayCast((fixture, point, normal, fraction) -> {
                        if (fixture.getFilterData().categoryBits == Constants.BIT_WALL) {
                            if (fraction < shortestFraction) {
                                shortestFraction = fraction;
                                return fraction;
                            }
                        }
                        if (fixture.getFilterData().categoryBits == Constants.BIT_PLAYER) {
                            if (fraction < shortestFraction && ((PlayerBodyData) fixture.getUserData()).getPlayer().getHitboxfilter() != hitboxfilter) {
                                shortestFraction = fraction;
                                return fraction;
                            }
                        }
                        return -1.0f;
                    }, playerWorldLocation, newPredictedPosition);
                }

                //scale extrapolation by shortest fraction to avoid extrapolating through a wall
                if (shortestFraction != 1.0f && !predictedPosition.isZero()) {
                    float dist = predictedPosition.len() * shortestFraction - 1;
                    newPredictedPosition.set(playerWorldLocation).add(predictedPosition.nor().scl(dist));
                }
                setTransform(newPredictedPosition, 0.0f);
            }
            lastPosition.set(getPosition());
        }

        newPosition.set(getPixelPosition());
    }

    public void onReceiveSyncFromClient(Object o) {
        if (o instanceof Packets.SyncServerPlayer p) {

            //ignore packets sent out of order
            if (p.timestamp < lastTimestamp) { return; }

            lastTimestamp = p.timestamp;

            float currentTime = state.getTimer();
            latency = currentTime - p.clientTimestamp;
            latency = Math.max(latency, 0);
            float dt = Math.max(0.0f, historyDuration - latency);

            historyDuration -= dt;

        //we remove each frame in our history that is older than our latency
            while (!frames.isEmpty() && dt > 0) {
                ClientPredictionFrame frame = frames.get(0);
                if (dt >= frame.delta) {
                    dt -= frame.delta;
                    frames.removeIndex(0);
                } else {
                    //the last frame is trimmed so the total amount of time in our history is equal to our latency
                    float t = 1 - dt / frame.delta;

                    frame.delta -= dt;
                    frame.positionChange.scl(t);
                    break;
                }
            }

            if (getController() != null) {
                getController().syncClientKeyStrokes(p.mouseX, p.mouseY, p.pos, p.actions, p.timestamp);

                if (getUser() != null) {
                    int ping = (int) (latency * 1000);
                    if (getUser().getScores().getPing() != ping) {
                        getUser().getScores().setPing(ping);
                    }
                }
            }
        }
    }

    //because we predict, we never want to do standard interpolation
    @Override
    public void clientInterpolation() {}
}
