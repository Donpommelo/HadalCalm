package com.mygdx.hadal.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
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

    private static Texture lastCursorTexture;
    private static Cursor lastCursor;

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

        if (null != lastCursorTexture) {
            lastCursorTexture.dispose();
        }
        if (lastCursor != null) {
            lastCursor.dispose();
        }

        if (JSONManager.setting.isMouseRestrict()) {
            lastCursorTexture = new Texture(pm);
            Gdx.input.setCursorCatched(true);

            lastCursor = null;
        } else {
            Cursor newCursor = Gdx.graphics.newCursor(pm, PIXMAP_SIZE / 2, PIXMAP_SIZE / 2);
            Gdx.graphics.setCursor(newCursor);
            lastCursor = newCursor;

            Gdx.input.setCursorCatched(false);

            lastCursorTexture = null;
        }

        pm.dispose();
        cursor.dispose();
    }

    //This tracks the location of the user's (host) mouse
    private static final Vector3 tmpVec3 = new Vector3();

    public static void draw(SpriteBatch batch) {
        if (JSONManager.setting.isMouseRestrict() && null != lastCursorTexture) {


            MOUSE_POSITION.set(Gdx.input.getX(), Gdx.input.getY());

            MOUSE_POSITION.set(
                    MathUtils.clamp(MOUSE_POSITION.x, 0, Gdx.graphics.getWidth()),
                    MathUtils.clamp(MOUSE_POSITION.y, 0, Gdx.graphics.getHeight()));

            Gdx.input.setCursorPosition((int) MOUSE_POSITION.x, (int) MOUSE_POSITION.y);

            tmpVec3.set(MOUSE_POSITION.x, MOUSE_POSITION.y, 0);
            HadalGame.viewportUI.unproject(tmpVec3);

            batch.setProjectionMatrix(HadalGame.viewportUI.getCamera().combined);
            batch.begin();

            CURSOR_POSITION.set(tmpVec3.x, tmpVec3.y);

            batch.draw(lastCursorTexture, CURSOR_POSITION.x - MOUSE_DISPLACEMENT, CURSOR_POSITION.y - MOUSE_DISPLACEMENT);

            batch.end();
        }
    }

    public static void dispose() {
        if (null != lastCursorTexture) {
            lastCursorTexture.dispose();
        }
        if (lastCursor != null) {
            lastCursor.dispose();
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
