package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.managers.SoundManager;
import com.mygdx.hadal.text.Dialog;
import com.mygdx.hadal.text.DialogInfo;

import static com.mygdx.hadal.managers.SkinManager.*;

/**
 * The Dialogue box is an actor that appears in the stage when a dialogue is initiated. This happens through activating a
 * Radio event. These events add Dialogues to the queue which are cycled through either by a timer or by player input.
 * @author Profinder Parimpernickel
 */
public class DialogBox extends AHadalActor {

	//This is the scale that the text is drawn at.
	private static final float FONT_SCALE = 0.25f;
	private static final float FONT_SCALE_SMALL = 0.25f;

	private static final int BOX_X = 340;
	private static final int BOX_Y = 720;

	//These 2 variables keep track of the dialogue box's final location. These exist to make the box grow/move upon initiating
	private static final int MAX_X = 600;
	private static final int MAX_Y = 150;
	
	private static final int MAX_X_SMALL = 600;
	private static final int MAX_Y_SMALL = 80;
	
	private static final int MAX_TEXT_WIDTH = 440;
	private static final int MAX_TEXT_WIDTH_SMALL = 575;

	private static final int ADVANCE_WIDTH = 50;
	private static final int ADVANCE_HEIGHT = 30;

	private static final int BUST_HEIGHT = 120;
	private static final int BUST_WIDTH = 120;
	private static final int BUST_OFFSET_X = 10;
	private static final int BUST_OFFSET_Y = -130;

	private static final float TEXT_LERP_SPEED = 0.1f;

	//This float is the ratio of the max dimensions of the window before the text appears.
	//For example, the text will appear when the window's x = maxX * this variable
	private static final float TEXT_APPEAR_THRESHOLD = 0.9f;

	//This is a queue of dialogues in the order that they will be displayed.
	private final Queue<Dialog> dialogs = new Queue<>();

	//This counter keeps track of the lifespan of dialogues that have a set duration
	private float durationCount;

	//These 2 variables track the location of the dialogue box
	private float currX, currY;

	//this keeps track of the actor's animation frames
	protected float animCdCount;
	
	public DialogBox() {
		super(BOX_X, BOX_Y);
	}

	//accumulator used to make dialog box movement not tied to framerate
	private float syncAccumulator;
	private static final float syncTime = 1 / 60f;
	@Override
	public void act(float delta) {
		super.act(delta);
		animCdCount += delta;
		syncAccumulator += delta;
		
		while (syncAccumulator >= syncTime) {
			syncAccumulator -= syncTime;

			//Keep track of duration of dialogues and advance when duration completes
			if (0 < durationCount) {
				durationCount -= syncTime;
				
				if (0 >= durationCount) {
					nextDialogue();
				}
			}
			
			//dialogue box lerps towards max size.
			if (0 != dialogs.size) {
				if (dialogs.first().getInfo().isSmall()) {
					currX = currX + (MAX_X_SMALL - currX) * TEXT_LERP_SPEED;
					currY = currY + (MAX_Y_SMALL - currY) * TEXT_LERP_SPEED;
				} else {
					currX = currX + (MAX_X - currX) * TEXT_LERP_SPEED;
					currY = currY + (MAX_Y - currY) * TEXT_LERP_SPEED;
				}
			} else {
				currX = currX + (MAX_X - currX) * TEXT_LERP_SPEED;
				currY = currY + (MAX_Y - currY) * TEXT_LERP_SPEED;
			}
		}
	}
	
	/**
	 * This method is called to add a conversation to the dialogue queue.
	 * @param id: id of the new conversation. Look these up in Dialogue.json in assets/text
	 * @param radio: This is the event that triggered this dialogue if one exists. This field lets us make the dialogue link
	 * to another event upon completion.
	 * @param trigger: This is the event that will be triggered when the dialogue completes.
	 * @param type: type of dialog. (Atm this is used to make system messages a different color)
	 */
	public void addDialogue(String id, EventData radio, EventData trigger, DialogType type) {
		
		if (0 != dialogs.size) {
			if (dialogs.first().getInfo().isOverride()) {
				dialogs.clear();
			}
		}
		
		JsonValue dialog = JSONManager.dialogs.get(id);
		
		if (null != dialog) {
			for (JsonValue d : dialog) {
				addDialogue(JSONManager.JSON.fromJson(DialogInfo.class, d.toJson(OutputType.json)), radio, trigger, type);
			}	
		}
	}
	
	/**
	 * Instead of loading a conversation from the dialog text file, this is used for single dialogs.
	 * This is useful for dynamic text.
	 */
	public void addDialogue(DialogInfo info, EventData radio, EventData trigger, DialogType type) {

		//this does text filtering/formatting for the new text
		info.setDisplayedText();

		//If adding a dialogue to an empty queue, we must manually set its duration and reset window location.
		if (0 == dialogs.size) {
			durationCount = info.getDuration();

			currX = 0;
			currY = 0;

			SoundManager.play(SoundEffect.BLOP);
		}
		dialogs.addLast(new Dialog(info, radio, trigger, type));
	}
	
	/**
	 * This is just like the above method, except for a dynamically created dialog
	 */
	public void addDialogue(String name, String text, String sprite, boolean end, boolean override, boolean small, float dura,
							EventData radio, EventData trigger, DialogType type) {
		addDialogue(new DialogInfo(name, text, sprite, end, override, small, dura), radio, trigger, type);
	}

	/**
	 * This method moves to the next dialogue in the queue.
	 * It is called when the player presses the input that cycles through dialogue.
	 */
	public void nextDialogue() {

		//Do nothing if queue is empty
		if (0 != dialogs.size) {
			
			//If this dialogue is the last in a conversation, trigger the designated event.
			if (dialogs.first().getInfo().isEnd() && dialogs.first().getTrigger() != null && dialogs.first().getRadio() != null) {
				dialogs.first().getTrigger().onActivate(dialogs.first().getRadio(), HadalGame.usm.getOwnPlayer());
			}

			dialogs.removeFirst();
			
			//If there is a next dialogue in line, set its duration and reset window location.
			if (0 != dialogs.size) {
				durationCount = dialogs.first().getInfo().getDuration();
				currX = 0;
				currY = 0;
			}

			SoundManager.play(SoundEffect.BLOP);
		}
	}
	
	@Override
    public void draw(Batch batch, float alpha) {	 
		if (0 != dialogs.size) {
			
			Dialog first = dialogs.first();

			//system messages are red to distinguish them from story dialog
			if (DialogType.SYSTEM.equals(first.getType())) {
				FONT_UI.setColor(Color.RED);
			}
			
			if (first.getInfo().isSmall()) {
				FONT_UI.getData().setScale(FONT_SCALE_SMALL);
				SIMPLE_PATCH.draw(batch, getX(), getY() - currY, currX, currY);
				 
				//Only draw dialogue text if window has reached specified size.
				if (currX >= MAX_X_SMALL * TEXT_APPEAR_THRESHOLD) {
					FONT_UI.draw(batch, first.getInfo().getDisplayedText(), getX() + 20, getY() - 20,
							MAX_TEXT_WIDTH_SMALL, Align.left, true);
					SIMPLE_PATCH.draw(batch, getX() + MAX_X_SMALL - ADVANCE_WIDTH, getY() - MAX_Y_SMALL,
							ADVANCE_WIDTH, ADVANCE_HEIGHT);
					FONT_UI.draw(batch, PlayerAction.DIALOGUE.getKeyText(), getX() + 15 + MAX_X_SMALL - ADVANCE_WIDTH,
						getY() - MAX_Y_SMALL - 8 + ADVANCE_HEIGHT, MAX_TEXT_WIDTH_SMALL, Align.left, true);
				}
			} else {
				FONT_UI.getData().setScale(FONT_SCALE);
				SIMPLE_PATCH.draw(batch, getX(), getY() - currY, currX, currY);
				 
				//Only draw dialogue text if window has reached specified size.
				if (currX >= MAX_X * TEXT_APPEAR_THRESHOLD) {
					FONT_UI.draw(batch, first.getInfo().getDisplayedText(), getX() + 150, getY() - 20,
							MAX_TEXT_WIDTH, Align.left, true);
					SIMPLE_PATCH.draw(batch, getX() + MAX_X - ADVANCE_WIDTH, getY() - MAX_Y,
							ADVANCE_WIDTH, ADVANCE_HEIGHT);
					FONT_UI.draw(batch, PlayerAction.DIALOGUE.getKeyText(), getX() + 15 + MAX_X - ADVANCE_WIDTH,
						getY() - MAX_Y - 8 + ADVANCE_HEIGHT, MAX_TEXT_WIDTH, Align.left, true);
				}
				 
				if (null != first.getBust()) {
					batch.draw(first.getBust().getKeyFrame(animCdCount, true),
						getX() + BUST_OFFSET_X, getY() + BUST_OFFSET_Y, BUST_WIDTH, BUST_HEIGHT);
				}
			}

			//Return color to default values.
			if (DialogType.SYSTEM.equals(first.getType())) {
				FONT_UI.setColor(DEFAULT_TEXT_COLOR);
		    }
		}
    }
	
	public enum DialogType {
		DIALOG,
		SYSTEM
	}
}
