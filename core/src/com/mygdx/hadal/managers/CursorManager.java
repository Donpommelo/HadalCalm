package com.mygdx.hadal.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.HadalGame;

public class CursorManager {

    private static final int PIXMAP_SIZE = 128;
    private static final int MOUSE_DISPLACEMENT = 64;

    public static final Vector2 MOUSE_POSITION = new Vector2();
    public static final Vector2 CURSOR_POSITION = new Vector2();

    public static Texture currentCursor;

    public static void setCursor() {

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

        if (null != currentCursor) {
            currentCursor.dispose();
        }

        currentCursor = new Texture(pm);

        pm.dispose();
        cursor.dispose();

        Gdx.input.setCursorCatched(true);
    }

    //This tracks the location of the user's (host) mouse
    private static final Vector3 tmpVec3 = new Vector3();

    public static void draw(SpriteBatch batch) {

//        MOUSE_POSITION.add(Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
        MOUSE_POSITION.set(Gdx.input.getX(), Gdx.input.getY());

        MOUSE_POSITION.set(
                MathUtils.clamp(MOUSE_POSITION.x, 0, HadalGame.CONFIG_WIDTH),
                MathUtils.clamp(MOUSE_POSITION.y, 0, HadalGame.CONFIG_HEIGHT));

        tmpVec3.set(MOUSE_POSITION.x, MOUSE_POSITION.y, 0);
        HadalGame.viewportCamera.unproject(tmpVec3);

        batch.begin();

        CURSOR_POSITION.set(tmpVec3.x, tmpVec3.y);
        batch.draw(currentCursor, CURSOR_POSITION.x - MOUSE_DISPLACEMENT, CURSOR_POSITION.y - MOUSE_DISPLACEMENT);

        batch.end();

    }

    public static void dispose() {
        if (null != currentCursor) {
            currentCursor.dispose();
        }
    }

    public static String indexToCursorType() {
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
    public static Color indexToCursorColor() {
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

    public static float indexToCursorScale() {
        return switch (JSONManager.setting.getCursorSize()) {
            case 0 -> 0.5f;
            case 2 -> 1.0f;
            default -> 0.75f;
        };
    }
}
