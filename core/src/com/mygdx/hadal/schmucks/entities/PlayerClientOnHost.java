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
 * A PlayerClientOnHost represents a client's player on the server.
 * This receives sync packets from the clients and echoes them to all other clients.
 * @author Clodswallop Clidimir
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

				if (o != null) {
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
					p.posX, p.posY, p.veloX, p.veloY, p.mouseX, p.mouseY,
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

			getMouseHelper().setDesiredLocation(PacketUtil.shortToFloat(p.mouseX), PacketUtil.shortToFloat(p.mouseY));

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
				serverPos.set(PacketUtil.shortToFloat(p.posX), PacketUtil.shortToFloat(p.posY));

				prevVelo.set(serverVelo);
				serverVelo.set(PacketUtil.shortToFloat(p.veloX), PacketUtil.shortToFloat(p.veloY));

				serverAngle.setAngleRad(0);
			}
		}
	}
}
