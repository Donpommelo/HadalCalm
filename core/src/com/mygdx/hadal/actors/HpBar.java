package com.mygdx.hadal.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

public class HpBar extends AHadalActor {

	private Player player;
	private PlayState state;
	private ShapeRenderer shapeRenderer;
	
	private final float hpPerUnit = 2.5f;
	private final float fuelPerUnit = 1.5f;
	
	public HpBar(AssetManager assetManager, PlayState state, Player player) {
		super(assetManager);
		this.player = player;
		this.state = state;
		this.shapeRenderer = new ShapeRenderer();
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		shapeRenderer.setProjectionMatrix(state.hud.combined);
		shapeRenderer.begin(ShapeType.Filled);
		
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.rect(25, 25, player.getPlayerData().getMaxHp() * hpPerUnit, 50);
		shapeRenderer.setColor(Color.GREEN);
		shapeRenderer.rect(25, 25, player.getPlayerData().currentHp * hpPerUnit, 50);
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.rect(25, 75, player.getPlayerData().getMaxFuel() * fuelPerUnit, 25);
		shapeRenderer.setColor(Color.BLUE);
		shapeRenderer.rect(25, 75, player.getPlayerData().currentFuel * fuelPerUnit, 25);
		shapeRenderer.end();
	}

}
