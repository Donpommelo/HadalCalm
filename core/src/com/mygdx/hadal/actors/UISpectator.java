package com.mygdx.hadal.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;

import java.util.ArrayList;

public class UISpectator extends AHadalActor {

    protected PlayState state;
    private final BitmapFont font;

    private static final int mainX = 0;
    private static final int mainY = 0;

    private static final int textX = 15;
    private static final int titleY = 100;
    private static final int instructions1Y = 75;
    private static final int instructions2Y = 50;
    private static final int joinY = 25;

    private static final int windowWidth = 300;
    private static final int windowHeight = 125;

    private static final float fontScaleMedium = 0.3f;

    public UISpectator(PlayState state) {
        this.state = state;
        this.font = HadalGame.SYSTEM_FONT_UI;
    }

    @Override
    public void draw(Batch batch, float alpha) {

        if (!state.isSpectatorMode()) { return; }

        GameStateManager.getSimplePatch().draw(batch, mainX, mainY, windowWidth, windowHeight);

        font.getData().setScale(fontScaleMedium);

        if (freeCam) {
            font.draw(batch, "SPECTATING: FREE CAM", textX, titleY);
        } else {
            if (spectatorTarget != null) {
                font.draw(batch, "SPECTATING: " + spectatorTarget.getName(), textX, titleY);
            } else {
                font.draw(batch, "SPECTATING: N/A", textX, titleY);
            }
        }
        font.draw(batch, "LMB: DRAG FREE CAM", textX, instructions1Y);
        font.draw(batch, "RMB: CYCLE SPECTATE TARGET", textX, instructions2Y);

        if (state.isHub()) {
            font.draw(batch, "JOIN: " + PlayerAction.PAUSE.getKeyText() + " -> JOIN", textX, joinY);
        } else {
            font.draw(batch, "JOIN NEXT ROUND IN HUB", textX, joinY);
        }
    }

    private boolean mouseHeld;
    private final Vector2 lastMousePosition = new Vector2();
    private final Vector2 mousePosition = new Vector2();
    private final static float dragMultiplier = 2.5f;
    private boolean freeCam = true;
    private int targetId;
    private Player spectatorTarget;
    public void spectatorDragCamera(Vector2 target) {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            mousePosition.set(Gdx.input.getX(), -Gdx.input.getY());
            if (mouseHeld) {
                target.add(lastMousePosition.sub(mousePosition).scl(dragMultiplier));
            }
            mouseHeld = true;
            freeCam = true;
            lastMousePosition.set(mousePosition);
        } else {
            mouseHeld = false;
        }

        if (!freeCam && spectatorTarget != null) {
            if (spectatorTarget.getBody() != null && spectatorTarget.isAlive()) {
                target.set(spectatorTarget.getPixelPosition());
            }
        }
    }

    private final ArrayList<User> users = new ArrayList<>();
    public void findValidSpectatorTarget() {

        User currentUser;

        users.clear();
        if (state.isServer()) {
            users.addAll(HadalGame.server.getUsers().values());
            currentUser = HadalGame.server.getUsers().get(targetId);
        } else {
            users.addAll(HadalGame.client.getUsers().values());
            currentUser = HadalGame.client.getUsers().get(targetId);
        }

        boolean foundTarget = false;

        if (currentUser == null) {
            foundTarget = loopThroughUsers(0);
        } else {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).equals(currentUser)) {

                    if (freeCam) {
                        foundTarget = loopThroughUsers(i);
                    } else {
                        foundTarget = loopThroughUsers(i + 1);
                    }
                    break;
                }
            }
        }
        freeCam = !foundTarget;
    }

    private boolean loopThroughUsers(int startIndex) {

        for (int i = 0; i < users.size(); i++) {
            User nextUser = users.get((i + startIndex) % users.size());

            if (nextUser.getPlayer().getBody() != null && nextUser.getPlayer().isAlive()) {
                spectatorTarget = nextUser.getPlayer();
                targetId = spectatorTarget.getConnID();
                return true;
            }
        }
        return false;
    }
}
