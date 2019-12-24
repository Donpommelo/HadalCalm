package com.mygdx.hadal.stages;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogueBox;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

/**
 * This is a special stage specifically for play states.
 * atm, its only used to process dialogues
 * @author Zachary Tu
 *
 */
public class PlayStateStage extends Stage {

	//Play States have a dialog box
	private DialogueBox dialogue;
	
	public PlayStateStage(PlayState state) {
		
		dialogue = new DialogueBox(state.getGsm(), 0, HadalGame.CONFIG_HEIGHT);
		addActor(dialogue);
	}
	
	/**
	 * This adds a dialog from saved dialog json to the dialog box
	 * @param id: conversation id
	 * @param radio: The event that triggers this
	 * @param trigger: The event thta this triggers. (if any)
	 */
	public void addDialogue(String id, EventData radio, EventData trigger) {
		dialogue.addDialogue(id, radio, trigger);
	}
	
	/**
	 * This is just like the above method, except for a dynamically created dialog
	 */
	public void addDialogue(String name, String text, String sprite, boolean end, boolean override, boolean small, float dura,
			EventData radio, EventData trigger) {
		dialogue.addDialogue(name, text, sprite, end, override, small, dura, radio, trigger);
	}
	
	/**
	 * This advances the dialog box to the next dialog
	 */
	public void nextDialogue() {
		dialogue.nextDialogue();
	}
}
