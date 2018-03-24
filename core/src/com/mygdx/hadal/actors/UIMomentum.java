package com.mygdx.hadal.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * UIMomentum appears in the bottom right screen and displays information about the player's momentum freezing cd and stored momentums
 * @author Zachary Tu
 *
 */
public class UIMomentum extends AHadalActor{

	private Player player;
	private PlayState state;
	private BitmapFont font;
	
	private TextureAtlas atlas;
	
	private TextureRegion base, ready, overlay;
	private Array<AtlasRegion> arrow;
	
	private float scale = 0.75f;
	private static final int x = 0;
	private static final int y = 0;
	
	//These 2 determine the magnitude of velocity needed to show up as larger arrows
	private static final int threshold1 = 100;
	private static final int threshold2 = 300;
	
	public UIMomentum(AssetManager assetManager, PlayState state, Player player) {
		super(assetManager);
		this.player = player;
		this.state = state;
		this.font = HadalGame.SYSTEM_FONT_UI;
		
		this.atlas = (TextureAtlas) HadalGame.assetManager.get(AssetList.UIATLAS.toString());
		this.base = atlas.findRegion("UI_momentum_base");
		this.ready = atlas.findRegion("UI_momentum_ready");
		this.overlay = atlas.findRegion("UI_momentum_overlay");
		
		this.arrow = atlas.findRegions("UI_momentum_arrow");
		
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		batch.setProjectionMatrix(state.hud.combined);

		batch.draw(base, x + HadalGame.CONFIG_WIDTH - base.getRegionWidth() * scale, y, base.getRegionWidth() * scale, base.getRegionHeight() * scale);
		
		//Indicate cooldown on momentum freeze
		if (player.getMomentumCdCount() < 0) {
			batch.draw(ready, x + HadalGame.CONFIG_WIDTH - base.getRegionWidth() * scale, y, base.getRegionWidth() * scale, base.getRegionHeight() * scale);
		} else {
			font.getData().setScale(0.5f);
			font.draw(batch, Math.round(player.getMomentumCdCount()) +" CD", x + HadalGame.CONFIG_WIDTH - base.getRegionWidth() * scale + 34, 64);
		}
		
		batch.draw(overlay, x + HadalGame.CONFIG_WIDTH - base.getRegionWidth() * scale, y, base.getRegionWidth() * scale, base.getRegionHeight() * scale);
		
		//If any momentums stored, display them according to orientation and magnitude
		if (player.getMomentums().size != 0) {
			Vector2 nextVec = player.getMomentums().first();
			
			TextureRegion tex = arrow.get(0);
			
			if (nextVec.len2() > threshold1) {
				tex = arrow.get(1);
			}

			if (nextVec.len2() > threshold2) {
				tex = arrow.get(2);
			}
			
			batch.draw(tex, x + HadalGame.CONFIG_WIDTH - base.getRegionWidth() * scale, y,
					base.getRegionWidth() * scale / 2, base.getRegionHeight() * scale / 2,
					base.getRegionWidth() * scale, base.getRegionWidth() * scale,
					1, 1, nextVec.angle() - 90);
		}
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
