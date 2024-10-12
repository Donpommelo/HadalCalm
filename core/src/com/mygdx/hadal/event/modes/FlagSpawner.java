package com.mygdx.hadal.event.modes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.ClientIllusion;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.utils.TextUtil;
import com.mygdx.hadal.utils.b2d.HadalBody;

import static com.mygdx.hadal.constants.Constants.MAX_NAME_LENGTH;

/**
 *
 * Triggered Behavior: This event is triggered when a team scores. Increment score and display notification
 * Triggering Behavior: N/A
 * <p>
 * Fields: teamIndex: int index of the team that is trying to score by bringing enemy flag to this event
 *
 * @author Noporon Nashmere
 */
public class FlagSpawner extends Event {

    private final static float PARTICLE_DURATION = 5.0f;

    //index of the team whose flag this spawns
    private final int teamIndex;

    //whether a flag is currently present at the spawner
    private boolean flagPresent;

    public FlagSpawner(PlayState state, Vector2 startPos, Vector2 size, int teamIndex) {
        super(state, startPos, size);
        this.teamIndex = teamIndex;
    }

    @Override
    public void create() {
        this.eventData = new EventData(this) {

            @Override
            public void onActivate(EventData activator, Player p) {
                spawnerParticles(false);

                //give score credit to the player and give notification
                if (null != p) {
                    String playerName = TextUtil.getPlayerColorName(p, MAX_NAME_LENGTH);
                    state.getUIManager().getKillFeed().addNotification(UIText.CTF_CAPTURE.text(playerName), false);
                    state.getMode().processPlayerScoreChange(state, p, 1);
                }
                state.getMode().processTeamScoreChange(state, teamIndex, 1);
            }
        };

        this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, BodyConstants.BIT_SENSOR, (short) 0)
                .setBodyType(BodyDef.BodyType.KinematicBody)
                .addToWorld(world);
    }

    private static final float SPAWN_DELAY = 1.0f;
    private static final float CHECK_INTERVAL = 0.2f;
    private FlagCapturable flag;
    private float spawnCountdown;
    private float controllerCount;
    @Override
    public void controller(float delta) {

        //flag is spawned after a set delay
        if (0.0f < spawnCountdown) {
            spawnCountdown -= delta;
            if (spawnCountdown <= 0.0f) {
                spawnFlag();
            }
        } else {
            //spawn a flag if it is dead or nonexistent
            boolean flagded = false;
            if (null == flag) {
                flagded = true;
            } else if (!flag.isAlive()) {
                flagded = true;
            }
            if (flagded) {
                spawnCountdown = SPAWN_DELAY;
            }
        }

        controllerCount += delta;
        if (CHECK_INTERVAL <= controllerCount) {
            controllerCount = 0.0f;
            for (HadalEntity entity : eventData.getSchmucks()) {
                if (entity instanceof FlagCapturable flagCapturable) {
                    flagCapturable.checkCapture(this);
                }
            }
        }
        messageCount -= delta;
    }

    /**
     * This spawns a flag event at the spawner location
     */
    private void spawnFlag() {
        spawnerParticles(true);
        flag = new FlagCapturable(state, new Vector2(getPixelPosition()), this, teamIndex);
        flagPresent = true;
    }

    private static final float MESSAGE_COOLDOWN = 5.0f;
    private float messageCount;
    public void triggerFailMessage() {

        //message is activated when attempting to capture flag while enemy holds your flag
        if (0.0f >= messageCount) {
            if (teamIndex < AlignmentFilter.currentTeams.length) {
                messageCount = MESSAGE_COOLDOWN;
                String teamColor = AlignmentFilter.currentTeams[teamIndex].getColoredAdjective();
                state.getUIManager().getKillFeed().addNotification(UIText.CTF_CAPTURE_FAIL.text(teamColor), true);
            }
        }
    }

    private void spawnerParticles(boolean global) {
        ParticleEntity particle = new ParticleEntity(state, this, Particle.DIATOM_IMPACT_LARGE, 0, PARTICLE_DURATION,
                true, global ? SyncType.CREATESYNC : SyncType.NOSYNC);
        if (teamIndex < AlignmentFilter.currentTeams.length) {
            particle.setColor(AlignmentFilter.currentTeams[teamIndex].getPalette().getIcon());
        }

        if (!state.isServer()) {
            ((ClientState) state).addEntity(particle.getEntityID(), particle, false, PlayState.ObjectLayer.EFFECT);
        }
    }

    public int getTeamIndex() { return teamIndex; }

    @Override
    public void loadDefaultProperties() {
        setScaleAlign(ClientIllusion.alignType.CENTER_STRETCH);
        setServerSyncType(eventSyncTypes.ECHO_ACTIVATE);
        setClientSyncType(eventSyncTypes.ACTIVATE);
        addAmbientParticle(Particle.RING);
    }

    public boolean isFlagPresent() { return flagPresent; }

    public void setFlagPresent(boolean flagPresent) { this.flagPresent = flagPresent; }
}
