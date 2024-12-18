package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.ClientIllusion;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.FeetData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * This event is a solid block that can be passed by hitboxes, but not the player.
 * When the player presses crouch when standing on it, they will pass through it.
 * <p>
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * <p>
 * Fields:
 * N/A
 * 
 * @author Thaditya Troseidon
 */
public class DropThroughPlatform extends Event {
	
	public DropThroughPlatform(PlayState state, Vector2 startPos, Vector2 size) {
		super(state, startPos, size);
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
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
			 * When the player's feet sensor leaves the platform, its filter becomes passable by dropthrough again.
			 */
			@Override
			public void onRelease(HadalData fixB) {
				if (fixB != null) {
					if (fixB instanceof FeetData feet) {
						HadalEntity entity = fixB.getEntity();

						if (entity.getMainFixture() != null) {
							Filter filter = entity.getMainFixture().getFilterData();
							filter.maskBits = (short) (filter.maskBits &~ BodyConstants.BIT_DROPTHROUGHWALL);
							entity.getMainFixture().setFilterData(filter);
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

		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_DROPTHROUGHWALL,
				(short) (BodyConstants.BIT_WALL | BodyConstants.BIT_SENSOR | BodyConstants.BIT_PLAYER | BodyConstants.BIT_PROJECTILE | BodyConstants.BIT_ENEMY), (short) 0)
				.setBodyType(BodyDef.BodyType.KinematicBody)
				.setSensor(false)
				.addToWorld(world);
	}
	
	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.UI_RELOAD_BAR);
		setScaleAlign(ClientIllusion.alignType.CENTER_STRETCH);
	}
}
