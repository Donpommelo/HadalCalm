package com.mygdx.hadal.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

public class playUI extends AHadalActor{

	private Player player;
	private PlayState state;
	private BitmapFont font;
	
	public playUI(AssetManager assetManager, PlayState state, Player player) {
		super(assetManager);
		this.player = player;
		this.state = state;
		this.font = new BitmapFont();
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		batch.setProjectionMatrix(state.hud.combined);
//		batch.begin();
		font.getData().setScale(1.5f);
		font.draw(batch, "Score: " + state.score+ " Hp: " + Math.round(player.getPlayerData().currentHp) + "/" + player.getPlayerData().getMaxHp() + " Fuel: " + Math.round(player.getPlayerData().currentFuel), 60, 80);
		font.draw(batch, player.getPlayerData().currentTool.getText(), 60, 60);
		if (player.momentums.size != 0) {
			font.draw(batch, "Saved Momentum: " + player.momentums.first(), 60, 40);
		}
		if (player.currentEvent != null) {
			font.draw(batch, player.currentEvent.getText(), 60, 20);
		}
//		batch.end();
	}

}
