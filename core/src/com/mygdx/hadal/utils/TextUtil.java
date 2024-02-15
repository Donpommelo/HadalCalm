package com.mygdx.hadal.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.server.AlignmentFilter;

public class TextUtil {

    private static final Vector3 rgb = new Vector3();

    /**
     * Remove non alpha numeric characters from a string. Used to sanitize input into search bars
     */
    public static String removeNonAlphaNumeric(String str) {
        return str.replaceAll("[^a-zA-Z0-9]", "");
    }

    /**
     * This method returns a player's "color" corresponding to their team color or their character with no team.
     * This is used to color code player name as well as for streak particle coloring
     */
    public static Vector3 getPlayerColor(Player player) {

        //return empty vector if player's data has not been created yet.
        Loadout loadout = player.getUser().getLoadoutManager().getActiveLoadout();
        if (AlignmentFilter.NONE.equals(loadout.team)) {
            return loadout.character.getPalette().getIcon().getRGB();
        } else if (loadout.team.getPalette().getIcon().getRGB().isZero()) {
            return loadout.character.getPalette().getIcon().getRGB();
        } else {
            return loadout.team.getPalette().getIcon().getRGB();
        }
    }

    /**
     * This returns a string corresponding to a player's colored name. (optionally abridged)
     * Used for kill feed messages and chat window names.
     */
    public static String getPlayerColorName(Schmuck schmuck, int maxNameLen) {

        if (null == schmuck) { return ""; }

        if (schmuck instanceof Player player) {
            String displayedName = player.getName();

            if (displayedName.length() > maxNameLen) {
                displayedName = displayedName.substring(0, maxNameLen).concat("...");
            }

            //get the player's color and use color markup to add color tags.
            rgb.set(getPlayerColor(player));
            String hex = "#" + Integer.toHexString(Color.rgb888(rgb.x, rgb.y, rgb.z));
            return "[" + hex + "]" + displayedName + "[]";
        } else {
            return schmuck.getName();
        }
    }

    /**
     * Works similarly to getPlayerColorName(), only for text not associated with a single player; usually a teams instead
     */
    public static String getColorName(HadalColor color, String name) {
        String hex = "#" + Integer.toHexString(Color.rgb888(color.getColor()));
        return "[" + hex + "]" + name + "[]";
    }
}
