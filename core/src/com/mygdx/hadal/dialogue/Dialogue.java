package com.mygdx.hadal.dialogue;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.managers.AssetList;

/**
 * A Dialogue is a instance of a dude saying a thing. These are used by the DialogueBox in a PlayStateStage to display
 * conversations to the player 
 * @author Zachary Tu
 *
 */
public class Dialogue {

	//These strings are to be displayed in the box
	private String name, text, sprite;
	
	//This indicates whether this dialogue is the end of the conversation it is a part of
	private boolean end;
	
	//These are the events that triggered and will be triggered by this dialogue respectively.
	private EventData radio, trigger;
	
	//This is the duration in seconds that the dialogue will be active.This can be set to 0 for dialogues that need to be actively skipped
	private float duration;
	
	private Animation<TextureRegion> bust;
	
	private static final float speed = 0.05f;
	
	public Dialogue(String name, String text, String sprite, boolean end, float duration, EventData radio, EventData trigger) {
		this.name = name;
		this.text = text;
		this.sprite = sprite;
		this.end = end;
		this.duration = duration;
		this.radio = radio;
		this.trigger = trigger;
		
		if (sprite != "") {
			
			characterBusts character = characterBusts.valueOf(sprite);
			bust = new Animation<TextureRegion>(speed, character.getAtlas().findRegions(character.getSprite()));
		}
	}

	public enum characterBusts {
		
		PELICAN_MASKED((TextureAtlas) HadalGame.assetManager.get(AssetList.PELICANATLAS.toString()), "pelican"),
		
		
		;
		
		private TextureAtlas atlas;
		private String sprite;
		
		characterBusts(TextureAtlas atlas, String sprite) {
			this.atlas = atlas;
			this.sprite = sprite;
		}

		public TextureAtlas getAtlas() {
			return atlas;
		}

		public String getSprite() {
			return sprite;
		}		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public String getSprite() {
		return sprite;
	}

	public void setSprite(String sprite) {
		this.sprite = sprite;
	}
	
	public Animation<TextureRegion> getBust() {
		return bust;
	}

	public void setBust(Animation<TextureRegion> bust) {
		this.bust = bust;
	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public EventData getRadio() {
		return radio;
	}

	public void setRadio(EventData radio) {
		this.radio = radio;
	}

	public EventData getTrigger() {
		return trigger;
	}

	public void setTrigger(EventData trigger) {
		this.trigger = trigger;
	}

}
