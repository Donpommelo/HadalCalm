package com.mygdx.hadal.managers;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.hadal.actors.*;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.states.PlayState;

public class UIManager {

    protected final PlayState state;

    //Various play state ui elements. Some are initialized right away while others require the stage to be made first.
    protected UIArtifacts uiArtifact;
    protected UIExtra uiExtra;
    private UIObjective uiObjective;
    private UISpectator uiSpectator;
    protected ChatWheel chatWheel;
    private DialogBox dialogBox;

    private UIPlay uiPlay;
    private UIHub uiHub;
    protected MessageWindow messageWindow;
    protected KillFeed killFeed;
    protected ScoreWindow scoreWindow;

    public UIManager(PlayState state) {
        this.state = state;
        initUIElementsCreate();
    }

    public void initUIElementsCreate() {
        this.uiArtifact = new UIArtifacts(state);
        this.uiExtra = new UIExtra(state);
        this.uiObjective = new UIObjective(state);
        this.uiSpectator = new UISpectator(state);
        this.chatWheel = new ChatWheel(state);
        this.dialogBox = new DialogBox();
    }

    public void initUIElementsShow(Stage stage) {
        //If ui elements have not been created, create them. (upon first showing the state)
        if (uiPlay == null) {
            uiPlay = new UIPlay(state);

            uiHub = new UIHub(state);
            messageWindow = new MessageWindow(state, stage);
            killFeed = new KillFeed(state);
            scoreWindow = new ScoreWindow(state);
        }

        //Add and sync ui elements in case of unpause or new playState
        stage.addActor(uiPlay);
        stage.addActor(uiObjective);
        stage.addActor(uiExtra);
        stage.addActor(dialogBox);
        stage.addActor(uiSpectator);

        chatWheel.addTable(stage);
        uiArtifact.addTable(stage);
    }

    /**
     * This sets the game's boss, filling the boss ui.
     * @param enemy: This is the boss whose hp will be used for the boss hp bar
     */
    public void setBoss(Enemy enemy) {
        uiPlay.setBoss(enemy, enemy.getName());
        uiExtra.setBoss();
    }

    /**
     * This is called when the boss is defeated, clearing its hp bar from the ui.
     * We also have to tell the client to do the same.
     */
    public void clearBoss() {
        uiPlay.clearBoss();
        uiExtra.clearBoss();
    }

    public UIPlay getUiPlay() { return uiPlay; }

    public UIExtra getUiExtra() { return uiExtra; }

    public UIArtifacts getUiArtifact() { return uiArtifact; }

    public UIHub getUiHub() { return uiHub; }

    public UIObjective getUiObjective() { return uiObjective; }

    public UISpectator getUiSpectator() { return uiSpectator; }

    public MessageWindow getMessageWindow() { return messageWindow; }

    public KillFeed getKillFeed() { return killFeed; }

    public ChatWheel getChatWheel() { return chatWheel; }

    public ScoreWindow getScoreWindow() { return scoreWindow; }

    public DialogBox getDialogBox() { return dialogBox; }
}
