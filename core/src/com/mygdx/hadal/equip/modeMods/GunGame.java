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

/**
 * This "Artifact" is automaticall applied to all characters when the gun-game mode is selected
 * it process the mode's weapon switching and win condition
 * @author Quamilton Quirfitticelli
 */
public class GunGame extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 0;

	//this is an ordered list of weapons the player cycles through upon getting kills
	public static final UnlockEquip[] weaponOrder = {TORPEDO_LAUNCHER, CR4PCANNON, CHARGE_BEAM, BOUNCING_BLADE,
		SNIPER_RIFLE, BANANA, ICEBERG, LASER_RIFLE, BOILER, MINIGUN, LASER_GUIDED_ROCKET, BATTERING_RAM,
	MORAYGUN, PARTY_POPPER, TRICK_GUN, DUELING_CORKGUN, TESLA_COIL, FISTICUFFS};

	public GunGame() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {

			//this is the index of the gun we are currently on
			private int currentGunIndex;

			//this is here to prevent multiple kills on the same frame from registering as multiple weapon cycles
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

					//advance to the next weapon and update score
					currentGunIndex++;
					state.getUiExtra().changeFields(((Player) inflicted.getSchmuck()), 1, 0, 0.0f, 0.0f, false);
					setWeapon();

					//give the player a message about their new weapon and overall progress
					if (currentGunIndex < weaponOrder.length) {
						String message = weaponOrder[currentGunIndex].toString() + ": " + currentGunIndex + "/" + weaponOrder.length;
						state.getKillFeed().sendNotification(message, ((Player) inflicted.getSchmuck()));
					}
				}
			}

			/**
			 * This updates the player's weapon
			 */
			private void setWeapon() {

				if (currentGunIndex < weaponOrder.length) {
					Player player = ((Player) inflicted.getSchmuck());

					//we identify their next weapon based on their score (so that it properly updates upon respawning)
					User user = HadalGame.server.getUsers().get(player.getConnID());
					if (user != null) {
						SavedPlayerFields field = user.getScores();
						currentGunIndex = field.getScore();
					}

					//this sets the player's weapon to the new one and syncs client loadouts
					player.getPlayerData().getMultitools()[0] = UnlocktoItem.getUnlock(weaponOrder[currentGunIndex], player);
					player.getPlayerData().getLoadout().multitools[0] = weaponOrder[currentGunIndex];
					player.getPlayerData().setEquip();
					player.getPlayerData().syncServerLoadoutChange(false);
				} else {

					//upon finishing all weapons, we end the game
					state.levelEnd(ResultsState.magicWord, false);
				}
			}
		};
		return enchantment;
	}
}
