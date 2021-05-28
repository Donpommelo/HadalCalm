package com.mygdx.hadal.map;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

public class SetLivesOnStart extends ModeSetting {

    @Override
    public String loadSettingStart(PlayState state) {
        String uiLivesId = TiledObjectUtil.getPrefabTriggerId();
        String gameLivesId = TiledObjectUtil.getPrefabTriggerId();

        int startLives = state.getGsm().getSetting().getLives();

        RectangleMapObject game = new RectangleMapObject();
        game.setName("Game");
        game.getProperties().put("sync", "ALL");
        game.getProperties().put("triggeredId", gameLivesId);

        if (startLives != 0) {
            RectangleMapObject ui = new RectangleMapObject();
            ui.setName("UI");
            ui.getProperties().put("clear", false);
            ui.getProperties().put("tags", "LIVES");
            ui.getProperties().put("triggeredId", uiLivesId);

            game.getProperties().put("lives", startLives - 1);

            TiledObjectUtil.parseTiledEvent(state, ui);
        } else {
            state.setUnlimitedLife(true);
        }

        TiledObjectUtil.parseTiledEvent(state, game);

        return ",uiLivesId,gameLivesId";
    }
}
