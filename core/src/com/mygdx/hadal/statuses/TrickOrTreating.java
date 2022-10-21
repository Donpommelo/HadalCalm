package com.mygdx.hadal.statuses;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.modes.TrickorTreatBucket;
import com.mygdx.hadal.event.modes.TrickorTreatCandy;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;

/**
 */
public class TrickOrTreating extends Status {

	private static final float CANDY_STEAL_TIME_MIN = 0.2f;
	private static final float CANDY_STEAL_TIME_MAX = 1.6f;
	private static final float CANDY_STEAL_THRESHOLD_MIN = 20;

	private static final float CANDY_RETURN_TIME_MIN = 1.0f;

	private final Player player;
	private int candyCount;

	public TrickOrTreating(PlayState state, BodyData i) {
		super(state, i);
		player = ((Player) inflicted.getSchmuck());
	}

	private float controllerCount;
	@Override
	public void timePassing(float delta) {
		Event event = player.getCurrentEvent();
		if (null != event) {
			if (event instanceof TrickorTreatBucket bucket) {
				if (AlignmentFilter.currentTeams[bucket.getTeamIndex()] != player.getPlayerData().getLoadout().team) {
					int enemyCandyCount = AlignmentFilter.teamScores[bucket.getTeamIndex()];
					if (0 < enemyCandyCount) {
						controllerCount += delta;
						if (controllerCount >= getCandyStealTime(enemyCandyCount)) {
							stealCandy(bucket);
							controllerCount = 0;
						}
					}
				} else {
					if (0 < candyCount) {
						controllerCount += delta;
						if (controllerCount >= CANDY_RETURN_TIME_MIN) {
							returnCandy(bucket);
							controllerCount = 0;
						}
					}
				}
			}
		}
	}

	private void stealCandy(TrickorTreatBucket bucket) {
		state.getMode().processTeamScoreChange(state, bucket.getTeamIndex(), -1);
		new TrickorTreatCandy(state, bucket.getPixelPosition());
	}

	private void returnCandy(TrickorTreatBucket bucket) {
		state.getMode().processPlayerScoreChange(state, player, 1);
		state.getMode().processTeamScoreChange(state, bucket.getTeamIndex(), 1);
		candyCount--;
	}

	private float getCandyStealTime(int bucketCandyCount) {
		float candyScore = Math.min(Math.max(bucketCandyCount - candyCount, 0) / CANDY_STEAL_THRESHOLD_MIN, 1.0f);
		return CANDY_STEAL_TIME_MAX - candyScore * (CANDY_STEAL_TIME_MAX - CANDY_STEAL_TIME_MIN);
	}

	public void incrementCandyCount() { candyCount++; }
}
