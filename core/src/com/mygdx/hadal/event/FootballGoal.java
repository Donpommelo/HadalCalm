package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.HText;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import static com.mygdx.hadal.utils.Constants.MAX_NAME_LENGTH;

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

                if (standardParticle != null) {
                    standardParticle.onForBurst(1.0f);
                }

                //give score credit to the player and give notification
                if (p != null) {

                    String playerName = WeaponUtils.getPlayerColorName(p, MAX_NAME_LENGTH);

                    if (teamIndex < AlignmentFilter.currentTeams.length) {
                        if (p.getStartLoadout().team == AlignmentFilter.currentTeams[teamIndex]) {
                            state.getKillFeed().addNotification(HText.FOOTBALL_GOAL.text(playerName), false);
                            state.getMode().processPlayerScoreChange(state, p, 1);
                        } else {
                            state.getKillFeed().addNotification(HText.FOOTBALL_GOAL_OWN.text(playerName), false);
                            state.getMode().processPlayerScoreChange(state, p, -1);
                        }
                    }
                }
                state.getMode().processTeamScoreChange(state, teamIndex, 1);
            }
        };

        this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true,
            Constants.BIT_SENSOR, Constants.BIT_PROJECTILE, (short) 0, true, eventData);
        this.body.setType(BodyDef.BodyType.KinematicBody);
    }

    @Override
    public void loadDefaultProperties() {
        setEventSprite(Sprite.PORTAL);
        setScaleAlign("CENTER_STRETCH");
        setSyncType(eventSyncTypes.ALL);
    }
}
