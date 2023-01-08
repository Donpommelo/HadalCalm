package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.PlayState;

/**
 * A ClientPlayer represents a client's own player.
 * This processes things like client prediction
 * @author Hepepper Hasufferson
 */
public class PlayerClientOnHost extends Player {

	public PlayerClientOnHost(PlayState state, Vector2 startPos, String name, Loadout startLoadout, PlayerBodyData oldData,
							  int connID, User user, boolean reset, Event start) {
		super(state, startPos, name, startLoadout, oldData, connID, user, reset, start);
		receivingSyncs = true;
	}

	@Override
	public void controller(float delta) {
		super.controller(delta);
		//process each buffered snapshot starting from the oldest to the most recent
		while (!bufferedTimestamps.isEmpty()) {
			if (state.getTimer() >= nextTimeStamp) {
				Object[] o = bufferedTimestamps.removeIndex(0);

				//check timestamp in case snapshots are sent out of order
				if ((float) o[1] > nextTimeStamp) {
					prevTimeStamp = nextTimeStamp;
					nextTimeStamp = (float) o[1];
				}

				//its ok to sync out of order packets, b/c the interpolation won't do anything
				onClientSync(o[0]);
			} else {
				break;
			}
		}

		//interpolate this entity between most recent snapshots. Use accumulator to be independent from framerate
		clientSyncAccumulator += delta;
		while (clientSyncAccumulator >= Constants.INTERVAL) {
			clientSyncAccumulator -= Constants.INTERVAL;
			clientInterpolation();
		}
	}

	@Override
	public void onServerSync() {}

	@Override
	public void onReceiveSync(Object o, float timestamp) {
		super.onReceiveSync(o, timestamp + 4 * PlayState.SYNC_TIME);

		short statusCode = getStatusCode();

		HadalGame.server.sendToAllExceptUDP(getConnID(), new PacketsSync.SyncPlayer(entityID, getPosition(), getLinearVelocity(),
				entityAge, timestamp + 2 * PlayState.SYNC_TIME, moveState, getBodyData().getCurrentHp(),
				mouseAngle, playerData.getCurrentSlot(),
				playerData.getCurrentTool().isReloading() ? reloadPercent : -1.0f,
				playerData.getCurrentTool().isCharging() ? chargePercent : -1.0f,
				playerData.getCurrentFuel(),
				playerData.getCurrentTool().getClipLeft(), playerData.getCurrentTool().getAmmoLeft(),
				playerData.getActiveItem().chargePercent(),
				getMainFixture().getFilterData().maskBits, blinded, statusCode));
	}

	@Override
	public void onClientSync(Object o) {
		if (o instanceof PacketsSync.SyncClientSnapshot p) {
			if (null != body) {
				prevPos.set(serverPos);
				serverPos.set(p.pos);

				prevVelo.set(serverVelo);
				serverVelo.set(p.velocity);

				serverAngle.setAngleRad(0);
			}
		}
	}
}
