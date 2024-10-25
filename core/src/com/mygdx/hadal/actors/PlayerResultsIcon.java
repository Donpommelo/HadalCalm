package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.effects.ShadedSprite;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.SpriteManager;
import com.mygdx.hadal.save.CosmeticSlot;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.save.UnlockCosmetic;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.users.LoadoutManager;
import com.mygdx.hadal.users.ScoreManager;
import com.mygdx.hadal.users.StringManager;
import com.mygdx.hadal.users.User;

import static com.mygdx.hadal.constants.Constants.MAX_NAME_LENGTH;
import static com.mygdx.hadal.managers.SkinManager.FONT_UI;

/**
 * A PlayerResultsIcon represents a single player in the results screen.
 * These are ordered by score
 */
public class PlayerResultsIcon extends AHadalActor {

	private static final float FONT_SCALE = 0.22f;
	private static final float SPRITE_SCALE = 0.25f;

	private static final float READY_WIDTH = 96.0f;
	private static final float READY_HEIGHT = 96.0f;

	private static final float SPRITE_HEIGHT = 922.0f;
	private static final float SPRITE_WIDTH = 614.0f;
	private static final float SPRITE_OFFSET_Y = 10.0f;

	private static final float TEXT_OFFSET_X = 20.0f;
	private static final float TEXT_OFFSET_Y = -10.0f;
	private static final float TEXT_WIDTH = 125.0f;

	private static final float READY_OFFSET_X = 20.0f;
	private static final float READY_OFFSET_Y = 150.0f;

	private final ShadedSprite shadedSprite;
	private final AlignmentFilter team;
	private final UnlockCharacter character;
	private final UnlockCosmetic[] cosmetics;
	private final TextureRegion readyIcon;
	private final float iconWidth, iconHeight;

	private final Vector2 cosmeticOffset = new Vector2();

	//this string identifies the player as well as their score information
	private final String name;

	//has this player readied up?
	private boolean ready;

	public PlayerResultsIcon(SpriteBatch batch, User user) {
		ScoreManager scoreManager = user.getScoreManager();
		StringManager stringManager = user.getStringManager();
		LoadoutManager loadoutManager = user.getLoadoutManager();
		this.name = UIText.RESULTS_INFO.text(stringManager.getNameAbridged(MAX_NAME_LENGTH), Integer.toString(scoreManager.getKills()),
				Integer.toString(scoreManager.getDeaths()), Integer.toString(scoreManager.getAssists()), Integer.toString(scoreManager.getScore()));

		this.readyIcon = SpriteManager.getFrame(Sprite.EMOTE_READY);
		this.team = loadoutManager.getActiveLoadout().team;
		this.character = loadoutManager.getActiveLoadout().character;
		this.cosmetics = loadoutManager.getActiveLoadout().cosmetics;

		//if the player won the game, we display a winning sprite. Otherwise: sluggo.
		Array<TextureRegion> playerSprite = new Array<>();
		if (scoreManager.isWonLast()) {
			playerSprite.addAll(SpriteManager.getFrames(character.getBuffSprite()));
			this.iconWidth = SpriteManager.getFrame(character.getBuffSprite()).getRegionWidth() * SPRITE_SCALE;
			this.iconHeight = SpriteManager.getFrame(character.getBuffSprite()).getRegionHeight() * SPRITE_SCALE;
			this.cosmeticOffset.set(character.getBuffHatOffset()).scl(SPRITE_SCALE);
		} else {
			playerSprite.addAll(SpriteManager.getFrames(character.getSlugSprite()));
			this.iconWidth = SpriteManager.getFrame(character.getSlugSprite()).getRegionWidth() * SPRITE_SCALE;
			this.iconHeight = SpriteManager.getFrame(character.getSlugSprite()).getRegionHeight() * SPRITE_SCALE;
			this.cosmeticOffset.set(character.getSlugHatOffset()).scl(SPRITE_SCALE);
		}

		setHeight(SPRITE_HEIGHT * SPRITE_SCALE);
		setWidth(SPRITE_WIDTH * SPRITE_SCALE);

		//create sprite with shader applied. Class is necessary to avoid class cast exception
		shadedSprite = new ShadedSprite(batch, team, character, playerSprite.toArray(TextureRegion.class));
	}

	private float animationTimeExtra;
	@Override
	public void act(float delta) {
		super.act(delta);
		animationTimeExtra += delta;
	}

	private final Vector2 cosmeticLocation = new Vector2();
	@Override
    public void draw(Batch batch, float alpha) {
		float spriteX = getX() + SPRITE_OFFSET_Y;
		float spriteY = getY();
		batch.draw(shadedSprite.getSprite(), spriteX, spriteY, iconWidth, iconHeight);
		cosmeticLocation.set(spriteX + cosmeticOffset.x, spriteY + cosmeticOffset.y);
		//draw cosmetics on the slug/buff players
		for (UnlockCosmetic cosmetic : cosmetics) {
			if (!CosmeticSlot.HEAD.equals(cosmetic.getCosmeticSlot())) {
				cosmetic.render(batch, team, character, animationTimeExtra, SPRITE_SCALE, false, cosmeticLocation, cosmeticLocation);
			}
		}

		FONT_UI.getData().setScale(FONT_SCALE);
		FONT_UI.draw(batch, name, getX() + TEXT_OFFSET_X,getY() + TEXT_OFFSET_Y, TEXT_WIDTH, Align.center, true);

		if (ready) {
			batch.draw(readyIcon, getX() + READY_WIDTH + READY_OFFSET_X, getY() + READY_OFFSET_Y, -READY_WIDTH, READY_HEIGHT);
		}
    }

    public void dispose() {
		shadedSprite.dispose();
	}

	public void setReady(boolean ready) { this.ready = ready; }
}
