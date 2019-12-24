package com.mygdx.hadal.utils.b2d;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * This creates and returns fixtures. Used when we need to add an extra fixture to a body created by the BodyBuilder.
 * @author Zachary Tu
 *
 */
public class FixtureBuilder {

	/**
	 * This creates a FixtureDef to be used to create a Fixture on a body.
	 * @param center: Center of the body to stick the fixture.
	 * @param size: sixe of the fixture
	 * @param sensor: Will this fixture pass through fixtures it collide with?
	 * @param angle: The angle to turn the fixture compared to the body.
	 * @param cBits: What type of fixture is this?
	 * @param mBits: What types of fixture does this collide with?
	 * @param gIndex: Extra filter. <0 = never collide with fixture with same value. >0 = always collide with fixture with same value.
	 * @return: a Fixture def that will create a fixture.
	 */
	public static FixtureDef createFixtureDef(Vector2 center, Vector2 size, boolean sensor, float angle, float density, float resti, float friction, short cBits, short mBits, short gIndex) {
		FixtureDef fixtureDef = new FixtureDef();
		
		PolygonShape pShape = new PolygonShape();
		fixtureDef.shape = pShape;
		
		pShape.setAsBox(size.x / PPM / 2, size.y / PPM / 2, new Vector2(center).scl(1 / PPM), angle);
		
		if (sensor) {
			fixtureDef.isSensor = true;
		} else {
			fixtureDef.isSensor = false;
		}
		
		fixtureDef.density = density;
        fixtureDef.restitution = resti;		
        fixtureDef.friction = friction;		
		fixtureDef.filter.categoryBits = cBits;
        fixtureDef.filter.maskBits = mBits;
        fixtureDef.filter.groupIndex = gIndex;
		
		return fixtureDef;
	}
}
