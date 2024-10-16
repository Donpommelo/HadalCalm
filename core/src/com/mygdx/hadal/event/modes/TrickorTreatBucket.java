package com.mygdx.hadal.event.modes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.TrickOrTreating;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 *
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * <p>
 * Fields: teamIndex: int index of the team that is trying to score by bringing enemy candy to this event
 *
 * @author Matannia Muchnold
 */
public class TrickorTreatBucket extends Event {

    private static final float BUCKET_WIDTH = 404.0f;
    private static final float BUCKET_HEIGHT = 418.0f;
    private static final float BUCKET_SIZE_SCALE = 0.5f;

    private static final float BUCKET_FIRE_OFFSETX = 79.0f;
    private static final float BUCKET_FIRE_OFFSETY = -5.0f;

    //index of the team whose flag this spawns
    private final int teamIndex;

    private final boolean mirror;

    private boolean lightSet;

    public TrickorTreatBucket(PlayState state, Vector2 startPos, Vector2 size, int teamIndex, boolean mirror) {
        super(state, startPos, size);
        this.teamIndex = teamIndex;
        this.mirror = mirror;
    }

    @Override
    public void create() {
        this.eventData = new EventData(this) {

            @Override
            public void onActivate(EventData activator, Player p) {
                if (teamIndex < AlignmentFilter.currentTeams.length) {
                    if (AlignmentFilter.currentTeams[teamIndex] != p.getUser().getLoadoutManager().getActiveLoadout().team) {
                        state.getMode().processTeamScoreChange(state, teamIndex, -1);
                    } else {
                        state.getMode().processTeamScoreChange(state, teamIndex, 1);
                    }
                }
            }
        };

        this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR,
                (short) (BodyConstants.BIT_PLAYER | BodyConstants.BIT_PROJECTILE), (short) 0)
                .setBodyType(BodyDef.BodyType.KinematicBody)
                .addToWorld(world);

        setLightColor();
    }

    @Override
    public void controller(float delta) {
        for (HadalEntity entity : getEventData().getSchmucks()) {
            if (entity instanceof Player player) {
                Status candyStatus = player.getPlayerData().getStatus(TrickOrTreating.class);
                if (null != candyStatus) {
                    if (candyStatus instanceof TrickOrTreating trickOrTreating) {
                        trickOrTreating.bucketCheck(this, delta);
                    }
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch batch, Vector2 entityLocation) {
        int enemyCandyCount;

        //we need to check teamIndex since clients may not receive team info before event is created
        if (teamIndex >= AlignmentFilter.teamScores.length) {
            enemyCandyCount = 0;
        } else {
            enemyCandyCount = AlignmentFilter.teamScores[teamIndex];
        }
        
        Sprite bucketSprite = 0 < enemyCandyCount ? Sprite.CANDY_STAND : Sprite.CANDY_STAND_EMPTY;

        batch.draw(bucketSprite.getFrame(),
                entityLocation.x - BUCKET_WIDTH * BUCKET_SIZE_SCALE / 2,
                entityLocation.y - BUCKET_HEIGHT * BUCKET_SIZE_SCALE / 2,
                BUCKET_WIDTH * BUCKET_SIZE_SCALE / 2, BUCKET_HEIGHT * BUCKET_SIZE_SCALE / 2,
                BUCKET_WIDTH * BUCKET_SIZE_SCALE, BUCKET_HEIGHT * BUCKET_SIZE_SCALE, mirror ? -1 : 1, 1, 0);

        if (!lightSet) {
            setLightColor();
        }
    }

    public void setLightColor() {
        if (teamIndex < AlignmentFilter.currentTeams.length) {
            HadalColor color = AlignmentFilter.currentTeams[teamIndex].getPalette().getIcon();

            ParticleEntity fire = new ParticleEntity(state, this, Particle.GHOST_LIGHT, 0, 0, true,
                    SyncType.NOSYNC).setColor(color);

            fire.setOffset((mirror ? -1 : 1) * BUCKET_FIRE_OFFSETX, BUCKET_FIRE_OFFSETY);

            if (!state.isServer()) {
                ((ClientState) state).addEntity(fire.getEntityID(), fire, false, ClientState.ObjectLayer.EFFECT);
            }

            state.getUIManager().getUiObjective().addObjective(this, Sprite.CANDY_BUCKET, color,true, false, true);
            lightSet = true;
        }
    }

    public int getTeamIndex() { return teamIndex; }

    @Override
    public void loadDefaultProperties() {
        setServerSyncType(eventSyncTypes.ECHO_ACTIVATE);
        setClientSyncType(eventSyncTypes.ACTIVATE);
    }
}
