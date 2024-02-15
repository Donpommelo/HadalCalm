package com.mygdx.hadal.users;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

/**
 * The StringManager contains various utilities that keep track of and modify the user's name and other text for ui
 * This is separated into its own manager for code organization purposes
 */
public class StringManager {

    private final User user;

    //user's name. Set upon initialization and not changed without reconnecting
    private final String name;

    public StringManager(User user, String name) {
        this.user = user;
        this.name = name;
    }

    //this gets the name displayed in score windows. Gives an indication of which player is the host
    public String getPingText() {
        if (user.getConnID() == 0) {
            return "(HOST) ";
        } else if (user.getConnID() < 0) {
            return "(BOT) ";
        } else {
            return "(" + user.getPing() + " ms) ";
        }
    }

    public String getNameShort() {
        return name;
    }

    /**
     * This abridges a name according to a max length in characters
     * Used for results state player names
     * @param maxNameLen: the max characters
     * @return the new abridged name
     */
    public String getNameAbridged(int maxNameLen) {
        String displayedName = getNameShort();

        //if a name is too long, we add an '...' to the end
        if (displayedName.length() > maxNameLen) {
            displayedName = displayedName.substring(0, maxNameLen).concat("...");
        }

        return displayedName;
    }

    private static final Vector3 rgb = new Vector3();
    /**
     * This returns an abridged version of the user's name
     * Additionally, the name will be colored according to the user's alignment
     * Used for score window and play ui
     * @param maxNameLen: Max length of name. Any more will be abridged with ellipses
     * @return the modified name
     */
    public String getNameAbridgedColored(int maxNameLen) {
        String displayedName = getNameShort();

        //if a name is too long, we add an '-' to the end
        if (displayedName.length() > maxNameLen) {
            displayedName = displayedName.substring(0, maxNameLen).concat("-");
        }

        if (user.getTeamFilter().getPalette().getIcon().getRGB().isZero()) {
            rgb.setZero();
        } else {
            rgb.set(user.getTeamFilter().getPalette().getIcon().getRGB());
        }

        String hex = "#" + Integer.toHexString(Color.rgb888(rgb.x, rgb.y, rgb.z));
        return "[" + hex + "]" + displayedName + "[]";
    }

    public String getName() { return name; }
}
