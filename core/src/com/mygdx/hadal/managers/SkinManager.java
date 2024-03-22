package com.mygdx.hadal.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.mygdx.hadal.HadalGame;

public class SkinManager {

    //skin for ui windows as well as other patches and atlases. Why are these kept here? Dunno.
    public static Skin SKIN;
    public static NinePatchDrawable DIALOG_PATCH, SIMPLE_PATCH, BOSS_GAUGE_PATCH, BOSS_GAUGE_GREY_PATCH,
            BOSS_GAUGE_RED_PATCH, BOSS_GAUGE_CATCHUP_PATCH;

    public static final Color DEFAULT_TEXT_COLOR = Color.WHITE;

    //FONT_UI is used for most ui, FONT_UI_ALT is used for things like message window and kill messages
    //FONT_SPRITE labels sprites in the world. Its scale is always 1.0f and should be considered placeholder
    public static BitmapFont FONT_UI, FONT_UI_SKIN, FONT_UI_ALT, FONT_SPRITE;

    /**
     * This loads several assets like atlases, skins and 9patches, particle pool.
     * This is called by init state after the atlases have been loaded.
     */
    public static void loadAssets() {
        FONT_UI = new BitmapFont(Gdx.files.internal(AssetList.FIXEDSYS_FONT.toString()), false);
        FONT_UI.getData().markupEnabled = true;
        FONT_UI_SKIN = new BitmapFont(Gdx.files.internal(AssetList.FIXEDSYS_FONT.toString()), false);
        FONT_UI_SKIN.getData().markupEnabled = true;
        FONT_UI_ALT = new BitmapFont(Gdx.files.internal(AssetList.VERDANA_FONT.toString()), false);
        FONT_UI_ALT.getData().markupEnabled = true;
        FONT_SPRITE = new BitmapFont();

        FONT_UI.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        FONT_SPRITE.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        SKIN = new Skin();
        SKIN.add("default-font", FONT_UI_SKIN);
        SKIN.getFont("default-font").getData().setScale(0.25f, 0.25f);
        SKIN.addRegions(HadalGame.assetManager.get(AssetList.UISKIN_ATL.toString()));
        SKIN.load(Gdx.files.internal("ui/uiskin.json"));

        DIALOG_PATCH = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.UIPATCH_ATL.toString())).createPatch("UI_box_dialogue"));
        SIMPLE_PATCH = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.UIPATCH_ATL.toString())).createPatch("UI_box_simple"));

        BOSS_GAUGE_PATCH = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.BOSSGAUGE_ATL.toString())).createPatch("boss_gauge"));
        BOSS_GAUGE_GREY_PATCH = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.BOSSGAUGE_ATL.toString())).createPatch("boss_gauge_grey"));
        BOSS_GAUGE_RED_PATCH = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.BOSSGAUGE_ATL.toString())).createPatch("boss_gauge_red"));
        BOSS_GAUGE_CATCHUP_PATCH = new NinePatchDrawable(((TextureAtlas) HadalGame.assetManager.get(AssetList.BOSSGAUGE_ATL
                .toString())).createPatch("boss_gauge_dark_red"));

        //this lets us not declare every attribute of shaders.
        ShaderProgram.pedantic = false;
    }

    public static void dispose() {
        if (null != FONT_UI) {
            FONT_UI.dispose();
        }
        if (null != FONT_UI_ALT) {
            FONT_UI_ALT.dispose();
        }
        if (null != FONT_SPRITE) {
            FONT_SPRITE.dispose();
        }
        if (null != SKIN) {
            SKIN.dispose();
        }
    }
}
