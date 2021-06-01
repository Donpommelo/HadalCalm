package com.mygdx.hadal.equip.modeMods;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.ResultsState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.UnlocktoItem;

import static com.mygdx.hadal.save.UnlockEquip.*;

public class GunGame extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 0;

	public static final UnlockEquip[] weaponOrder = {TORPEDO_LAUNCHER, CR4PCANNON, CHARGE_BEAM, BOILER,
		SNIPER_RIFLE, BANANA, ICEBERG, LASER_RIFLE, BOUNCING_BLADE, MINIGUN, LASER_GUIDED_ROCKET, BATTERING_RAM,
	MORAYGUN, PARTY_POPPER, TRICK_GUN, DUELING_CORKGUN, TESLA_COIL, FISTICUFFS};

	public GunGame() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {

			private int currentGunIndex;
			private boolean killedThisFrame;

			@Override
			public void playerCreate() {
				setWeapon();
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
					state.getUiExtra().changeFields(((Player) inflicted.getSchmuck()), 1, 0, 0.0f, 0.0f, false);
					setWeapon();

					if (currentGunIndex < weaponOrder.length) {
						String message = weaponOrder[currentGunIndex].toString() + ": " + currentGunIndex + "/" + weaponOrder.length;
						state.getKillFeed().sendNotification(message, ((Player) inflicted.getSchmuck()));
					}
				}
			}

			private void setWeapon() {

				if (currentGunIndex < weaponOrder.length) {
					Player player = ((Player) inflicted.getSchmuck());

					User user = HadalGame.server.getUsers().get(player.getConnID());
					if (user != null) {
						SavedPlayerFields field = user.getScores();
						currentGunIndex = field.getScore();
					}

					player.getPlayerData().getMultitools()[0] = UnlocktoItem.getUnlock(weaponOrder[currentGunIndex], player);
					player.getPlayerData().getLoadout().multitools[0] = weaponOrder[currentGunIndex];
					player.getPlayerData().setEquip();
					player.getPlayerData().syncServerLoadoutChange(false);
				} else {
					state.levelEnd(ResultsState.magicWord, false);
				}
			}
		};
		return enchantment;
	}
}
