package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.PlayerSpriteHelper;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.AlignmentFilter;

/**
 */
public class HubOptionPlayer extends HubOption {

	private static final float SCALE = 0.35f;
	private static final Vector2 PLAYER_SPRITE_OFFSET = new Vector2(150, 250);

	private final PlayerSpriteHelper playerSpriteHelper;
	private float animationTime;

	public HubOptionPlayer(String text, Player player, UnlockCharacter character, AlignmentFilter team) {
		super(text, null);
		this.playerSpriteHelper = new PlayerSpriteHelper(player, SCALE);
		playerSpriteHelper.replaceBodySprite(player.getState().getBatch(), character, team);
	}

	@Override
    public void draw(Batch batch, float alpha) {
		super.draw(batch, alpha);
		playerSpriteHelper.render(batch, 0.0f, MoveState.MOVE_LEFT, animationTime, animationTime,
				true, PLAYER_SPRITE_OFFSET, false);
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

	public PlayerSpriteHelper getPlayerSpriteHelper() { return playerSpriteHelper; }
}
