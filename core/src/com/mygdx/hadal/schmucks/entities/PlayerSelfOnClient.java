package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

import static com.mygdx.hadal.states.PlayState.SYNC_TIME;

/**
 * A ClientPlayer represents a client's own player.
 * This processes things like client prediction
 * @author Hepepper Hasufferson
 */
public class PlayerSelfOnClient extends Player {

	public PlayerSelfOnClient(PlayState state, Vector2 startPos, String name, Loadout startLoadout, PlayerBodyData oldData,
							  int connID, User user, boolean reset, Event start) {
		super(state, startPos, name, startLoadout, oldData, connID, user, reset, start);
	}

	@Override
	public void onReceiveSync(Object o, float timestamp) {
		super.onReceiveSync(o, timestamp);
		
		if (o instanceof PacketsSync.SyncEntity p) {

		}
	}

	private float syncAccumulator;
	private final Vector2 newPosition = new Vector2();
	@Override
	public void clientController(float delta) {
		super.clientController(delta);

		processMovement(delta);
		processFuel(delta);

		newPosition.set(getPixelPosition());
		mouseAngle.set(newPosition.x, newPosition.y)
				.sub(((ClientState) state).getMousePosition().x, ((ClientState) state).getMousePosition().y);
		attackAngle = MathUtils.atan2(mouseAngle.y, mouseAngle.x) * MathUtils.radDeg;

		//Apply base hp regen
		getBodyData().regainHp(getBodyData().getStat(Stats.HP_REGEN) * delta, getBodyData(), true, DamageTag.REGEN);

		//process cooldowns on firing
		shootCdCount -= delta;

		syncAccumulator += delta;
		if (syncAccumulator >= SYNC_TIME) {
			syncAccumulator = 0;

			HadalGame.client.sendUDP(new PacketsSync.SyncClientSnapshot(getPosition(), getLinearVelocity(),
					entityAge, state.getTimer(), moveState,
					mouseAngle,
					playerData.getCurrentFuel(),
					getMainFixture().getFilterData().maskBits));
		}
	}

	@Override
	public void clientInterpolation() {}
}
