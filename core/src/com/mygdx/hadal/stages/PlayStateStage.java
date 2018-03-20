package com.mygdx.hadal.stages;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogueBox;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

public class PlayStateStage extends Stage {

	private DialogueBox dialogue;
	
	public PlayStateStage(PlayState state) {
		
		dialogue = new DialogueBox(HadalGame.assetManager, state.getGsm(), 0, HadalGame.CONFIG_HEIGHT);
		addActor(dialogue);
	}
	
	public void addDialogue(String id, EventData radio, EventData trigger) {
		dialogue.addDialogue(id, radio, trigger);
	}
	
	public void nextDialogue() {
		dialogue.nextDialogue();
	}
}
