package com.mygdx.hadal.stages;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogueBox;
import com.mygdx.hadal.actors.HpBar;
import com.mygdx.hadal.states.PlayState;

public class PlayStateStage extends Stage {

	DialogueBox dialogue;
	
	public PlayStateStage(PlayState state) {
		
		dialogue = new DialogueBox(HadalGame.assetManager, 400, HadalGame.CONFIG_HEIGHT - 100);
		addActor(new HpBar(HadalGame.assetManager, state, state.getPlayer()));
		addActor(dialogue);
	}
	
	public void addDialogue(String id) {
		dialogue.addDialogue(id);
	}
	
	public void nextDialogue() {
		dialogue.nextDialogue();
	}
}
