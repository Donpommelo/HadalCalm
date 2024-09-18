package com.mygdx.hadal.managers;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.hadal.actors.*;
import com.mygdx.hadal.states.PlayState;

public class UIManager {

    private final PlayState state;

    //Various play state ui elements. Some are initialized right away while others require the stage to be made first.
    private final UIArtifacts uiArtifact;
    private final UIExtra uiExtra;
    private final UIObjective uiObjective;
    private final UISpectator uiSpectator;
    private final ChatWheel chatWheel;
    private final DialogBox dialogBox;

    private UIPlay uiPlay;
    private UIHub uiHub;
    private MessageWindow messageWindow;
    private KillFeed killFeed;
    private ScoreWindow scoreWindow;

    public UIManager(PlayState state) {
        this.state = state;

        this.uiArtifact = new UIArtifacts(state);
        this.uiExtra = new UIExtra(state);
        this.uiObjective = new UIObjective(state);
        this.uiSpectator = new UISpectator(state);
        this.chatWheel = new ChatWheel(state);
        this.dialogBox = new DialogBox(state);
    }

    public void initUIElements(Stage stage) {
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
