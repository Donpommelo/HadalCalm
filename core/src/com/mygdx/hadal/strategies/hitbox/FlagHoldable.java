package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 */
public class FlagHoldable extends HitboxStrategy {

	//this is the entity that this hbox is fixed to. Usually the user for melee hboxes. Some hboxes have another hboxes fixed to them like sticky bombs
	private Player target;

	//is the flag held by a player? Has the flag been removed from its spawn location?
	private boolean captured, awayFromSpawn;

	//the timer until a dropped flag returns to spawn
	private static final float returnTime = 10.0f;
	private float returnTimer;

	private static final int maxNameLength = 25;

	//counter keeps track of player score incrementing
	private float timeCount = 0;

	public FlagHoldable(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
		hbox.setSyncDefault(false);
		hbox.setSyncInstant(true);
	}

	@Override
	public void onHit(HadalData fixB) {

		if (!captured) {
			if (fixB != null) {
				if (fixB instanceof PlayerBodyData) {
					captured = true;
					awayFromSpawn = true;
					target = ((PlayerBodyData) fixB).getPlayer();

					hbox.getBody().setGravityScale(0.0f);
					String playerName = WeaponUtils.getPlayerColorName(target, maxNameLength);
					state.getKillFeed().addNotification(playerName + " PICKED UP THE FLAG!", true);
				}
			}
		}
	}

	private final Vector2 hbLocation = new Vector2();
	@Override
	public void controller(float delta) {

		//if the flag holder dies, the flag drops and will return after some time
		if (captured) {
			if (!target.isAlive()) {
				captured = false;
				hbox.getBody().setGravityScale(1.0f);
				returnTimer = returnTime;

				state.getKillFeed().addNotification("FLAG WAS DROPPED!", true);
			} else {
				hbLocation.set(target.getPosition());
				hbox.setTransform(hbLocation, hbox.getAngle());

				timeCount += delta;
				if (timeCount >= 1.0f) {
					timeCount = 0;
					state.getUiExtra().changeFields(target, 1, 0, 0.0f, 0.0f, false);
				}
			}
		} else if (awayFromSpawn) {
			returnTimer -= delta;

			if (returnTimer <= 0.0f) {
				hbox.die();
				state.getKillFeed().addNotification("FLAG WAS RETURNED!" , true);
			}
		}
	}
}
