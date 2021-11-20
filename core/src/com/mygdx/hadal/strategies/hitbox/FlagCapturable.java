package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.event.FlagBlocker;
import com.mygdx.hadal.event.SpawnerFlag;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.CarryingFlag;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.text.HText;

import static com.mygdx.hadal.utils.Constants.MAX_NAME_LENGTH;

/**
 * This strategy indicates that this hbox is a flag in capture-the-flag mode
 *
 * @author Hufferty Hibbooey
 */
public class FlagCapturable extends HitboxStrategy {

	//this is the entity that this hbox is fixed to. Usually the user for melee hboxes. Some hboxes have another hboxes fixed to them like sticky bombs
	private Player target;
	private Player lastHolder;

	//the team who this flag belongs to
	private final int teamIndex;

	//is the flag held by a player? Has the flag been removed from its spawn location?
	private boolean captured, awayFromSpawn;

	//the timer until a dropped flag returns to spawn
	private static final float returnTime = 10.0f;
	private float returnTimer;

	//this is a status inflicted upon the flag carrier
	private Status flagDebuff;

	private final SpawnerFlag spawner;
	private FlagBlocker lastBlocker;

	public FlagCapturable(PlayState state, Hitbox proj, BodyData user, SpawnerFlag spawner, int teamIndex) {
		super(state, proj, user);
		this.spawner = spawner;
		this.teamIndex = teamIndex;

		hbox.setSyncDefault(false);
		hbox.setSyncInstant(true);
	}

	@Override
	public void onHit(HadalData fixB) {

		if (fixB != null) {
			if (!captured) {
				if (fixB instanceof PlayerBodyData playerData) {

					boolean blockPickup = false;
					if (lastBlocker != null) {
						if (lastBlocker.getEventData().getSchmucks().contains(hbox) &&
								lastBlocker.getEventData().getSchmucks().contains(fixB.getEntity())) {
							blockPickup = true;
						}
					}

					if (!blockPickup) {
						//if the flag touches an enemy player, it is picked up, displaying a notification and tracking the player
						if (teamIndex < AlignmentFilter.currentTeams.length) {
							if (playerData.getLoadout().team != AlignmentFilter.currentTeams[teamIndex]) {
								captured = true;
								awayFromSpawn = true;
								target = playerData.getPlayer();
								lastHolder = target;
								flagDebuff = new CarryingFlag(state, target.getBodyData());
								target.getPlayerData().addStatus(flagDebuff);

								hbox.getBody().setGravityScale(0.0f);
								String playerName = WeaponUtils.getPlayerColorName(target, MAX_NAME_LENGTH);
								state.getKillFeed().addNotification(HText.CTF_PICKUP.text(playerName), true);

								spawner.setFlagPresent(false);
							}
						}
					}
				}
			} else {
				//if touching a "flag blocker" whule held, the flag is automatically dropped
				if (fixB.getEntity() instanceof FlagBlocker blocker) {
					if (blocker.getTeamIndex() != teamIndex) {
						dropFlag();
						lastBlocker = blocker;
					}
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
				dropFlag();
			} else {
				hbLocation.set(target.getPosition());
				hbox.setTransform(hbLocation, hbox.getAngle());
			}
		} else if (awayFromSpawn) {
			returnTimer -= delta;

			if (returnTimer <= 0.0f) {
				hbox.die();

				if (teamIndex < AlignmentFilter.currentTeams.length) {
					String teamColor = AlignmentFilter.currentTeams[teamIndex].getColoredAdjective();
					teamColor = WeaponUtils.getColorName(AlignmentFilter.currentTeams[teamIndex].getColor1(), teamColor);
					state.getKillFeed().addNotification(HText.CTF_RETURN.text(teamColor), true);
				}
			}
		}
	}

	public void checkCapture(SpawnerFlag flag) {
		//if this hbox touches an enemy flag spawn, it is "captured", scoring a point and disappearing
		if (flag.getTeamIndex() != teamIndex) {

			//in order to capture, you must have your own flag present.
			if (flag.isFlagPresent()) {
				flag.getEventData().preActivate(null, lastHolder);
				hbox.die();

				if (target != null) {
					if (target.getPlayerData() != null) {
						if (flagDebuff != null) {
							target.getPlayerData().removeStatus(flagDebuff);
						}
					}
				}
			} else {
				flag.triggerFailMessage();
			}
		}
	}

	private void dropFlag() {
		captured = false;
		hbox.getBody().setGravityScale(1.0f);
		returnTimer = returnTime;

		if (teamIndex < AlignmentFilter.currentTeams.length) {
			String teamColor = AlignmentFilter.currentTeams[teamIndex].getColoredAdjective();
			state.getKillFeed().addNotification(HText.CTF_DROPPED.text(teamColor), true);
		}
	}

	public SpawnerFlag getSpawner() { return spawner; }

	public Player getTarget() { return target; }

	public int getTeamIndex() { return teamIndex; }

	public boolean isCaptured() { return captured; }
}
