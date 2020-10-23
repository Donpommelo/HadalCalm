package com.mygdx.hadal.dialog;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.managers.AssetList;

/**
 * A Dialogue is a instance of a fella saying a thing. These are used by the DialogueBox in a PlayStateStage to display conversations to the player 
 * @author Bruguana Brilphos
 */
public class Dialog {

	//this contains info about the dialog read from json
	private final DialogInfo info;
	
	//These are the events that triggered and will be triggered by this dialogue respectively.
	private final EventData radio, trigger;
	
	//These are the sprite frame of the character ust displayed during the dialog and the speed of its animation
	private Animation<TextureRegion> bust;
	private static final float speed = 0.1f;
	
	private final DialogType type;
	
	public Dialog(DialogInfo info, EventData radio, EventData trigger, DialogType type) {
		this.info = info;
		this.radio = radio;
		this.trigger = trigger;
		this.type = type;
		
		if (!info.getSprite().equals("")) {
			characterBusts character = characterBusts.valueOf(info.getSprite());
			bust = new Animation<>(speed, character.getAtlas().findRegions(character.getSprite()));
		}
	}

	public enum characterBusts {
		
		PELICAN_MASKED(HadalGame.assetManager.get(AssetList.PELICANATLAS.toString()), "pelican"),
		;
		
		private final TextureAtlas atlas;
		private final String sprite;
		
		characterBusts(TextureAtlas atlas, String sprite) {
			this.atlas = atlas;
			this.sprite = sprite;
		}

		public TextureAtlas getAtlas() { return atlas; }

		public String getSprite() {	return sprite; }		
	}

	public DialogInfo getInfo() { return info; }
	
	public Animation<TextureRegion> getBust() { return bust; }

	public EventData getRadio() { return radio; }

	public EventData getTrigger() {	return trigger; }
	
	public DialogType getType() { return type; }
}
