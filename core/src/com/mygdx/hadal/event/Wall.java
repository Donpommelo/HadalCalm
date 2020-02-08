package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;

/**
 * A Semipermeable wall is a wall that some objects can pass through
 * 
 * Triggered Behavior: N/A.
 * Triggering Behavior: N/A.
 * 
 * Fields:
 * player: Boolean that describes whether players can pass through this. Default: true
 * hbox: Boolean that describes whether hitboxes can pass through this. Optional. Default: true
 * event: Boolean that describes whether this sensor touches events. Optional. Default: true
 * enemy: Boolean that describes whether this sensor touches enemies. Optional. Default: true
 * @author Zachary Tu
 *
 */
public class Wall extends Event {

	private ChainShape shape;
	private Vector2[] vertices;
	
	public Wall(PlayState state, ChainShape shape) {
		super(state);
		this.shape = shape;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this, UserDataTypes.WALL);
		BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        
        body = state.getWorld().createBody(bdef);
        body.createFixture(shape, 1.0f);
        Filter filter = new Filter();
		filter.categoryBits = (short) (Constants.BIT_WALL);
		filter.maskBits = (short) (Constants.BIT_SENSOR | Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE);
        body.getFixtureList().get(0).setFilterData(filter);
        body.getFixtureList().get(0).setUserData(eventData);
        
        vertices = new Vector2[shape.getVertexCount()];
        for (int i = 0; i < vertices.length; i++) {
        	vertices[i] = new Vector2();
        	shape.getVertex(i, vertices[i]);
        }
        
        shape.dispose();
	}

	public Vector2[] getVertices() { return vertices; }
}
