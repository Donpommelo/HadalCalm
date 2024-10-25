package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.managers.PacketManager;
import com.mygdx.hadal.managers.SpriteManager;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.utils.PacketUtil;

/**
 * A ClientPlayer represents a client's own player.
 * This processes things like client prediction
 * @author Hepepper Hasufferson
 */
public class PlayerClientOnHost extends Player {

	public PlayerClientOnHost(PlayState state, Vector2 startPos, String name, PlayerBodyData oldData,
							  User user, boolean reset, Event start) {
		super(state, startPos, name, oldData, user, reset, start);
		receivingSyncs = true;
	}

	@Override
	public void controller(float delta) {
		super.controller(delta);

		//process each buffered snapshot starting from the oldest to the most recent
		while (!bufferedTimestamps.isEmpty()) {
			if (state.getTimer() >= nextTimeStamp) {
				Object[] o = bufferedTimestamps.removeIndex(0);

				if (null != o) {
					//check timestamp in case snapshots are sent out of order
					if ((float) o[1] > nextTimeStamp) {
						prevTimeStamp = nextTimeStamp;
						nextTimeStamp = (float) o[1];
					}
					//its ok to sync out of order packets, b/c the interpolation won't do anything
					onClientSync(o[0]);
				}
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
	protected void processEquipment(float delta) {}

	@Override
	public void onServerSync() {}

	@Override
	public void onReceiveSync(Object o, float timestamp) {
		super.onReceiveSync(o, timestamp);

		if (o instanceof PacketsSync.SyncClientSnapshot p) {
			PacketManager.serverUDPAllExcept(getUser().getConnID(), new PacketsSync.SyncPlayerSnapshot((byte) getUser().getConnID(),
					p.pos, p.velocity, p.mousePosition,
					state.getTimer(), p.moveState,
					p.hpPercent,
					p.fuelPercent,
					p.currentSlot,
					p.reloadPercent,
					p.chargePercent,
					p.conditionCode));
		}
	}

	@Override
	public void onClientSync(Object o) {
		if (o instanceof PacketsSync.SyncClientSnapshot p) {
			getBodyData().setCurrentHp(PacketUtil.byteToPercent(p.hpPercent) * getBodyData().getStat(Stats.MAX_HP));
			getBodyData().setCurrentFuel(PacketUtil.byteToPercent(p.fuelPercent) * getBodyData().getStat(Stats.MAX_FUEL));

			moveState = p.moveState;

			getMouseHelper().setDesiredLocation(p.mousePosition.x, p.mousePosition.y);

			getEquipHelper().setCurrentSlot(p.currentSlot);
			getEquipHelper().setCurrentTool(getEquipHelper().getMultitools()[p.currentSlot]);
			setToolSprite(SpriteManager.getFrame(getEquipHelper().getCurrentTool().getWeaponSprite()));

			float reloadPercent = PacketUtil.byteToPercent(p.reloadPercent);
			getEquipHelper().getCurrentTool().setReloading(reloadPercent != -1.0f, true);
			getUiHelper().setReloadPercent(reloadPercent);
			getEquipHelper().getCurrentTool().setReloadCd(reloadPercent);

			float chargePercent = PacketUtil.byteToPercent(p.chargePercent);
			getEquipHelper().getCurrentTool().setCharging(chargePercent != -1.0f);
			getUiHelper().setChargePercent(chargePercent);
			getEquipHelper().getCurrentTool().setChargeCd(chargePercent);

			processConditionCode(p.conditionCode);

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
