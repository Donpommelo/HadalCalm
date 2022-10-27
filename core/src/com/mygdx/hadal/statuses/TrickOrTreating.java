package com.mygdx.hadal.statuses;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.PickupUtils;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.modes.TrickorTreatBucket;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.PickupVacuum;
import com.mygdx.hadal.text.UIText;

import static com.mygdx.hadal.constants.Constants.MAX_NAME_LENGTH;

/**
 */
public class TrickOrTreating extends Status {

	private static final float CANDY_STEAL_TIME_MIN = 0.2f;
	private static final float CANDY_STEAL_TIME_MAX = 1.5f;
	private static final float CANDY_STEAL_THRESHOLD_MIN = 10;
	private static final float CANDY_ICON_SCALE = 0.5f;
	private static final float FONT_SCALE = 0.3f;

	private static final float CANDY_RETURN_TIME_MIN = 0.3f;
	private static final float RETURN_MESSAGE_TIMER = 1.0f;

	private final TextureRegion candyIcon;
	private final Player player;
	private int candyCount;

	private boolean recentReturn;
	private float recentReturnTime;
	private int recentReturnCount;

	public TrickOrTreating(PlayState state, BodyData i) {
		super(state, i);
		player = ((Player) inflicted.getSchmuck());
		player.getUser().getScores().setExtraModeScore(candyCount);

		this.candyIcon = Sprite.CANDY_A.getFrame();
	}

	private float controllerCount;
	@Override
	public void timePassing(float delta) {
		if (0.0f < recentReturnTime ) {
			recentReturnTime -= delta;
			if (0.0f > recentReturnTime) {
				String playerName = WeaponUtils.getPlayerColorName(player, MAX_NAME_LENGTH);
				state.getKillFeed().addNotification(UIText.CANDY_RETRIEVED.text(playerName, "" + recentReturnCount), true);

				recentReturn = false;
				recentReturnCount = 0;
			}
		}
	}

	@Override
	public void onRender(SpriteBatch batch, Vector2 playerLocation, Vector2 playerSize) {
		if (0 < player.getUser().getScores().getExtraModeScore()) {
			batch.draw(candyIcon,
					playerLocation.x - playerSize.x / 2 - 20,
					playerLocation.y + playerSize.y / 2 + 35,
					candyIcon.getRegionWidth() * CANDY_ICON_SCALE, candyIcon.getRegionHeight() * CANDY_ICON_SCALE);

			HadalGame.FONT_UI.getData().setScale(FONT_SCALE);
			HadalGame.FONT_UI.draw(batch, "X " + player.getUser().getScores().getExtraModeScore(),
					playerLocation.x - playerSize.x / 2 + 15,
					playerLocation.y + playerSize.y / 2 + 50);
		}
	}

	@Override
	public void onDeath(BodyData perp, DamageSource source) {
		if (0 < candyCount) {
			PickupUtils.spawnCandy(state, player, player.getPixelPosition(), player.getLinearVelocity(), candyCount);
			incrementCandyCount(-candyCount);
		}
	}

	public void bucketCheck(TrickorTreatBucket bucket, float delta) {
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

	private void stealCandy(TrickorTreatBucket bucket) {
		state.getMode().processTeamScoreChange(state, bucket.getTeamIndex(), -1);
		Hitbox candy = SyncedAttack.CANDY.initiateSyncedAttackSingle(state, player, bucket.getPixelPosition(), new Vector2());
		PickupVacuum vacuumStrategy = new PickupVacuum(state, candy, player.getBodyData());
		vacuumStrategy.startVacuum(player);
		candy.addStrategy(vacuumStrategy);
	}

	private void returnCandy(TrickorTreatBucket bucket) {
		incrementCandyCount(-1);

		Hitbox candy = SyncedAttack.CANDY.initiateSyncedAttackSingle(state, player, player.getPixelPosition(), new Vector2());
		PickupVacuum vacuumStrategy = new PickupVacuum(state, candy, player.getBodyData());
		vacuumStrategy.startVacuum(bucket);
		candy.addStrategy(vacuumStrategy);

		recentReturnTime = RETURN_MESSAGE_TIMER;
		if (recentReturn) {
			recentReturnCount++;
		} else {
			recentReturn = true;
			recentReturnCount = 1;
		}
	}

	public void incrementCandyCount(int amount) {
		candyCount += amount;
		player.getUser().getScores().setExtraModeScore(candyCount);
		player.getUser().setScoreUpdated(true);
	}

	private float getCandyStealTime(int bucketCandyCount) {
		float candyScore = Math.min(Math.max(bucketCandyCount - candyCount, 0) / CANDY_STEAL_THRESHOLD_MIN, 1.0f);
		return CANDY_STEAL_TIME_MAX - candyScore * (CANDY_STEAL_TIME_MAX - CANDY_STEAL_TIME_MIN);
	}

	public int getCandyCount() { return candyCount; }
}
