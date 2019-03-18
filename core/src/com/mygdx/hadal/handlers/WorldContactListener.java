package com.mygdx.hadal.handlers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.userdata.FeetData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;

/**
 * This listener keeps track of whenever 2 fixtures in the box2d world collide.
 * @author Zachary Tu
 *
 */
public class WorldContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		HadalData fixA = (HadalData) contact.getFixtureA().getUserData();
		HadalData fixB = (HadalData) contact.getFixtureB().getUserData();
		
		//When 2 fixtures collide, increment their number of contacts.
		//Projectiles and events should register hits.
		if (fixA != null) {
			fixA.setNumContacts(fixA.getNumContacts() + 1);
			if (fixA.getType().equals(UserDataTypes.HITBOX)) {
				((HitboxData) fixA).onHit(fixB);
			}
			if (fixA.getType().equals(UserDataTypes.EVENT)) {
				((EventData) fixA).onTouch(fixB);
			}
			if (fixA.getType().equals(UserDataTypes.FEET)) {
				((FeetData) fixA).onStomp(fixB);
			}
			
		}
		if (fixB != null) {
			fixB.setNumContacts(fixB.getNumContacts() + 1);
			if (fixB.getType().equals(UserDataTypes.HITBOX)) {
				((HitboxData) fixB).onHit(fixA);
			}
			if (fixB.getType().equals(UserDataTypes.EVENT)) {
				((EventData) fixB).onTouch(fixA);
			}
			if (fixB.getType().equals(UserDataTypes.FEET)) {
				((FeetData) fixB).onStomp(fixA);
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		HadalData fixA = (HadalData) contact.getFixtureA().getUserData();
		HadalData fixB = (HadalData) contact.getFixtureB().getUserData();
		
		if (fixA != null) {
			fixA.setNumContacts(fixA.getNumContacts() - 1);
			if (fixA.getType().equals(UserDataTypes.EVENT)) {
				((EventData) fixA).onRelease(fixB);
			}
		}
		if (fixB != null) {
			fixB.setNumContacts(fixB.getNumContacts() - 1);
			if (fixB.getType().equals(UserDataTypes.EVENT)) {
				((EventData) fixB).onRelease(fixA);
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}

}
