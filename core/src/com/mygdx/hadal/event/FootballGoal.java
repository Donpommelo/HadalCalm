package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

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

    private static final int maxNameLength = 25;
    @Override
    public void create() {
        this.eventData = new EventData(this) {

            @Override
            public void onActivate(EventData activator, Player p) {
                state.getUiExtra().changeTeamField(teamIndex, 1);

                if (standardParticle != null) {
                    standardParticle.onForBurst(1.0f);
                }

                //give score credit to the player and give notification
                if (p != null) {

                    String playerName = WeaponUtils.getPlayerColorName(p, maxNameLength);

                    if (teamIndex < AlignmentFilter.currentTeams.length) {
                        if (p.getStartLoadout().team == AlignmentFilter.currentTeams[teamIndex]) {
                            state.getKillFeed().addNotification(playerName + " SCORED A GOAL!", false);
                            state.getUiExtra().changeFields(p, 1, 0, 0, 0, false);
                        } else {
                            state.getKillFeed().addNotification(playerName + " SCORED ON THEIR OWN GOAL!", false);
                            state.getUiExtra().changeFields(p, -1, 0, 0, 0, false);
                        }
                    }
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
        setScaleAlign("CENTER_STRETCH");
        setSyncType(eventSyncTypes.ALL);
    }
}
