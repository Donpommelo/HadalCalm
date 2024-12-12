package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.FeetData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;

/**
 * A Wall is a solid wall parsed from polylines from tiled. This is a version that the player can drop through.
 * <p>
 * Triggered Behavior: N/A.
 * Triggering Behavior: N/A.
 * <p>
 * Fields:
 * @author Hizarus Hirfinitzel
 */
public class WallDropthrough extends Event {

	private final ChainShape shape;

	public WallDropthrough(PlayState state, ChainShape shape) {
		super(state);
		this.shape = shape;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this, UserDataType.EVENT) {
			
			/**
			 * When touching the player's foot sensor, this event sets the player's filter to collide with dropthrough platforms.
			 * This also makes it so that when coming from below, the player can pass through until their feet touch it.
			 * This sudden collision causes the player to "pop" above the platform. Make these platforms skinny.
			 */
			@Override
			public void onTouch(HadalData fixB) {
				if (fixB != null) {
					if (fixB instanceof FeetData feet) {
						
						HadalEntity entity = fixB.getEntity();
						
						//let a fastfalling player drop through without stopping
						if (entity instanceof Player p) {
							if (p.getFastfallHelper().isFastFalling()) { return; }
						}
						if (entity.getMainFixture() != null) {
							Filter filter = entity.getMainFixture().getFilterData();
							filter.maskBits = (short) (filter.maskBits | BodyConstants.BIT_DROPTHROUGHWALL);
							entity.getMainFixture().setFilterData(filter);
						}


						feet.getTerrain().add(this.event);
					}
				}
			}
			
			/**
			 * When the player's feet sensor leaves the platform, its filter becomes passable by dropthroughs again.
			 */
			@Override
			public void onRelease(HadalData fixB) {
				if (fixB != null) {
					if (fixB instanceof FeetData feet) {
						HadalEntity entity = fixB.getEntity();
						
						if (feet.getTerrain().size == 1) {
							if (entity.getMainFixture() != null) {
								Filter filter = entity.getMainFixture().getFilterData();
								filter.maskBits = (short) (filter.maskBits &~ BodyConstants.BIT_DROPTHROUGHWALL);
								entity.getMainFixture().setFilterData(filter);
							}
						}

						feet.getTerrain().removeValue(this.event, false);
					}
				}
			}
			
			/**
			 * When the player crouches on this event, this platform sets its filter to no longer collide.
			 */
			@Override
			public void onInteract(Player p) {
				if (p.getMainFixture() != null) {
					Filter filter = p.getMainFixture().getFilterData();
					filter.maskBits = (short) (filter.maskBits &~ BodyConstants.BIT_DROPTHROUGHWALL);
					p.getMainFixture().setFilterData(filter);
				}
			}
			
		};
		BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        
        body = state.getWorld().createBody(bdef);
        body.createFixture(shape, 1.0f);
        Filter filter = new Filter();
		filter.categoryBits = BodyConstants.BIT_DROPTHROUGHWALL;
		filter.maskBits = (short) (BodyConstants.BIT_SENSOR | BodyConstants.BIT_PLAYER | BodyConstants.BIT_PROJECTILE);
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
