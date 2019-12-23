package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * An info flag displays text when the player walks over it. This is a temporary means of information until more sophisticated ui is done
 *
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * 
 * Fields:
 * N/A
 * 
 * @author Zachary Tu
 *
 */
public class Text extends Event {

	private static final String name = "Text";

	private String text;
	
	private boolean open;
	
	public Text(PlayState state, Vector2 startPos, Vector2 size, String text) {
		super(state, name, startPos, size);
		this.text = text;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this);
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),	(short) 0, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		if (open && eventData.getSchmucks().isEmpty()) {
			open = false;
		}
		if (!open && !eventData.getSchmucks().isEmpty()) {
			open = true;
		}
	}
	
	@Override
	public void render(SpriteBatch batch) {
		
		if (open) {
			HadalGame.SYSTEM_FONT_UI.getData().setScale(0.60f);
			if (getConnectedEvent() != null) {
				if (getConnectedEvent().getBody() != null) {
					HadalGame.SYSTEM_FONT_UI.draw(batch, text, getConnectedEvent().getPixelPosition().x, getConnectedEvent().getPixelPosition().y);
				} else {
					HadalGame.SYSTEM_FONT_UI.draw(batch, text, getPixelPosition().x, getPixelPosition().y);
				}
			} else {
				HadalGame.SYSTEM_FONT_UI.draw(batch, text, getPixelPosition().x, getPixelPosition().y);
			}
		}
	}
}