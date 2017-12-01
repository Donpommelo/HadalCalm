package com.mygdx.hadal.handlers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.hadal.schmucks.userdata.HadalSchmuck;

public class WorldContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		HadalSchmuck fixA = (HadalSchmuck) contact.getFixtureA().getUserData();
		HadalSchmuck fixB = (HadalSchmuck) contact.getFixtureB().getUserData();
		
		if (fixA != null) {
			fixA.setNumContacts(fixA.getNumContacts() + 1);
		}
		if (fixB != null) {
			fixB.setNumContacts(fixB.getNumContacts() + 1);
		}
	}

	@Override
	public void endContact(Contact contact) {
		HadalSchmuck fixA = (HadalSchmuck) contact.getFixtureA().getUserData();
		HadalSchmuck fixB = (HadalSchmuck) contact.getFixtureB().getUserData();
		
		if (fixA != null) {
			fixA.setNumContacts(fixA.getNumContacts() - 1);
		}
		if (fixB != null) {
			fixB.setNumContacts(fixB.getNumContacts() - 1);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
