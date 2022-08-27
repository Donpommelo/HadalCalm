package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.PlayerSpriteHelper;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.save.UnlockCosmetic;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.AlignmentFilter;

/**
 * A HubOptionPlayer is a special HubOption that contains a player sprite helper
 * This is used when a ui element must render a player; including its cosmetics and team color shader
 * @author Mavatappi Morcfold
 */
public class HubOptionPlayer extends HubOption {

	private static final float SCALE = 0.35f;
	private static final Vector2 PLAYER_SPRITE_OFFSET = new Vector2(150, 250);

	private final UnlockCosmetic cosmetic;
	private final boolean renderCosmetic;
	private final PlayerSpriteHelper playerSpriteHelper;
	private final Vector2 playerOffset = new Vector2(PLAYER_SPRITE_OFFSET);

	private float attackAngle;
	private MoveState moveState = MoveState.MOVE_LEFT;
	private float animationTime;

	//should the player sprite be bobbing up and down as it moves?
	private boolean bob = true;

	public HubOptionPlayer(String text, Player player, UnlockCharacter character, AlignmentFilter team,
						   boolean renderCosmetic, UnlockCosmetic cosmetic, float scale) {
		super(text, null);
		this.renderCosmetic = renderCosmetic;
		this.cosmetic = cosmetic;
		this.playerSpriteHelper = new PlayerSpriteHelper(player, scale);
		playerSpriteHelper.replaceBodySprite(player.getState().getBatch(), character, team);
	}

	public HubOptionPlayer(String text, Player player, UnlockCharacter character, AlignmentFilter team,
						   boolean renderCosmetic, UnlockCosmetic cosmetic) {
		this(text, player, character, team, renderCosmetic, cosmetic, SCALE);
	}

	@Override
    public void draw(Batch batch, float alpha) {
		super.draw(batch, alpha);

		playerSpriteHelper.render(batch, attackAngle, moveState, animationTime, animationTime,
				true, playerOffset, renderCosmetic, cosmetic, bob);
    }

    @Override
	public void act(float delta) {
		super.act(delta);
		animationTime += delta;
	}

	@Override
	public boolean remove() {
		playerSpriteHelper.dispose(PlayerSpriteHelper.DespawnType.LEVEL_TRANSITION);
		return super.remove();
	}

	public HubOptionPlayer setPlayerOffset(Vector2 playerOffset) {
		this.playerOffset.set(playerOffset);
		updateHitBox();
		return this;
	}

	public HubOptionPlayer setAttackAngle(float attackAngle) {
		this.attackAngle = attackAngle;
		return this;
	}

	public HubOptionPlayer setMoveState(MoveState moveState) {
		this.moveState = moveState;
		return this;
	}

	public HubOptionPlayer setBob(boolean bob) {
		this.bob = bob;
		return this;
	}

	public PlayerSpriteHelper getPlayerSpriteHelper() { return playerSpriteHelper; }
}
