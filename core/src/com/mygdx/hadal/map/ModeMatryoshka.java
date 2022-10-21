package com.mygdx.hadal.map;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;

import static com.mygdx.hadal.states.PlayState.DEFAULT_FADE_OUT_SPEED;

/**
 *  This modifier makes players instantly respawn upon death in the same location
 *  All players will have 8 lives and become smaller each time they die
 *  @author Plombino Phumpernickel
 */
public class ModeMatryoshka extends ModeSetting {

    private static final int LIVES_NUM = 8;
    private static final float[] SIZE_SCALE_LIST = {0.4f, 0.6f, 0.8f, 1.0f, 1.2f, 1.4f, 1.6f, 1.8f};

    @Override
    public String loadSettingStart(PlayState state, GameMode mode) {

        //all players start with 8 lives
        for (User user : HadalGame.server.getUsers().values()) {
            user.getScores().setLives(LIVES_NUM);
            user.setScoreUpdated(true);
        }
        return "";
    }

    @Override
    public void modifyNewPlayer(PlayState state, GameMode mode, Loadout newLoadout, Player p, short hitboxFilter) {
        if (!state.isServer()) { return; }
        //when a new player is spawned, their size is set according to the number of lives they have left
        if (null != p.getUser()) {
            int livesLeft = Math.min(p.getUser().getScores().getLives(), SIZE_SCALE_LIST.length) - 1;
            p.setScaleModifier(SIZE_SCALE_LIST[livesLeft]);
            p.setDontMoveCamera(true);
        }
    }

    @Override
    public void processPlayerDeath(PlayState state, GameMode mode, Schmuck perp, Player vic, DamageSource source, DamageTag... tags) {

        //null check in case this is an "extra kill" to give summoner kill credit for a summon
        if (null != vic) {
            //When a player dies, they lose 1 life and respawn instantly
            User user = vic.getUser();
            if (null != user) {
                user.getScores().setLives(user.getScores().getLives() - 1);
                if (user.getScores().getLives() <= 0) {
                    mode.processPlayerLivesOut(state, vic);
                } else {
                    //this ensures that players will respawn in the same location that they died
                    if (DamageSource.MAP_FALL != source) {
                        user.setOverrideSpawn(vic.getPixelPosition());
                        user.respawn(state);
                    } else {
                        //we don't want players to respawn instantly if they die by falling
                        user.beginTransition(state, PlayState.TransitionState.RESPAWN, false, DEFAULT_FADE_OUT_SPEED, state.getRespawnTime());
                    }
                }
            }
        }
    }
}
