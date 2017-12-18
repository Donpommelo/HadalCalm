package com.mygdx.hadal.utils.b2d;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class FixtureBuilder {

	public static FixtureDef createFixtureDef(float w, float h, Vector2 center, boolean sensor, float angle,
			short cBits, short mBits, short gIndex) {
		FixtureDef fixtureDef = new FixtureDef();
		
		PolygonShape pShape = new PolygonShape();
		fixtureDef.shape = pShape;
		pShape.setAsBox(w / PPM / 2, h / PPM / 2, center, angle);
		
		if (sensor) {
			fixtureDef.isSensor = true;
		} else {
			fixtureDef.isSensor = false;
		}
		
		fixtureDef.filter.categoryBits = cBits;
        fixtureDef.filter.maskBits = mBits;
        fixtureDef.filter.groupIndex = gIndex;
		
		return fixtureDef;
	}
}
