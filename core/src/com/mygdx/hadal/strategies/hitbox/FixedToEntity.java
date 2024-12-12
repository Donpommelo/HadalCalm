package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.ClientState;
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
	private boolean rotate;

	//does this hbox kill the entity it is attached to when it dies?
	private boolean killOnDeath;

	//this is the entity that this hbox is fixed to. Usually the user for melee hboxes. Some hboxes have another hboxes fixed to them like sticky bombs
	private HadalEntity target;

	//is this attached to the same entity that created it? Used to prevent hbox filter from being changed to hurt creator
	private boolean attachedToUser;

	public FixedToEntity(PlayState state, Hitbox proj, BodyData user, Vector2 angle, Vector2 center) {
		super(state, proj, user);
		this.center.set(center);
		this.angle.set(angle);

		this.target = creator.getSchmuck();
		this.attachedToUser = true;
	}
	
	public FixedToEntity(PlayState state, Hitbox proj, BodyData user, HadalEntity target, Vector2 angle, Vector2 center) {
		this(state, proj, user, angle, center);
		this.target = target;
		this.attachedToUser = false;
	}

	private final Vector2 hbLocation = new Vector2();
	@Override
	public void create() {
		if (target.isAlive()) {
			hbLocation.set(target.getPosition()).add(center);
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
			hbLocation.set(target.getPosition()).add(center);
			if (rotate) {
				hbox.setTransform(hbLocation, target.getAngle() + angle.angleRad());
			} else {
				hbox.setTransform(hbLocation, hbox.getAngle());
			}
			if (attachedToUser) {
				if (creator.getSchmuck().getHitboxFilter() != hbox.getFilter()) {
					hbox.setFilter(creator.getSchmuck().getHitboxFilter());
				}
			}

			//setting velocity here us lets server send orientation information through synced attacks
			hbox.setLinearVelocity(target.getLinearVelocity());
		}
	}

	@Override
	public void die() {
		if (killOnDeath) {
			if (target.isAlive()) {
				if (hbox.getState().isServer()) {
					target.queueDeletion();
				} else {
					((ClientState) state).removeEntity(target.getEntityID());
				}
			}
		}
	}

	public FixedToEntity setRotate(boolean rotate) {
		this.rotate = rotate;
		return this;
	}

	public FixedToEntity setKillOnDeath(boolean killOnDeath) {
		this.killOnDeath = killOnDeath;
		return this;
	}
}
