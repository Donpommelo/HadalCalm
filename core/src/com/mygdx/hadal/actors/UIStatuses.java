package com.mygdx.hadal.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class UIStatuses extends AHadalActor{

	private BitmapFont font;
	
	private Player player;
	private PlayState state;
	
	public UIStatuses(AssetManager assetManager, PlayState state, Player player) {
		super(assetManager);
		this.state = state;
		this.player = player;
		
		this.font = HadalGame.SYSTEM_FONT_UI;
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		batch.setProjectionMatrix(state.hud.combined);

		font.getData().setScale(0.4f);
		
		String text = "";
		for (Status s : player.getPlayerData().getStatuses()) {
			text = text.concat(s.getName() + " \n");
		}
		
		for (Status s : player.getPlayerData().getStatusesChecked()) {
			text = text.concat(s.getName() + " \n");
		}
		
		font.draw(batch, text, 0, HadalGame.CONFIG_HEIGHT - 10);
	}
	

}
