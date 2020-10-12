package com.mygdx.hadal.server;

import com.mygdx.hadal.schmucks.bodies.MouseTracker;
import com.mygdx.hadal.schmucks.bodies.Player;

public class User {

    private Player player;
    private MouseTracker mouse;
    private SavedPlayerFields scores;
    private SavedPlayerFieldsExtra scoresExtra;
    private boolean scoreUpdated;

    public User(Player player, MouseTracker mouse, SavedPlayerFields scores, SavedPlayerFieldsExtra scoresExtra) {
        this.player = player;
        this.mouse = mouse;
        this.scores = scores;
        this.scoresExtra = scoresExtra;
        scoreUpdated = true;
    }

    public Player getPlayer() { return player; }

    public MouseTracker getMouse() { return mouse; }

    public SavedPlayerFields getScores() { return scores; }

    public SavedPlayerFieldsExtra getScoresExtra() { return scoresExtra; }

    public boolean isScoreUpdated() { return scoreUpdated; }

    public void setPlayer(Player player) { this.player = player; }

    public void setMouse(MouseTracker mouse) { this.mouse = mouse; }

    public void setScores(SavedPlayerFields scores) { this.scores = scores; }

    public void setScoresExtra(SavedPlayerFieldsExtra scoresExtra) { this.scoresExtra = scoresExtra; }

    public void setScoreUpdated(boolean scoreUpdated) { this.scoreUpdated = scoreUpdated; }
}
