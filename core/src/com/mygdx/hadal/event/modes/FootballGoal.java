package com.mygdx.hadal.event.modes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.ClientIllusion;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import static com.mygdx.hadal.constants.Constants.MAX_NAME_LENGTH;

/**
 * This event spawns a naval-mine-football for the football game mode.
 * When the mine is destroyed, another will be spawned
 *
 * Triggered Behavior: This event is triggered when a team score. Increment score and display notification
 * Triggering Behavior: N/A
 *
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

                if (state.isServer()) {
                    ParticleEntity particle = new ParticleEntity(state, event, Particle.DIATOM_IMPACT_LARGE, 0, PARTICLE_DURATION,
                            true, SyncType.CREATESYNC);
                    if (teamIndex < AlignmentFilter.currentTeams.length) {
                        particle.setColor(AlignmentFilter.currentTeams[teamIndex].getPalette().getIcon());
                    }
                }

                //give score credit to the player and give notification
                if (p != null) {

                    String playerName = WeaponUtils.getPlayerColorName(p, MAX_NAME_LENGTH);

                    if (teamIndex < AlignmentFilter.currentTeams.length) {
                        if (p.getStartLoadout().team == AlignmentFilter.currentTeams[teamIndex]) {
                            state.getKillFeed().addNotification(UIText.FOOTBALL_GOAL.text(playerName), false);
                            state.getMode().processPlayerScoreChange(state, p, 1);
                        } else {
                            state.getKillFeed().addNotification(UIText.FOOTBALL_GOAL_OWN.text(playerName), false);
                            state.getMode().processPlayerScoreChange(state, p, -1);
                        }
                    }
                    state.getMode().processTeamScoreChange(state, teamIndex, 1);
                }
            }
        };

        this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true,
            Constants.BIT_SENSOR, Constants.BIT_PROJECTILE, (short) 0, true, eventData);
        this.body.setType(BodyDef.BodyType.KinematicBody);
    }

    @Override
    public void loadDefaultProperties() {
        setEventSprite(Sprite.PORTAL);
        setScaleAlign(ClientIllusion.alignType.CENTER_STRETCH);
        setSyncType(eventSyncTypes.ALL);
    }
}
