package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.PacketManager;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.userdata.FeetData;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.utils.b2d.HadalFixture;

/**
 * This contains a number of helper functions used by various events
 *
 */
public class EventUtils {

    /**
     * This sets a single objective marker on a specified entity
     */
    public static void setObjectiveMarker(PlayState state, HadalEntity event, Sprite sprite, HadalColor color,
                                          boolean displayObjectiveOffScreen, boolean displayObjectiveOnScreen, boolean displayClearCircle) {
        if (state.isServer()) {
            state.getUIManager().getUiObjective().addObjective(event, sprite, color, displayObjectiveOffScreen, displayObjectiveOnScreen, displayClearCircle);
            PacketManager.serverTCPAll(new Packets.SyncObjectiveMarker(event.getEntityID(), color,
                    displayObjectiveOffScreen, displayObjectiveOnScreen, displayClearCircle, sprite));
        }
    }

    /**
     * This sets a single objective marker on a specified entity.
     * The marker is only set for players on a specific team
     */
    public static void setObjectiveMarkerTeam(PlayState state, HadalEntity event, Sprite sprite, HadalColor color,
                                          boolean displayObjectiveOffScreen, boolean displayObjectiveOnScreen, boolean displayClearCircle,
                                          AlignmentFilter team) {

        if (state.isServer()) {
            for (ObjectMap.Entry<Integer, User> user : HadalGame.usm.getUsers()) {
                if (user.value.isSpectator() || user.value.getTeamFilter() == team) {
                    if (user.key == 0) {
                        state.getUIManager().getUiObjective().addObjective(event, sprite, color, displayObjectiveOffScreen, displayObjectiveOnScreen, displayClearCircle);
                    } else {
                        PacketManager.serverTCP(user.key, new Packets.SyncObjectiveMarker(event.getEntityID(), color,
                                displayObjectiveOffScreen, displayObjectiveOnScreen, displayClearCircle, sprite));
                    }
                }
            }
        }
    }

    /**
     * This adds a foot fixture to an event, allowing it to selectively pass through dropthrough platforms
     */
    public static void addFeetFixture(HadalEntity event) {
        FeetData feetData = new FeetData(UserDataType.FEET, event);
        new HadalFixture(
                new Vector2(1.0f / 2,  - event.getSize().y / 2),
                new Vector2(event.getSize().x, event.getSize().y / 8),
                BodyConstants.BIT_SENSOR, BodyConstants.BIT_DROPTHROUGHWALL, (short) 0)
                .setFriction(1.0f)
                .addToBody(event.getBody())
                .setUserData(feetData);
    }
}
