package com.mygdx.hadal.event;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	
	public Text(PlayState state, int width, int height, int x, int y, String text) {
		super(state, name, width, height, x, y);
		this.text = text;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this);
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
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
			batch.setProjectionMatrix(state.sprite.combined);
			HadalGame.SYSTEM_FONT_UI.getData().setScale(0.60f);
			if (getConnectedEvent() != null) {
				if (getConnectedEvent().getBody() != null) {
					HadalGame.SYSTEM_FONT_UI.draw(batch, text, getConnectedEvent().getPosition().x * PPM, getConnectedEvent().getBody().getPosition().y * PPM);
				} else {
					HadalGame.SYSTEM_FONT_UI.draw(batch, text, getPosition().x * PPM, getPosition().y * PPM);
				}
			} else {
				HadalGame.SYSTEM_FONT_UI.draw(batch, text, getPosition().x * PPM, getPosition().y * PPM);
			}
		}
	}
}