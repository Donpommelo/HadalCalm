package com.mygdx.hadal.schmucks.strategies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;

public class HitboxOnContactStickStrategy extends HitboxStrategy{
	
	private boolean stickToWalls, stickToDudes, stuckToWall, stuckToDude;
	private float angle;
	private HadalEntity target;
	private Vector2 location = new Vector2();
	
	public HitboxOnContactStickStrategy(PlayState state, Hitbox proj, BodyData user, boolean walls, boolean dudes) {
		super(state, proj, user);
		this.stickToWalls = walls;
		this.stickToDudes = dudes;
		this.stuckToWall = false;
		this.stuckToDude = false;
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if ((!stuckToWall || stickToWalls) && (!stuckToDude || stickToDudes)) {
			if (fixB != null) {
				if (fixB.getType().equals(UserDataTypes.BODY) && stickToDudes) {
					stuckToDude = true;
					target = fixB.getEntity();
					angle = hbox.getOrientation();
					location.set(
							hbox.getPosition().x - target.getPosition().x, 
							hbox.getPosition().y - target.getPosition().y);		
				}
				if (fixB.getType().equals(UserDataTypes.WALL) && stickToWalls) {
					stuckToWall = true;
					target = fixB.getEntity();
					angle = hbox.getOrientation();
					location.set(
							hbox.getPosition().x - target.getPosition().x, 
							hbox.getPosition().y - target.getPosition().y);		
				}
			} else if (stickToWalls) {
				stuckToWall = true;
				angle = hbox.getOrientation();
				location.set(hbox.getPosition());
			}
		}
	}
	
	@Override
	public void controller(float delta) {
		if (stuckToWall && target == null && location != null) {
			hbox.setTransform(location, angle);
		} else if ((stuckToDude || stuckToWall) && target != null && location != null) {
			if (target.isAlive()) {
				hbox.setTransform(target.getPosition().add(location), angle);
			} else {
				stuckToDude = false;
			}
		}
	}
}
