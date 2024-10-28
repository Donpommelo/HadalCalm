package com.mygdx.hadal.server.states;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.FadeManager;
import com.mygdx.hadal.managers.PacketManager;
import com.mygdx.hadal.managers.StateManager;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.ResultsState;
import com.mygdx.hadal.users.User;

public class ResultsStateHeadless extends ResultsState {

    /**
     */
    public ResultsStateHeadless(HadalGame app, PlayState ps) {
        super(app, "", ps, null);
    }

    @Override
    public void initializeVisuals() {}

    @Override
    public void show() {}

    @Override
    public void readyPlayer(int playerID) {
        User readyUser = HadalGame.usm.getUsers().get(playerID);
        if (readyUser != null && !readyUser.isSpectator()) {
            readyUser.getScoreManager().setReady(true);
            PacketManager.serverTCPAll(new Packets.ClientReady(users.indexOf(readyUser, false)));
        }

        //When all players are ready, reddy will be true and we return to the hub
        boolean reddy = true;
        for (User user : HadalGame.usm.getUsers().values()) {
            if (!user.isSpectator() && !user.getScoreManager().isReady()) {
                reddy = false;
                break;
            }
        }

        //When the server is ready, we return to hub and tell all clients to do the same.
        if (reddy) {
            allReady();
        }
    }

    @Override
    public void allReady() {
        PacketManager.serverTCP(HadalGame.usm.getHostID(), new Packets.ServerNextMapRequest());
    }

    @Override
    public void exitResultsState(boolean returnToHub, UnlockLevel nextLevel) {
        FadeManager.setRunAfterTransition(() -> {
            StateManager.removeState(ResultsStateHeadless.class, false);
            if (returnToHub) {
                StateManager.states.push(new PlayStateHeadless(app, UnlockLevel.HUB_MULTI, GameMode.HUB, true, ""));
            } else {
                StateManager.states.push(new PlayStateHeadless(app, nextLevel, ps.getMode(), true, ""));
            }
            StateManager.states.peek().show();
        });
        FadeManager.fadeOut();
    }

    @Override
    public void update(float delta) {
        //besides packet processing, headless server does not render anything
        ps.processCommonStateProperties(delta, true);
    }

    @Override
    public void dispose() {}
}
