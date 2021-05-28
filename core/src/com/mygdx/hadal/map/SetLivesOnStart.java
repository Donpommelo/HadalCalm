package com.mygdx.hadal.map;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

public class SetLivesOnStart extends ModeSetting {

    @Override
    public String loadUIStart(PlayState state) {
        int startLives = state.getGsm().getSetting().getLives();
        if (startLives != 0) {
            return "LIVES";
        }
        return "";
    }

    @Override
    public String loadSettingStart(PlayState state) {
        String gameLivesId = TiledObjectUtil.getPrefabTriggerId();

        int startLives = state.getGsm().getSetting().getLives();

        if (startLives != 0) {
            RectangleMapObject game = new RectangleMapObject();
            game.setName("Game");
            game.getProperties().put("sync", "ALL");
            game.getProperties().put("triggeredId", gameLivesId);

            game.getProperties().put("lives", startLives - 1);
            TiledObjectUtil.parseTiledEvent(state, game);

        } else {
            state.setUnlimitedLife(true);
        }
        return gameLivesId;
    }
}
