package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.dialog.Dialog;
import com.mygdx.hadal.dialog.DialogInfo;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.states.PlayState;

/**
 * The Dialogue box is an actor that appears in the stage when a dialogue is initiated. This happens through activating a
 * Radio event. These events add Dialogues to the queue which are cycled through either by a timer or by player input.
 * @author Profinder Parimpernickel
 */
public class DialogBox extends AHadalActor {

	//This is the font that the text is drawn with.
	private final BitmapFont font;

	//This is the scale that the text is drawn at.
	private static final float scale = 0.25f;
	private static final float scaleSmall = 0.25f;

	//This is a queue of dialogues in the order that they will be displayed.
	private final Queue<Dialog> dialogs;

	//Reference to the gsm. Used to reference gsm fields
	private final PlayState ps;
	
	//This counter keeps track of the lifespan of dialogues that have a set duration
	private float durationCount = 0;
	
	//These 2 variables track the location of the dialogue box
	private float currX, currY;

	private static final int boxX = 340;
	private static final int boxY = 720;

	//These 2 variables keep track of the dialogue box's final location. These exist to make the box grow/move upon initiating
	private static final int maxX = 600;
	private static final int maxY = 150;
	
	private static final int maxXSmall = 600;
	private static final int maxYSmall = 80;
	
	private static final int maxTextWidth = 440;
	private static final int maxTextWidthSmall = 575;

	private static final int advanceWidth = 50;
	private static final int advanceHeight = 30;

	private static final int bustHeight = 120;
	private static final int bustWidth = 120;
	private static final int bustOffsetX = 10;
	private static final int bustOffsetY = -130;

	//This float is the ratio of the max dimensions of the window before the text appears.
	//For example, the text will appear when the window's x = maxX * this variable
	private static final float textAppearThreshold = 0.9f;
	
	//this keeps track of the actor's animation frames
	protected float animCdCount;
	
	public DialogBox(PlayState ps) {
		super(boxX, boxY);
		this.ps = ps;

		dialogs = new Queue<>();
		
		font = HadalGame.SYSTEM_FONT_UI;
		
		animCdCount = 0;
	}

	//accumulator used to make dialog box movement not tied to framerate
	private float syncAccumulator = 0.0f;
	private static final float syncTime = 1 / 60f;
	@Override
	public void act(float delta) {
		super.act(delta);
		animCdCount += delta;
		syncAccumulator += delta;
		
		while (syncAccumulator >= syncTime) {
			syncAccumulator -= syncTime;

			//Keep track of duration of dialogues and advance when duration completes
			if (durationCount > 0) {
				durationCount -= syncTime;
				
				if (durationCount <= 0) {
					nextDialogue();
				}
			}
			
			//dialogue box lerps towards max size.
			if (dialogs.size != 0) {
				if (dialogs.first().getInfo().isSmall()) {
					currX = currX + (maxXSmall - currX) * 0.1f;
					currY = currY + (maxYSmall - currY) * 0.1f;
				} else {
					currX = currX + (maxX - currX) * 0.1f;
					currY = currY + (maxY - currY) * 0.1f;
				}
			} else {
				currX = currX + (maxX - currX) * 0.1f;
				currY = currY + (maxY - currY) * 0.1f;
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
		
		if (dialogs.size != 0) {
			if (dialogs.first().getInfo().isOverride()) {
				dialogs.clear();
			}
		}
		
		JsonValue dialog = GameStateManager.dialogs.get(id);
		
		if (dialog != null) {
			for (JsonValue d : dialog) {
				addDialogue(GameStateManager.json.fromJson(DialogInfo.class, d.toJson(OutputType.json)), radio, trigger, type);
			}	
		}
	}
	
	/**
	 * Instead of loading a conversation from the dialog text file, this is used for single dialogs.
	 * This is useful for dynamic text.
	 */
	public void addDialogue(DialogInfo info, EventData radio, EventData trigger, DialogType type) {

		//this does text filtering/formatting for the new text
		info.setDisplayedText(ps.getGsm());

		//If adding a dialogue to an empty queue, we must manually set its duration and reset window location.
		if (dialogs.size == 0) {
			durationCount = info.getDuration();

			currX = 0;
			currY = 0;

			SoundEffect.BLOP.play(ps.getGsm(), 1.0f, false);
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
		if (dialogs.size != 0) {
			
			//If this dialogue is the last in a conversation, trigger the designated event.
			if (dialogs.first().getInfo().isEnd() && dialogs.first().getTrigger() != null && dialogs.first().getRadio() != null) {
				dialogs.first().getTrigger().onActivate(dialogs.first().getRadio(), ps.getPlayer());
			}

			dialogs.removeFirst();
			
			//If there is a next dialogue in line, set its duration and reset window location.
			if (dialogs.size != 0) {
				durationCount = dialogs.first().getInfo().getDuration();
				currX = 0;
				currY = 0;
			}
			
			SoundEffect.BLOP.play(ps.getGsm(), 1.0f, false);
		}
	}
	
	@Override
    public void draw(Batch batch, float alpha) {	 
		if (dialogs.size != 0) {
			
			Dialog first = dialogs.first();

			//system messages are red to distinguish them from story dialog
			if (first.getType().equals(DialogType.SYSTEM)) {
				font.setColor(Color.RED);
			}
			
			if (first.getInfo().isSmall()) {
				font.getData().setScale(scaleSmall);
				GameStateManager.getSimplePatch().draw(batch, getX(), getY() - currY, currX, currY);
				 
				//Only draw dialogue text if window has reached specified size.
				if (currX >= maxXSmall * textAppearThreshold) {
					font.draw(batch, first.getInfo().getDisplayedText(), getX() + 20, getY() - 20, maxTextWidthSmall, Align.left, true);
					GameStateManager.getSimplePatch().draw(batch, getX() + maxXSmall - advanceWidth, getY() - maxYSmall, advanceWidth, advanceHeight);
					font.draw(batch, PlayerAction.DIALOGUE.getKeyText(), getX() + 15 + maxXSmall - advanceWidth, getY() - maxYSmall - 8 + advanceHeight, maxTextWidthSmall, Align.left, true);
				}
			} else {
				font.getData().setScale(scale);
				GameStateManager.getDialogPatch().draw(batch, getX(), getY() - currY, currX, currY);
				 
				//Only draw dialogue text if window has reached specified size.
				if (currX >= maxX * textAppearThreshold) {
					font.draw(batch, first.getInfo().getDisplayedText(), getX() + 150, getY() - 20, maxTextWidth, Align.left, true);
					GameStateManager.getSimplePatch().draw(batch, getX() + maxX - advanceWidth, getY() - maxY, advanceWidth, advanceHeight);
					font.draw(batch, PlayerAction.DIALOGUE.getKeyText(), getX() + 15 + maxX - advanceWidth, getY() - maxY - 8 + advanceHeight, maxTextWidth, Align.left, true);
				}
				 
				if (first.getBust() != null) {
					batch.draw(first.getBust().getKeyFrame(animCdCount, true),
								getX() + bustOffsetX, getY() + bustOffsetY, bustWidth, bustHeight);
				}
			}
			
			if (first.getType().equals(DialogType.SYSTEM)) {
				font.setColor(HadalGame.DEFAULT_TEXT_COLOR);
		    }
		}
		 
	     //Return scale and color to default values.
	     font.getData().setScale(1.0f);
    }
	
	public enum DialogType {
		DIALOG,
		KILL,
		SYSTEM
	}
}
