package com.mygdx.hadal.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Sprite;
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

	private TextureRegion playerSprite;
	private final UnlockCharacter character;
	private final UnlockCosmetic[] cosmetics;
	private final TextureRegion readyIcon;

	private final Vector2 cosmeticOffset = new Vector2();

	//this string identifies the player as well as their score information
	private final String name;

	//this fbo is used to accurately draw their sprite with the correct team color
	private final FrameBuffer fbo;

	//has this player readied up?
	private boolean ready;

	public PlayerResultsIcon(SpriteBatch batch, SavedPlayerFields fields, SavedPlayerFieldsExtra fieldsExtra) {
		this.name = UIText.RESULTS_INFO.text(fields.getNameAbridged(MAX_NAME_LENGTH), Integer.toString(fields.getKills()),
				Integer.toString(fields.getDeaths()), Integer.toString(fields.getAssists()), Integer.toString(fields.getScore()));

		this.readyIcon = Sprite.EMOTE_READY.getFrame();
		this.character = fieldsExtra.getLoadout().character;
		this.cosmetics = fieldsExtra.getLoadout().cosmetics;

		//if the player won the game, we display a winning sprite. Otherwise: sluggo.
		if (fields.isWonLast()) {
			this.playerSprite = character.getBuffSprite().getFrame();
			this.cosmeticOffset.set(character.getBuffHatOffset()).scl(spriteScale);
		} else {
			this.playerSprite = character.getSlugSprite().getFrame();
			this.cosmeticOffset.set(character.getSlugHatOffset()).scl(spriteScale);
		}

		setHeight(spriteHeight * spriteScale);
		setWidth(spriteWidth * spriteScale);

		//Based on the player color, we create an fbo to accurately display their sprite.
		AlignmentFilter team = fieldsExtra.getLoadout().team;

		fbo = new FrameBuffer(Pixmap.Format.RGBA4444, playerSprite.getRegionWidth(), playerSprite.getRegionHeight(), true);
		fbo.begin();

		//clear buffer, set camera
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.getProjectionMatrix().setToOrtho2D(0, 0, fbo.getWidth(), fbo.getHeight());

		//use shader to apply new team color
		batch.begin();
		ShaderProgram shader;
		if (team.isTeam() && team != AlignmentFilter.NONE) {
			shader = team.getShader(character);
		} else {
			shader = character.getPalette().getShader(character);
		}
		batch.setShader(shader);

		batch.draw(playerSprite, 0, 0);

		if (shader != null) {
			batch.setShader(null);
		}
		batch.end();
		fbo.end();

		if (shader != null) {
			shader.dispose();
		}

		TextureRegion fboRegion = new TextureRegion(fbo.getColorBufferTexture());

		playerSprite = new TextureRegion(fboRegion, fboRegion.getRegionX(), fboRegion.getRegionHeight() - fboRegion.getRegionY(),
			fboRegion.getRegionWidth(), - fboRegion.getRegionHeight());
	}

	private float animationTimeExtra;
	@Override
	public void act(float delta) {
		super.act(delta);
		animationTimeExtra += delta;
	}

	@Override
    public void draw(Batch batch, float alpha) {
		float spriteX = getX() + spriteOffsetY;
		float spriteY = getY();
		batch.draw(playerSprite, spriteX, spriteY, playerSprite.getRegionWidth() * spriteScale, playerSprite.getRegionHeight() * spriteScale);

		//draw cosmetics on the slug/buff players
		for (UnlockCosmetic cosmetic : cosmetics) {
			cosmetic.render(batch, character, animationTimeExtra, spriteScale, false,
					spriteX + cosmeticOffset.x, spriteY + cosmeticOffset.y);
		}

		HadalGame.FONT_UI.getData().setScale(fontScale);
		HadalGame.FONT_UI.draw(batch, name, getX() + textOffsetX,getY() + textOffsetY, textWidth, Align.center, true);

		if (ready) {
			batch.draw(readyIcon, getX() + readyWidth + readyOffsetX, getY() + readyOffsetY, -readyWidth, readyHeight);
		}
    }

    public void dispose() {
		fbo.dispose();
	}

	public void setReady(boolean ready) { this.ready = ready; }
}
