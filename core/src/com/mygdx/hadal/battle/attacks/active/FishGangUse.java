package com.mygdx.hadal.battle.attacks.active;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttacker;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.enemies.Scissorfish;
import com.mygdx.hadal.schmucks.entities.enemies.Spittlefish;
import com.mygdx.hadal.schmucks.entities.enemies.Torpedofish;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Summoned;
import com.mygdx.hadal.statuses.Temporary;

public class FishGangUse extends SyncedAttacker {

    public static final int NUM_FISH = 5;
    private static final float FISH_LIFESPAN = 20.0f;

    @Override
    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
        if (user instanceof Player player) {
            for (int i = 0; i < NUM_FISH; i++) {
                float randFloat = MathUtils.random();
                if (randFloat < 0.34f) {
                    new Scissorfish(state, startPosition, 0.0f, player.getHitboxFilter()) {

                        @Override
                        public void create() {
                            super.create();
                            getBodyData().addStatus(new Temporary(state, FISH_LIFESPAN, getBodyData(), getBodyData(), FISH_LIFESPAN));
                            getBodyData().addStatus(new Summoned(state, getBodyData(), player));
                        }
                    };

                } else if (randFloat < 0.68f) {
                    new Spittlefish(state, startPosition, 0.0f, player.getHitboxFilter()) {

                        @Override
                        public void create() {
                            super.create();
                            getBodyData().addStatus(new Temporary(state, FISH_LIFESPAN, getBodyData(), getBodyData(), FISH_LIFESPAN));
                            getBodyData().addStatus(new Summoned(state, getBodyData(), player));
                        }
                    };
                } else {
                    new Torpedofish(state, startPosition, 0.0f, player.getHitboxFilter()) {

                        @Override
                        public void create() {
                            super.create();
                            getBodyData().addStatus(new Temporary(state, FISH_LIFESPAN, getBodyData(), getBodyData(), FISH_LIFESPAN));
                            getBodyData().addStatus(new Summoned(state, getBodyData(), player));
                        }
                    };
                }
            }
        }
    }
}