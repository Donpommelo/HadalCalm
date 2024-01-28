package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.PacketUtil;

import static com.mygdx.hadal.states.PlayState.SYNC_TIME;

/**
 * A ClientPlayer represents a client's own player.
 * This processes things like client prediction
 * @author Hepepper Hasufferson
 */
public class PlayerSelfOnClient extends Player {

	public PlayerSelfOnClient(PlayState state, Vector2 startPos, String name, Loadout startLoadout, PlayerBodyData oldData,
							  User user, boolean reset, Event start) {
		super(state, startPos, name, startLoadout, oldData, user, reset, start);
	}

	private float syncAccumulator;
	@Override
	public void clientController(float delta) {
		super.clientController(delta);

		processMovement(delta, playerVelocity);
		processEquipment(delta);
		processMiscellaneous(delta);

		//Apply base hp regen
		getBodyData().regainHp(getBodyData().getStat(Stats.HP_REGEN) * delta, getBodyData(), true, DamageTag.REGEN);

		syncAccumulator += delta;
		if (syncAccumulator >= SYNC_TIME) {
			syncAccumulator = 0;

			short conditionCode = getConditionCode();
			float adjustedTime = state.getTimer() + 2 * ((ClientState) state).getLatency() + 4 * PlayState.SYNC_TIME;

			HadalGame.client.sendUDP(new PacketsSync.SyncClientSnapshot(getPosition(), getLinearVelocity(),
					getMouseHelper().getPosition(),	adjustedTime, moveState,
					PacketUtil.percentToByte(getBodyData().getCurrentHp() / getBodyData().getStat(Stats.MAX_HP)),
					PacketUtil.percentToByte(getBodyData().getCurrentFuel() / getBodyData().getStat(Stats.MAX_FUEL)),
					(byte) getEquipHelper().getCurrentSlot(),
					PacketUtil.percentToByte(getEquipHelper().getCurrentTool().isReloading() ? getUiHelper().getReloadPercent() : -1.0f),
					PacketUtil.percentToByte(getEquipHelper().getCurrentTool().isCharging() ? getUiHelper().getChargePercent() : -1.0f),
					conditionCode));
		}
	}

	@Override
	public void clientInterpolation() {}
}
