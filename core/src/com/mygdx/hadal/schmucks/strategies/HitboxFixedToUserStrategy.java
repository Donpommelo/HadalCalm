package com.mygdx.hadal.schmucks.strategies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class HitboxFixedToUserStrategy extends HitboxStrategy {
	
	private Vector2 center = new Vector2();
	private Vector2 angle = new Vector2();
	private boolean rotate;
	
	public HitboxFixedToUserStrategy(PlayState state, Hitbox proj, BodyData user, Vector2 angle, Vector2 center, boolean rotate) {
		super(state, proj, user);
		this.center = center;
		this.angle = angle;
		this.rotate = rotate;
	}
	
	@Override
	public void controller(float delta) {
	
		if (!creator.getSchmuck().isAlive()) {
			hbox.queueDeletion();
		} else {
			Vector2 hbLocation = creator.getSchmuck().getPosition().add(center);
			if (rotate) {
				hbox.setTransform(hbLocation, creator.getSchmuck().getBody().getAngle() + angle.angle());
			} else {
				hbox.setTransform(hbLocation, angle.angleRad());
			}
		}
	}
}
