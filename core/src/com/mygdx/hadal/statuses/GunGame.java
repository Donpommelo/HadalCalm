package com.mygdx.hadal.statuses;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.UnlocktoItem;

import static com.mygdx.hadal.save.UnlockEquip.*;

/**

 */
public class GunGame extends Status {


	private static final UnlockEquip[] weaponOrder = {TORPEDO_LAUNCHER, MACHINEGUN, BOOMERANG, BOILER,
		DUELING_CORKGUN, PARTY_POPPER, MINIGUN, BATTERING_RAM, KAMABOKANNON, MORNING_STAR};

	private int currentGunIndex;
	private boolean killedThisFrame;

	public GunGame(PlayState state, BodyData i) {
		super(state, i);
	}

	@Override
	public void playerCreate() {

		Player player = ((Player) inflicted.getSchmuck());
		User user = HadalGame.server.getUsers().get(player.getConnID());
		if (user != null) {
			SavedPlayerFields field = user.getScores();
			currentGunIndex = field.getScore();
		}

		if (currentGunIndex < weaponOrder.length) {
			setWeapon();
		}
	}

	@Override
	public void timePassing(float delta) {
		killedThisFrame = false;
	}

	@Override
	public void onKill(BodyData vic) {
		if (!killedThisFrame) {
			killedThisFrame = true;

			currentGunIndex++;

			if (currentGunIndex < weaponOrder.length) {
				setWeapon();
			} else {
				//TODO: win gaem
			}
		}
	}

	private void setWeapon() {
		Player player = ((Player) inflicted.getSchmuck());
		player.getPlayerData().getMultitools()[0] = UnlocktoItem.getUnlock(weaponOrder[currentGunIndex], player);
		player.getPlayerData().getLoadout().multitools[0] = weaponOrder[currentGunIndex];
		player.getPlayerData().setEquip();
	}
}
