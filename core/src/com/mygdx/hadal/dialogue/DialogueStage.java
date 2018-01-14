package com.mygdx.hadal.dialogue;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogueBox;
import com.mygdx.hadal.states.PlayState;

public class DialogueStage extends Stage {

	DialogueBox dialogue;
	
	public DialogueStage(PlayState state) {
		
		dialogue = new DialogueBox(HadalGame.assetManager, 400, HadalGame.CONFIG_HEIGHT - 100);

		addActor(dialogue);
	}
	
	public void addDialogue(String id) {
		dialogue.addDialogue(id);
	}
	
	public void nextDialogue() {
		dialogue.nextDialogue();
	}
}
