package com.mygdx.hadal.utils.b2d;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.HadalData;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * This util is for creating box2d bodies.
 * @author Zachary Tu
 *
 */
public class BodyBuilder {

	/**
	 * This creates a box with a specific set of properties and adds it to a world. This is called by pretty much every entity to 
	 * create itself. This automatically adds a single fixture to the body.
	 * @param world: The box2d world to add the body to.
	 * @param x: starting x position. This is in pixels. Divide by PPM.
	 * @param y: starting x position. This is in pixels. Divide by PPM.
	 * @param w: width. This is in pixels. Divide by PPM.
	 * @param h: height. This is in pixels. Divide by PPM.
	 * @param grav: Effect of gravity. 0 = no gravity. 1 = normal gravity.
	 * @param density: Density. Higher = less impact from other forces.
	 * @param resti: Restitution = Bounciness.
	 * @param isStatic: True for bodies that do not move.
	 * @param canRotate: Can it rotate?
	 * @param cBits: What type of body is this?
	 * @param mBits: What types of bodies does this collide with?
	 * @param gIndex: Extra filter. <0 = never collide with bodies with same value. >0 = always collide with bodies with same value.
	 * @param sensor: Can fixtures that collide with this pass through it?
	 * @param userData: HadalData of the body.
	 * @return: The newly created body.
	 */
    public static Body createBox(final World world, float x, float y, float w, float h, float grav, float density, float resti,
    		boolean isStatic, boolean canRotate, short cBits, short mBits, short gIndex, boolean sensor, HadalData userData) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.fixedRotation = canRotate;
        bodyDef.position.set(x / PPM, y / PPM);

        if(isStatic) {
            bodyDef.type = BodyDef.BodyType.StaticBody;
        } else {
            bodyDef.type = BodyDef.BodyType.DynamicBody;
        }

        PolygonShape shape = new PolygonShape();
        
        //There is not really a reason I am dividing by 2 but I don't really feel like changing it. Sorry.
        shape.setAsBox(w / PPM / 2, h / PPM / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.restitution = resti;
        //fixtureDef.friction = 0;
        fixtureDef.filter.categoryBits = cBits;
        fixtureDef.filter.maskBits = mBits;
        fixtureDef.filter.groupIndex = gIndex;
        
        Body body = world.createBody(bodyDef);
        if (sensor) {
        	fixtureDef.isSensor = true;
        } else {
        	fixtureDef.isSensor = false;
        }
        body.createFixture(fixtureDef).setUserData(userData);
        body.setGravityScale(grav);
        shape.dispose();
        
        return body;
    }

}
