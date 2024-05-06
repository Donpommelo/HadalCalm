package com.mygdx.hadal.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;

public class CursorManager {

    private static final int PIXMAP_SIZE = 128;

    //this is the last cursor used. We save this so we can dispose of it properly
    private static Cursor lastCursor;

    public void setCursor() {

        //when we set a new cursor, we dispose of the old one (if existent)
        if (lastCursor != null) {
            lastCursor.dispose();
        }

        //draw designated cursor to pixmap
        Pixmap cursor = new Pixmap(Gdx.files.internal(indexToCursorType()));

        Pixmap pm = new Pixmap(PIXMAP_SIZE, PIXMAP_SIZE, Pixmap.Format.RGBA8888);

        int scaledWidth = (int) (indexToCursorScale() * cursor.getWidth());
        int scaledHeight = (int) (indexToCursorScale() * cursor.getHeight());

        pm.drawPixmap(cursor,
                0, 0, cursor.getWidth() + 1, cursor.getHeight() + 1,
                (PIXMAP_SIZE - scaledWidth) / 2, (PIXMAP_SIZE - scaledHeight) / 2, scaledWidth, scaledHeight);

        //color pixmap with chosen color
        Color newColor = indexToCursorColor();
        for (int y = 0; y < pm.getHeight(); y++) {
            for (int x = 0; x < pm.getWidth(); x++) {
                Color color = new Color();
                Color.rgba8888ToColor(color, pm.getPixel(x, y));
                if (color.a != 0.0f) {
                    pm.setColor(newColor.r, newColor.g, newColor.b, color.a);
                    pm.fillRectangle(x, y, 1, 1);
                }
            }
        }

        //set new cursor and dispose of last used cursor to prevent memory leak
        Cursor newCursor = Gdx.graphics.newCursor(pm, PIXMAP_SIZE / 2, PIXMAP_SIZE / 2);
        Gdx.graphics.setCursor(newCursor);
        lastCursor = newCursor;
        pm.dispose();
        cursor.dispose();
    }

    public String indexToCursorType() {
        return switch (JSONManager.setting.getCursorType()) {
            case 1 -> "cursors/crosshair_b.png";
            case 2 -> "cursors/crosshair_c.png";
            case 3 -> "cursors/crosshair_d.png";
            case 4 -> "cursors/crosshair_e.png";
            case 5 -> "cursors/crosshair_f.png";
            case 6 -> "cursors/crosshair_g.png";
            case 7 -> "cursors/crosshair_h.png";
            case 8 -> "cursors/crosshair_i.png";
            case 9 -> "cursors/crosshair_j.png";
            case 10 -> "cursors/crosshair_k.png";
            case 11 -> "cursors/crosshair_l.png";
            default -> "cursors/crosshair_a.png";
        };
    }

    /**
     * Convert cursor color from index in list
     */
    public Color indexToCursorColor() {
        return switch (JSONManager.setting.getCursorColor()) {
            case 0 -> Color.BLACK;
            case 1 -> Color.CYAN;
            case 2 -> Color.LIME;
            case 3 -> Color.MAGENTA;
            case 4 -> Color.RED;
            case 6 -> Color.YELLOW;
            default -> Color.WHITE;
        };
    }

    public float indexToCursorScale() {
        return switch (JSONManager.setting.getCursorSize()) {
            case 0 -> 0.5f;
            case 2 -> 1.0f;
            default -> 0.75f;
        };
    }
}
