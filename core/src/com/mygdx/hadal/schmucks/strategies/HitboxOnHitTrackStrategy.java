package com.mygdx.hadal.schmucks.strategies;

import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;

public class HitboxOnHitTrackStrategy extends HitboxStrategy{
	
	private boolean tracked, override;
	private HadalEntity target;
	
	public HitboxOnHitTrackStrategy(PlayState state, Hitbox proj, BodyData user, boolean override) {
		super(state, proj, user);
		this.override = override;
		this.tracked = false;
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (!tracked && fixB != null) {
			if (fixB.getType().equals(UserDataTypes.BODY)) {
				tracked = true;
				target = fixB.getEntity();	
				hbox.setLinearVelocity(0, 0);
			}
		}
	}
	
	@Override
	public void controller(float delta) {
		if (tracked && target != null) {
			if (target.isAlive() && target.getBody() != null) {
				if (override) {
					target.setTransform(hbox.getPosition(), 0);
				} else {
					hbox.setTransform(target.getPosition(), 0);
				}
			} else {
				hbox.die();
			}
		}
	}

	public HadalEntity getTarget() {
		return target;
	}

	public void setTarget(HadalEntity target) {
		this.target = target;
	}	
}
