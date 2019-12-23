package com.mygdx.hadal.schmucks.strategies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;

public abstract class HitboxStrategy implements IHitboxStrategy{

	//reference to game state.
	protected PlayState state;
	
	//The hitbox containing this data
	protected Hitbox hbox;
	
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
	public void die() {}
	
	@Override
	public void render(SpriteBatch batch) {}
}
