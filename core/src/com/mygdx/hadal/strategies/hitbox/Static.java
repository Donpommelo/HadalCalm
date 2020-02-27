package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox static and unmoving.
 * @author Zachary Tu
 *
 */
public class Static extends HitboxStrategy{
	
	public Static(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
	}
	
	@Override
	public void create() {
		WeldJointDef joint = new WeldJointDef();
		joint.bodyA = state.getAnchor().getBody();
		joint.bodyB = hbox.getBody();
		joint.localAnchorA.set(hbox.getPosition().x, hbox.getPosition().y);
		joint.localAnchorB.set(0, 0);
		state.getWorld().createJoint(joint);
	}
}
