package com.mygdx.hadal.schmucks.strategies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;

public class HitboxDefaultStrategy extends HitboxStrategy{
	
	private boolean adjustAngle;
	
	public HitboxDefaultStrategy(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
		this.adjustAngle = true;
	}
	
	public HitboxDefaultStrategy(PlayState state, Hitbox proj, BodyData user, boolean adjustAngle) {
		super(state, proj, user);
		this.adjustAngle = adjustAngle;	
	}
	
	@Override
	public void controller(float delta) {
		hbox.setLifeSpan(hbox.getLifeSpan() - delta);
		if (hbox.getLifeSpan() <= 0) {
			hbox.die();
		}
		
		if (adjustAngle) {
			hbox.setTransform(hbox.getPosition().x, hbox.getPosition().y, 
					(float)(Math.atan2(hbox.getLinearVelocity().y , hbox.getLinearVelocity().x)));
		}
	}

	@Override
	public void push(float impulseX, float impulseY) {
		hbox.applyLinearImpulse(new Vector2(impulseX, impulseY).scl(0.2f));
	}
	
	@Override
	public void onHit(HadalData fixB) {}
	
	@Override
	public void die() {
		hbox.queueDeletion();
	}
	
	@Override
	public void render(SpriteBatch batch) {
		
	}
}
