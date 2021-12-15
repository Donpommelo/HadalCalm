package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.UserDataType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;

/**
 * A Wall is a solid wall parsed from polylines from tiled.
 * 
 * Triggered Behavior: N/A.
 * Triggering Behavior: N/A.
 * 
 * Fields:
 * @author Lelfupton Loporon
 */
public class Wall extends Event {

	private final ChainShape shape;

	public Wall(PlayState state, ChainShape shape) {
		super(state);
		this.shape = shape;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this, UserDataType.WALL);
		BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        
        body = state.getWorld().createBody(bdef);
        body.createFixture(shape, 1.0f);
        Filter filter = new Filter();
		filter.categoryBits = Constants.BIT_WALL;
		filter.maskBits = (short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE);
        body.getFixtureList().get(0).setFilterData(filter);
        body.getFixtureList().get(0).setUserData(eventData);

		Vector2[] vertices = new Vector2[shape.getVertexCount()];
        for (int i = 0; i < vertices.length; i++) {
        	vertices[i] = new Vector2();
        	shape.getVertex(i, vertices[i]);
        }
        shape.dispose();
	}
}
