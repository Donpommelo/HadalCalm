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
	 * This creates a FixtureDef to be used to create a Fixture on a body..
	 * @param w: width. This is in pixels. Divide by PPM.
	 * @param h: height. This is in pixels. Divide by PPM.
	 * @param center: Center of the body to stick the fixture.
	 * @param sensor: Will this fixture pass through fixtures it collide with?
	 * @param angle: The angle to turn the fixture compared to the body.
	 * @param cBits: What type of fixture is this?
	 * @param mBits: What types of fixture does this collide with?
	 * @param gIndex: Extra filter. <0 = never collide with fixture with same value. >0 = always collide with fixture with same value.
	 * @return: a Fixture def that will create a fixture.
	 */
	public static FixtureDef createFixtureDef(float w, float h, Vector2 center, boolean sensor, float angle, 
			float density, float resti,	short cBits, short mBits, short gIndex) {
		FixtureDef fixtureDef = new FixtureDef();
		
		PolygonShape pShape = new PolygonShape();
		fixtureDef.shape = pShape;
		
        //There is not really a reason I am dividing by 2 but I don't really feel like changing it. Sorry.
		pShape.setAsBox(w / PPM / 2, h / PPM / 2, center, angle);
		
		if (sensor) {
			fixtureDef.isSensor = true;
		} else {
			fixtureDef.isSensor = false;
		}
		
		fixtureDef.density = density;
        fixtureDef.restitution = resti;		
		fixtureDef.filter.categoryBits = cBits;
        fixtureDef.filter.maskBits = mBits;
        fixtureDef.filter.groupIndex = gIndex;
		
		return fixtureDef;
	}
}
