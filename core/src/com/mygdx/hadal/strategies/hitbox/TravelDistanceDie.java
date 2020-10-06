package com.mygdx.hadal.strategies.hitbox;


import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * HitboxTravelDistanceDieStrategy is used for hitboxes that travel a set distance and then die
 * @author Zachary Tu
 */
public class TravelDistanceDie extends HitboxStrategy {
	
	//the location the hitbox is created and the location that it will die
	private final Vector2 startLocation = new Vector2();
	
	//the distance the hbox travels before dying
	private final float distance;
	
	public TravelDistanceDie(PlayState state, Hitbox proj, BodyData user, float distance) {
		super(state, proj, user);
		this.distance = distance;
	}
	
	@Override
	public void create() { this.startLocation.set(hbox.getPosition()); }
	
	@Override
	public void controller(float delta) {
		if (startLocation.dst(hbox.getPosition()) >= distance) {
			hbox.die();
		}
	}
}
