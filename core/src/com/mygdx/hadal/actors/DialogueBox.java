package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.dialogue.Dialogue;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * The Dialogue box is an actor that appears in the staage when a dialogue is initiated. This happens through activating a
 * Radio event. These events add Dialogues to the queue which are cycled through either by a timer or by player input.
 * @author Zachary Tu
 *
 */
public class DialogueBox extends AHadalActor {

	//This is the font that the text is drawn with.
	private BitmapFont font;

	//This is the scale that the text is drawn at.
	private float scale = 0.5f;
	private float scaleSmall = 0.3f;

	//This is a queue of dialogues in the order that they will be displayed.
	private Queue<Dialogue> dialogues;

	//Reference to the gsm. Used to reference gsm fields like the 9patch to draw the window with.
	private GameStateManager gsm;
	
	//This counter keeps track of the lifespan of dialogues that have a set duration
	private float durationCount = 0;
	
	//These 2 variables track the location of the dialogue box
	private float currX, currY;
	
	//These 2 variables keep track of the dialogue box's final location. These exist to make the box grow/move upon initiating
	private static final int maxX = 800;
	private static final int maxY = 150;
	
	private static final int maxXSmall = 700;
	private static final int maxYSmall = 80;
	
	//This float is the ratio of the max dimensions of the window before the text appears.
	//For example, the text will appear when the window's x = maxX * this variable
	private static final float textAppearThreshold = 0.9f;
	
	protected float animCdCount;
	
	public DialogueBox(GameStateManager stateManager, int x, int y) {
		super(x, y);
		
		this.gsm = stateManager;

		dialogues = new Queue<Dialogue>();
		
		font = HadalGame.SYSTEM_FONT_UI;
		
		font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		
		animCdCount = 0;
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		animCdCount += delta;
		
		//Keep track of duration of dialogues
		if (durationCount > 0) {
			durationCount -= delta;
			
			if(durationCount <= 0) {
				nextDialogue();
			}
		}

		//dialogue box lerps towards max size.
		if (dialogues.size != 0) {
			if (dialogues.first().isSmall()) {
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
	
	/**
	 * This method is called to add a conversation to the dialogue queue.
	 * @param id: id of the new conversation. Look these up in Dialogue.json in assets/text
	 * @param radio: This is the event that triggered this dialogue if one exists. This field lets us make the dialogue link
	 * to another event upon completion.
	 * @param trigger: This is the event that will be triggered when the dialogue completes.
	 */
	public void addDialogue(String id, EventData radio, EventData trigger) {
		
		if (dialogues.size != 0) {
			if (dialogues.first().isOverride()) {
				dialogues.clear();
			}
		}
		
		JsonValue dialog = GameStateManager.getDialogs().get(id);
		
		if (dialog != null) {
			for (JsonValue d : dialog) {
				addDialogue(d.getString("Name"), d.getString("Text"), d.getString("Sprite"), d.getBoolean("End", false),
						d.getBoolean("Override", false), d.getBoolean("Small", false), d.getFloat("Duration", 0), radio, trigger);
			}	
		}
	}
	
	/**
	 * Instead of loading a conversation from the dialog text file, this is used for single dialogs.
	 * This is useful for dynamic text.
	 */
	public void addDialogue(String name, String text, String sprite, boolean end, boolean override, boolean small, float dura, EventData radio, EventData trigger) {
		
		//If adding a dialogue to an empty queue, we must manually set its duration and reset window location.
		if (dialogues.size == 0) {
			durationCount = dura;
			
			currX = 0;
			currY = 0;
		}
		
		dialogues.addLast(new Dialogue(name, text, sprite, end, override, small, dura, radio, trigger));
	}
	
	/**
	 * This method moves to the next dialogue in the queue.
	 * It is called when the player presses the input that cycles through dialogue.
	 */
	public void nextDialogue() {

		//Do nothing if queue is empty
		if (dialogues.size != 0) {
			
			//If this dialogue is the last in a conversation, trigger the designated event.
			if (dialogues.first().isEnd() && dialogues.first().getTrigger() != null && dialogues.first().getRadio() != null) {
				dialogues.first().getTrigger().onActivate(dialogues.first().getRadio(), null);
			}
			
			dialogues.removeFirst();
			
			//If there is a next dialogue in line, set its duration and reset window location.
			if (dialogues.size != 0) {
				durationCount = dialogues.first().getDuration();
				currX = 0;
				currY = 0;
			}
		}
	}
	
	
	private Dialogue first;
	@Override
    public void draw(Batch batch, float alpha) {	 
		if (dialogues.size != 0) {
			 
			first = dialogues.first();
			if (first.isSmall()) {
				font.getData().setScale(scaleSmall);
				gsm.getSimplePatch().draw(batch, getX(), getY() - currY, currX, currY);
				 
				//Only draw dialogue text if window has reached specified size.
				if (currX >= maxXSmall * textAppearThreshold) {
					font.draw(batch, first.getName() +": " + first.getText(), getX() + 20, getY() - 20, maxXSmall, -1, true);
				}
			} else {
				font.getData().setScale(scale);
				gsm.getDialogPatch().draw(batch, getX(), getY() - currY, currX, currY);
				 
				//Only draw dialogue text if window has reached specified size.
				if (currX >= maxX * textAppearThreshold) {
			        font.draw(batch, first.getName() +": " + first.getText(), getX() + 150, getY() - 20, maxX - 150, -1, true);
				}
				 
				if (first.getBust() != null) {
					batch.draw((TextureRegion) first.getBust().getKeyFrame(animCdCount, true), 
								getX() + 10, getY() - 130, 
								100 / 2, 100 / 2,
								120, 120, 1, 1, 0);
				}
			}
		}
		 
	     //Return scale and color to default values.
	     font.getData().setScale(1.0f);
    }	
}
