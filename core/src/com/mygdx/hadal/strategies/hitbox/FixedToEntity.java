package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox fixed to the user. It replaces melee hboxes
 * @author Laneymaker Lelarbus
 */
public class FixedToEntity extends HitboxStrategy {
	
	//the point on the player that this hbox is attached to
	private final Vector2 center = new Vector2();
	
	//the angle that this hbox is fixed at
	private final Vector2 angle = new Vector2();
	
	//does this hbox rotate when the user does?
	private final boolean rotate;
	
	//this is the entity that this hbox is fixed to. Usually the user for melee hboxes. Some hboxes have another hboxes fixed to them like sticky bombs
	private HadalEntity target;
	
	public FixedToEntity(PlayState state, Hitbox proj, BodyData user, Vector2 angle, Vector2 center, boolean rotate) {
		super(state, proj, user);
		this.center.set(center);
		this.angle.set(angle);
		this.rotate = rotate;
		
		hbox.setSyncDefault(false);
		hbox.setSyncInstant(true);
		
		this.target = creator.getSchmuck();
	}
	
	public FixedToEntity(PlayState state, Hitbox proj, BodyData user, HadalEntity target, Vector2 angle, Vector2 center, boolean rotate) {
		this(state, proj, user, angle, center, rotate);
		this.target = target;
	}
	
	@Override
	public void create() {
		if (target.isAlive()) {
			Vector2 hbLocation = target.getPosition().add(center);
			if (rotate) {
				hbox.setTransform(hbLocation, target.getAngle() + angle.angleRad());
			} else {
				hbox.setTransform(hbLocation, angle.angleRad());
			}
		}
	}
	
	@Override
	public void controller(float delta) {
		if (!target.isAlive()) {
			hbox.die();
		} else {
			Vector2 hbLocation = target.getPosition().add(center);
			if (rotate) {
				hbox.setTransform(hbLocation, target.getAngle() + angle.angleRad());
			} else {
				hbox.setTransform(hbLocation, hbox.getAngle());
			}
		}
	}
}
