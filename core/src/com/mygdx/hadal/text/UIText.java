package com.mygdx.hadal.text;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * A UIText is a string that shows up anywhere in the game. These are managed in 1 place to make it easier to edit
 * Eventually, this will also be used for language setting changing
 * @author Spulbbury Stincilart
 */
public enum UIText {
    STRING_NOT_FOUND("STRING_NOT_FOUND"),
    QUESTION("QUESTION"),
    NOTHING("NOTHING"),

    INFO_ABOUT("INFO_ABOUT"),
    INFO_CREDITS_CODE("INFO_CREDITS_CODE"),
    INFO_CREDITS_ART("INFO_CREDITS_ART"),
    INFO_CREDITS_MUSIC("INFO_CREDITS_MUSIC"),
    INFO_CREDITS_SOUND("INFO_CREDITS_SOUND"),
    INFO_TIPS("INFO_TIPS"),
    INFO_MISC("INFO_MISC"),
    INFO_HELP("INFO_HELP"),
    INFO_START("INFO_START"),

    YOUR_NAME("TITLE_YOUR_NAME"),
    GEN_NAME("TITLE_GEN_NAME"),
    MULTIPLAYER("TITLE_MULTIPLAYER"),
    SINGLEPLAYER("TITLE_SINGLEPLAYER"),
    VERSION("TITLE_VERSION"),
    PLEASE_ENTER_NAME("TITLE_NOTIF_NAME"),
    RAND_NAME_GEN("TITLE_NOTIF_NAME_GEN"),

    SOUND_ROOM("EXTRA_SOUND"),
    ABOUT("EXTRA_ABOUT"),
    TIPS("EXTRA_TIPS"),
    CREDITS("EXTRA_CREDITS"),
    PLAY("SOUND_PLAY"),
    PAUSE("SOUND_PAUSE"),
    STOP("SOUND_STOP"),
    NEXT("SOUND_NEXT"),
    CONTINUE("SOUND_CONTINUE"),
    CONTINUE_DESC("SOUND_CONTINUE_DESC"),
    NOW_PLAYING("SOUND_NOW_PLAYING"),
    NOW_PLAYING_DEFAULT("SOUND_NOW_PLAYING_DEFAULT"),
    LOOP_OPTIONS("SOUND_LOOP_OPTIONS"),

    DISPLAY("SETTING_DISPLAY"),
    CONTROLS("SETTING_CONTROLS"),
    AUDIO("SETTING_AUDIO"),
    SERVER("SETTING_SERVER"),
    RESET("SETTING_RESET"),
    RESOLUTION("DISPLAY_RESOLUTION"),
    RESOLUTION_OPTIONS("DISPLAY_RESOLUTION_OPTIONS"),
    FRAMERATE("DISPLAY_FRAMERATE"),
    FRAMERATE_OPTIONS("DISPLAY_FRAMERATE_OPTIONS"),
    CURSOR_TYPE("DISPLAY_CURSOR_TYPE"),
    CURSOR_TYPE_OPTIONS("DISPLAY_CURSOR_TYPE_OPTIONS"),
    CURSOR_SIZE("DISPLAY_CURSOR_SIZE"),
    CURSOR_SIZE_OPTIONS("DISPLAY_CURSOR_SIZE_OPTIONS"),
    CURSOR_COLOR("DISPLAY_CURSOR_COLOR"),
    CURSOR_COLOR_OPTIONS("DISPLAY_CURSOR_COLOR_OPTIONS"),
    FULLSCREEN("DISPLAY_FULLSCREEN"),
    VSYNC("DISPLAY_VSYNC"),
    ALT_TAB("DISPLAY_ALT_TAB"),
    DEBUG_OUTLINES("DISPLAY_DEBUG_OUTLINES"),
    VISIBLE_NAMES("DISPLAY_NAMES"),
    VISIBLE_HP("DISPLAY_HP"),
    CAMERA_AIM("DISPLAY_CAMERA_AIM"),
    CAMERA_AIM_DESC("DISPLAY_CAMERA_AIM_DESC"),
    SCREEN_SHAKE("DISPLAY_SCREEN_SHAKE"),
    SOUND_VOLUME("AUDIO_SOUND_VOLUME"),
    MUSIC_VOLUME("AUDIO_MUSIC_VOLUME"),
    MASTER_VOLUME("AUDIO_MASTER_VOLUME"),
    HITSOUND("AUDIO_HITSOUND"),
    HITSOUND_VOLUME("AUDIO_HITSOUND_VOLUME"),
    HITSOUND_OPTIONS("AUDIO_HITSOUND_OPTIONS"),
    SERVER_SIZE("SERVER_MAX_SIZE"),
    PORT_NUMBER("SERVER_PORT"),
    SERVER_PASSWORD("SERVER_PASSWORD"),
    ARTIFACT_SLOTS("SERVER_ARTIFACT_SLOTS"),
    NAME_ALLITERATION("MISC_NAME_ALLITERATION"),
    CONSOLE_ENABLED("MISC_CONSOLE_ENABLED"),
    VERBOSE_DEATH_MESSAGE("MISC_VERBOSE_DEATH_MESSAGE"),
    MULTIPLAYER_PAUSE("MISC_MULTIPLAYER_PAUSE"),
    EXPORT_CHAT("MISC_EXPORT_CHAT"),
    UPNP("MISC_UPNP"),
    HIDE_HUD("MISC_HIDE_HUD"),

    JOIN("LOBBY_JOIN"),
    LOBBIES("LOBBY_LOBBIES"),
    REFRESH("LOBBY_REFRESH"),
    ENTER_IP("LOBBY_ENTER_IP"),
    CONNECT_IP("LOBBY_CONNECT_IP"),
    HOST("LOBBY_HOST"),
    SERVER_NAME("LOBBY_SERVER_NAME"),
    SERVER_NAME_DEFAULT("LOBBY_SERVER_NAME_DEFAULT"),
    SERVER_SETTING_CHANGE("LOBBY_SERVER_SETTING_CHANGE"),
    SERVER_CREATE("LOBBY_SERVER_CREATE"),
    SEARCHING_SERVER("LOBBY_NOTIF_SEARCHING_SERVER"),
    SEARCHING_MM("LOBBY_NOTIF_SEARCHING_MM"),
    CONNECTED("LOBBY_NOTIF_CONNECTED"),
    JOINING("LOBBY_NOTIF_JOINING"),
    HOSTED("LOBBY_NOTIF_HOSTED"),
    NO_LOBBIES("LOBBY_NOTIF_NO_LOBBIES"),
    LOBBIES_RETRIEVED("LOBBY_NOTIF_LOBBIES_RETRIEVED"),
    CONNECTION_FAILED("LOBBY_NOTIF_CONNECTION_FAILED"),
    CONNECTION_MM_FAILED("LOBBY_NOTIF_MM_CONNECTION_FAILED"),
    PASSWORD("LOBBY_PASSWORD"),
    PASSWORD_ENTER("LOBBY_PASSWORD_ENTER"),

    PAUSE_BY("PAUSE_BY"),
    PAUSE_NOT("PAUSE_NOT"),
    RESUME("PAUSE_RESUME"),
    SPECTATE("PAUSE_SPECTATE"),
    REJOIN("PAUSE_REJOIN"),
    UNPAUSED("PAUSE_NOTIF_UNPAUSE"),

    READY("RESULT_READY"),
    FORCE_READY("RESULT_FORCE_READY"),
    DAMAGE_DEALT("RESULT_DAMAGE_DEALT"),
    FRIENDLY_FIRE("RESULT_FRIENDLY_FIRE"),
    SELF_DAMAGE("RESULT_SELF_DAMAGE"),
    DAMAGE_RECEIVED("RESULT_DAMAGE_RECEIVED"),
    RESULT_WEAPON("RESULT_WEAPON"),
    RESULT_ACTIVE("RESULT_ACTIVE"),

    PLAYER_WINS("PLAY_WINS"),
    GAME("PLAY_GAME"),
    SPECTATOR_ENTER("PLAY_SPECTATOR_ENTER"),
    SPECTATOR_EXIT("PLAY_SPECTATOR_EXIT"),
    SERVER_FULL("PLAY_SERVER_FULL"),

    BACK("MENU_BACK"),
    ENTER_NAME("MENU_ENTER_NAME"),
    EXIT("MENU_EXIT"),
    EXTRA("MENU_EXTRA"),
    MISC("MENU_MISC"),
    NA("MENU_NA"),
    RETURN("MENU_RETURN"),
    RETURN_HUB("MENU_RETURN_HUB"),
    SETTINGS("MENU_SETTINGS"),

    YOU_HAVE_SLAIN("KF_YOU_HAVE_SLAIN"),
    RESPAWN_IN("KF_RESPAWN"),
    AWAITING_REVIVE("KF_AWAITING_REVIVE"),
    KILLED_BY("KF_KILLED_BY"),
    DEATH_CAUSE("KF_DEATH_CAUSE"),
    DEATH_CAUSE_YOURSELF("KF_DEATH_CAUSE_YOURSELF"),
    DEATH_SELF("DEATH_MESSAGE_SELF"),
    DEATH_KILL("DEATH_MESSAGE_KILL"),
    DEATH_ENEMY("DEATH_MESSAGE_ENEMY"),
    DEATH_MISC("DEATH_MESSAGE_MISC"),
    SEND("MSG_SEND"),
    RESULTS_INFO("RESULTS_INFO"),
    PLAYER("SCORE_PLAYER"),
    KDA("SCORE_KDA"),
    SCORE("SCORE_SCORE"),
    WINS("SCORE_WINS"),
    ARTIFACTS("SCORE_ARTIFACTS"),
    SERVER_ARTIFACT_SLOTS("SCORE_ARTIFACT_SLOTS"),
    SERVER_CAPACITY("SCORE_SERVER_CAPACITY"),
    MUTE("SCORE_MUTE"),
    UNMUTE("SCORE_UNMUTE"),
    BAN("SCORE_BAN"),
    TIMER_REMAINING("TIMER_REMAINING"),
    SEARCH("HUB_SEARCH"),
    SEARCH_OPTIONS("HUB_SEARCH_OPTIONS"),
    FILTER_TAGS("HUB_FILTER_TAGS"),
    FILTER_COST("HUB_FILTER_COST"),
    FILTER_COST_OPTIONS("HUB_FILTER_COST_OPTIONS"),
    CURRENT_ARTIFACTS("HUB_CURRENT_ARTIFACTS"),
    UNEQUIP("HUB_UNEQUIP"),
    ARTIFACT_INFO("HUB_ARTIFACT_INFO"),
    SLOTS_REMAINING("HUB_SLOTS_REMAINING"),
    GAME_MODES("HUB_GAME_MODES"),
    COSMETIC_SLOTS("HUB_COSMETIC_SLOTS"),
    MAP_SUGGEST("HUB_MAP_SUGGEST"),
    QUARTERMASTER_COST("HUB_QUARTERMASTER_COST"),
    NAVIGATION_ACTIVATION("HUB_NAVIGATION_ACTIVATION"),
    RELIQUARY_TAGS("HUB_RELIQUARY_TAGS"),
    OUTFIT_SAVE("HUB_OUTFIT_SAVE"),
    OUTFIT_NAME("HUB_OUTFIT_NAME"),
    OUTFIT_DELETE("HUB_OUTFIT_DELETE"),
    TAB_MAPS("HUB_TAB_MAPS"),
    TAB_SETTINGS("HUB_TAB_SETTINGS"),
    TAB_MODIFIERS("HUB_TAB_MODIFIERS"),

    SPECTATING("SPECTATOR_SPECTATING"),
    SPECTATING_FREECAM("SPECTATOR_SPECTATING_FREECAM"),
    SPECTATING_NA("SPECTATOR_SPECTATING_NA"),
    SPECTATING_LMB("SPECTATOR_LMB"),
    SPECTATING_RMB("SPECTATOR_RMB"),
    JOIN_OPTION("SPECTATOR_JOIN_OPTION"),
    JOIN_OPTION_HOST("SPECTATOR_JOIN_OPTION_HOST"),
    JOIN_CANT("SPECTATOR_JOIN_CANT"),
    TOGGLE("SPECTATOR_TOGGLE"),
    UI_SCRAP("UI_SCRAP"),
    UI_LIVES("UI_LIVES"),
    UI_SCORE("UI_SCORE"),
    UI_HISCORE("UI_HISCORE"),
    UI_TIMER("UI_TIMER"),
    UI_GUNGAME("UI_GUNGAME"),
    UI_VICTORY("UI_VICTORY"),

    LEVEL("MODE_LEVEL"),
    ELIMINATED("MODE_ELIMINATED"),
    MODIFIER("MODE_MODIFIER"),
    MODIFIER_UNCHECK("MODE_MODIFIER_UNCHECK"),
    MODIFIER_BOUNCE("MODE_MODIFIER_BOUNCE"),
    MODIFIER_BOUNCE_UI("MODE_MODIFIER_BOUNCE_UI"),
    MODIFIER_FAST("MODE_MODIFIER_FAST"),
    MODIFIER_FAST_UI("MODE_MODIFIER_FAST_UI"),
    MODIFIER_SLOW("MODE_MODIFIER_SLOW"),
    MODIFIER_SLOW_UI("MODE_MODIFIER_SLOW_UI"),
    MODIFIER_INVIS("MODE_MODIFIER_INVIS"),
    MODIFIER_INVIS_UI("MODE_MODIFIER_INVIS_UI"),
    MODIFIER_SMALL("MODE_MODIFIER_SMALL"),
    MODIFIER_SMALL_UI("MODE_MODIFIER_SMALL_UI"),
    MODIFIER_LARGE("MODE_MODIFIER_LARGE"),
    MODIFIER_LARGE_UI("MODE_MODIFIER_LARGE_UI"),
    MODIFIER_SLIDE("MODE_MODIFIER_SLIDE"),
    MODIFIER_SLIDE_UI("MODE_MODIFIER_SLIDE_UI"),
    MODIFIER_MEDIEVAL("MODE_MODIFIER_MEDIEVAL"),
    MODIFIER_MEDIEVAL_UI("MODE_MODIFIER_MEDIEVAL_UI"),
    MODIFIER_MEDIEVAL_DESC("MODE_MODIFIER_MEDIEVAL_DESC"),
    MODIFIER_VISIBLE_HP("MODE_MODIFIER_VISIBLE_HP"),
    MODIFIER_VISIBLE_HP_UI("MODE_MODIFIER_VISIBLE_HP_UI"),
    MODIFIER_ZERO_GRAV("MODE_MODIFIER_ZERO_GRAV"),
    MODIFIER_ZERO_GRAV_UI("MODE_MODIFIER_ZERO_GRAV_UI"),
    SETTING_BASE_HP("MODE_SETTING_BASE_HP"),
    SETTING_BASE_HP_OPTIONS("MODE_SETTING_BASE_HP_OPTIONS"),
    SETTING_BOT_NUMBER("MODE_SETTING_BOT_NUMBER"),
    SETTING_BOT_NUMBER_OPTIONS("MODE_SETTING_BOT_NUMBER_OPTIONS"),
    SETTING_BOTS_NUMBER_DESC("MODE_SETTING_BOT_NUMBER_DESC"),
    SETTING_BOT_DIFFICULTY("MODE_SETTING_BOT_DIFFICULTY"),
    SETTING_BOT_DIFFICULTY_OPTIONS("MODE_SETTING_BOT_DIFFICULTY_OPTIONS"),
    SETTING_WEAPON_DROP("MODE_SETTING_WEAPON_DROP"),
    SETTING_RESPAWN("MODE_SETTING_RESPAWN"),
    SETTING_RESPAWN_OPTIONS("MODE_SETTING_RESPAWN_OPTIONS"),
    SETTING_LIVES("MODE_SETTING_LIVES"),
    SETTING_LIVES_OPTIONS("MODE_SETTING_LIVES_OPTIONS"),
    SETTING_LIVES_OUT("MODE_SETTING_LIVES_OUT"),
    SETTING_SCORECAP("MODE_SETTING_SCORECAP"),
    SETTING_SCORECAP_OPTIONS("MODE_SETTING_SCORECAP_OPTIONS"),
    SETTING_SCORECAP_DESC("MODE_SETTING_SCORECAP_DESC"),
    SETTING_SCORECAP_TEAM_DESC("MODE_SETTING_SCORECAP_TEAM_DESC"),
    SETTING_SCORECAP_UI("MODE_SETTING_SCORECAP_UI"),
    SETTING_TIMER("MODE_SETTING_TIMER"),
    SETTING_TIMER_OPTIONS("MODE_SETTING_TIMER_OPTIONS"),
    SETTING_TEAM_MODE("MODE_SETTING_TEAM_MODE"),
    SETTING_TEAM_MODE_OPTIONS("MODE_SETTING_TEAM_MODE_OPTIONS"),
    SETTING_TEAM_MODE_DESC("MODE_SETTING_TEAM_MODE_DESC"),
    SETTING_TEAM_NUM("MODE_SETTING_TEAM_NUM"),
    SETTING_TEAM_NUM_OPTIONS("MODE_SETTING_TEAM_NUM_OPTIONS"),
    SETTING_TEAM_NUM_DESC("MODE_SETTING_TEAM_NUM_DESC"),
    SETTING_OUTFIT("MODE_SETTING_OUTFIT"),
    SETTING_OUTFIT_DEFAULT("MODE_SETTING_OUTFIT_DEFAULT"),
    SETTING_OUTFIT_NOTIF("MODE_SETTING_OUTFIT_NOTIF"),
    SETTING_OUTFIT_DESC("MODE_SETTING_OUTFIT_DESC"),
    SETTING_LOADOUT_MODE("MODE_SETTING_LOADOUT_MODE"),
    SETTING_LOADOUT_MODE_OPTIONS("MODE_SETTING_LOADOUT_MODE_OPTIONS"),
    SETTING_LOADOUT_MODE_DESC("MODE_SETTING_LOADOUT_MODE_DESC"),

    UNLOCK_ACTIVE("UNLOCK_ACTIVE"),
    UNLOCK_WEAPON("UNLOCK_WEAPON"),
    UNLOCK_ARTIFACT("UNLOCK_ARTIFACT"),
    UNLOCK_CHARACTER("UNLOCK_CHARACTER"),
    UNLOCK_LEVEL("UNLOCK_LEVEL"),

    FOOTBALL_GOAL("FOOTBALL_GOAL"),
    FOOTBALL_GOAL_OWN("FOOTBALL_GOAL_OWN"),
    CTF_CAPTURE("CTF_CAPTURE"),
    CTF_CAPTURE_FAIL("CTF_CAPTURE_FAIL"),
    CTF_PICKUP("CTF_PICKUP"),
    CTF_DEFENDED("CTF_DEFENDED"),
    CTF_DROPPED("CTF_DROPPED"),
    CTF_RETURNED("CTF_RETURNED"),
    CTF_RETURN("CTF_RETURN"),
    KM_PICKUP("KM_PICKUP"),
    KM_DROPPED("KM_DROPPED"),
    KM_RETURN("KM_RETURN"),
    GRAVE_REVIVE("GRAVE_REVIVE"),
    GRAVE_REVIVER("GRAVE_REVIVER"),
    PLAYERS_ALIVE("PLAYERS_ALIVE"),
    CANDY_RETRIEVED("CANDY_RETRIEVED"),

    CLIENT_CONNECTED("SERVER_CLIENT_CONNECTED"),
    CLIENT_JOINED("SERVER_CLIENT_JOINED"),
    CLIENT_DISCONNECTED("SERVER_CLIENT_DISCONNECTED"),
    DISCONNECTED("SERVER_DISCONNECTED"),
    INCOMPATIBLE("SERVER_INCOMPATIBLE"),
    INCORRECT_PASSWORD("SERVER_INCORRECT_PASSWORD"),
    SERVER_UNPAUSED("SERVER_UNPAUSED"),
    KICKED("SERVER_KICKED"),
    PORT_FAIL("SERVER_PORT_FAIL"),

    CHARGING("PLAYER_CHARGING"),
    RELOADING("PLAYER_RELOADING"),
    OUT_OF_AMMO("PLAYER_OUT_OF_AMMO"),
    HEAT("WEAPON_HEAT"),
    OVERHEAT("WEAPON_OVERHEAT"),
    HIT_IT("WEAPON_HIT_IT"),
    SHAKE_MOUSE("WEAPON_SHAKE_MOUSE"),
    FIRE("WEAPON_FIRE"),
    CHARGE("WEAPON_CHARGE"),
    SUPERCHARGE("WEAPON_SUPERCHARGE"),

    WALK_RIGHT("CONTROLS_WALK_RIGHT"),
    WALK_LEFT("CONTROLS_WALK_LEFT"),
    JUMP("CONTROLS_JUMP"),
    FASTFALL("CONTROLS_FASTFALL"),
    SHOOT("CONTROLS_SHOOT"),
    BOOST("CONTROLS_BOOST"),
    INTERACT("CONTROLS_INTERACT"),
    USE_MAGIC("CONTROLS_MAGIC"),
    RELOAD("CONTROLS_RELOAD"),
    DIALOG("CONTROLS_DIALOG"),
    SWITCH_TO_LAST("CONTROLS_SWITCH_TO_LAST"),
    SWITCH_TO_1("CONTROLS_SWITCH_TO_1"),
    SWITCH_TO_2("CONTROLS_SWITCH_TO_2"),
    SWITCH_TO_3("CONTROLS_SWITCH_TO_3"),
    SWITCH_TO_4("CONTROLS_SWITCH_TO_4"),
    WEAPON_CYCLE_UP("CONTROLS_WEAPON_CYCLE_UP"),
    WEAPON_CYCLE_DOWN("CONTROLS_WEAPON_CYCLE_DOWN"),
    CHAT("CONTROLS_CHAT"),
    SCORE_WINDOW("CONTROLS_SCORE_WINDOW"),
    PING("CONTROLS_PING"),
    CHAT_WHEEL("CONTROLS_CHAT_WHEEL"),
    MOUSE_LEFT("KEY_MOUSE_LEFT"),
    MOUSE_RIGHT("KEY_MOUSE_RIGHT"),
    MOUSE_MIDDLE("KEY_MOUSE_MIDDLE"),
    M_WHEEL_UP("KEY_MOUSE_WHEEL_UP"),
    M_WHEEL_DOWN("KEY_MOUSE_WHEEL_DOWN"),

    ARMORY("HUB_EVENT_ARMORY"),
    RELIQUARY("HUB_EVENT_RELIQUARY"),
    ARCANERY("HUB_EVENT_ARCANERY"),
    NAVIGATIONS("HUB_EVENT_NAVIGATIONS"),
    DORMITORY("HUB_EVENT_DORMITORY"),
    PAINTER("HUB_EVENT_PAINTER"),
    HABERDASHER("HUB_EVENT_HABERDASHER"),
    COMMUNICATIONS("HUB_EVENT_COMMUNICATIONS"),
    OUTFITTER("HUB_EVENT_OUTFITTER"),
    WALLPAPER("HUB_EVENT_WALLPAPER"),
    QUARTERMASTER("HUB_EVENT_QUARTERMASTER"),

    TEXT_TRAINING("HUB_TEXT_TRAINING"),
    TEXT_SPAWN_ENEMY("HUB_TEXT_SPAWN_ENEMY"),
    TEXT_BONUS_LEVELS("HUB_TEXT_BONUS_LEVELS"),
    TEXT_ARENA("HUB_TEXT_ARENA"),
    TEXT_TEAM_COLORS("HUB_TEXT_TEAM_COLORS"),
    TEXT_GUNS("HUB_TEXT_GUNS"),
    TEXT_ARTIFACTS("HUB_TEXT_ARTIFACTS"),
    TEXT_MAGIC("HUB_TEXT_MAGIC"),
    COMMUNICATIONS_OPTIONS("HUB_COMMUNICATIONS_OPTIONS"),
    TEXT_POEM("HUB_TEXT_POEM"),
    POEM_OPTIONS("HUB_POEM_OPTIONS"),

    TEXT_HUB("SANDBOX_TEXT_HUB"),
    TEXT_EVENTS("SANDBOX_TEXT_EVENTS"),
    TEXT_ENEMIES("SANDBOX_TEXT_ENEMIES"),
    ENEMIES_OPTIONS("SANDBOX_ENEMIES_OPTIONS"),

    TEXT_MOVEMENT("TUTORIAL_TEXT_MOVEMENT"),
    TEXT_INTERACT("TUTORIAL_TEXT_INTERACT"),
    TEXT_JUMP("TUTORIAL_TEXT_JUMP"),
    TEXT_BOOST("TUTORIAL_TEXT_BOOST"),
    TEXT_CROUCH("TUTORIAL_TEXT_CROUCH"),
    TEXT_ATTACK("TUTORIAL_TEXT_ATTACK"),
    TEXT_WEAPON_SWITCH("TUTORIAL_TEXT_WEAPON_SWITCH"),
    TEXT_RELOAD("TUTORIAL_TEXT_RELOAD"),

    TEAM("TEAM_TEAM"),
    ENVIRONMENT("ENVIRONMENT"),
    WEAPON("WEAPON"),
    MAGIC("MAGIC"),
    ARTIFACT("ARTIFACT"),
    MONSTER("MONSTER"),

    SLOT_HEAD("COSMETIC_SLOT_HEAD"),
    SLOT_EYE("COSMETIC_SLOT_EYE"),
    SLOT_HAT1("COSMETIC_SLOT_HAT1"),
    SLOT_HAT2("COSMETIC_SLOT_HAT2"),
    SLOT_MOUTH1("COSMETIC_SLOT_MOUTH1"),
    SLOT_MOUTH2("COSMETIC_SLOT_MOUTH2"),
    SLOT_NOSE("COSMETIC_SLOT_NOSE"),
    SLOT_DECAL_HEAD("COSMETIC_SLOT_DECAL_HEAD"),
    SLOT_DECAL_BODY("COSMETIC_SLOT_DECAL_BODY"),

    SIZE("MAP_SIZE"),
    TINY("MAP_TINY"),
    SMALL("MAP_SMALL"),
    MEDIUM("MAP_MEDIUM"),
    LARGE("MAP_LARGE"),
    EXTRA_LARGE("MAP_EXTRA_LARGE"),
    GIANT("MAP_GIANT"),

    ;

    //key used to find the text in UIStrings.json file
    private final String key;

    //cached text if string has been read from json before.
    private String cachedText;

    UIText(String key) {
        this.key = key;
    }

    /**
     * @param replace: list of strings to replace tags in string from json
     * @return String to be displayed in game
     */
    public String text(String... replace) {

        //no replacements means a static text. Read from file and cache
        if (0 == replace.length) {
            if (null == cachedText) {
                JsonValue text = GameStateManager.uiStrings.get(key);
                if (null != text) {
                    cachedText = text.asString();
                } else {
                    cachedText = GameStateManager.uiStrings.get(STRING_NOT_FOUND.key).asString();
                }
            }
            return cachedText;
        } else {

            //iterate through replace tags and replace with input strings
            JsonValue text = GameStateManager.uiStrings.get(key);
            if (null != text) {
                String tempText = text.asString();
                for (int i = 0; i < replace.length; i++) {
                    if (null != replace[i]) {
                        tempText = tempText.replace("<s" + i + ">", replace[i]);
                    }
                }
                return tempText;
            } else {
                return GameStateManager.uiStrings.get(STRING_NOT_FOUND.key).asString();
            }
        }
    }

    private static final ObjectMap<String, UIText> TEXT_BY_NAME = new ObjectMap<>();
    static {
        for (UIText u : UIText.values()) {
            TEXT_BY_NAME.put(u.toString(), u);
        }
    }
    public static UIText getByName(String s) {
        return TEXT_BY_NAME.get(s, STRING_NOT_FOUND);
    }
}
