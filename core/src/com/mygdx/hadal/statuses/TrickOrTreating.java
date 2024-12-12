package com.mygdx.hadal.statuses;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.PickupUtils;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.modes.TrickorTreatBucket;
import com.mygdx.hadal.managers.SpriteManager;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.PickupVacuum;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.utils.TextUtil;

import static com.mygdx.hadal.constants.Constants.MAX_NAME_LENGTH;
import static com.mygdx.hadal.managers.SkinManager.FONT_UI;

/**
 * The TrickOrTreating status is applied to players when playing the special event mode.
 * This keeps track of player held candy and process candy stealing, dropping and returning
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

	//these keep track of recently returned candy; used for global message.
	private boolean recentReturn;
	private float recentReturnTime;
	private int recentReturnCount;

	public TrickOrTreating(PlayState state, BodyData i) {
		super(state, i);
		player = ((Player) inflicted.getSchmuck());
		player.getUser().getScoreManager().setExtraModeScore(candyCount);

		this.candyIcon = SpriteManager.getFrame(Sprite.CANDY_A);
	}

	private float controllerCount;
	@Override
	public void timePassing(float delta) {

		//after returning candy, a message is emitted after a delay after last candy is returned
		if (recentReturnTime > 0.0f && state.isServer()) {
			recentReturnTime -= delta;
			if (recentReturnTime < 0.0f) {
				String playerName = TextUtil.getPlayerColorName(player, MAX_NAME_LENGTH);
				state.getUIManager().getKillFeed().addNotification(UIText.CANDY_RETRIEVED.text(playerName, "" + recentReturnCount), true);

				recentReturn = false;
				recentReturnCount = 0;
			}
		}
	}

	@Override
	public void onRender(SpriteBatch batch, Vector2 playerLocation, Vector2 playerSize) {

		//draw candy icon and count when rendering player with candy
		if (0 < player.getUser().getScoreManager().getExtraModeScore()) {
			batch.draw(candyIcon,
					playerLocation.x - playerSize.x / 2 - 20,
					playerLocation.y + playerSize.y / 2 + 35,
					candyIcon.getRegionWidth() * CANDY_ICON_SCALE, candyIcon.getRegionHeight() * CANDY_ICON_SCALE);

			FONT_UI.getData().setScale(FONT_SCALE);
			FONT_UI.draw(batch, "X " + player.getUser().getScoreManager().getExtraModeScore(),
					playerLocation.x - playerSize.x / 2 + 15,
					playerLocation.y + playerSize.y / 2 + 50);
		}
	}

	@Override
	public void onDeath(BodyData perp, DamageSource source, DamageTag... tags) {

		//all candy is dropped on death
		if (0 < candyCount && state.isServer()) {
			PickupUtils.spawnCandy(state, player, player.getPixelPosition(), player.getLinearVelocity(), candyCount);
			incrementCandyCount(-candyCount);
		}
	}

	/**
	 * run when player is nearby bucket. Returns candy to friendly bucket or steals from enemy one
	 */
	public void bucketCheck(TrickorTreatBucket bucket, float delta) {
		if (AlignmentFilter.currentTeams[bucket.getTeamIndex()] != player.getUser().getLoadoutManager().getActiveLoadout().team) {
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

	/**
	 * Reduces bucket candy and spawns a cnady pickup that is vacuumed towards the player
	 */
	private void stealCandy(TrickorTreatBucket bucket) {
		bucket.getEventData().preActivate(null, player);
		Hitbox candy = SyncedAttack.CANDY.initiateSyncedAttackSingle(state, player, bucket.getPixelPosition(), new Vector2());
		PickupVacuum vacuumStrategy = new PickupVacuum(state, candy, player.getBodyData());
		vacuumStrategy.startVacuum(player);
		candy.addStrategy(vacuumStrategy);
	}

	/**
	 * Reduces held candy and spawns a candy pickup that is vacuumed towards the bucket
	 */
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
		player.getUser().getScoreManager().setExtraModeScore(candyCount);
		player.getUser().setScoreUpdated(true);
	}

	/**
	 * This controls candy steal speed. Dependent on the amount of candy left in the bucket
	 */
	private float getCandyStealTime(int bucketCandyCount) {
		float candyScore = Math.min(Math.max(bucketCandyCount - candyCount, 0) / CANDY_STEAL_THRESHOLD_MIN, 1.0f);
		return CANDY_STEAL_TIME_MAX - candyScore * (CANDY_STEAL_TIME_MAX - CANDY_STEAL_TIME_MIN);
	}

	public int getCandyCount() { return candyCount; }
}
