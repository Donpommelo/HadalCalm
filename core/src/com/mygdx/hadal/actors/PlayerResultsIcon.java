package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.ShadedSprite;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.save.CosmeticSlot;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.save.UnlockCosmetic;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.server.SavedPlayerFieldsExtra;
import com.mygdx.hadal.text.UIText;

import static com.mygdx.hadal.utils.Constants.MAX_NAME_LENGTH;

/**
 * A PlayerResultsIcon represents a single player in the results screen.
 * These are ordered by score
 */
public class PlayerResultsIcon extends AHadalActor {

	private static final float fontScale = 0.22f;
	private static final float spriteScale = 0.25f;

	private static final float readyWidth = 96.0f;
	private static final float readyHeight = 96.0f;

	private static final float spriteHeight = 922.0f;
	private static final float spriteWidth = 614.0f;
	private static final float spriteOffsetY = 10.0f;

	private static final float textOffsetX = 20.0f;
	private static final float textOffsetY = -10.0f;
	private static final float textWidth = 125.0f;

	private static final float readyOffsetX = 20.0f;
	private static final float readyOffsetY = 150.0f;

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

	public PlayerResultsIcon(SpriteBatch batch, SavedPlayerFields fields, SavedPlayerFieldsExtra fieldsExtra) {
		this.name = UIText.RESULTS_INFO.text(fields.getNameAbridged(MAX_NAME_LENGTH), Integer.toString(fields.getKills()),
				Integer.toString(fields.getDeaths()), Integer.toString(fields.getAssists()), Integer.toString(fields.getScore()));

		this.readyIcon = Sprite.EMOTE_READY.getFrame();
		this.team = fieldsExtra.getLoadout().team;
		this.character = fieldsExtra.getLoadout().character;
		this.cosmetics = fieldsExtra.getLoadout().cosmetics;

		//if the player won the game, we display a winning sprite. Otherwise: sluggo.
		Array<TextureRegion> playerSprite = new Array<>();
		if (fields.isWonLast()) {
			playerSprite.addAll(character.getBuffSprite().getFrames());
			this.iconWidth = character.getBuffSprite().getFrame().getRegionWidth() * spriteScale;
			this.iconHeight = character.getBuffSprite().getFrame().getRegionHeight() * spriteScale;
			this.cosmeticOffset.set(character.getBuffHatOffset()).scl(spriteScale);
		} else {
			playerSprite.addAll(character.getSlugSprite().getFrames());
			this.iconWidth = character.getSlugSprite().getFrame().getRegionWidth() * spriteScale;
			this.iconHeight = character.getSlugSprite().getFrame().getRegionHeight() * spriteScale;
			this.cosmeticOffset.set(character.getSlugHatOffset()).scl(spriteScale);
		}

		setHeight(spriteHeight * spriteScale);
		setWidth(spriteWidth * spriteScale);

		//Based on the player color, we create an fbo to accurately display their sprite.
		shadedSprite = new ShadedSprite(batch, team, character, playerSprite.toArray());
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
		float spriteX = getX() + spriteOffsetY;
		float spriteY = getY();
		batch.draw(shadedSprite.getSprite(), spriteX, spriteY, iconWidth, iconHeight);
		cosmeticLocation.set(spriteX + cosmeticOffset.x, spriteY + cosmeticOffset.y);
		//draw cosmetics on the slug/buff players
		for (UnlockCosmetic cosmetic : cosmetics) {
			if (!cosmetic.getCosmeticSlot().equals(CosmeticSlot.HEAD)) {
				cosmetic.render(batch, team, character, animationTimeExtra, spriteScale, false, cosmeticLocation);
			}
		}

		HadalGame.FONT_UI.getData().setScale(fontScale);
		HadalGame.FONT_UI.draw(batch, name, getX() + textOffsetX,getY() + textOffsetY, textWidth, Align.center, true);

		if (ready) {
			batch.draw(readyIcon, getX() + readyWidth + readyOffsetX, getY() + readyOffsetY, -readyWidth, readyHeight);
		}
    }

    public void dispose() {
		shadedSprite.dispose();
	}

	public void setReady(boolean ready) { this.ready = ready; }
}
