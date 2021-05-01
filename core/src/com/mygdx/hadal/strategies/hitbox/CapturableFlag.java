package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.SpawnerFlag;
import com.mygdx.hadal.event.userdata.EventData;
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

/**
 */
public class CapturableFlag extends HitboxStrategy {

	//this is the entity that this hbox is fixed to. Usually the user for melee hboxes. Some hboxes have another hboxes fixed to them like sticky bombs
	private Player target;
	private Player lastHolder;

	private final int teamIndex;

	private boolean captured, awayFromSpawn;

	private static final float returnTime = 10.0f;
	private float returnTimer;

	private Status flagDebuff;

	public CapturableFlag(PlayState state, Hitbox proj, BodyData user, int teamIndex) {
		super(state, proj, user);
		this.teamIndex = teamIndex;

		hbox.setSyncDefault(false);
		hbox.setSyncInstant(true);
	}

	@Override
	public void onHit(HadalData fixB) {

		if (fixB != null) {
			if (fixB.getEntity() instanceof SpawnerFlag) {

				if (((SpawnerFlag) fixB.getEntity()).getTeamIndex() != teamIndex) {
					((EventData) fixB).preActivate(null, lastHolder);
					hbox.die();

					if (target != null) {
						if (target.getPlayerData() != null) {
							if (flagDebuff != null) {
								target.getPlayerData().removeStatus(flagDebuff);
							}
						}
					}
				}
			}
		}

		if (!captured) {
			if (fixB != null) {
				if (fixB instanceof PlayerBodyData) {

					if (teamIndex < AlignmentFilter.currentTeams.length) {
						if (((PlayerBodyData) fixB).getLoadout().team != AlignmentFilter.currentTeams[teamIndex]) {
							captured = true;
							awayFromSpawn = true;
							target = ((PlayerBodyData) fixB).getPlayer();
							lastHolder = target;
							flagDebuff = new CarryingFlag(state, target.getBodyData());
							target.getPlayerData().addStatus(flagDebuff);

							hbox.getBody().setGravityScale(0.0f);
						}
					}
				}
			}
		}
	}

	private final Vector2 hbLocation = new Vector2();
	@Override
	public void controller(float delta) {

		if (captured) {
			if (!target.isAlive()) {
				captured = false;
				hbox.getBody().setGravityScale(1.0f);
				returnTimer = returnTime;

				if (teamIndex < AlignmentFilter.currentTeams.length) {
					String teamColor = AlignmentFilter.currentTeams[teamIndex].getAdjective();
					state.getKillFeed().addNotification(teamColor + " FLAG WAS DROPPED!", true);
				}

			} else {
				hbLocation.set(target.getPosition());
				hbox.setTransform(hbLocation, hbox.getAngle());
			}
		} else if (awayFromSpawn) {
			returnTimer -= delta;

			if (returnTimer <= 0.0f) {
				hbox.die();

				if (teamIndex < AlignmentFilter.currentTeams.length) {
					String teamColor = AlignmentFilter.currentTeams[teamIndex].getAdjective();
					state.getKillFeed().addNotification(teamColor + " FLAG WAS RETURNED!" , true);
				}
			}
		}
	}
}
