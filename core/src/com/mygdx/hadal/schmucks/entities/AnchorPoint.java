package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.UserDataType;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * Anchor points are used to connect to "static" entities that can't actually be static or else they would not register collisions.
 * @author Nesevelt Nursaneymaker
 */
public class AnchorPoint extends HadalEntity {
	
	public AnchorPoint(PlayState state) {
		super(state, new Vector2(), new Vector2(1, 1));
	}

	@Override
	public void create() {
		this.hadalData = new HadalData(UserDataType.EVENT, this);
		this.body = BodyBuilder.createBox(world, startPos, size, 0, 1, 0, true, true,
				Constants.BIT_SENSOR, (short) 0, (short) 0, true, hadalData);
	}

	@Override
	public void controller(float delta) {}

	@Override
	public void render(SpriteBatch batch) {}
	
	//we want this entity to not send any sync packets to the client because it never changes.
	@Override
	public void onServerSync() {}
}
