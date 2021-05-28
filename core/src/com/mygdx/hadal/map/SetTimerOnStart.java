package com.mygdx.hadal.map;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.save.Setting;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

public class SetTimerOnStart extends ModeSetting {

    private String endText;

    public SetTimerOnStart(String endText) {
        this.endText = endText;
    }

    @Override
    public String loadUIStart(PlayState state) {
        int startTimer = state.getGsm().getSetting().getPVPTimer();
        if (startTimer != 0) {
            return "TIMER";
        }
        return "";
    }

    @Override
    public String loadSettingStart(PlayState state) {
        String gameTimerId = TiledObjectUtil.getPrefabTriggerId();

        int startTimer = state.getGsm().getSetting().getPVPTimer();

        RectangleMapObject game = new RectangleMapObject();
        game.setName("Game");
        game.getProperties().put("sync", "ALL");
        game.getProperties().put("triggeredId", gameTimerId);

        if (startTimer != 0) {
            game.getProperties().put("timer", Setting.indexToTimer(startTimer));
            game.getProperties().put("timerIncr", -1.0f);
        }

        RectangleMapObject end = new RectangleMapObject();
        end.setName("End");
        end.getProperties().put("text", endText);
        end.getProperties().put("triggeredId", "runOnGlobalTimerConclude");

        TiledObjectUtil.parseTiledEvent(state, game);
        TiledObjectUtil.parseTiledEvent(state, end);

        return gameTimerId;
    }
}
