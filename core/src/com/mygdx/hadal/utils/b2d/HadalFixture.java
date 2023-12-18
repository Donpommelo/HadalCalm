package com.mygdx.hadal.utils.b2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.hadal.constants.BodyConstants;

import static com.mygdx.hadal.constants.Constants.PPM;

/**
 * Class used for the creation of fixtures using a factory.
 *
 * sensor: Will this fixture pass through fixtures it collide with?
 * angle: The angle to turn the fixture compared to the body.
 * density: float density of the fixture
 * restitution: bounciness of the fixture
 * friction: slipperiness of the friction
 *
 * @author Blickadee Brobottoms
 */
public class HadalFixture {

    protected final Vector2 center = new Vector2();
    protected final Vector2 size = new Vector2();
    protected final short cBits, mBits, gIndex;

    private boolean sensor = true;
    private float angle, density, restitution, friction;
    private int shape = BodyConstants.POLYGON;

    /**
     * This creates a Hadal Fixture that be used to create a Fixture on a body.
     * @param center: Center of the body to stick the fixture.
     * @param size: size of the fixture
     * @param cBits: What type of fixture is this?
     * @param mBits: What types of fixture does this collide with?
     * @param gIndex: Extra filter. less than 0 = never collide with fixture with same value. greater than 0 = always collide with fixture with same value.
     */
    public HadalFixture(Vector2 center, Vector2 size, short cBits, short mBits, short gIndex) {
        this.center.set(center);
        this.size.set(size);
        this.cBits = cBits;
        this.mBits = mBits;
        this.gIndex = gIndex;
    }

    /**
     * Create the desired fixture attached to a body.
     * One reason to do it like this is so that the fixture properties can be set dynamically while still disposing
     * the shape at the correct time
     *
     * @param body: body that this fixture will be attached to.
     * @return the newly created fixture
     */
    public Fixture addToBody(Body body) {
        FixtureDef fixtureDef = new FixtureDef();
        Shape shape;

        if (this.shape == BodyConstants.CIRCLE) {
            shape = new CircleShape();
            fixtureDef.shape = shape;
            shape.setRadius(size.x);
            ((CircleShape) shape).setPosition(center);
        } else {
            shape = new PolygonShape();
            fixtureDef.shape = shape;
            ((PolygonShape) shape).setAsBox(size.x / PPM / 2, size.y / PPM / 2, new Vector2(center).scl(1 / PPM), angle);
        }

        fixtureDef.isSensor = sensor;
        fixtureDef.density = density;
        fixtureDef.restitution = restitution;
        fixtureDef.friction = friction;
        fixtureDef.filter.categoryBits = cBits;
        fixtureDef.filter.maskBits = mBits;
        fixtureDef.filter.groupIndex = gIndex;

        Fixture fixture = body.createFixture(fixtureDef);
        shape.dispose();

        return fixture;
    }

    /**
     * This is for adding a body directly to the world.
     * For the HadalFixture, this does nothing.
     * HadalBody overrides this so that its addToWorld() can work as part of the factory method.
     */
    public Body addToWorld(World world) { return null; }

    public HadalFixture setSensor(boolean sensor) {
        this.sensor = sensor;
        return this;
    }

    public HadalFixture setAngle(float angle) {
        this.angle = angle;
        return this;
    }

    public HadalFixture setDensity(float density) {
        this.density = density;
        return this;
    }

    public HadalFixture setRestitution(float restitution) {
        this.restitution = restitution;
        return this;
    }

    public HadalFixture setFriction(float friction) {
        this.friction = friction;
        return this;
    }

    public HadalFixture setShape(int shape) {
        this.shape = shape;
        return this;
    }
}
