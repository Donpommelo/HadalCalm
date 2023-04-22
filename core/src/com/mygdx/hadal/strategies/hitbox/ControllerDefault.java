package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This is the default strategy that most hboxes have
 * This process lifespan decreasing, dying and being pushed.
 * @author Ghitcoin Gokrantz
 */
public class ControllerDefault extends HitboxStrategy {
	
	public ControllerDefault(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
	}
	
	@Override
	public void controller(float delta) {
		
		//default behavior of hboxes is to die when lifespan expires
		hbox.setLifeSpan(hbox.getLifeSpan() - delta);
		if (hbox.getLifeSpan() <= 0) {
			hbox.die();
		}
	}

	@Override
	public void push(Vector2 push) {
		hbox.applyLinearImpulse(push);
	}
	
	@Override
	public void die() {
		if (hbox.getState().isServer()) {
			hbox.queueDeletion();
		} else {
			hbox.setAlive(false);
			((ClientState) state).removeEntity(hbox.getEntityID());
		}
	}
}
