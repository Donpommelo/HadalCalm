package com.mygdx.hadal.strategies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

/**
 * A Hbox strategy implements the methods used to affect a hitbox after it has been created
 * @author Frictuzmo Fressucroix
 */
public abstract class HitboxStrategy implements IHitboxStrategy {

	//reference to game state.
	protected final PlayState state;
	
	//The hitbox containing this data
	protected final Hitbox hbox;
	
	//the data of the schmuck who created this hitbox
	protected BodyData creator;
	
	public HitboxStrategy(PlayState state, Hitbox proj, BodyData user) {
		this.state = state;
		this.hbox = proj;
		this.creator = user;
	}

	@Override
	public void create() {}
	
	@Override
	public void controller(float delta) {}

	@Override
	public void push(Vector2 push) {}
	
	@Override
	public void onHit(HadalData fixB) {}
	
	@Override
	public void receiveDamage(BodyData perp, float basedamage, Vector2 knockback, DamageTypes... tags) {}
	
	@Override
	public void die() {}
	
	public void setCreator(BodyData creator) { this.creator = creator; }
}
