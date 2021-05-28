package com.mygdx.hadal.map;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.save.Setting;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

public class SetTimerOnStart extends ModeSetting {

    @Override
    public String loadSettingStart(PlayState state) {
        String uiTimerId = TiledObjectUtil.getPrefabTriggerId();
        String gameTimerId = TiledObjectUtil.getPrefabTriggerId();

        int startTimer = state.getGsm().getSetting().getPVPTimer();

        RectangleMapObject game = new RectangleMapObject();
        game.setName("Game");
        game.getProperties().put("sync", "ALL");
        game.getProperties().put("triggeredId", gameTimerId);

        RectangleMapObject uiTimer = new RectangleMapObject();
        uiTimer.setName("UI");
        uiTimer.getProperties().put("clear", false);

        if (startTimer != 0) {
            uiTimer.getProperties().put("tags", "TIMER");
            uiTimer.getProperties().put("triggeredId", uiTimerId);

            game.getProperties().put("timer", Setting.indexToTimer(startTimer));
            game.getProperties().put("timerIncr", -1.0f);
        }

        TiledObjectUtil.parseTiledEvent(state, game);
        TiledObjectUtil.parseTiledEvent(state, uiTimer);

        return ",uiTimerId,gameTimerId";
    }
}
