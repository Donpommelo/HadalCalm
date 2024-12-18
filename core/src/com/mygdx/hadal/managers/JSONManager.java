package com.mygdx.hadal.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.save.*;
import com.mygdx.hadal.save.Record;

/**
 * JSONManager loads and saves json files for things like settings and records.
 * It also handles things like game/ui text which are also stored as json files
 */
public class JSONManager {

    //these version numbers are used to decide whether we need to create a brand new save due to structrual changes
    public static final int SAVE_VERSION = 3;
    public static final int LAST_COMPATIBLE_SAVE_VERSION = 1;

    //Json reader here. Use this instead of creating new ones elsewhere.
    public static final Json JSON = new Json();
    public static final JsonReader READER = new JsonReader();

    //This is a stored list of all the dialogs/death/misc messages in the game, read from json file.
    public static JsonValue dialogs, deathMessages, shops, randomText, uiStrings, gameStrings, tips;

    public static ShopInfo weaponInfo, artifactInfo, magicInfo;

    //These are the player's saved field. These store player info.
    public static Record record;
    public static Setting setting;
    public static SavedLoadout loadout;
    public static SavedOutfits outfits;

    //This contains the settings that are shared with clients (or shared from server if we are the client)
    public static SharedSetting sharedSetting, hostSetting;

    public static void initJSON() {

        //we set output settings to json so intellij editor doesn't get pissy about double quotes
        JSON.setOutputType(JsonWriter.OutputType.json);

        //load text strings. Do this before loading saves
        dialogs = READER.parse(Gdx.files.internal("text/Dialogue.json"));
        deathMessages = READER.parse(Gdx.files.internal("text/DeathMessages.json"));
        randomText = READER.parse(Gdx.files.internal("text/RandomText.json"));
        uiStrings = READER.parse(Gdx.files.internal("text/UIStrings.json"));
        gameStrings = READER.parse(Gdx.files.internal("text/GameStrings.json"));
        tips = READER.parse(Gdx.files.internal("text/Tips.json"));
        shops = READER.parse(Gdx.files.internal("save/Shops.json"));

        weaponInfo = JSON.fromJson(ShopInfo.class, shops.get("weapons1").toJson(JsonWriter.OutputType.json));
        artifactInfo = JSON.fromJson(ShopInfo.class, shops.get("artifacts1").toJson(JsonWriter.OutputType.json));
        magicInfo = JSON.fromJson(ShopInfo.class, shops.get("actives1").toJson(JsonWriter.OutputType.json));

        //Load data from saves: hotkeys, records, loadout, settings and unlocks
        PlayerAction.retrieveKeys();
        record = Record.retrieveRecord();

        //reset save file if the version is incompatible with the current version
        if (record.getSaveVersion() < JSONManager.LAST_COMPATIBLE_SAVE_VERSION) {
            Record.createNewRecord();
            SavedLoadout.createAndSaveNewLoadout();
            Setting.createNewSetting();
            SavedOutfits.createNewOutfits();
        }

        loadout = SavedLoadout.retrieveLoadout();
        setting = Setting.retrieveSetting();
        outfits = SavedOutfits.retrieveOutfits();

        sharedSetting = setting.generateSharedSetting();
        hostSetting = setting.generateSharedSetting();
    }

    /**
     * Set display/cursor.
     * Graphics settings are separated from main function so headless server can skip this part
     */
    public static void initJSONDisplay(HadalGame app) {
        //set the game's display to match the player's saved settings
        setting.setDisplay(app, null, true);
        CursorManager.setCursor();
    }
}
