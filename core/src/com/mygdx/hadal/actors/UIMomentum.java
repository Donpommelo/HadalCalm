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
	
	public UIMomentum(AssetManager assetManager, PlayState state, Player player) {
		super(assetManager);
		this.player = player;
		this.state = state;
		this.font = new BitmapFont();
		
		this.atlas = (TextureAtlas) HadalGame.assetManager.get(AssetList.UIATLAS.toString());
		this.base = atlas.findRegion("ui_momentum_base");
		this.ready = atlas.findRegion("ui_momentum_ready");
		this.overlay = atlas.findRegion("ui_momentum_overlay");
		
		this.arrow = atlas.findRegions("ui_momentum_arrow");
		
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		batch.setProjectionMatrix(state.hud.combined);

		batch.draw(base, x + HadalGame.CONFIG_WIDTH - base.getRegionWidth() * scale, y, base.getRegionWidth() * scale, base.getRegionHeight() * scale);
		
		if (player.momentumCdCount < 0) {
			batch.draw(ready, x + HadalGame.CONFIG_WIDTH - base.getRegionWidth() * scale, y, base.getRegionWidth() * scale, base.getRegionHeight() * scale);
		} else {
			font.getData().setScale(1.5f);
			font.draw(batch, Math.round(player.momentumCdCount) +" CD", x + HadalGame.CONFIG_WIDTH - base.getRegionWidth() * scale + 40, 120);
		}
		
		batch.draw(overlay, x + HadalGame.CONFIG_WIDTH - base.getRegionWidth() * scale, y, base.getRegionWidth() * scale, base.getRegionHeight() * scale);
		
		if (player.momentums.size != 0) {
			Vector2 nextVec = player.momentums.first();
			
			batch.draw(arrow.get(2), x + HadalGame.CONFIG_WIDTH - base.getRegionWidth() * scale, y,
					base.getRegionWidth() * scale / 2, base.getRegionHeight() * scale / 2,
					base.getRegionWidth() * scale, base.getRegionWidth() * scale,
					1, 1, nextVec.angle() - 90);
		}
	}

}
