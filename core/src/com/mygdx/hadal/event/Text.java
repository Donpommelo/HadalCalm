package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.TextFilterUtil;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * A Text just displays some words
 * <p>
 * Triggered Behavior: N/A.
 * Triggering Behavior: N/A.
 * <p>
 * Fields:
 * text: htext enum for string to be displayed
 * scale: float of the size of the text
 * @author Whorheart Wanicotti
 */
public class Text extends Event {

	private final String text;
	private final float scale;
	
	public Text(PlayState state, Vector2 startPos, Vector2 size, String text, float scale) {
		super(state, startPos , size);
		this.text = TextFilterUtil.filterText(UIText.getByName(text).text());
		this.scale = scale;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this);
		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, (short) 0, (short) 0)
				.setBodyType(BodyDef.BodyType.KinematicBody)
				.addToWorld(world);
	}
	
	@Override
	public void render(SpriteBatch batch, Vector2 entityLocation) {
		batch.setProjectionMatrix(state.getCamera().combined);
		HadalGame.FONT_UI.getData().setScale(scale);
		HadalGame.FONT_UI.draw(batch, text, entityLocation.x, entityLocation.y);
	}
}
