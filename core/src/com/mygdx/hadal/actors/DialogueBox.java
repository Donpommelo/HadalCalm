package com.mygdx.hadal.actors;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
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

	//These objects are used to read dialogues from the text file that store them.
	private JsonReader json;
	private JsonValue base;
	
	//This is a queue of dialogues in the order that they will be displayed.
	private Queue<Dialogue> dialogues;

	//Reference to the gsm. Used to reference gsm fields like the 9patch to draw the window with.
	private GameStateManager gsm;
	
	//This counter keeps track of the lifespan of dialogues that have a set duration
	private float durationCount = 0;
	
	//These 2 variables track the location of the dialogue box
	private float currX, currY;
	
	//These 2 variables keep track of the dialogue box's final location. These exist to make the box grow/move upon initiating
	private static final int maxX = 1000;
	private static final int maxY = 200;
	
	//This float is the ratio of the max dimensions of the window before the text appears.
	//For example, the text will appear when the window's x = maxX * this variable
	private static final float textAppearThreshold = 0.9f;
	
	protected float animCdCount;
	
	public DialogueBox(AssetManager assetManager, GameStateManager stateManager, int x, int y) {
		super(assetManager, x, y);
		
		this.gsm = stateManager;

		json = new JsonReader();
		base = json.parse(Gdx.files.internal("text/Dialogue.json"));
		
		dialogues = new Queue<Dialogue>();
		
		font = HadalGame.SYSTEM_FONT_UI;
		
		font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		
		animCdCount = 0;
		
		updateHitBox();
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
		currX = currX + (maxX - currX) * 0.1f;
		currY = currY + (maxY - currY) * 0.1f;
	}
	
	/**
	 * This method is called to add a conversation to the dialogue queue.
	 * @param id: id of the new conversation. Look these up in Dialogue.json in assets/text
	 * @param radio: This is the event that triggered this dialogue if one exists. This field lets us make the dialogue link
	 * to another event upon completion.
	 * @param trigger: This is the event that will be triggered when the dialogue completes.
	 */
	public void addDialogue(String id, EventData radio, EventData trigger) {
		
		JsonValue dialog = base.get(id);
		
		for (JsonValue d : dialog) {
			
			//If adding a dialogue to an empty queue, we must manually set its duration and reset window location.
			if (dialogues.size == 0) {
				durationCount = d.getFloat("Duration", 0);
				
				currX = 0;
				currY = 0;
			}

			dialogues.addLast(new Dialogue(d.getString("Name"), d.getString("Text"), d.getString("Sprite"), d.getBoolean("End", false),
					d.getFloat("Duration", 0), radio, trigger));
		}		
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
				dialogues.first().getTrigger().onActivate(dialogues.first().getRadio());
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
	
	@Override
    public void draw(Batch batch, float alpha) {
		 font.getData().setScale(scale);
		 
		 if (dialogues.size != 0) {
			 
			 Dialogue first = dialogues.first();
			 gsm.getDialogPatch().draw(batch, getX(), getY() - 200 + maxY - currY, currX, currY);
			 
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
		 
         //Return scale and color to default values.
         font.getData().setScale(1.0f);
    }	
}
