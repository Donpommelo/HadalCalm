package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * A Text just displays some words
 * 
 * Triggered Behavior: N/A.
 * Triggering Behavior: N/A.
 * 
 * Fields:
 * text: string to be displayed
 * @author Zachary Tu
 *
 */
public class Text extends Event {

	private String text;
	private float scale;
	
	public Text(PlayState state, Vector2 startPos, Vector2 size, String text, float scale) {
		super(state, startPos , size);
		this.text = text;
		this.scale = scale;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this);
		this.body = BodyBuilder.createBox(world, startPos, size, 0, 0, 0, 0, false, false, Constants.BIT_SENSOR, (short) 0, (short) 0, true, eventData);
		body.setType(BodyType.KinematicBody);
	}
	
	@Override
	public void render(SpriteBatch batch) {
		batch.setProjectionMatrix(state.camera.combined);
		HadalGame.SYSTEM_FONT_UI.getData().setScale(scale);
		HadalGame.SYSTEM_FONT_UI.draw(batch, text, getPixelPosition().x, getPixelPosition().y);
	}
	
	@Override
	public void loadDefaultProperties() {
		setSyncType(eventSyncTypes.ALL);
	}
}
