package com.mygdx.hadal.map;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.TransitionManager.TransitionState;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.users.Transition;
import com.mygdx.hadal.users.User;

/**
 *  This modifier makes players instantly respawn upon death in the same location
 *  All players will have 8 lives and become smaller each time they die
 *  @author Plombino Phumpernickel
 */
public class ModeMatryoshka extends ModeSetting {

    private static final int LIVES_NUM = 8;
    private static final float[] SIZE_SCALE_LIST = {-0.6f, -0.4f, -0.2f, 0.0f, 0.2f, 0.4f, 0.6f, 0.8f};

    @Override
    public String loadSettingStart(PlayState state, GameMode mode) {

        //all players start with 8 lives
        for (User user : HadalGame.usm.getUsers().values()) {
            user.getScoreManager().setLives(LIVES_NUM);
            user.setScoreUpdated(true);
        }
        return "";
    }

    @Override
    public void modifyNewPlayer(PlayState state, GameMode mode, Loadout newLoadout, Player p, short hitboxFilter) {
        if (!state.isServer()) { return; }
        //when a new player is spawned, their size is set according to the number of lives they have left
        if (p.getUser() != null) {
            int livesLeft = Math.min(p.getUser().getScoreManager().getLives(), SIZE_SCALE_LIST.length) - 1;
            p.changeScaleModifier(SIZE_SCALE_LIST[livesLeft]);
            p.setDontMoveCamera(true);
        }
    }

    @Override
    public void processPlayerDeath(PlayState state, GameMode mode, Schmuck perp, Player vic, DamageSource source, DamageTag... tags) {

        //null check in case this is an "extra kill" to give summoner kill credit for a summon
        if (vic != null) {
            //When a player dies, they lose 1 life and respawn instantly
            User user = vic.getUser();
            if (user != null) {
                user.getScoreManager().setLives(user.getScoreManager().getLives() - 1);
                if (user.getScoreManager().getLives() <= 0) {
                    mode.processPlayerLivesOut(state, vic);
                } else {
                    //this ensures that players will respawn in the same location that they died
                    if (DamageSource.MAP_FALL != source) {
                        user.getTransitionManager().setOverrideSpawn(vic.getPixelPosition());
                        user.getTransitionManager().respawn(state);
                    } else {
                        //we don't want players to respawn instantly if they die by falling
                        user.getTransitionManager().beginTransition(state,
                                new Transition()
                                        .setNextState(TransitionState.RESPAWN)
                                        .setFadeDelay(state.getRespawnTime(vic))
                                        .setOverride(true));
                    }
                }
            }
        }
    }
}
