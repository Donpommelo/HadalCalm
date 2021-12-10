package com.mygdx.hadal.text;

import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.hadal.managers.GameStateManager;

public enum HText {
    STRING_NOT_FOUND("STRING_NOT_FOUND"),

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

    FORCE_RETURN("RESULT_FORCE_RETURN"),
    DAMAGE_DEALT("RESULT_DAMAGE_DEALT"),
    FRIENDLY_FIRE("RESULT_FRIENDLY_FIRE"),
    SELF_DAMAGE("RESULT_SELF_DAMAGE"),
    DAMAGE_RECEIVED("RESULT_DAMAGE_RECEIVED"),
    WEAPON("RESULT_WEAPON"),
    ACTIVE("RESULT_ACTIVE"),

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
    SEND("MSG_SEND"),
    RESULTS_INFO("RESULTS_INFO"),
    PLAYER("SCORE_PLAYER"),
    KDA("SCORE_KDA"),
    SCORE("SCORE_SCORE"),
    WINS("SCORE_WINS"),
    SERVER_SETTINGS("SCORE_SERVER_SETTINGS"),
    SERVER_ARTIFACT_SLOTS("SCORE_ARTIFACT_SLOTS"),
    SERVER_PAUSE("SCORE_PAUSE_ENABLED"),
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
    SPECTATING("SPECTATOR_SPECTATING"),
    SPECTATING_FREECAM("SPECTATOR_SPECTATING_FREECAM"),
    SPECTATING_NA("SPECTATOR_SPECTATING_NA"),
    SPECTATING_LMB("SPECTATOR_LMB"),
    SPECTATING_RMB("SPECTATOR_RMB"),
    JOIN_OPTION("SPECTATOR_JOIN_OPTION"),
    JOIN_OPTION_HOST("SPECTATOR_JOIN_OPTION_HOST"),
    JOIN_CANT("SPECTATOR_JOIN_CANT"),
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
    MODIFIER_VISIBLE_HP("MODE_MODIFIER_VISIBLE_HP"),
    MODIFIER_VISIBLE_HP_UI("MODE_MODIFIER_VISIBLE_HP_UI"),
    MODIFIER_ZERO_GRAV("MODE_MODIFIER_ZERO_GRAV"),
    MODIFIER_ZERO_GRAV_UI("MODE_MODIFIER_ZERO_GRAV_UI"),
    SETTING_BASE_HP("MODE_SETTING_BASE_HP"),
    SETTING_BASE_HP_OPTIONS("MODE_SETTING_BASE_HP_OPTIONS"),
    SETTING_BOT_NUMBER("MODE_SETTING_BOT_NUMBER"),
    SETTING_BOT_NUMBER_OPTIONS("MODE_SETTING_BOT_NUMBER_OPTIONS"),
    SETTING_WEAPON_DROP("MODE_SETTING_WEAPON_DROP"),
    SETTING_RESPAWN("MODE_SETTING_RESPAWN"),
    SETTING_RESPAWN_OPTIONS("MODE_SETTING_RESPAWN_OPTIONS"),
    SETTING_LIVES("MODE_SETTING_LIVES"),
    SETTING_LIVES_OPTIONS("MODE_SETTING_LIVES_OPTIONS"),
    SETTING_LIVES_OUT("MODE_SETTING_LIVES_OUT"),
    SETTING_SCORECAP("MODE_SETTING_SCORECAP"),
    SETTING_SCORECAP_OPTIONS("MODE_SETTING_SCORECAP_OPTIONS"),
    SETTING_SCORECAP_UI("MODE_SETTING_SCORECAP_UI"),
    SETTING_TIMER("MODE_SETTING_TIMER"),
    SETTING_TIMER_OPTIONS("MODE_SETTING_TIMER_OPTIONS"),
    SETTING_TEAM_MODE("MODE_SETTING_TEAM_MODE"),
    SETTING_TEAM_MODE_OPTIONS("MODE_SETTING_TEAM_MODE_OPTIONS"),
    SETTING_TEAM_NUM("MODE_SETTING_TEAM_NUM"),
    SETTING_TEAM_NUM_OPTIONS("MODE_SETTING_TEAM_NUM_OPTIONS"),

    GAME_MODES("HUB_GAME_MODES"),
    MAP_SUGGEST("HUB_MAP_SUGGEST"),
    QUARTERMASTER_COST("HUB_QUARTERMASTER_COST"),
    NAVIGATION_TAGS("HUB_NAVIGATION_TAGS"),
    NAVIGATION_ACTIVATION("HUB_NAVIGATION_ACTIVATION"),
    RELIQUARY_TAGS("HUB_RELIQUARY_TAGS"),

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
    CTF_RETURN("CTF_RETURN"),
    KM_PICKUP("KM_PICKUP"),
    KM_DROPPED("KM_DROPPED"),
    KM_RETURN("KM_RETURN"),

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

    ;

    private final String key;
    private String cachedText;

    HText(String key) {
        this.key = key;
    }

    public String text(String... replace) {
        if (replace.length == 0) {
            if (cachedText == null) {
                JsonValue text = GameStateManager.gameStrings.get(key);
                if (text != null) {
                    cachedText = text.asString();
                } else {
                    cachedText = GameStateManager.gameStrings.get(STRING_NOT_FOUND.key).asString();
                }
            }
            return cachedText;
        } else {
            JsonValue text = GameStateManager.gameStrings.get(key);
            if (text != null) {
                String tempText = text.asString();
                for (int i = 0; i < replace.length; i++) {
                    if (replace[i] != null) {
                        tempText = tempText.replace("<s" + i + ">", replace[i]);
                    }
                }
                return tempText;
            } else {
                return GameStateManager.gameStrings.get(STRING_NOT_FOUND.key).asString();
            }
        }
    }
}
