package com.mygdx.hadal.actors;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.dialogue.Dialogue;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.managers.GameStateManager;

public class DialogueBox extends AHadalActor {

	private BitmapFont font;
//	private Color color;
	
	private float scale = 0.5f;

	JsonReader json;
	JsonValue base;
	
	private Queue<Dialogue> dialogues;

	private GameStateManager gsm;
	
	public DialogueBox(AssetManager assetManager, GameStateManager stateManager, int x, int y) {
		super(assetManager, x, y);
		
		this.gsm = stateManager;

		json = new JsonReader();
		base = json.parse(Gdx.files.internal("text/Dialogue.json"));
		
		dialogues = new Queue<Dialogue>();
		
		font = HadalGame.SYSTEM_FONT_UI;
//		color = HadalGame.DEFAULT_TEXT_COLOR;
		font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		updateHitBox();
	}
	
	public void addDialogue(String id, EventData radio, EventData trigger) {
		
		JsonValue dialog = base.get(id);
		
		for (JsonValue d : dialog) {
			dialogues.addLast(new Dialogue(d.getString("Name"), d.getString("Text"), d.getBoolean("End", false), radio, trigger));
		}		
	}
	
	public void nextDialogue() {
		if (dialogues.size != 0) {
			
			if (dialogues.first().isEnd() && dialogues.first().getTrigger() != null && dialogues.first().getRadio() != null) {
				dialogues.first().getTrigger().onActivate(dialogues.first().getRadio());
			}
			
			dialogues.removeFirst();
		}
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		 font.getData().setScale(scale);
		 font.setColor(Color.WHITE);
		 
		 if (dialogues.size != 0) {
			 gsm.getPatch().draw(batch, getX(), getY() - 200, 1000, 200);
	         font.draw(batch, dialogues.first().getName() +": " + dialogues.first().getText(), getX() + 20, getY() - 20);
		 }
		 
         //Return scale and color to default values.
         font.getData().setScale(1.0f);
         font.setColor(HadalGame.DEFAULT_TEXT_COLOR);
    }	
}
