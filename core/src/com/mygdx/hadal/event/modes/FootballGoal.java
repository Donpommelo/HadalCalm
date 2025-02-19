package com.mygdx.hadal.event.modes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.entities.ClientIllusion;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.utils.TextUtil;
import com.mygdx.hadal.utils.b2d.HadalBody;

import static com.mygdx.hadal.constants.Constants.MAX_NAME_LENGTH;

/**
 * This event spawns a naval-mine-football for the football game mode.
 * When the mine is destroyed, another will be spawned
 * <p>
 * Triggered Behavior: This event is triggered when a team score. Increment score and display notification
 * Triggering Behavior: N/A
 * <p>
 * Fields: teamIndex: int index of the team that is trying to score on this goal
 *
 * @author Himilius Huctuford
 */
public class FootballGoal extends Event {

    private final static float PARTICLE_DURATION = 5.0f;

    private final int teamIndex;

    public FootballGoal(PlayState state, Vector2 startPos, Vector2 size, int teamIndex) {
        super(state, startPos, size);
        this.teamIndex = teamIndex;
    }

    @Override
    public void create() {
        this.eventData = new EventData(this) {

            @Override
            public void onActivate(EventData activator, Player p) {
                HadalColor color = HadalColor.NOTHING;
                if (teamIndex < AlignmentFilter.currentTeams.length) {
                    color = AlignmentFilter.currentTeams[teamIndex].getPalette().getIcon();
                }

                EffectEntityManager.getParticle(state, new ParticleCreate(Particle.DIATOM_IMPACT_LARGE, event)
                        .setLifespan(PARTICLE_DURATION)
                        .setColor(color));

                //give score credit to the player and give notification
                if (p != null) {

                    String playerName = TextUtil.getPlayerColorName(p, MAX_NAME_LENGTH);

                    if (teamIndex < AlignmentFilter.currentTeams.length) {
                        if (p.getUser().getLoadoutManager().getActiveLoadout().team == AlignmentFilter.currentTeams[teamIndex]) {
                            state.getUIManager().getKillFeed().addNotification(UIText.FOOTBALL_GOAL.text(playerName), false);
                            state.getMode().processPlayerScoreChange(state, p, 1);
                        } else {
                            state.getUIManager().getKillFeed().addNotification(UIText.FOOTBALL_GOAL_OWN.text(playerName), false);
                            state.getMode().processPlayerScoreChange(state, p, -1);
                        }
                    }
                    state.getMode().processTeamScoreChange(state, teamIndex, 1);
                }
            }
        };

        this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, BodyConstants.BIT_PROJECTILE, (short) 0)
                .setBodyType(BodyDef.BodyType.KinematicBody)
                .addToWorld(world);
    }

    @Override
    public void loadDefaultProperties() {
        setEventSprite(Sprite.PORTAL);
        setScaleAlign(ClientIllusion.alignType.CENTER_STRETCH);

        setServerSyncType(eventSyncTypes.ECHO_ACTIVATE);
        setClientSyncType(eventSyncTypes.ACTIVATE);
    }

    public int getTeamIndex() { return teamIndex; }
}
