package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.FeetData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;

/**
 * A Wall is a solid wall parsed from polylines from tiled. This is a version that the player can drop through
 * 
 * Triggered Behavior: N/A.
 * Triggering Behavior: N/A.
 * 
 * Fields:
 * @author Zachary Tu
 *
 */
public class WallDropthrough extends Event {

	private ChainShape shape;
	private Vector2[] vertices;
	
	public WallDropthrough(PlayState state, ChainShape shape) {
		super(state);
		this.shape = shape;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this, UserDataTypes.EVENT) {
			
			/**
			 * When touching the player's foot sensor, this event sets the player's filter to collide with dropthrough platforms.
			 * This also makes it so that when coming from below, the player can pass through until their feet touch it.
			 * This sudden collision causes the player to "pop" above the platform. Make these platforms skinny.
			 */
			@Override
			public void onTouch(HadalData fixB) {
				if (fixB != null) {
					if (fixB instanceof FeetData) {
						
						HadalEntity entity = ((FeetData) fixB).getEntity();
						
						if (entity instanceof Player) {
							Player p = (Player) entity;
							
							if (p.isFastFalling()) {
								return;
							}
						}
						Filter filter = entity.getMainFixture().getFilterData();
						filter.maskBits = (short) (filter.maskBits | Constants.BIT_DROPTHROUGHWALL);
						entity.getMainFixture().setFilterData(filter);
						
						((FeetData) fixB).getTerrain().add(this.event);
					}
				}
			}
			
			/**
			 * When the player's feet sensor leaves the platform, its filter becomes passable by dropthroughs again.
			 */
			@Override
			public void onRelease(HadalData fixB) {
				if (fixB != null) {
					if (fixB instanceof FeetData) {
						HadalEntity entity = ((FeetData) fixB).getEntity();
						
						if (((FeetData) fixB).getTerrain().size() == 1) {
							Filter filter = entity.getMainFixture().getFilterData();
							filter.maskBits = (short) (filter.maskBits &~ Constants.BIT_DROPTHROUGHWALL);
							entity.getMainFixture().setFilterData(filter);
						}
						
						((FeetData) fixB).getTerrain().remove(this.event);
					}
				}
			}
			
			/**
			 * When the player crouches on this event, this platform sets its filter to no longer collide.
			 */
			@Override
			public void onInteract(Player p) {
				Filter filter = p.getMainFixture().getFilterData();
				filter.maskBits = (short) (filter.maskBits &~ Constants.BIT_DROPTHROUGHWALL);
				p.getMainFixture().setFilterData(filter);
			}
			
		};
		BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        
        body = state.getWorld().createBody(bdef);
        body.createFixture(shape, 1.0f);
        Filter filter = new Filter();
		filter.categoryBits = (short) (Constants.BIT_DROPTHROUGHWALL);
		filter.maskBits = (short) (Constants.BIT_SENSOR | Constants.BIT_PLAYER | Constants.BIT_PROJECTILE);
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
