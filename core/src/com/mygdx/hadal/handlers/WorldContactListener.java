package com.mygdx.hadal.handlers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.ProjectileData;

public class WorldContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		HadalData fixA = (HadalData) contact.getFixtureA().getUserData();
		HadalData fixB = (HadalData) contact.getFixtureB().getUserData();

		if (fixA != null) {
			fixA.setNumContacts(fixA.getNumContacts() + 1);
			if (fixA.getType().equals(UserDataTypes.PROJECTILE)) {

				((ProjectileData) fixA).onHit(fixB);
			}
			
		}
		if (fixB != null) {
			fixB.setNumContacts(fixB.getNumContacts() + 1);
			if (fixB.getType().equals(UserDataTypes.PROJECTILE)) {

				((ProjectileData) fixB).onHit(fixA);
			}
		}
		
	}

	@Override
	public void endContact(Contact contact) {
		HadalData fixA = (HadalData) contact.getFixtureA().getUserData();
		HadalData fixB = (HadalData) contact.getFixtureB().getUserData();
		
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
