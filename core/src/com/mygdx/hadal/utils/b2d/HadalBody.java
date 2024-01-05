package com.mygdx.hadal.utils.b2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.HadalData;

import static com.mygdx.hadal.constants.Constants.PPM;

/**
 * Class used for the creation of bodies using a factory.
 * Think of HadalBody and HadalFixture less of "Bodies" and "Fixtures", but rather "Thing that makes Bodies + their
 * initial Fixture" and "Thing that makes Fixture". Hence why this extends HadalFixture.
 * <p>
 * This is organized into its own class due to ordering. I want to set up the body/fixture's fields using a factory,
 * then create the body/fixture, then dispose of the used shape which must be retained somewhere.
 * <p>
 * gravity: Effect of gravity. 0 = no gravity. 1 = normal gravity.
 * bodyType: Dynamic, static or kinematic (default = dynamic.
 * fixedRotate: Can it not rotate?
 * hadalData: The user data of the entity this body belongs to
 *
 * @author Shampanelle Sweviticus
 */
public class HadalBody extends  HadalFixture {
    private final Vector2 position = new Vector2();
    private final HadalData hadalData;

    private BodyDef.BodyType bodyType = BodyDef.BodyType.DynamicBody;
    private boolean fixedRotate = true;
    private float gravity;

    /**
     * This creates a Hadal Body that be used to create a Body in the world, plus its initial fixture.
     * @param position: the starting position of this body
     * @param size: the size of this body
     * @param cBits: What type of body is this?
     * @param mBits: What types of bodies does this collide with?
     * @param gIndex: Extra filter. less than 0 = never collide with bodies with same value. greater than 0 = always collide with bodies with same value.
     */
    public HadalBody(HadalData hadalData, Vector2 position, Vector2 size, short cBits, short mBits, short gIndex) {
        super(new Vector2(), size, cBits, mBits, gIndex);
        this.hadalData = hadalData;
        this.position.set(position);

        //Default properties for a body's "main" fixture are different.
        this.setDensity(1.0f);
    }

    /**
     * This adds a Body and its initial Fixture to the input world, returning the new Body
     */
    @Override
    public Body addToWorld(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.fixedRotation = fixedRotate;
        if (Float.isFinite(position.x) && Float.isFinite(position.y)) {
            bodyDef.position.set(new Vector2(position).scl(1 / PPM));
        }

        bodyDef.type = bodyType;

        //Create the Body
        Body body = world.createBody(bodyDef);

        //Create the Body's first Fixture, and set its fields
        addToBody(body).setUserData(hadalData);
        body.setGravityScale(gravity);

        return body;
    }

    public HadalBody setBodyType(BodyDef.BodyType bodyType) {
        this.bodyType = bodyType;
        return this;
    }

    public HadalBody setFixedRotate(boolean fixedRotate) {
        this.fixedRotate = fixedRotate;
        return this;
    }

    public HadalBody setGravity(float gravity) {
        this.gravity = gravity;
        return this;
    }
}
