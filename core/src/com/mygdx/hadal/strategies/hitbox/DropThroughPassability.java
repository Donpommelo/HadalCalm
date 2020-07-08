package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.FeetData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

/**
 * This is the  strategy for hboxes to allow them to selectively pass through drop-through platforms
 * To do this, we add a "foot sensor" to the bottom of the hbox
 * @author Zachary Tu
 */
public class DropThroughPassability extends HitboxStrategy {
	
	//Fixtures and user data
	private Fixture feet;
	private FeetData feetData;
		
	public DropThroughPassability(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
	}
	
	@Override
	public void create() {
		super.create();
		
		this.feetData = new FeetData(UserDataTypes.FEET, hbox); 
		this.feet = hbox.getBody().createFixture(FixtureBuilder.createFixtureDef(new Vector2(1 / 2,  - hbox.getSize().y / 2), new Vector2(hbox.getSize().x, hbox.getSize().y / 8), true, 0, 0, 0, 0,
				Constants.BIT_SENSOR, Constants.BIT_DROPTHROUGHWALL, hbox.getFilter()));
		
		feet.setUserData(feetData);
	}
}
